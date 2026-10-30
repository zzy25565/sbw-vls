"""Create separate runtime, editable aircraft and real-game review deliveries."""
from pathlib import Path
import zipfile,json,hashlib,shutil
root=Path(__file__).resolve().parents[1];out=root.parent/'交付/VLS_1.0.0k_模型修复';out.mkdir(parents=True,exist_ok=True)
ids=list(json.loads((root/'tools/model_repairs.json').read_text(encoding='utf-8')));work=root/'local/blender-repairs';shots=root.parent/'sbw_loadout/run/screenshots'
report=json.loads((root/'local/release-checks.json').read_text(encoding='utf-8'));jar=Path(report['output']);assert hashlib.file_digest(jar.open('rb'),'sha256').hexdigest()==report['sha256'];shutil.copy2(jar,out/jar.name)
readme='''VLS 1.0.0k Blender 模型修复源文件（2026-10-30）

Blender 5.2.2 LTS。五个 .blend 均已打包原贴图，双击可独立打开。
*_smooth_skin 是实际转换的外壳；REFERENCE_actual_game_pose_do_not_export
是原有部件的游戏姿态对照，只用于编辑参考，不能重复输出。

converted/ 下为游戏中实际复验过的 .geo.json；对应包为 zzy-vehicles-1.0.0k.jar。
original_textures/ 为原始 PNG，字节未变。改变的仅是指定外壳，其他源部件保留。
scripts/ 为重建、转换和审查脚本。完整工程：https://github.com/zzy25565/sbw-vls

继续编辑：把 .blend 放回工程 local/blender-repairs/；在编辑模式修改
*_smooth_skin，保留 UV。使用 blender_export_edited.py 导出已有 .blend，
不会重新生成或覆盖手工编辑；再运行 audit_blender_repairs.py 和 build_release.py。
blender_smooth_airframes.py 则是从原包重建，会替换工作文件，勿用它导出手改结果。
所有脚本输入包和完整命令见随附审查说明。五款曲面三角面增加，未做 FPS 基准。

审查压缩包中的游戏截图与 Blender 灯光预览分目录存放；Blender 预览不作为游戏截图。
'''
with zipfile.ZipFile(out/'VLS_五款飞机_Blender源文件_1.0.0k.zip','w',zipfile.ZIP_DEFLATED,compresslevel=6) as z:
 z.writestr('README_使用说明.txt',readme)
 for vid in ids:
  z.write(work/f'{vid}.blend',f'blender/{vid}.blend');z.write(root/'resources/assets/zzyvehicles/models/bedrock/vehicle'/f'{vid}.geo.json',f'converted/{vid}.geo.json')
  z.write(root/'local/blender-input'/f'{vid}.png',f'original_textures/{vid}.png')
  z.write(work/f'{vid}-conversion.json',f'reports/{vid}-conversion.json')
 for p in (root/'tools').glob('*.py'):z.write(p,'scripts/'+p.name)
 for p in (root/'tools').glob('*.json'):z.write(p,'scripts/'+p.name)
 z.write(root/'local/blender-settings.json','reports/blender-settings.json')
 z.write(root/'docs/二战模型清理_1.0.0k.md','模型审查与复现说明.md')
with zipfile.ZipFile(out/'VLS_真实游戏截图与审查_1.0.0k.zip','w',zipfile.ZIP_DEFLATED,compresslevel=6) as z:
 for p in sorted(shots.glob('inspect_*.png')):z.write(p,'游戏初查_60机300图/'+p.name)
 for p in sorted(shots.glob('blenderfinal_*.png')):z.write(p,'游戏最终复验_5机25图/'+p.name)
 for p in sorted(shots.glob('fixed_mi17_*.png')):z.write(p,'Mi17朝向复验/'+p.name)
 for vid in ids:z.write(work/f'{vid}-blender-preview.png',f'Blender预览_不是游戏截图/{vid}.png')
 for p in [root/'local/blender-repair-checks.json',root/'local/release-checks.json',root/'docs/二战模型清理_1.0.0k.md']:z.write(p,'reports/'+p.name)
 log=(root.parent/'sbw_loadout/build/vls-blender-final-review.log').read_text(encoding='utf-8',errors='replace');z.writestr('reports/游戏复验记录.txt','\n'.join(l for l in log.splitlines() if 'VLS_CLOSE_' in l or 'BUILD SUCCESSFUL' in l))
(out/'README_交付说明.txt').write_text(readme,encoding='utf-8')
checks=[]
for p in sorted(out.iterdir()):
 if p.name=='SHA256SUMS.txt':continue
 checks.append(hashlib.file_digest(p.open('rb'),'sha256').hexdigest()+'  '+p.name)
(out/'SHA256SUMS.txt').write_text('\n'.join(checks)+'\n',encoding='utf-8')
print(json.dumps({'directory':str(out),'files':[{ 'name':p.name,'bytes':p.stat().st_size} for p in out.iterdir()]},ensure_ascii=True))
