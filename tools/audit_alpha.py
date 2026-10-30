"""Flag fully transparent UV islands on front-quarter components; diagnostics, not auto-fixes."""
import json,zipfile,io,math
import numpy as np
from PIL import Image
from inspect_aircraft import SOURCE,OUT,resource
from review_geometry import geometry

rows=[]
with zipfile.ZipFile(SOURCE) as z:
  inv=json.loads((OUT/'aircraft-inventory.json').read_text(encoding='utf-8'))
  for row in inv['aircraft']:
    doc=json.loads(z.read(row['geo']));g=doc['minecraft:geometry'][0];desc=g['description']
    tex=next((r['path'] for r in row['refs'] if r['path'].endswith('.png')),None)
    if not tex:continue
    im=Image.open(io.BytesIO(z.read(resource(tex)))).convert('RGBA');alpha=np.array(im)[:,:,3]
    tw=desc.get('texture_width',im.width);th=desc.get('texture_height',im.height)
    boxes,stats=geometry(doc);box_index=0; flags=[]
    zmin=min(s['lo'][2] for s in stats);zmax=max(s['hi'][2] for s in stats)
    for b in g['bones']:
      for ci,c in enumerate(b.get('cubes',[])):
        bn,points=boxes[box_index];box_index+=1
        if points[:,2].mean()>zmin+(zmax-zmin)*.28:continue
        size=np.abs(c['size']);vol=np.prod(size)/4096
        if vol<.002:continue
        uv=c.get('uv');rects=[]
        if isinstance(uv,list):
          u,v=uv;w,h,d=size
          rects=[(u+d,v+d,w,h),(u+2*d+w,v+d,w,h),(u,v+d,d,h),(u+d+w,v+d,d,h),(u+d,v,w,d),(u+d+w,v,w,d)]
        elif isinstance(uv,dict):
          for face in uv.values():
            u,v=face['uv'];w,h=face.get('uv_size',[0,0]);rects.append((u,v,w,h))
        samples=[]
        for u,v,w,h in rects:
          if not w or not h:continue
          xs=(np.linspace(u+.1*w,u+.9*w,5)/tw*im.width).astype(int)%im.width
          ys=(np.linspace(v+.1*h,v+.9*h,5)/th*im.height).astype(int)%im.height
          samples.extend(alpha[np.ix_(ys,xs)].reshape(-1).tolist())
        if samples and max(samples)==0:flags.append({'bone':b['name'],'cube':ci,'volume':round(float(vol),5),'lo':points.min(0).tolist(),'hi':points.max(0).tolist()})
    if flags:rows.append({'id':row['id'],'transparent_front_cubes':flags})
(OUT/'alpha-diagnostics.json').write_text(json.dumps(rows,ensure_ascii=False,indent=2),encoding='utf-8')
print([(r['id'],[(f['bone'],f['cube'],f['volume']) for f in r['transparent_front_cubes']]) for r in rows])
