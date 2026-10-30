"""Apply the explicitly enumerated WWII removal to the workspace copy only."""
from pathlib import Path
import re, json, subprocess
ROOT=Path(__file__).resolve().parents[1]
assert ROOT == Path(r'D:\自定义挂载\sbw_vls')
manifest=json.loads((ROOT/'tools/removed_ww2.json').read_text(encoding='utf-8'))
ids=set(manifest['current'])|set(manifest['previously_removed'])
for path in (ROOT/'resources').rglob('*'):
    if not path.is_file():continue
    if any(path.name == vid+ext for vid in ids for ext in ('.json','.png','.geo.json','.animation.json')):
        assert path.resolve().is_relative_to((ROOT/'resources').resolve())
        path.unlink()
sizes=ROOT/'java/com/zzy/sbwvls/MergedVehicles.java'
text=subprocess.check_output(['git','show','HEAD:java/com/zzy/sbwvls/MergedVehicles.java'],cwd=ROOT).decode('utf-8')
for vid in ids:text=re.sub(r'^[ \t]*SIZES\.put\("'+re.escape(vid)+r'",[^\r\n]*\);\r?\n', '',text,flags=re.M)
sizes.write_bytes(text.encode('utf-8'))
tabs=ROOT/'java/com/zzy/sbwvls/CreativeTabs.java'; text=tabs.read_bytes().decode('utf-8')
def update(m):
    args=re.findall(r'"([^"]+)"',m[1]); category,icon,*items=args
    items=[x for x in items if x not in ids]
    if not items:return ''
    if icon in ids:icon=items[0]
    return 'cat('+', '.join(json.dumps(x) for x in [category,icon,*items])+');'
text=re.sub(r'cat\(("[^;]+)\);',update,text)
tabs.write_bytes(text.encode('utf-8'))
print('Removed current platforms:',','.join(manifest['current']))
print('Remaining data definitions:',len(list((ROOT/'resources/data/zzyvehicles/sbw/vehicles').glob('*.json'))))
