"""Verify the converted skins and prove all other model parts are unchanged."""
from pathlib import Path
import json,zipfile,math,collections
import numpy as np
root=Path(__file__).resolve().parents[1];rules=json.loads((root/'tools/model_repairs.json').read_text(encoding='utf-8'));results=[]
with zipfile.ZipFile(root/'local/input-1.0.0j.jar') as z:
 for vid,rr in rules.items():
  rule=rr[0];rel=f'assets/zzyvehicles/models/bedrock/vehicle/{vid}.geo.json'
  old=json.loads(z.read(rel));new=json.loads((root/'resources'/rel).read_text(encoding='utf-8'));a=old['minecraft:geometry'][0];b=new['minecraft:geometry'][0]
  assert len(a['bones'])==len(b['bones'])
  target=next(x for x in b['bones'] if x['name']==rule['bone']);pm=target['poly_mesh']
  restored=json.loads(json.dumps(new));r=next(x for x in restored['minecraft:geometry'][0]['bones'] if x['name']==rule['bone']);del r['poly_mesh']
  original=next(x for x in a['bones'] if x['name']==rule['bone']);kept=[c for i,c in enumerate(original['cubes']) if i not in set(rule['cubes'])];assert r['cubes']==kept
  r['cubes']=original['cubes'];assert restored==old,'An unrelated source model field changed'
  p=np.array(pm['positions']);n=np.array(pm['normals']);uv=np.array(pm['uvs']);tri=np.array(pm['polys'])
  assert all(np.isfinite(x).all() for x in [p,n,uv]) and np.max(abs(np.linalg.norm(n,axis=1)-1))<1e-5
  assert tri.shape[1:]==(3,3) and tri[:,:,0].min()>=0 and tri[:,:,0].max()<len(p)
  assert tri[:,:,1].min()>=0 and tri[:,:,1].max()<len(n) and tri[:,:,2].min()>=0 and tri[:,:,2].max()<len(uv)
  v=p[tri[:,:,0]];cross=np.cross(v[:,1]-v[:,0],v[:,2]-v[:,0]);areas=np.linalg.norm(cross,axis=1)
  assert areas.min()>1e-10,'Degenerate triangle'
  normals=n[tri[:,:,1]].mean(axis=1);assert np.einsum('ij,ij->i',cross,normals).min()>0,'Normal/winding disagreement'
  edges=collections.Counter(tuple(sorted((int(t[i]),int(t[(i+1)%3])))) for t in tri[:,:,0] for i in range(3));assert set(edges.values())=={2},'Non-manifold skin'
  report=json.loads((root/'local/blender-repairs'/f'{vid}-conversion.json').read_text(encoding='utf-8'));delta=float(np.max(abs(np.array(report['before_bounds_blender'])-np.array(report['after_bounds_blender']))))
  assert delta<.15,'Review excessive silhouette boundary change'
  results.append({'id':vid,'source_cubes_replaced':len(rule['cubes']),'triangles':len(tri),'vertices':len(p),'closed_manifold':True,'other_model_fields_unchanged':True,'max_axis_boundary_change_m':round(delta,5),'paint_rgba':report['paint_srgb8'],'glass_faces':report['glass_faces']})
(root/'local/blender-repair-checks.json').write_text(json.dumps(results,ensure_ascii=False,indent=2),encoding='utf-8');print(json.dumps(results,ensure_ascii=False,indent=2))
