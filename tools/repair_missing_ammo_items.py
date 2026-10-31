"""Replace two unregistered VVP ammunition items with the SBW equivalents.

Preserves projectile type, damage, velocity, reload and all per-ammo overrides.
Pseudo ammo such as @RifleAmmo, @HeavyAmmo and FE is valid and stays untouched.
"""
from pathlib import Path
import json
ROOT=Path(__file__).resolve().parents[1]
mapping={'vvp:item_30mm':'superbwarfare:small_shell_ap','vvp:ap_shell':'superbwarfare:large_shell_ap'}
changed=[]
def fix(v):
 if isinstance(v,dict):
  out={k:fix(w) for k,w in v.items()}
  if 'AmmoType' in out:
   a=out['AmmoType'];arr=a if isinstance(a,list) else [a]
   for i,e in enumerate(arr):
    if isinstance(e,str):arr[i]=mapping.get(e,e)
    elif isinstance(e,dict) and e.get('Ammo') in mapping:e['Ammo']=mapping[e['Ammo']]
   out['AmmoType']=arr if isinstance(a,list) else arr[0]
  return out
 if isinstance(v,list):return [fix(w) for w in v]
 return v
for p in (ROOT/'resources/data/zzyvehicles/sbw/vehicles').glob('*.json'):
 old=json.loads(p.read_text(encoding='utf-8'));new=fix(old)
 if old!=new:p.write_text(json.dumps(new,ensure_ascii=False,indent=2)+'\n',encoding='utf-8');changed.append(p.stem)
(ROOT/'local/review-20261031/ammo-item-repairs.json').write_text(json.dumps({'mapping':mapping,'vehicles':changed},indent=2),encoding='utf-8');print('MISSING_AMMO_ITEMS_FIXED',len(changed),','.join(changed))
