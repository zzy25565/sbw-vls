"""Repair legacy SBW icon paths and one-seat vehicles pointing at a nonexistent gunner."""
from pathlib import Path
import json,zipfile
ROOT=Path(__file__).resolve().parents[1]
sources=list(Path(r'C:\Users\z\Desktop\卓越前线附属').glob('*.jar'))
references={
 'superbwarfare:textures/vehicle_icon/ah_64_icon.png':'dragonrise_reforge:textures/vehicle_icon/ah64_icon.png',
 'superbwarfare:textures/vehicle_icon/humvee_icon.png':'dragonrise_reforge:textures/vehicle_icon/humvee_icon.png',
 'superbwarfare:textures/overlay/vehicle/weapon/icons/aam.png':'dragonrise_reforge:textures/overlay/vehicle/weapon/icons/aam.png',
 'superbwarfare:textures/overlay/vehicle/weapon/icons/cannon_23mm.png':'dragonrise_reforge:textures/overlay/vehicle/weapon/icons/cannon_23mm.png',
 'superbwarfare:textures/vehicle_icon/bmp2_icon.png':'superbwarfare:textures/vehicle_icon/bmp_2_icon.png',
 'superbwarfare:textures/screens/vehicle_weapon/cannon_120mm.png':'superbwarfare:textures/overlay/vehicle/weapon/icons/ap_shell.png',
 'superbwarfare:textures/screens/vehicle_weapon/gun_7_62mm.png':'superbwarfare:textures/overlay/vehicle/weapon/icons/gun_7_62mm.png',
 'superbwarfare:textures/screens/vehicle_weapon/gun_12_7mm.png':'superbwarfare:textures/overlay/vehicle/weapon/icons/gun_12_7mm.png',
}
for name in ['m12','m12gau','m12hmg','m12roc','m12tra','m274','m274m','m808','t77h','uh144']:
 ref=f'halovecs:textures/vehicle_icon/{name}_icon.png';references[ref]=ref
for name in ['a_2','cannon_70mm']:
 ref=f'halovecs:textures/overlay/vehicle/weapon/icons/{name}.png';references[ref]=ref
manifest=ROOT/'tools/display_resource_overrides.json';overrides=set(json.loads(manifest.read_text(encoding='utf-8')))
mapping={};copied=[]
for old,ref in references.items():
 if ref.startswith('superbwarfare:'):mapping[old]=ref;continue
 ns,path=ref.split(':',1);original='assets/'+ns+'/'+path;target='assets/zzyvehicles/restored/'+ns+'/'+path
 for source in sources:
  with zipfile.ZipFile(source) as z:
   if original not in z.namelist():continue
   p=ROOT/'resources'/target;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(z.read(original));overrides.add(target)
   mapping[old]='zzyvehicles:restored/'+ns+'/'+path;copied.append({'from':old,'original':ref,'source':source.name});break
 else:raise ValueError('Missing original '+ref)
def replace(v):
 if isinstance(v,dict):return {k:replace(w) for k,w in v.items()}
 if isinstance(v,list):return [replace(w) for w in v]
 return mapping.get(v,v) if isinstance(v,str) else v
seats=[]
for p in (ROOT/'resources/data/zzyvehicles/sbw/vehicles').glob('*.json'):
 d=json.loads(p.read_text(encoding='utf-8'));n=replace(d)
 # Index 20 on supply trucks is an original no-gunner sentinel. Do not activate it.
 if p.stem in {'camel','iris_slm','iris_slm_fcs','ka50'}:
  assert len(n['Seats'])==1
  if n['TurretControllerIndex']!=0:seats.append(p.stem)
  n['TurretControllerIndex']=0
 if n!=d:p.write_text(json.dumps(n,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
manifest.write_text(json.dumps(sorted(overrides),indent=2)+'\n',encoding='utf-8')
(ROOT/'local/review-20261031/ui-reference-repairs.json').write_text(json.dumps({'mapping':mapping,'originals':copied,'one_seat_controllers':seats},ensure_ascii=False,indent=2),encoding='utf-8')
print('UI paths:',len(mapping),'controller repairs:',seats)
