"""Package the runtime-review build, reproducible resource patch and real-game evidence."""
from pathlib import Path
import json,zipfile,hashlib,shutil,re
ROOT=Path(__file__).resolve().parents[1];QA=ROOT/'local/review-20261031'
GAME=Path(r'D:\无bug\.minecraft\versions\1.21.1-NeoForge_21.1.265')
OUT=ROOT.parent/'交付/VLS_1.0.0n_运行审查';OUT.mkdir(parents=True,exist_ok=True)
release=json.loads((ROOT/'local/release-checks.json').read_text(encoding='utf-8'))
jar=Path(release['output']);assert hashlib.file_digest(jar.open('rb'),'sha256').hexdigest()==release['sha256']
assert not (GAME/'mods/vls-review-qa.jar').exists(),'Remove temporary QA mod before delivery'
assert hashlib.file_digest((GAME/'mods'/jar.name).open('rb'),'sha256').hexdigest()==release['sha256']
shutil.copy2(jar,OUT/jar.name)
stages={'verified':('verified-fullpack.log',63,6),'final':('final-exteriors-fullpack.log',281,3),'optics':('optics-fullpack.log',36,6)}
runs={}
for stage,(file,count,views) in stages.items():
 text=(QA/file).read_text(encoding='utf-8',errors='replace')
 assert f'RUNTIME_REVIEW_COMPLETE {count} vehicles {count*views} screenshots' in text,stage
 shots=sorted((GAME/'screenshots').glob(f'runtime_{stage}_*.png'))
 assert len(shots)==count*views,(stage,len(shots))
 zoom=dict(re.findall(r'RUNTIME_REVIEW_ZOOM (\S+) active=(true|false)',text))
 drive={k:float(v) for k,v in re.findall(r'RUNTIME_REVIEW_DRIVE (\S+) displacement=([\d.E+-]+)',text)}
 runs[stage]={'vehicles':re.findall(r'RUNTIME_REVIEW_SPAWN (\S+)',text),'screenshots':len(shots),'zoom_inactive':[k for k,v in zoom.items() if v=='false'],'drive_displacement_m':drive,'missing_geometry':re.findall(r'RUNTIME_REVIEW_MISSING_GEOMETRY (\S+)',text)}
summary={'release_version':'1.0.0n','sha256':release['sha256'],'exterior_pass_sha256':(QA/'exterior-build-sha256.txt').read_text().strip(),'runs':runs,
 'scope':'The 281-model exterior pass precedes the final 80-tick cycle and optics/icon fixes. The 36-model optics pass uses the delivered JAR. Static pose, native zoom and short native forward-input physics checks; not full combat, terrain, aircraft flight or all animations.',
 'temporary_qa_removed':True}
(QA/'delivery-verification.json').write_text(json.dumps(summary,ensure_ascii=False,indent=2),encoding='utf-8')
resources=set(json.loads((ROOT/'tools/display_resource_overrides.json').read_text(encoding='utf-8')))
for file in ['model_repairs.json','orientation_repairs.json']:
 for vid in json.loads((ROOT/'tools'/file).read_text(encoding='utf-8')):resources.add(f'assets/zzyvehicles/models/bedrock/vehicle/{vid}.geo.json')
resource_manifest={n:hashlib.file_digest((ROOT/'resources'/n).open('rb'),'sha256').hexdigest() for n in sorted(resources)}
with zipfile.ZipFile(OUT/'vls-runtime-resources-1.0.0n.zip','w',zipfile.ZIP_DEFLATED,compresslevel=6) as z:
 for n in sorted(resources):z.write(ROOT/'resources'/n,'resources/'+n)
 z.writestr('resource-sha256.json',json.dumps(resource_manifest,indent=2))
 z.writestr('README.txt','Extract into the repository root before building. Not a standalone mod or resource pack. Requires the pinned 1.0.0m base JAR and the existing Java 21 development classpath. See docs/载具运行审查_20261031.md.\n')
reports=['delivery-verification.json','track-profile-recovery.json','entity-track-periods.json','display-resource-recovery.json','placeholder-texture-recovery.json','ammo-item-repairs.json','display-name-recovery.json','ui-reference-repairs.json','high-detail-triangle-audit.json','foreign-weapon-resources.json','track-curve-sweep.txt','data-change-audit.json']
with zipfile.ZipFile(OUT/'vls-runtime-review-1.0.0n.zip','w',zipfile.ZIP_DEFLATED,compresslevel=6) as z:
 for stage,(file,count,views) in stages.items():
  for p in sorted((GAME/'screenshots').glob(f'runtime_{stage}_*.png')):z.write(p,f'game/{stage}/'+p.name)
  text=(QA/file).read_text(encoding='utf-8',errors='replace');z.writestr('reports/runtime-'+stage+'.log','\n'.join(l for l in text.splitlines() if 'RUNTIME_REVIEW_' in l)+'\n')
 for name in reports:z.write(QA/name,'reports/'+name)
 clean=dict(release);clean['output']=Path(clean['output']).name
 z.writestr('reports/release-checks.json',json.dumps(clean,ensure_ascii=False,indent=2))
 z.write(ROOT/'docs/载具运行审查_20261031.md','运行审查报告.md')
 z.write(QA/'qa-src/review/RuntimeReview.java','qa-source/RuntimeReview.java')
 for p in sorted(QA.glob('final-all-side-*.jpg')):z.write(p,'contact-sheets/'+p.name)
shutil.copy2(ROOT/'docs/载具运行审查_20261031.md',OUT/'runtime-review.md')
shutil.copy2(QA/'delivery-verification.json',OUT/'delivery-verification.json')
(OUT/'SHA256SUMS.txt').write_text('\n'.join(hashlib.file_digest(p.open('rb'),'sha256').hexdigest()+'  '+p.name for p in sorted(OUT.iterdir()) if p.is_file() and p.name!='SHA256SUMS.txt')+'\n',encoding='utf-8')
print(json.dumps({'directory':str(OUT),'files':[{ 'name':p.name,'bytes':p.stat().st_size} for p in OUT.iterdir()]},ensure_ascii=False,indent=2))
