from pathlib import Path
import json,zipfile
ROOT=Path(__file__).resolve().parents[1]
rules=json.loads((ROOT/'tools/orientation_repairs.json').read_text(encoding='utf-8'))
with zipfile.ZipFile(ROOT/'local/input-1.0.0j.jar') as z:
 for vid,rule in rules.items():
  path=f'assets/zzyvehicles/models/bedrock/vehicle/{vid}.geo.json'
  doc=json.loads(z.read(path));geo=doc['minecraft:geometry'][0];bones=geo['bones'];old=json.loads(json.dumps(bones))
  name='vls_axis_alignment'
  assert all(b['name']!=name for b in bones)
  roots=[b['name'] for b in bones if not b.get('parent')]
  for b in bones:
   if b['name'] in roots:b['parent']=name
  bones.insert(0,{'name':name,'pivot':[0,0,0],'rotation':[0,rule['yaw'],0]})
  assert sum(len(b.get('cubes',[])) for b in old)==sum(len(b.get('cubes',[])) for b in bones)
  for a,b in zip(old,bones[1:]):
   assert {k:v for k,v in a.items() if k!='parent'}=={k:v for k,v in b.items() if k!='parent'}
  out=ROOT/'resources'/path;out.parent.mkdir(parents=True,exist_ok=True)
  out.write_text(json.dumps(doc,ensure_ascii=False,separators=(',',':')),encoding='utf-8')
  print(vid,'visual yaw corrected',rule['yaw'],'roots',roots,'original cube/UV data preserved')
