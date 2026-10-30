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
    for n in sorted(names):
        if not n.endswith('.json'):continue
        raw=z.read(n).decode('utf-8-sig');doc=json.loads(raw)
        if pattern.search(raw):refs.append(n)
        if '/sbw/vehicles/' not in n or not n.startswith('assets/'):continue
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
    assert 'cn/zzy/vehicles/ZzyVehicles.class' in names
    assert not any('Fixture' in n or 'Smoke' in n or '/test/' in n for n in names if n.endswith('.class'))
    assert len(names)==len(z.namelist()),'Duplicate zip members'
    report['removed_resource_references']=refs;report['missing_model_references']=broken
    report['production_classes_only']=True
    assert hashlib.file_digest(Path(report['output']).open('rb'),'sha256').hexdigest()==report['sha256']
(ROOT/'local/release-checks.json').write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding='utf-8')
print('PASS: removed refs=0, missing model refs=0, duplicate zip members=0, fixture classes=0')
