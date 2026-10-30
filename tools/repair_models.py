"""Reproducible exact UV edits; never generate, decimate or replace geometry."""
from pathlib import Path
import json,zipfile,io,hashlib
from PIL import Image
ROOT=Path(__file__).resolve().parents[1]
rules=json.loads((ROOT/'tools/model_repairs.json').read_text(encoding='utf-8'))
summary=[]
with zipfile.ZipFile(ROOT/'local/input-1.0.0j.jar') as z:
  for vid,patches in rules.items():
    geo=f'assets/zzyvehicles/models/bedrock/vehicle/{vid}.geo.json'
    doc=json.loads(z.read(geo));g=doc['minecraft:geometry'][0];before=json.loads(json.dumps(g))
    im=Image.open(io.BytesIO(z.read(f'assets/zzyvehicles/textures/bedrock/vehicle/{vid}.png'))).convert('RGBA')
    tw=g['description']['texture_width'];th=g['description']['texture_height'];count=0
    for patch in patches:
      bone=next(b for b in g['bones'] if b['name']==patch['bone'])
      def sample(uv):return im.getpixel((int((uv[0]+.5)/tw*im.width),int((uv[1]+.5)/th*im.height)))
      assert sample(patch['from'])[3]==0,(vid,'old sample must be fully transparent')
      assert sample(patch['to'])[3]==255,(vid,'new sample must be original opaque paint')
      for ci,cube in enumerate(bone.get('cubes',[])):
        if ci not in patch['cubes']:continue
        uv=cube.get('uv')
        if not isinstance(uv,dict) or len(uv)!=6:continue
        if not all(f.get('uv')==patch['from'] and f.get('uv_size')==[1,1] for f in uv.values()):continue
        for f in uv.values():f['uv']=list(patch['to'])
        count+=1
    assert count==sum(len(p['cubes']) for p in patches) and count>0,(vid,'repair count does not match manifest',count)
    for a,b in zip(before['bones'],g['bones']):
      assert {k:v for k,v in a.items() if k!='cubes'}=={k:v for k,v in b.items() if k!='cubes'}
      assert len(a.get('cubes',[]))==len(b.get('cubes',[]))
      for ca,cb in zip(a.get('cubes',[]),b.get('cubes',[])):
        assert {k:v for k,v in ca.items() if k!='uv'}=={k:v for k,v in cb.items() if k!='uv'}
    path=ROOT/'resources'/geo;path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(json.dumps(doc,ensure_ascii=False,separators=(',',':')),encoding='utf-8')
    summary.append({'id':vid,'cubes_repaired':count,'geometry_unchanged':True,'texture_unchanged':True})
(ROOT/'local/model-repairs-checks.json').write_text(json.dumps(summary,ensure_ascii=False,indent=2),encoding='utf-8')
print(summary)
