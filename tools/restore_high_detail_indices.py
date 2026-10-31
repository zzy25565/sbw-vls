"""Use the existing per-material renderer for all nine unmodified WT meshes.

Its coordinate and UV conventions are the original export's conventions. A native
SBM placeholder rig is only for resource indexing, never a replacement for the mesh.
"""
from pathlib import Path
import json,zipfile
ROOT=Path(__file__).resolve().parents[1]
IDS=['bmd_4m','bmp_3','challenger_2','iris_slm_fcs','iris_slm','leopard_2a5','t_80bvm','t_80u','zsu_23_4']
p=ROOT/'tools/display_resource_overrides.json';overrides=set(json.loads(p.read_text(encoding='utf-8')))
with zipfile.ZipFile(ROOT/'local/input-1.0.0m.jar') as z:
 for vid in IDS:
  path=f'assets/zzyvehicles/models/bedrock/vehicle/{vid}.geo.json';doc=json.loads(z.read(path));g=doc['minecraft:geometry'][0]
  assert any('zzy_material' in b and 'poly_mesh' in b for b in g['bones'])
  for bone in g['bones']:
   bone.pop('poly_mesh',None);bone.pop('cubes',None)
  rig=f'assets/zzyvehicles/models/bedrock/vehicle/{vid}.rig.geo.json';target=ROOT/'resources'/rig;target.parent.mkdir(parents=True,exist_ok=True);target.write_text(json.dumps(doc,separators=(',',':')),encoding='utf-8');overrides.add(rig)
  index=f'assets/zzyvehicles/sbw/vehicles/{vid}.json';target=ROOT/'resources'/index;target.parent.mkdir(parents=True,exist_ok=True)
  target.write_text(json.dumps({'Models':[{'Model':'zzyvehicles:'+rig.split('zzyvehicles/',1)[1],'Texture':f'zzyvehicles:textures/vehicles/{vid}/material_00.png'}]},indent=2)+'\n',encoding='utf-8');overrides.add(index)
p.write_text(json.dumps(sorted(overrides),indent=2)+'\n',encoding='utf-8');print('HIGH_DETAIL_RENDERER_INDICES',len(IDS),'source meshes unchanged')
