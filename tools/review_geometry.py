"""Model-space contact sheets, recursive bind transforms and structural checks.

Does not infer faults from aircraft entity AABB size or trim thin wings by percentiles.
"""
from pathlib import Path
import json, zipfile, math, itertools, io
import numpy as np
from PIL import Image, ImageDraw, ImageFont
from inspect_aircraft import SOURCE, OUT, resource

FONT = ImageFont.truetype('C:/Windows/Fonts/arial.ttf',14)
def rotation(r,p):
    x,y,z=np.radians(np.array(r)*[-1,1,-1]); sx,cx=math.sin(x),math.cos(x); sy,cy=math.sin(y),math.cos(y); sz,cz=math.sin(z),math.cos(z)
    R=np.array([[cz,-sz,0],[sz,cz,0],[0,0,1]])@np.array([[cy,0,sy],[0,1,0],[-sy,0,cy]])@np.array([[1,0,0],[0,cx,-sx],[0,sx,cx]])
    M=np.eye(4);M[:3,:3]=R;M[:3,3]=np.array(p)-R@np.array(p);return M
FACES=[[0,1,3,2],[4,6,7,5],[0,4,5,1],[2,3,7,6],[0,2,6,4],[1,5,7,3]]
def geometry(doc):
    bs=doc['minecraft:geometry'][0]['bones']; by={b['name']:b for b in bs}; mats={}; ancestry=set()
    def matrix(n):
        if n in mats:return mats[n]
        if n in ancestry:raise ValueError('Cyclic bone '+n)
        ancestry.add(n); b=by[n]; m=rotation(b.get('rotation',[0,0,0]),b.get('pivot',[0,0,0]))
        if b.get('parent'):m=matrix(b['parent'])@m
        mats[n]=m;ancestry.remove(n);return m
    boxes=[]; stats=[]
    for b in bs:
        M=matrix(b['name']); vertices=[]
        for c in b.get('cubes',[]):
            o=np.array(c['origin']);s=np.array(c['size']);inflate=c.get('inflate',0)
            a=np.array([o+np.array(k)*s+np.where(np.array(k),inflate,-inflate) for k in itertools.product([0,1],repeat=3)])
            CM=rotation(c.get('rotation',[0,0,0]),c.get('pivot',[0,0,0])); a=(M@CM@np.c_[a,np.ones(8)].T).T[:,:3]/16
            vertices.extend(a);boxes.append((b['name'],a))
        pm=b.get('poly_mesh',{})
        if pm:
            a=np.array(pm['positions']);a=(M@np.c_[a,np.ones(len(a))].T).T[:,:3]/16;vertices.extend(a)
        if len(vertices):
            a=np.array(vertices); stats.append(dict(name=b['name'],parent=b.get('parent'),lo=a.min(0).tolist(),hi=a.max(0).tolist(),cubes=len(b.get('cubes',[])),poly=len(pm.get('positions',[]))))
    return boxes,stats

def sheet_tile(vid,boxes,stats):
    im=Image.new('RGB',(600,330),(239,242,245)); d=ImageDraw.Draw(im);d.text((10,6),vid,fill=(15,25,40),font=FONT)
    allv=np.concatenate([a for _,a in boxes]) if boxes else np.array([[0,0,0],[1,1,1]])
    for vi,axes in enumerate(((0,2),(2,1))):
        xy=allv[:,axes];lo=xy.min(0);hi=xy.max(0);scale=min(280/max(.1,hi[0]-lo[0]),278/max(.1,hi[1]-lo[1]));center=(lo+hi)/2
        d.text((vi*300+10,28),'TOP X/Z' if vi==0 else 'SIDE Z/Y',fill=(70,80,95),font=FONT)
        ordered=sorted(boxes,key=lambda q:q[1][:,({0:1,1:0}[vi])].mean())
        for name,a in ordered:
            pts=(a[:,axes]-center)*np.array([scale,-scale])+[vi*300+150,185]
            weapon=any(k in name.lower() for k in ('missile','bomb','rocket','agbomb'))
            for f in FACES:
                d.polygon([tuple(pts[i]) for i in f],fill=(212,125,77) if weapon else (122,151,168),outline=(75,91,101))
    return im

if __name__=='__main__':
    inventory=json.loads((OUT/'aircraft-inventory.json').read_text(encoding='utf-8')); results=[]; tiles=[]
    with zipfile.ZipFile(SOURCE) as z:
        for row in inventory['aircraft']:
            doc=json.loads(z.read(row['geo']));boxes,stats=geometry(doc); vid=row['id']
            palettes=[]
            for r in row['refs']:
                if r['path'].endswith('.png') and r['exists']:
                    im=Image.open(io.BytesIO(z.read(resource(r['path'])))).convert('RGBA'); colors=im.getcolors(im.width*im.height)
                    palettes.append(dict(path=r['path'],size=im.size,colors=len(colors) if colors else None,flat=bool(colors and len(colors)<=2)))
            result=dict(id=vid,bones=stats,textures=palettes,cycles=False,
                        missing=[r for r in row['refs'] if not r['exists']],
                        invalid_bounds=[b['name'] for b in stats if not np.isfinite(b['lo']+b['hi']).all()],
                        empty=row['empty_geometry'],duplicate=row['duplicate_bones'],missing_parents=row['missing_parents'])
            results.append(result);tile=sheet_tile(vid,boxes,stats);tile.save(OUT/f'review-{vid}.png');tiles.append(tile)
        for page,start in enumerate(range(0,len(tiles),12),1):
            contact=Image.new('RGB',(1800,1320),(255,255,255))
            for i,t in enumerate(tiles[start:start+12]):contact.paste(t,((i%3)*600,(i//3)*330))
            contact.save(OUT/f'aircraft-contact-{page}.png')
    (OUT/'geometry-review.json').write_text(json.dumps(results,ensure_ascii=False,indent=2),encoding='utf-8')
    print('REVIEW',len(results),'flat',[(r['id'],t['path']) for r in results for t in r['textures'] if t['flat']])
    print('ERR',[(r['id'],r['missing'],r['invalid_bounds'],r['empty'],r['duplicate'],r['missing_parents']) for r in results if r['missing'] or r['invalid_bounds'] or r['empty'] or r['duplicate'] or r['missing_parents']])
