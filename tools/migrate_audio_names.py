# -*- coding: utf-8 -*-
"""
真迁移: 音效(含引擎音)从 dragonrise_reforge 原附属包搬进 zzyvehicles 命名空间。
- 172 个 .ogg -> assets/zzyvehicles/sounds/**（路径不变）
- 89 个 sound event -> assets/zzyvehicles/sounds.json（名称不变）
- 载具 JSON 中 SoundInfo.* 与 EngineSound 的 dragonrise_reforge:<event> -> zzyvehicles:<event>
- 武器显示名: 原包 zh_cn 官方中文名优先, 缺失则去命名空间前缀
- 弹丸/弹药物品: 沿用 SBW 映射 (阶段2 再改为自研实体, 依据 SBW 开源源码)

输入: local/input-1.0.0n.jar (原始, 音效引用未迁移)
      C:/Users/z/Desktop/卓越前线附属/【[SBW]龙之崛起：重铸】dragonrise_reforge-1.5.1-release.jar
输出: resources/data/zzyvehicles/sbw/vehicles/*.json
      resources/assets/zzyvehicles/sounds/** (+ sounds.json)
"""
import json, zipfile, os, sys, shutil

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
INPUT = os.path.join(ROOT, 'local', 'input-1.0.0n.jar')
ORIG = r'C:/Users/z/Desktop/卓越前线附属/【[SBW]龙之崛起：重铸】dragonrise_reforge-1.5.1-release.jar'
OUT_V = os.path.join(ROOT, 'resources', 'data', 'zzyvehicles', 'sbw', 'vehicles')
OUT_A = os.path.join(ROOT, 'resources', 'assets', 'zzyvehicles')

DR, SB, ZY = 'dragonrise_reforge:', 'superbwarfare:', 'zzyvehicles:'

PROJECTILE_MAP = {
    'agm65': SB + 'agm_65', 'aim120': SB + 'ru_9m336_missile',
    'kd_88a': SB + 'kh_39', 'ls_6': SB + 'mk_82', 'ls_6_ir': SB + 'kh_39',
    'jdam_er': SB + 'mk_82', 'gbu_24': SB + 'mk_84', 'gbu_12': SB + 'mk_82',
    'gb_250': SB + 'sc_250',
}
AMMO_MAP = {
    '105mmHE': 'medium_shell_he', '155mm_m60_cm': 'large_shell_cm',
    '155mm_m60_he': 'large_shell_he', '155mm_m60_wp': 'large_shell_wp',
    '30mm_dtb02_30_he': 'small_shell_he', '30mm_dty02_30_hei': 'small_shell_he',
    '30mm_hefi_he': 'small_shell_he', '30mm_mk258_he': 'small_shell_he',
    '30mm_shotgun': 'small_shell_gs', '3OF46': 'large_shell_he',
    '9M117M1': 'medium_anti_ground_missile', 'DM12': 'large_shell_he',
    'K280': 'large_shell_ap', 'LAHAT': 'medium_anti_ground_missile',
    'M393A2': 'medium_shell_he', 'M908': 'large_shell_he',
    'OR-281U': 'small_shell_he', 'aasmg': 'medium_anti_air_missile',
    'agm65': 'medium_anti_ground_missile', 'aim120': 'medium_anti_air_missile',
    'aps03': 'large_shell_ap', 'auto_loader': 'medium_shell_he',
    'ba11_1': 'large_shell_ap', 'chonmaho5_115mm_he': 'large_shell_he',
    'dtb10105': 'medium_shell_he', 'dtb10125': 'large_shell_he',
    'dyh_ground': 'medium_anti_ground_missile',
    'fusion_battle_cannon_explosive': 'large_shell_he',
    'gb_250': 'medium_aerial_bomb', 'gbu12': 'medium_aerial_bomb',
    'gbu24': 'medium_aerial_bomb', 'gp105': 'medium_shell_ap',
    'gp125': 'large_shell_ap', 'hyr0he': 'large_shell_he',
    'jdam_er': 'medium_aerial_bomb', 'kd88a': 'medium_anti_ground_missile',
    'ls_6': 'medium_aerial_bomb', 'ls_6_ir': 'medium_aerial_bomb',
    'm61a': 'small_shell_he', 'mi24v_asm_ground': 'medium_anti_ground_missile',
    'occ120f1': 'large_shell_ap', 'of6_he': 'large_shell_he',
    'silverhand_40mm_he': 'small_shell_he', 'silverhand_68mm_he': 'small_shell_he',
    'type59he': 'medium_shell_he',
}

unresolved = []

# 原包未自带、回落到 SBW 自带同名事件的音效
SOUND_FALLBACK = {'into_cannon': SB + 'into_cannon', 'into_missile': SB + 'into_missile',
                  'javelin_locking': SB + 'javelin_locking', 'javelin_locked': SB + 'javelin_locked'}

def main():
    for p, label in [(INPUT, 'input-1.0.0n.jar'), (ORIG, '原附属包')]:
        if not os.path.exists(p):
            print('缺少:', label, p); sys.exit(1)

    zo = zipfile.ZipFile(ORIG)
    zi = zipfile.ZipFile(INPUT)

    # 1) 音效事件表
    events = json.loads(zo.read('assets/dragonrise_reforge/sounds.json'))
    ev_names = set(events)
    print('音效事件:', len(ev_names))

    # 2) 武器名中文表（原包 lang: "dragonrise_reforge:xxx": "中文名"）
    zh = json.loads(zo.read('assets/dragonrise_reforge/lang/zh_cn.json'))
    name_zh = {}
    for k, v in zh.items():
        if k.startswith(DR):
            name_zh[k] = v
    print('官方中文武器名:', len(name_zh), '条')

    # 3) 拷贝 ogg
    n_ogg = 0
    for n in zo.namelist():
        if n.startswith('assets/dragonrise_reforge/sounds/') and n.endswith('.ogg'):
            rel = n[len('assets/dragonrise_reforge/'):]           # sounds/**.ogg
            dst = os.path.join(OUT_A, *rel.split('/'))
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            with open(dst, 'wb') as f:
                f.write(zo.read(n))
            n_ogg += 1
    print('拷贝 ogg:', n_ogg)

    # 4) 写 sounds.json（名称不变, 资源指向 zzyvehicles）
    sounds = {}
    for k, v in events.items():
        snds = []
        for s in v.get('sounds', []):
            if isinstance(s, str):
                s = {'name': s} if ':' in s else {'name': ZY + s}
            else:
                s = dict(s)
                nm = s.get('name', '')
                if nm.startswith(DR):
                    s['name'] = ZY + nm[len(DR):]
            snds.append(s)
        e = dict(v); e['sounds'] = snds
        sounds[k] = e
    os.makedirs(OUT_A, exist_ok=True)
    with open(os.path.join(OUT_A, 'sounds.json'), 'w', encoding='utf-8') as f:
        json.dump(sounds, f, ensure_ascii=False, indent=2)
    print('写入 sounds.json:', len(sounds), '事件')

    # 5) 载具 JSON 迁移
    def map_val(raw, path):
        if not raw.startswith(DR):
            return raw
        key = raw[len(DR):]
        low = path.lower()
        if low.endswith('enginesound') or 'sound' in low:
            if key in ev_names:
                return ZY + key
            if key in SOUND_FALLBACK:
                return SOUND_FALLBACK[key]
            unresolved.append((path, raw, '音效不在原包事件表'))
            return raw
        if low.endswith('.projectile'):
            if key not in PROJECTILE_MAP:
                unresolved.append((path, raw, '弹丸无映射')); return raw
            return PROJECTILE_MAP[key]
        if 'ammotype' in low or low.endswith('.ammo'):
            if key not in AMMO_MAP:
                unresolved.append((path, raw, '弹药无映射')); return raw
            return SB + AMMO_MAP[key]
        if low.endswith('.name'):
            return name_zh.get(raw, key)   # 中文优先, 否则型号名
        unresolved.append((path, raw, '未知路径')); return raw

    def walk(obj, path):
        if isinstance(obj, dict):
            return {k: walk(v, path + '.' + str(k) if path else str(k)) for k, v in obj.items()}
        if isinstance(obj, list):
            return [walk(v, path) for v in obj]
        if isinstance(obj, str):
            return map_val(obj, path)
        return obj

    os.makedirs(OUT_V, exist_ok=True)
    total = changed = 0
    for n in zi.namelist():
        if not (n.startswith('data/zzyvehicles/sbw/vehicles/') and n.endswith('.json')):
            continue
        total += 1
        d = json.loads(zi.read(n))
        before = json.dumps(d, sort_keys=True)
        d2 = walk(d, '')
        after = json.dumps(d2, sort_keys=True)
        p = os.path.join(OUT_V, n.split('/')[-1])
        with open(p, 'w', encoding='utf-8') as f:
            json.dump(d2, f, ensure_ascii=False, indent=1)
            f.write('\n')
        if before != after:
            changed += 1
    print('载具: %d 份, 改动 %d 份' % (total, changed))
    if unresolved:
        print('!! 未解析 %d 处:' % len(unresolved))
        for x in unresolved[:20]: print('   ', x)
        sys.exit(2)
    print('全部引用已解析')

if __name__ == '__main__':
    main()
