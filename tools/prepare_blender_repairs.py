"""Choose paint from each original livery's dominant neutral airframe pixels, not a new color."""
from pathlib import Path
import json,zipfile,io,collections
import numpy as np
from PIL import Image
ROOT=Path(__file__).resolve().parents[1]
rules=json.loads((ROOT/'tools/model_repairs.json').read_text(encoding='utf-8'))
settings={}
with zipfile.ZipFile(ROOT/'local/input-1.0.0j.jar') as z:
 for vid in rules:
  data=z.read(f'assets/zzyvehicles/textures/bedrock/vehicle/{vid}.png')
  im=Image.open(io.BytesIO(data)).convert('RGBA');a=np.array(im)
  colors=[(n,c) for c,n in collections.Counter(map(tuple,a.reshape(-1,4))).items() if c[3]==255 and 40<sum(map(int,c[:3]))/3<220 and int(max(c[:3]))-int(min(c[:3]))<40]
  _,rgb=max(colors);mask=np.all(a==rgb,axis=2);chosen=None
  for size in [17,9,3,1]:
   integral=np.pad(mask.astype(np.int64),((1,0),(1,0))).cumsum(0).cumsum(1)
   sums=integral[size:,size:]-integral[:-size,size:]-integral[size:,:-size]+integral[:-size,:-size]
   p=np.argwhere(sums==size*size)
   if len(p):y,x=map(int,p[0]);chosen=[x+size//2,y+size//2];break
  assert chosen is not None
  texture=ROOT/'local/blender-input'/f'{vid}.png';texture.parent.mkdir(exist_ok=True);texture.write_bytes(data)
  settings[vid]={'paint_srgb8':list(map(int,rgb)),'paint_pixel':chosen,'texture_size':list(im.size),'voxel_size':0.035,'smooth_iterations':60,'smooth_factor':0.8,'subdivision':1}
 settings['j16']['glass_region']=[[-.70,3.02,-7.45],[.70,4.25,-4.30]]
(ROOT/'local/blender-settings.json').write_text(json.dumps(settings,indent=2),encoding='utf-8')
print(settings)
