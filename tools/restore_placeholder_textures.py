"""Resolve importer-created grey placeholders from original entity/renderer texture declarations."""
from pathlib import Path
import json,re,zipfile,subprocess,hashlib
ROOT=Path(__file__).resolve().parents[1]
JDK=Path(r'C:\Program Files\Microsoft\jdk-21.0.9.10-hotspot\bin')
source=Path(r'D:\WorkBuddyProjects\2026-09-05-17-04-12\restored_from_vss\snapshot_2\fcp-1.2.1.jar')
low=json.loads((ROOT/'local/review-20261031/low-color-textures.json').read_text(encoding='utf-8'))
def norm(s):return re.sub('[^a-z0-9]','',s.lower())
overrides=set(json.loads((ROOT/'tools/display_resource_overrides.json').read_text(encoding='utf-8')));restored=[];unresolved=[]
with zipfile.ZipFile(source) as z:
 classes=[n for n in z.namelist() if n.endswith(('Entity.class','Renderer.class')) and '$' not in n]
 for row in low:
  if row['colors']!=[[4096,[150,150,155,255]]]:continue
  vid=row['id'];key=norm(vid);keys={key,key.removeprefix('empl')};matches=[n for n in classes if norm(Path(n).stem.removesuffix('Entity').removesuffix('Renderer')) in keys]
  matches.sort(key=lambda n:0 if n.endswith('Entity.class') else 1);found=None
  for n in matches:
   r=subprocess.run([str(JDK/'javap.exe'),'-classpath',str(source),'-c','-p',n[:-6].replace('/','.')],capture_output=True);text=r.stdout.decode('utf-8',errors='replace')
   paths=re.findall(r'// String (textures/entity/[^\s]+\.png)',text)
   if paths and 'assets/fcp/'+paths[0] in z.namelist():found=(n,'assets/fcp/'+paths[0]);break
  if found is None and vid.startswith('hmmwv_'):
   # These are attachment variants of HumveeVehicle, not separate entity classes.
   # HumveeUnarmedEntity.buildTextures() constructs this path from SKINS[0].
   path='assets/fcp/textures/entity/humvee/hmww_tex_1.png'
   if path in z.namelist():found=('frontline/combat/fcp/entity/vehicle/Humvee/HumveeUnarmedEntity.class (shared default skin)',path)
  if found:
   clazz,path=found;target='assets/zzyvehicles/textures/bedrock/vehicle/'+vid+'.png';p=ROOT/'resources'/target;p.parent.mkdir(parents=True,exist_ok=True);data=z.read(path);p.write_bytes(data);overrides.add(target);restored.append({'id':vid,'class':clazz,'original_texture':path,'sha256':hashlib.sha256(data).hexdigest()})
  else:unresolved.append(vid)
(ROOT/'tools/display_resource_overrides.json').write_text(json.dumps(sorted(overrides),indent=2)+'\n',encoding='utf-8')
(ROOT/'local/review-20261031/placeholder-texture-recovery.json').write_text(json.dumps({'restored':restored,'unresolved':unresolved},indent=2),encoding='utf-8');print('PLACEHOLDER_TEXTURES_RECOVERED',len(restored));print('UNRESOLVED',','.join(unresolved))
