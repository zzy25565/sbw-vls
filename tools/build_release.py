"""Rebuild the workspace Java sources, preserve unchanged assets from the verified input JAR."""
from pathlib import Path
import subprocess,json,zipfile,re,hashlib,sys
ROOT=Path(__file__).resolve().parents[1]
INPUT=ROOT/'local/input-1.0.0j.jar'
assert hashlib.file_digest(INPUT.open('rb'),'sha256').hexdigest()=='be27945c2217c0b879f95cfb83c24b5bd379af674e0d4c13d25e1ed8ae8006f7', 'Unexpected base JAR'
subprocess.run([sys.executable,str(ROOT/'tools/audit_blender_repairs.py')],check=True,capture_output=True)
VERSION='1.0.0k'
OUT=ROOT/'local'/('zzy-vehicles-'+VERSION+'.jar')
manifest=json.loads((ROOT/'tools/removed_ww2.json').read_text(encoding='utf-8'))
removed=set(manifest['current'])|set(manifest['previously_removed'])
JDK=Path(r'C:\Program Files\Microsoft\jdk-21.0.9.10-hotspot\bin')
cp=(ROOT.parent/'sbw_loadout/build/radar-compile-classpath.txt').read_text(encoding='utf-8').splitlines()
cp.append(str(ROOT.parent/'sbw_loadout/libs/mae-1.1.4.jar'))
classes=ROOT/'local/classes';classes.mkdir(exist_ok=True)
args=['-encoding','UTF-8','-proc:none','-source','21','-target','21','-cp',';'.join(cp),'-d',str(classes)]+[str(p) for p in (ROOT/'java').rglob('*.java')]
argfile=ROOT/'local/javac.args';argfile.write_text('\n'.join('"'+s.replace('\\','/')+'"' for s in args),encoding='utf-8')
result=subprocess.run([str(JDK/'javac.exe'),'@'+str(argfile)],capture_output=True)
(ROOT/'local/compile.log').write_bytes(result.stdout+result.stderr)
if result.returncode: print((result.stdout+result.stderr).decode(errors='replace'));sys.exit(result.returncode)
def is_removed(path):
    return any(Path(path).name==v+e for v in removed for e in ('.json','.png','.geo.json','.animation.json'))
with zipfile.ZipFile(INPUT) as original:
    huey=original.read('assets/zzyvehicles/textures/bedrock/vehicle/huey.png')
assert hashlib.sha256(huey).hexdigest()=='0065b2f6e1fdb019f9f20dfb112b238f7e3c6d5dc4dff42c180f788c64016a67'
changes=[]; dropped=[]; copied=[]
repaired_models=json.loads((ROOT/'tools/model_repairs.json').read_text(encoding='utf-8'))
repaired_models.update(json.loads((ROOT/'tools/orientation_repairs.json').read_text(encoding='utf-8')))
with zipfile.ZipFile(INPUT) as src,zipfile.ZipFile(OUT,'w',zipfile.ZIP_DEFLATED,compresslevel=6) as dst:
    for n in src.namelist():
        if n.endswith('/'):continue
        if is_removed(n):dropped.append(n);continue
        if n.endswith('.class') and n.startswith(('cn/','com/')):continue
        data=src.read(n);new=data
        if n=='META-INF/neoforge.mods.toml':
            s=data.decode('utf-8');s=s.replace('version="1.0.0j"','version="'+VERSION+'"')
            s=re.sub(r'^displayName=.*$', 'displayName="VLS 附属"',s,flags=re.M)
            s=re.sub(r'^description=.*$', 'description="SBW VLS 附属载具包（281 台载具定义，modId 保持 zzyvehicles 以兼容旧存档）。包含二战平台清理、五款飞机机头 Blender 重建、Mi-17 朝向修复；并为 venom/mi17/toyota_seiki/z_sts_akhmat/aavc7c1 接入武器系统。"',s,flags=re.M)
            new=s.encode('utf-8')
        elif n.startswith('assets/') and '/lang/' in n and n.endswith('.json'):
            lang=json.loads(data); lang={k:v for k,v in lang.items() if not any(vv in k.split('.') for vv in removed)}
            new=json.dumps(lang,ensure_ascii=False,indent=2).encode('utf-8')
        elif n=='assets/zzyvehicles/textures/bedrock/vehicle/huey_rockets.png':new=huey
        elif n in {f'assets/zzyvehicles/models/bedrock/vehicle/{vid}.geo.json' for vid in repaired_models}:
            new=(ROOT/'resources'/n).read_bytes()
        # Only explicit workspace resource edits override the input; other geometry stays byte-identical.
        rp=ROOT/'resources'/n
        if rp.exists() and n.startswith('data/'):
            doc=json.loads(rp.read_text(encoding='utf-8'))
            if 'Loadout' in doc:new=rp.read_bytes()
        if new!=data:changes.append(n)
        else:copied.append(n)
        dst.writestr(n,new)
    for p in classes.rglob('*.class'):dst.writestr(p.relative_to(classes).as_posix(),p.read_bytes())
with zipfile.ZipFile(OUT) as z:
    assert z.testzip() is None
    assert not any(is_removed(n) for n in z.namelist())
    data=[n for n in z.namelist() if n.startswith('data/zzyvehicles/sbw/vehicles/') and n.endswith('.json')]
    assert len(data)==281, len(data)
    aircraft=[n for n in data if json.loads(z.read(n)).get('Type') in ['Airplane','Helicopter']]
    assert len(aircraft)==60
report=dict(version=VERSION,source_sha256=hashlib.file_digest(INPUT.open('rb'),'sha256').hexdigest(),
            output=str(OUT),sha256=hashlib.file_digest(OUT.open('rb'),'sha256').hexdigest(),
            removed_platforms=manifest['current'],removed_resources=dropped,changed_resources=changes,
            preserved_resources=len(copied),definitions=len(data),aircraft=len(aircraft))
(ROOT/'local/release-checks.json').write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding='utf-8')
print(json.dumps({k:v for k,v in report.items() if k not in ['removed_resources','changed_resources']},ensure_ascii=False,indent=2))
