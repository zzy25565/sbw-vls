"""Independent checks against the final distributable, including shared resource references."""
from pathlib import Path
import json,zipfile,re,hashlib
ROOT=Path(__file__).resolve().parents[1]
report=json.loads((ROOT/'local/release-checks.json').read_text(encoding='utf-8'))
removed=json.loads((ROOT/'tools/removed_ww2.json').read_text(encoding='utf-8'))
ids=set(removed['current'])|set(removed['previously_removed'])
pattern=re.compile(r'(?:zzyvehicles|sbw_vls):(?:'+ '|'.join(map(re.escape,ids))+r')(?=["\s/]|$)')
with zipfile.ZipFile(report['output']) as z:
    names=set(z.namelist());refs=[];broken=[]
    with zipfile.ZipFile(ROOT.parent/'sbw_loadout/libs/superbwarfare-0.8.9.1.jar') as upstream:
        available=names|set(upstream.namelist())
    missing_png=[];bad_indices=[];bad_seats=[]
    for n in sorted(names):
        if not n.endswith('.json'):continue
        raw=z.read(n).decode('utf-8-sig');doc=json.loads(raw)
        if pattern.search(raw):refs.append(n)
        if n.startswith('data/zzyvehicles/sbw/vehicles/'):
            def check_png(v):
                if isinstance(v,dict):
                    for w in v.values():check_png(w)
                elif isinstance(v,list):
                    for w in v:check_png(w)
                elif isinstance(v,str) and ':' in v and v.endswith('.png'):
                    ns,path=v.split(':',1)
                    if 'assets/'+ns+'/'+path not in available:missing_png.append([n,v])
            check_png(doc)
            weapons=doc.get('Weapons',{})
            for i,seat in enumerate(doc.get('Seats',[])):
                if not isinstance(seat,dict):continue  # Shared named seat templates are resolved by SBW.
                for key in seat.get('Weapons',[]):
                    if key not in weapons:bad_seats.append([n,i,key])
        if '/sbw/vehicles/' not in n or not n.startswith('assets/'):continue
        if not doc.get('Models'):bad_indices.append(n)
        def visit(v):
            if isinstance(v,dict):
                for w in v.values():visit(w)
            elif isinstance(v,list):
                for w in v:visit(w)
            elif isinstance(v,str) and v.startswith('zzyvehicles:') and v.endswith(('.png','.json')):
                p='assets/zzyvehicles/'+v.split(':',1)[1]
                if p not in names:broken.append([n,p])
        visit(doc)
    assert not refs,refs
    assert not broken,broken
    assert not missing_png,missing_png
    assert not bad_indices,bad_indices
    assert not bad_seats,bad_seats
    assert 'cn/zzy/vehicles/ZzyVehicles.class' in names
    assert not any('Fixture' in n or 'Smoke' in n or '/test/' in n for n in names if n.endswith('.class'))
    assert len(names)==len(z.namelist()),'Duplicate zip members'
    report['removed_resource_references']=refs;report['missing_model_references']=broken
    report['production_classes_only']=True
    report['missing_ui_png']=missing_png;report['bad_model_indices']=bad_indices;report['bad_seat_weapons']=bad_seats
    assert hashlib.file_digest(Path(report['output']).open('rb'),'sha256').hexdigest()==report['sha256']
(ROOT/'local/release-checks.json').write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding='utf-8')
print('PASS: removed refs=0, missing model/UI refs=0, bad model indices=0, bad seat weapon refs=0, duplicate ZIP members=0, fixture classes=0')
