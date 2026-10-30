"""Put the actual in-game pose of unmodified parts around the editable Blender skin.

Run QA first to create *-game-context.json. Context is never exported to Minecraft.
"""
import bpy,json,sys
from pathlib import Path
from mathutils import Vector
args=sys.argv[sys.argv.index('--')+1:];root=Path(args[0]);vid=args[1];work=root/'local/blender-repairs'
bpy.ops.wm.open_mainfile(filepath=str(work/f'{vid}.blend'))
skin=bpy.data.objects[vid+'_smooth_skin']
glass=skin.data.materials[1];gt=glass.node_tree.nodes.new('ShaderNodeTexImage');gt.image=next(i for i in bpy.data.images if i.name.endswith('.png'));gt.interpolation='Closest';gb=glass.node_tree.nodes.get('Principled BSDF');glass.node_tree.links.new(gt.outputs['Color'],gb.inputs['Base Color']);glass.node_tree.links.new(gt.outputs['Alpha'],gb.inputs['Alpha'])
for obj in list(bpy.data.objects):
 if obj!=skin:bpy.data.objects.remove(obj,do_unlink=True)
context=json.loads((work/f'{vid}-game-context.json').read_text(encoding='utf-8'));p=context['positions'];uvs=context['uvs']
verts=[p[k:k+3] for k in range(0,len(p),3)];faces=[[k+3,k+2,k+1,k] for k in range(0,len(verts),4)]
mesh=bpy.data.meshes.new('Unmodified original parts in actual game pose');mesh.from_pydata(verts,[],faces);mesh.update()
ref=bpy.data.objects.new('REFERENCE_actual_game_pose_do_not_export',mesh);bpy.context.collection.objects.link(ref)
uv=mesh.uv_layers.new(name='Exact baked game UV')
for poly in mesh.polygons:
 for li,vi in zip(poly.loop_indices,poly.vertices):uv.data[li].uv=uvs[vi*2:vi*2+2]
mat=bpy.data.materials.new('Original unmodified livery context');mat.use_nodes=True;nodes=mat.node_tree.nodes;bs=nodes.get('Principled BSDF');tex=nodes.new('ShaderNodeTexImage');tex.image=next(i for i in bpy.data.images if i.name.endswith('.png'));tex.interpolation='Closest';mat.node_tree.links.new(tex.outputs['Color'],bs.inputs['Base Color']);mat.node_tree.links.new(tex.outputs['Alpha'],bs.inputs['Alpha']);bs.inputs['Roughness'].default_value=.55;bs.inputs['Metallic'].default_value=.25;mesh.materials.append(mat)
points=[Vector(v) for v in verts]+[skin.matrix_world@v.co for v in skin.data.vertices];lo=Vector([min(v[k] for v in points) for k in range(3)]);hi=Vector([max(v[k] for v in points) for k in range(3)]);center=(lo+hi)/2;span=max(hi-lo)
def aim(obj,p):obj.rotation_euler=(p-obj.location).to_track_quat('-Z','Y').to_euler()
camdata=bpy.data.cameras.new('Front quarter preview');cam=bpy.data.objects.new('Front quarter preview',camdata);bpy.context.collection.objects.link(cam);cam.location=center+Vector((span*.75,span,span*.5));aim(cam,center);camdata.type='ORTHO';camdata.ortho_scale=span*1.4;bpy.context.scene.camera=cam
for name,pos,power,size in [('Key',(1,1,2),3000,span*.7),('Fill',(-1,.5,1),2000,span),('Rim',(0,-1,1.5),2500,span*.5)]:
 data=bpy.data.lights.new(name,'AREA');data.energy=power*(span/15)**2;data.shape='DISK';data.size=size;obj=bpy.data.objects.new(name,data);bpy.context.collection.objects.link(obj);obj.location=center+Vector(pos)*span;aim(obj,center)
scene=bpy.context.scene;scene.render.engine='CYCLES';scene.cycles.samples=32;scene.cycles.use_denoising=True;scene.render.resolution_x=1200;scene.render.resolution_y=900;scene.render.resolution_percentage=100;scene.world=bpy.data.worlds.new('Studio');scene.world.color=(.15,.15,.15);scene.render.image_settings.file_format='PNG';scene.render.filepath=str(work/f'{vid}-blender-preview.png')
for obj in bpy.context.selected_objects:obj.select_set(False)
skin.select_set(True);bpy.context.view_layer.objects.active=skin
bpy.ops.wm.save_as_mainfile(filepath=str(work/f'{vid}.blend'))
if '--render' in args:bpy.ops.render.render(write_still=True)
print('BLENDER_GAME_CONTEXT_READY',vid,'original_quads',len(faces),flush=True)
