# -*- coding: utf-8 -*-
"""
将 281 份载具定义中所有 dragonrise_reforge 引用迁移到 SBW 原生命名空间。
输入: 1.0.0n JAR (local/input-1.0.0n.jar)
输出: 重写后的 data/zzyvehicles/sbw/vehicles/*.json (就地写到 resources/, 供 build_release.py 使用)

映射原则:
  - 弹丸实体: 一对一优先(agm65->agm_65), 制导武器无法无损映射时保留同类玩法
    (aim120->ru_9m336_missile 红外防空; 制导炸弹->SBW 炸弹)
  - 弹药物品: 按口径/用途映射到 small/medium/large shell 与 anti_air/anti_ground missile
  - 音效: 按火炮口径/机枪口径/引擎类型映射到 SBW 现成音效(已被包内载具使用、实战有效)
  - 显示名: 仅去掉 "dragonrise_reforge:" 前缀, 保留型号名(界面直接可读, 不臆造译名)
"""
import json, zipfile, sys, os, collections

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
INPUT_JAR = os.path.join(ROOT, 'local', 'input-1.0.0n.jar')
OUT_DIR = os.path.join(ROOT, 'resources', 'data', 'zzyvehicles', 'sbw', 'vehicles')

DR = 'dragonrise_reforge:'
SB = 'superbwarfare:'

PROJECTILE_MAP = {
    'agm65': SB + 'agm_65',
    'aim120': SB + 'ru_9m336_missile',   # 无主动雷达空空弹, 用红外防空弹保留对空玩法
    'kd_88a': SB + 'kh_39',
    'ls_6': SB + 'mk_82',
    'ls_6_ir': SB + 'kh_39',             # IR 制导弹 -> KH-39(IR) 保制导
    'jdam_er': SB + 'mk_82',
    'gbu_24': SB + 'mk_84',
    'gbu_12': SB + 'mk_82',
    'gb_250': SB + 'sc_250',
}

AMMO_MAP = {
    '105mmHE': SB + 'medium_shell_he',
    '155mm_m60_cm': SB + 'large_shell_cm',
    '155mm_m60_he': SB + 'large_shell_he',
    '155mm_m60_wp': SB + 'large_shell_wp',
    '30mm_dtb02_30_he': SB + 'small_shell_he',
    '30mm_dty02_30_hei': SB + 'small_shell_he',
    '30mm_hefi_he': SB + 'small_shell_he',
    '30mm_mk258_he': SB + 'small_shell_he',
    '30mm_shotgun': SB + 'small_shell_gs',
    '3OF46': SB + 'large_shell_he',
    '9M117M1': SB + 'medium_anti_ground_missile',
    'DM12': SB + 'large_shell_he',
    'K280': SB + 'large_shell_ap',
    'LAHAT': SB + 'medium_anti_ground_missile',
    'M393A2': SB + 'medium_shell_he',
    'M908': SB + 'large_shell_he',
    'OR-281U': SB + 'small_shell_he',
    'aasmg': SB + 'medium_anti_air_missile',
    'agm65': SB + 'medium_anti_ground_missile',
    'aim120': SB + 'medium_anti_air_missile',
    'aps03': SB + 'large_shell_ap',
    'auto_loader': SB + 'medium_shell_he',
    'ba11_1': SB + 'large_shell_ap',
    'chonmaho5_115mm_he': SB + 'large_shell_he',
    'dtb10105': SB + 'medium_shell_he',
    'dtb10125': SB + 'large_shell_he',
    'dyh_ground': SB + 'medium_anti_ground_missile',
    'fusion_battle_cannon_explosive': SB + 'large_shell_he',
    'gb_250': SB + 'medium_aerial_bomb',
    'gbu12': SB + 'medium_aerial_bomb',
    'gbu24': SB + 'medium_aerial_bomb',
    'gp105': SB + 'medium_shell_ap',
    'gp125': SB + 'large_shell_ap',
    'hyr0he': SB + 'large_shell_he',
    'jdam_er': SB + 'medium_aerial_bomb',
    'kd88a': SB + 'medium_anti_ground_missile',
    'ls_6': SB + 'medium_aerial_bomb',
    'ls_6_ir': SB + 'medium_aerial_bomb',
    'm61a': SB + 'small_shell_he',
    'mi24v_asm_ground': SB + 'medium_anti_ground_missile',
    'occ120f1': SB + 'large_shell_ap',
    'of6_he': SB + 'large_shell_he',
    'silverhand_40mm_he': SB + 'small_shell_he',
    'silverhand_68mm_he': SB + 'small_shell_he',
    'type59he': SB + 'medium_shell_he',
}

SOUND_MAP = {
    'auto_loader': SB + 't_90a_reload',
    'cannon_125mm_fire_1p': SB + 't_90a_fire_1p',
    'cannon_125mm_fire_3p': SB + 't_90a_fire_3p',
    'cannon_125mm_fire_3p_far': SB + 't_90a_far',
    'cannon_125mm_fire_3p_very_far': SB + 't_90a_veryfar',
    'cannon_25mm_fire_1p': SB + 'bradley_cannon_fire_1p',
    'cannon_25mm_fire_3p': SB + 'bradley_cannon_fire_3p',
    'cannon_25mm_fire_3p_far': SB + 'bradley_cannon_far',
    'cannon_25mm_fire_3p_very_far': SB + 'bradley_cannon_veryfar',
    'cannon_30mm_fire_1p': SB + 'bmp_2_cannon_fire_1p',
    'cannon_30mm_fire_3p': SB + 'bmp_2_cannon_fire_3p',
    'cannon_30mm_fire_3p_far': SB + 'mi_28_cannon_far',
    'cannon_30mm_fire_3p_very_far': SB + 'mi_28_cannon_veryfar',
    'change_weapon': SB + 'into_cannon',
    'cyborg_tank_fire_1p': SB + 'prism_tank_fire_high_1p',
    'cyborg_tank_fire_3p': SB + 'prism_tank_fire_high_3p',
    'cyborg_tank_fire_3p_far': SB + 'prism_tank_fire_high_3p',
    'cyborg_tank_fire_3p_very_far': SB + 'prism_tank_fire_high_3p',
    'cyborg_tank_fire_coax': SB + 'coax_fire_1p',
    'cyborg_tank_fire_coax_far': SB + 'm_60_far',
    'cyborg_tank_fire_station': SB + 'prism_tank_fire_high_3p',
    'cyborg_tank_fire_station_far': SB + 'prism_tank_fire_high_3p',
    'cyborg_tank_reload': SB + 'plz_05_reload',
    'fire_sound_30mm': SB + 'bmp_2_cannon_fire_1p',
    'fire_sound_57mm': SB + 'bofos_fire_1p',
    'fire_sound_57mm_far': SB + 'bofos_fire_far',
    'hand_load': SB + 'm_1a_2_reload',
    'into_cannon': SB + 'into_cannon',
    'jet_engine_start': SB + 'a_10a_engine_start',
    'm1a2_fire_1p': SB + 'm_1a_2_fire_1p',
    'm1a2_fire_3p': SB + 'm_1a_2_fire_3p',
    'm1a2_fire_3p_far': SB + 'm_1a_2_far',
    'm1a2_reload': SB + 'm_1a_2_reload',
    'm2_fire_1p': SB + 'm_2_hb_fire_1p',
    'm2_fire_3p': SB + 'm_2_hb_fire_3p',
    'm2_fire_3p_far': SB + 'm_2_hb_far',
    'm2_fire_3p_very_far': SB + 'm_2_hb_veryfar',
    'mgfire1': SB + 'coax_fire_1p',
    'mgfire11': SB + 'coax_fire_1p',
    'plane_start1': SB + 'a_10a_engine_start',
    'qjz89_fire_1p': SB + 'm_2_hb_fire_1p',
    'qjz89_fire_3p': SB + 'm_2_hb_fire_3p',
    'qjz89_fire_3p_far': SB + 'm_2_hb_far',
    'qjz89_fire_3p_very_far': SB + 'm_2_hb_veryfar',
    's_coax_fire': SB + 'coax_fire_1p',
    't80_fire_1p': SB + 't_90a_fire_1p',
    't80_fire_3p': SB + 't_90a_fire_3p',
    't80_fire_3p_far': SB + 't_90a_far',
    't80_reload': SB + 't_90a_reload',
    'zbd04a_100mm_fire_1p': SB + 'yx_100_fire_1p',
    'zbd04a_100mm_fire_3p': SB + 'yx_100_fire_3p',
    'zbd04a_100mm_fire_3p_far': SB + 'yx_100_far',
    'zbd04a_100mm_reload': SB + 'yx_100_reload',
    'zbd04a_30mm_fire_1p': SB + 'bmp_2_cannon_fire_1p',
    'zbd04a_30mm_fire_3p': SB + 'bmp_2_cannon_fire_3p',
    'zbd04a_30mm_fire_3p_far': SB + 'mi_28_cannon_far',
    'ztz59_fire_1p': SB + 'yx_100_fire_1p',
    'ztz59_fire_3p_far': SB + 'yx_100_far',
    'ztz99a_reload': SB + 't_90a_reload',
}

ENGINE_MAP = {
    'cyborg_tank_engine': SB + 'prism_tank_engine',
    'jet_engine': SB + 'a_10a_engine',
    'm1a2_engine': SB + 't_90a_engine',
    'm2a3_engine_loop': SB + 'bmp_2_engine',
    'plane_loop': SB + 'a_10a_engine',
    't80_engine': SB + 't_90a_engine',
    'tank_engine_loop3': SB + 't_90a_engine',
    'wheel_loop1': SB + 'wheel_engine',
    'ztz99a_engine': SB + 'plz_05_engine',
}

unresolved = []

def map_value(raw, path):
    if not raw.startswith(DR):
        return raw
    key = raw[len(DR):]
    low = path.lower()
    if low.endswith('enginesound'):
        table = ENGINE_MAP
    elif low.endswith('.projectile'):
        table = PROJECTILE_MAP   # 含 AmmoType.Override.Projectile
    elif 'sound' in low:
        table = SOUND_MAP
    elif 'ammotype' in low or low.endswith('.ammo'):
        table = AMMO_MAP
    elif low.endswith('.name'):
        return key          # 去前缀, 保留型号名
    else:
        unresolved.append((path, raw))
        return raw
    if key not in table:
        unresolved.append((path, raw))
        return raw
    return table[key]

def walk(obj, path):
    if isinstance(obj, dict):
        return {k: walk(v, (path + '.' + str(k)) if path else str(k)) for k, v in obj.items()}
    if isinstance(obj, list):
        return [walk(v, path) for v in obj]
    if isinstance(obj, str):
        return map_value(obj, path)
    return obj

def main():
    if not os.path.exists(INPUT_JAR):
        print('缺少输入 JAR:', INPUT_JAR); sys.exit(1)
    os.makedirs(OUT_DIR, exist_ok=True)
    changed = 0
    total = 0
    with zipfile.ZipFile(INPUT_JAR) as z:
        for n in z.namelist():
            if not (n.startswith('data/zzyvehicles/sbw/vehicles/') and n.endswith('.json')):
                continue
            total += 1
            d = json.loads(z.read(n))
            before = json.dumps(d, sort_keys=True)
            d2 = walk(d, '')
            after = json.dumps(d2, sort_keys=True)
            name = n.split('/')[-1]
            with open(os.path.join(OUT_DIR, name), 'w', encoding='utf-8') as f:
                json.dump(d2, f, ensure_ascii=False, indent=1)
                f.write('\n')
            if before != after:
                changed += 1
    print('处理 %d 份载具, 有改动 %d 份' % (total, changed))
    if unresolved:
        print('!! 未解析引用 %d 处:' % len(unresolved))
        for p, v in unresolved[:30]:
            print('   ', p, '=', v)
        sys.exit(2)
    print('全部引用已解析')

if __name__ == '__main__':
    main()
