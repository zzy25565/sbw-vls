"""Export an edited .blend skin without regenerating it or exporting reference parts.

blender --background --python tools/blender_export_edited.py -- <repo> <id> [output]
"""
import bpy,json,zipfile,sys
from pathlib import Path
args=sys.argv[sys.argv.index('--')+1:];root=Path(args[0]);vid=args[1]
bpy.ops.wm.open_mainfile(filepath=str(root/'local/blender-repairs'/f'{vid}.blend'))
obj=bpy.data.objects[vid+'_smooth_skin'];deps=bpy.context.evaluated_depsgraph_get();evaluated=obj.evaluated_get(deps);mesh=evaluated.to_mesh(preserve_all_data_layers=True,depsgraph=deps)
rule=json.loads((root/'tools/model_repairs.json').read_text())[vid][0]
with zipfile.ZipFile(root/'local/input-1.0.0j.jar') as z:doc=json.loads(z.read(f'assets/zzyvehicles/models/bedrock/vehicle/{vid}.geo.json'))
bones=doc['minecraft:geometry'][0]['bones'];byname={b['name']:b for b in bones};bone=byname[rule['bone']];at=bone
while at:
 assert not any(at.get('rotation',[0,0,0])),'Export reviewed neutral body bones only'
 at=byname.get(at.get('parent'))
assert mesh.uv_layers.active is not None,'Keep the livery UV layer'
matrix=obj.matrix_world;normal_matrix=matrix.to_3x3().inverted().transposed();identity=all(abs(matrix[i][j]-(1 if i==j else 0))<1e-8 for i in range(4) for j in range(4))
def point(v):return [v.x*16,v.z*16,-v.y*16]
def normal(v):return [v.x,v.z,-v.y]
positions=[point(v.co if identity else matrix@v.co) for v in mesh.vertices];normals=[];uvs=[];polys=[];nm={};um={};mesh.calc_loop_triangles();uv=mesh.uv_layers.active
def index(a,m,v):
 key=tuple(round(float(x),7) for x in v)
 if key not in m:m[key]=len(a);a.append(list(key))
 return m[key]
for tri in mesh.loop_triangles:
 face=[]
 for vi,li in zip(tri.vertices,tri.loops):
  n=mesh.corner_normals[li].vector
  if not identity:n=(normal_matrix@n).normalized()
  face.append([vi,index(normals,nm,normal(n)),index(uvs,um,uv.data[li].uv)])
 polys.append(face)
bone['cubes']=[c for i,c in enumerate(bone['cubes']) if i not in set(rule['cubes'])];bone['poly_mesh']={'normalized_uvs':True,'positions':positions,'normals':normals,'uvs':uvs,'polys':polys}
output=Path(args[2]) if len(args)>2 else root/'resources/assets/zzyvehicles/models/bedrock/vehicle'/f'{vid}.geo.json'
output.write_text(json.dumps(doc,separators=(',',':')),encoding='utf-8');evaluated.to_mesh_clear();print('EDITED_BLEND_EXPORTED',vid,len(polys),str(output),flush=True)
