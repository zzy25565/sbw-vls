"""Recover referenced original HUD textures without installing incompatible old addons."""
from pathlib import Path
import json,zipfile
ROOT=Path(__file__).resolve().parents[1]
sources=list(Path(r'C:\Users\z\Desktop\卓越前线附属').glob('*.jar'))
references=[]
def scan(v):
 if isinstance(v,dict):
  for w in v.values():scan(w)
 elif isinstance(v,list):
  for w in v:scan(w)
 elif isinstance(v,str) and ':' in v and v.endswith('.png') and not v.startswith(('superbwarfare:','minecraft:','zzyvehicles:')):references.append(v)
with zipfile.ZipFile(ROOT/'local/input-1.0.0m.jar') as source:
 for name in source.namelist():
  if name.startswith('data/zzyvehicles/sbw/vehicles/') and name.endswith('.json'):scan(json.loads(source.read(name)))
textures=sorted(set(references));replacements={};copied=[]
manifest=ROOT/'tools/display_resource_overrides.json'
overrides=json.loads(manifest.read_text(encoding='utf-8')) if manifest.exists() else []
with zipfile.ZipFile(ROOT/'local/input-1.0.0m.jar') as base:
 for ref in textures:
  ns,path=ref.split(':',1);resource='assets/'+ns+'/'+path
  for source in sources:
   with zipfile.ZipFile(source) as z:
    if resource not in z.namelist():continue
    target='assets/zzyvehicles/restored/'+ns+'/'+path
    p=ROOT/'resources'/target;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(z.read(resource))
    replacements[ref]='zzyvehicles:restored/'+ns+'/'+path;overrides.append(target);copied.append({'from':ref,'to':replacements[ref],'source':source.name});break
  else:
   fallback={'gb_500.png':'mk_82.png','missile_kh25.png':'agm_65.png','missile_r60.png':'ru_9m336.png'}
   name=Path(path).name
   if name in fallback:replacements[ref]='superbwarfare:textures/overlay/vehicle/weapon/icons/'+fallback[name]
   elif name=='m1a2sepv1_icon.png':replacements[ref]='zzyvehicles:restored/dragonrise_reforge/textures/vehicle_icon/m1a2sepv2_icon.png'
   else:raise ValueError('Choose an explicit fallback for '+ref)
 def replace(v):
  if isinstance(v,dict):return {k:replace(w) for k,w in v.items()}
  if isinstance(v,list):return [replace(w) for w in v]
  return replacements.get(v,v) if isinstance(v,str) else v
 for p in (ROOT/'resources/data/zzyvehicles/sbw/vehicles').glob('*.json'):
  data=json.loads(p.read_text(encoding='utf-8'));new=replace(data)
  if data!=new:p.write_text(json.dumps(new,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
 # This ordinary cube model had a data document accidentally stored as its model index.
 index='assets/zzyvehicles/sbw/vehicles/toyota_seiki.json';p=ROOT/'resources'/index;p.parent.mkdir(parents=True,exist_ok=True)
 p.write_text(json.dumps({'Models':[{'Model':'zzyvehicles:models/bedrock/vehicle/toyota_seiki.geo.json','Texture':'zzyvehicles:textures/bedrock/vehicle/toyota_seiki.png'}]},indent=2)+'\n',encoding='utf-8');overrides.append(index)
manifest.write_text(json.dumps(sorted(set(overrides)),indent=2)+'\n',encoding='utf-8')
(ROOT/'local/review-20261031/display-resource-recovery.json').write_text(json.dumps({'copied':copied,'fallbacks':{k:v for k,v in replacements.items() if not any(x['from']==k for x in copied)}},ensure_ascii=False,indent=2),encoding='utf-8')
print('ORIGINAL_TEXTURES_RECOVERED',len(copied),'EXPLICIT_FALLBACKS',len(replacements)-len(copied))
