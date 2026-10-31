"""Restore referenced weapon translations and readable names for the current fleet only."""
from pathlib import Path
import json,re,zipfile
ROOT=Path(__file__).resolve().parents[1]
sources=list(Path(r'C:\Users\z\Desktop\卓越前线附属').glob('*.jar'))+[Path(r'D:\WorkBuddyProjects\2026-09-05-17-04-12\restored_from_vss\snapshot_2\fcp-1.2.1.jar')]
defs={p.stem:json.loads(p.read_text(encoding='utf-8')) for p in (ROOT/'resources/data/zzyvehicles/sbw/vehicles').glob('*.json')}
keys=set()
def scan(v):
 if isinstance(v,dict):
  for w in v.values():scan(w)
 elif isinstance(v,list):
  for w in v:scan(w)
 elif isinstance(v,str):
  if v.startswith(('weapon.','item.','entity.')):keys.add(v)
for v in defs.values():scan(v)
def norm(s):return re.sub('[^a-z0-9]','',s.lower())
overrides=set(json.loads((ROOT/'tools/display_resource_overrides.json').read_text(encoding='utf-8')));report={}
with zipfile.ZipFile(ROOT/'local/input-1.0.0m.jar') as base:
 for locale in ['zh_cn','en_us']:
  name=f'assets/zzyvehicles/lang/{locale}.json';lang=json.loads(base.read(name));candidates={};weapons={}
  for p in sources:
   with zipfile.ZipFile(p) as z:
    for n in z.namelist():
     if not n.startswith('assets/') or not n.endswith('/lang/'+locale+'.json'):continue
     try:src=json.loads(z.read(n))
     except (ValueError,UnicodeError):continue
     for k,v in src.items():
      if k in keys:weapons[k]=v
      if k.startswith('entity.') and len(k.split('.',2))==3:candidates.setdefault(norm(k.split('.',2)[2]),set()).add(v)
  used=0;fallback=0
  for id in defs:
   k='entity.zzyvehicles.'+id
   if k in lang:continue
   options=candidates.get(norm(id),set())
   if len(options)==1:lang[k]=next(iter(options));used+=1
   else:lang[k]=id.replace('_',' ').upper();fallback+=1
  lang.update(weapons);readable=0
  for key in keys:
   if key not in lang:lang[key]=key.rsplit('.',1)[-1].replace('_',' ').upper();readable+=1
  p=ROOT/'resources'/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(lang,ensure_ascii=False,indent=2)+'\n',encoding='utf-8');overrides.add(name)
  report[locale]={'weapon_keys_restored':len(weapons),'weapon_names_readable_id':readable,'entity_names_restored':used,'entity_names_readable_id':fallback,'unresolved_weapon_keys':sorted(keys-set(lang))}
(ROOT/'tools/display_resource_overrides.json').write_text(json.dumps(sorted(overrides),indent=2)+'\n',encoding='utf-8')
(ROOT/'local/review-20261031/display-name-recovery.json').write_text(json.dumps(report,indent=2),encoding='utf-8');print(json.dumps(report))
