"""Blender: reconstruct the reviewed voxel skin as a continuous, smooth high-resolution surface.

Keep other bones, textured parts, animations and full geometry untouched. No decimation.
"""
import bpy,json,zipfile,sys,math,hashlib
from pathlib import Path
from mathutils import Vector,Matrix,Euler
args=sys.argv[sys.argv.index('--')+1:];ROOT=Path(args[0]);vid=args[1]
settings=json.loads((ROOT/'local/blender-settings.json').read_text())[vid]
rule=json.loads((ROOT/'tools/model_repairs.json').read_text())[vid][0]
with zipfile.ZipFile(ROOT/'local/input-1.0.0j.jar') as z:doc=json.loads(z.read(f'assets/zzyvehicles/models/bedrock/vehicle/{vid}.geo.json'))
g=doc['minecraft:geometry'][0];bone=next(b for b in g['bones'] if b['name']==rule['bone']);selected=set(rule['cubes'])
bpy.ops.wm.read_factory_settings(use_empty=True)
FACES=[[0,1,3,2],[4,6,7,5],[0,4,5,1],[2,3,7,6],[0,2,6,4],[1,5,7,3]]
def to_blender(p):return Vector((p[0]/16,-p[2]/16,p[1]/16))
def to_game(p):return [p.x*16,p.z*16,-p.y*16]
def norm_game(p):return [p.x,p.z,-p.y]
verts=[];faces=[]
for i,c in enumerate(bone['cubes']):
 if i not in selected:continue
 assert not c.get('rotation'),'Review rotated skin cubes before remeshing'
 o=c['origin'];s=c['size'];base=len(verts)
 verts.extend(to_blender([o[0]+x*s[0],o[1]+y*s[1],o[2]+z*s[2]]) for x in [0,1] for y in [0,1] for z in [0,1])
 faces.extend([[base+j for j in f] for f in FACES])
mesh=bpy.data.meshes.new(vid+'_original_voxel_skin');mesh.from_pydata(verts,[],faces);mesh.update()
skin=bpy.data.objects.new(vid+'_smooth_skin',mesh);bpy.context.collection.objects.link(skin)
bpy.context.view_layer.objects.active=skin;skin.select_set(True)
before_bounds=[[min(v[k] for v in verts) for k in range(3)],[max(v[k] for v in verts) for k in range(3)]]
print('BLENDER_SKIN_INPUT',vid,len(selected),len(mesh.polygons),flush=True)
remesh=skin.modifiers.new('Continuous exterior union, no decimation','REMESH');remesh.mode='VOXEL';remesh.voxel_size=settings['voxel_size'];remesh.use_smooth_shade=True
bpy.ops.object.modifier_apply(modifier=remesh.name)
smooth=skin.modifiers.new('Remove voxel stair steps','SMOOTH');smooth.factor=settings['smooth_factor'];smooth.iterations=settings['smooth_iterations'];bpy.ops.object.modifier_apply(modifier=smooth.name)
sub=skin.modifiers.new('Curved silhouette','SUBSURF');sub.subdivision_type='CATMULL_CLARK';sub.levels=settings['subdivision'];sub.render_levels=settings['subdivision'];bpy.ops.object.modifier_apply(modifier=sub.name)
mesh=skin.data
for p in mesh.polygons:p.use_smooth=True
im=bpy.data.images.load(str(ROOT/'local/blender-input'/f'{vid}.png'));im.pack()
mat=bpy.data.materials.new(vid+'_matched_original_livery');mat.use_nodes=True
nodes=mat.node_tree.nodes;tex=nodes.new('ShaderNodeTexImage');tex.image=im;tex.interpolation='Closest'
bs=nodes.get('Principled BSDF');mat.node_tree.links.new(tex.outputs['Color'],bs.inputs['Base Color']);bs.inputs['Roughness'].default_value=.55;bs.inputs['Metallic'].default_value=.25
skin.data.materials.append(mat)
glass=bpy.data.materials.new(vid+'_original_clear_glazing');glass.use_nodes=True
gt=glass.node_tree.nodes.new('ShaderNodeTexImage');gt.image=im;gt.interpolation='Closest';gb=glass.node_tree.nodes.get('Principled BSDF')
glass.node_tree.links.new(gt.outputs['Color'],gb.inputs['Base Color']);glass.node_tree.links.new(gt.outputs['Alpha'],gb.inputs['Alpha']);skin.data.materials.append(glass)
tw,th=g['description']['texture_width'],g['description']['texture_height'];px,py=settings['paint_pixel']
paint_uv=((px+.5)/tw,1-(py+.5)/th);clear_uv=((rule['from'][0]+.5)/tw,1-(rule['from'][1]+.5)/th)
uv=mesh.uv_layers.new(name='Original_livery_color')
glass_faces=0
for p in mesh.polygons:
 co=Vector((0,0,0))
 for vi in p.vertices:co+=mesh.vertices[vi].co
 co/=len(p.vertices);raw=Vector(to_game(co))/16
 region=settings.get('glass_region');isglass=region is not None and all(region[0][k]<=raw[k]<=region[1][k] for k in range(3))
 if isglass:p.material_index=1;glass_faces+=1
 for li in p.loop_indices:uv.data[li].uv=clear_uv if isglass else paint_uv
mesh.calc_loop_triangles();positions=[to_game(v.co) for v in mesh.vertices];normals=[];uvs=[];polys=[]
normal_map={};uv_map={}
def idx(values,m,v):
 key=tuple(round(float(x),7) for x in v)
 if key not in m:m[key]=len(values);values.append(list(key))
 return m[key]
for tri in mesh.loop_triangles:
 polys.append([[vi,idx(normals,normal_map,norm_game(mesh.corner_normals[li].vector)),idx(uvs,uv_map,uv.data[li].uv)] for vi,li in zip(tri.vertices,tri.loops)])
bone['cubes']=[c for i,c in enumerate(bone['cubes']) if i not in selected]
assert 'poly_mesh' not in bone
bone['poly_mesh']={'normalized_uvs':True,'positions':positions,'normals':normals,'uvs':uvs,'polys':polys}
out=ROOT/'resources/assets/zzyvehicles/models/bedrock/vehicle'/f'{vid}.geo.json';out.parent.mkdir(parents=True,exist_ok=True);out.write_text(json.dumps(doc,separators=(',',':')),encoding='utf-8')
work=ROOT/'local/blender-repairs';work.mkdir(exist_ok=True)
bpy.ops.wm.save_as_mainfile(filepath=str(work/f'{vid}.blend'))
after_bounds=[[min(v.co[k] for v in mesh.vertices) for k in range(3)],[max(v.co[k] for v in mesh.vertices) for k in range(3)]]
report={'id':vid,'blender':bpy.app.version_string,'source_voxel_cubes':len(selected),'triangles':len(polys),'vertices':len(positions),'paint_srgb8':settings['paint_srgb8'],'paint_pixel':settings['paint_pixel'],'before_bounds_blender':before_bounds,'after_bounds_blender':after_bounds,'glass_faces':glass_faces,'decimation':False,'voxel_size':settings['voxel_size'],'blend':str(work/f'{vid}.blend'),'output':str(out)}
(work/f'{vid}-conversion.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print('BLENDER_AIRFRAME_EXPORTED',json.dumps(report),flush=True)
