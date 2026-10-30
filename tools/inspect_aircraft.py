"""Inventory actual release resources before changing any aircraft models."""
from pathlib import Path
import json, zipfile, hashlib, collections

ROOT = Path(__file__).resolve().parents[1]
SOURCE = Path(r'D:\WorkBuddyProjects\2026-10-26-20-02-34\dist\zzy-vehicles-1.0.0j.jar')
OUT = ROOT / 'local'
OUT.mkdir(exist_ok=True)
def resource(s):
    ns, path = s.split(':', 1)
    return 'assets/' + ns + '/' + path

def main():
    with zipfile.ZipFile(SOURCE) as z:
        names = set(z.namelist())
        rows = []
        for p in sorted(names):
            if not p.startswith('data/zzyvehicles/sbw/vehicles/') or not p.endswith('.json'): continue
            d = json.loads(z.read(p)); vid = Path(p).stem
            if d.get('Type') not in ('Airplane', 'Helicopter', 'Drone', 'AirShip'): continue
            index = f'assets/zzyvehicles/sbw/vehicles/{vid}.json'
            idx = json.loads(z.read(index)) if index in names else {}
            geo = f'assets/zzyvehicles/models/bedrock/vehicle/{vid}.geo.json'
            doc = json.loads(z.read(geo)) if geo in names else {}
            bones = doc.get('minecraft:geometry', [{}])[0].get('bones', [])
            refs = []
            def visit(v, key=''):
                if isinstance(v, dict):
                    for k, w in v.items(): visit(w, k)
                elif isinstance(v, list):
                    for w in v: visit(w, key)
                elif isinstance(v, str) and ':' in v and any(v.endswith(s) for s in ('.png','.json')):
                    refs.append({'key':key,'path':v,'exists':resource(v) in names})
            visit(idx)
            row = dict(id=vid,type=d['Type'],index=idx,geo=geo,refs=refs,
                       bones=[{'name':b['name'],'parent':b.get('parent'),'pivot':b.get('pivot',[0,0,0]),
                               'rotation':b.get('rotation',[0,0,0]),'cubes':len(b.get('cubes',[])),
                               'poly':len(b.get('poly_mesh',{}).get('positions',[]))} for b in bones],
                       weapons=d.get('Weapons'),obb=d.get('OBB'),animations=d.get('Animation'),
                       names=collections.Counter(b['name'] for b in bones),missing_parents=[],
                       duplicate_bones=[],empty_geometry=not any(b.get('cubes') or b.get('poly_mesh') for b in bones))
            bn = set(b['name'] for b in bones)
            row['missing_parents']=[b['name'] for b in bones if b.get('parent') and b['parent'] not in bn]
            row['duplicate_bones']=[n for n,c in row.pop('names').items() if c>1]
            rows.append(row)
        result={'source':str(SOURCE),'sha256':hashlib.file_digest(SOURCE.open('rb'),'sha256').hexdigest(),
                'types':dict(collections.Counter(r['type'] for r in rows)), 'aircraft':rows}
        (OUT/'aircraft-inventory.json').write_text(json.dumps(result,ensure_ascii=False,indent=2),encoding='utf-8')
        (OUT/'source-version.toml').write_bytes(z.read('META-INF/neoforge.mods.toml'))
        print('RELEASE',result['sha256'],result['types'])
        for r in rows:
            mounts=[b['name'] for b in r['bones'] if any(k in b['name'].lower() for k in ('missil','bomb','rocket','pylon','weapon','wing','gear','load','attach','hard','rack'))]
            print(r['id'],r['type'],'bones',len(r['bones']),'mounts',','.join(mounts),'MISSING',sum(not x['exists'] for x in r['refs']),'dup',r['duplicate_bones'],'parents',r['missing_parents'])

if __name__ == "__main__":
    main()
