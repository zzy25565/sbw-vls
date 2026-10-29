# SBW VLS 载具包（zzy-vehicles）

Superb Warfare 的自定义载具集合（Minecraft 1.21.1 / NeoForge）。当前收录 **185 台**载具定义（180 台合并载具 + 5 台高精平台）。版本 **1.0.0g**。

> **1.0.0b 起单命名空间**：原先的 `sbw_vls` 资源命名空间已并入 `zzyvehicles`，
> 一个 jar、一个实体注册表、一套资源目录。旧包里 `assets/sbw_vls` 与 `data/sbw_vls` 不再存在。

## ⚠️ 依赖

| 依赖 | 说明 |
|---|---|
| **Superb Warfare** | 必需。载具逻辑、武器与投射物由上游提供 |
| **SBW Radar**（[zzy25565/sbw-radar](https://github.com/zzy25565/sbw-radar)） | **必需**。本包内多台防空载具（Tunguska、Pantsir、Buk 等）依赖其雷达 HUD、TWS 引导与近炸引信；缺少雷达附属时这些车能生成但**无法锁定/引导** |

安装顺序：SBW → SBW Radar → 本载具包。

## 结构

```
java/                35 个 Java 源文件（实体、渲染器、资产加载）
resources/
├── pack.mcmeta
├── data/zzyvehicles/sbw/vehicles/    185 个载具定义 JSON
└── assets/zzyvehicles/models/bedrock/vehicle/
    └── {is2,tiger,m4a2,type97q,m808}.geo.json   5 台重划分骨骼的模型
```

> **美术资源不在本仓库**：`assets/` 下的几何（`.geo.json`）与贴图（`.png`）属于游戏衍生资源，未随仓库分发。构建时需自行放入。

## 载具定义格式

数据驱动，文件名即载具 ID（`iris_slm.json` → `sbw_vls:iris_slm`）。字段沿用上游 SBW 的载具 schema：

`MaxHealth` / `MaxEnergy` / `OBB` / `Seats` / `Weapons` / `EngineType` / `EngineInfo` / `TurretPos` 等。

参照实现：
- 轮式防空（IRIS-T SLM 发射车 / 雷达车）：克隆 `buk_m3.json` 结构，仅改 `OBB`、`EngineType`（Wheel）与武器
- 履带主战（T-80U）：`EngineType: Track`，主炮借用 `zsu234.json` 的 `Cannon` 定义

## 2026-10-25 新增

| 载具 | 说明 |
|---|---|
| `iris_slm` | IRIS-T SLM 发射车，260 HP，轮式，AAMissile 发射器 |
| `iris_slm_fcs` | IRIS-T SLM 雷达车，240 HP，轮式，无武器（纯雷达） |
| `t_80u` | T-80U，380 HP，履带，主炮 |

三台的几何均由 War Thunder 客户端导出后转换，**保留全部面数**（243k / 283k / 261k 面）。

## 1.0.0b：命名空间合并

| 项 | 1.0.0a | 1.0.0b |
|---|---|---|
| 资源命名空间 | `sbw_vls` + `zzyvehicles` 两套 | **只有 `zzyvehicles`** |
| 实体注册 | `SbwVlsMod.MODID = "sbw_vls"` | `"zzyvehicles"`，与 mod id 一致 |
| 注册条目 | 171 | **180**（补入 9 台 WT 导出载具） |
| 资源引用 | 混用两个命名空间 | 全部改写为 `zzyvehicles:` |
| jar 体积 | 292.4 MB | 226.3 MB（去掉重复模型） |

合并时处理的冲突：`buk_m3` 在两个命名空间各有一份（150 骨骼高精版 / 3 骨骼合并版），
保留高精版并丢弃合并版的数据（它从未注册实体）。

> 注：合并后的数据里仍有 2202 处 `dragonrise_reforge:` / `snvx:` 资源引用，
> 这些是原始 171 台车自带的武器图标与音效，需要安装对应整合包才有图有声。本包不影响载具本身。

## 1.0.0c：补炮塔碰撞盒

给 5 台可验证的坦克补了 `Part:"Turret"` 盒（坐标相对 `TurretPos`）：

| 载具 | 炮塔盒（宽×高×长） | 相对车体比例 |
|---|---|---|
| `m3stuart` | 1.43 × 0.86 × 1.44 | 宽 0.54 / 长 0.31 / 高 0.29 |
| `panzer4` | 2.74 × 1.46 × 3.68 | 宽 0.80 / 长 0.53 / 高 0.44 |
| `t3476` | 1.82 × 1.01 × 2.45 | 宽 0.59 / 长 0.34 / 高 0.33 |
| `type3` | 1.62 × 1.39 × 2.38 | 宽 0.48 / 长 0.23 / 高 0.35 |
| `type97` | 1.08 × 0.58 × 1.51 | 宽 0.50 / 长 0.24 / 高 0.25 |

同时修正 `aavc7c1`：它的 Turret 盒原先写成绝对坐标，与其姊妹车 `aav7a1` 的写法不一致，已改为相对坐标。

另有 5 台**没有补**，因为模型的炮塔骨骼里混进了车体，算出来的盒是整车尺寸，硬加会造出错误的命中体积：
`is2`（宽达车体 90%）、`tiger`（高 106%）、`m4a2`、`type97q`（只有 3 个立方体，碎片）、`m808`（纵向偏 43% 车长）。
这 5 台要修的是模型骨骼，不是 OBB。
`strv103` / `markv` / `test` 本来就没有炮塔（无炮塔歼击车 / 一战侧舷炮 / 测试条目），不补。

## 1.0.0d：炮塔骨骼重划 + OBB 半尺寸语义修正

### 关键修正：`OBB.Size` 是半尺寸

实测 `OBBInfo.getOBB()` 把 Size 直接传给 `OBB.extents`，`OBB.getVertices()` 取 `±extents`：

```java
new OBB(Vector3d(0,0,0), vec3ToVector3d(this.size), new Quaterniond(), part)   // extents = Size
// getVertices(): 8 个角点 = (±extents.x, ±extents.y, ±extents.z)
```

即 **Size = 盒子半尺寸**，不是完整边长。1.0.0c 及之前的自动生成盒把完整尺寸写了进去
（载具包作者手写的盒一直是对的），本次全部减半。受影响：
`bmd_4m` / `bmp_3` / `challenger_2` / `iris_slm` / `iris_slm_fcs` / `leopard_2a5` / `t_80bvm` / `t_80u` / `zsu_23_4`。

### 5 台炮塔骨骼重划分（is2 / tiger / m4a2 / type97q / m808）

原 `turret` 骨骼混入了车体立方体，导致炮塔包围盒等于整车。已按几何重新归属立方体
（几何字节、立方体总数、identifier 全部未变，只改骨骼归属），并补上炮塔盒：

| 载具 | 炮塔全尺寸（宽×高×长，块） | TurretPos |
|---|---|---|
| is2 | 2.580 × 1.313 × 4.073 | [0, 1.9386, 0.3631] |
| tiger | 2.894 × 1.307 × 3.036 | [-0.0093, 2.076, 0.0366] |
| m4a2 | 2.055 × 0.988 × 2.190 | [0, 1.707, 0.0625] |
| type97q | 1.821 × 0.671 × 2.205 | [-0.2511, 1.547, 0.3235] |
| m808 | 3.436 × 3.181 × 4.371 | [0, 3.2812, -3.4062] |

同时移除这 5 台原有的、未写 `Transform` 的旧炮塔盒，避免同一台车两个炮塔盒导致重复计伤。

### 另两处修正

- `m3stuart` / `panzer4` / `t3476` / `type3` / `type97`：它们本来就有作者手写的 `Part:"Turret"` 盒
  （只是没写 `Transform`，旧审查误判为"缺"），1.0.0c 追加的盒是用带 bug 的旋转算的，已撤销。
- `iris_slm` / `iris_slm_fcs` / `t_80u`：`assets` 与 `data` 两份数据重新同步。

### 关于旋转计算（踩过的坑）

`SimpleBedrockModel.BedrockModel.initialWithBoneItems` 对 X 取反、按 Z-Y-X 顺序应用欧拉角；
换算回模型坐标等价于 `(-X, +Y, -Z)`，且必须**递归应用父骨骼变换**。
用朴素的 `(+X,+Y,+Z)` 且不递归会得出"炮塔比车体还宽"之类的错误结论。

## 1.0.0e：骨骼名对齐上游变换约定

上游 `VehicleModel` 用**精确骨骼名**驱动变换：`base` / `root` / `turret` / `barrel` / `passengerWeaponStation`
（另有 `turretLaser` / `barrelLaser`；轮组、履带、外壳等由 `VehicleModelBoneGroups` 的正则匹配）。
名称必须精确相等，不是包含关键字。

`is2` / `tiger` 的根骨骼原本叫 `Tank`，缺 `base`，武器与车体变换接不上。本次改名：

| 载具 | 改动 | 子骨骼引用 | 立方体数 |
|---|---|---|---:|
| is2 | `Tank` → `base` | 2 个 | 604（不变） |
| tiger | `Tank` → `base` | 2 个 | 579（不变） |

改后父链：`turret ← base`，无缺失父引用。`m4a2` / `type97q` 已有 `base`，`m808` 连 `root` 都有，未改动。

> 观察（未改）：`is2` 与 `tiger` 各有一个轮骨骼（`Wheel15` / `Wheel8`）挂在根层、不在 `base` 之下。
> 它对整车移动无影响（根骨骼同样随实体变换），只在车体被单独动画时才会看出来。

## 1.0.0f：删除 spacebag，修复飞行器审查发现的缺陷

飞行器模型审查（`SBW/review/飞行器模型审查报告_20261029.md`）发现 `spacebag`：
模型只有 1.1×1.8×0.9 块，但 9 个 OBB 铺开成 8.25×3.94×14.75 块——碰撞盒与模型严重不符。
已确认删除：

| 项 | 动作 |
|---|---|
| `MergedVehicles.SIZES` | 删除注册条目（实体不再生成） |
| `CreativeTabs` | 移除创造模式标签页引用 |
| `data` / 模型索引 / geo / 独占贴图 | 从 jar 中移除 |
| lang | 移除 3 条条目（zh 2 条 + en 1 条） |

同时复核了 `su24m`：Blender 渲染确认机身/机头/座舱/机翼/尾翼完整连续，
之前标记的"离群部件"实为前半段机身（载具中枢在尾部，机头离中枢 20 格是正常几何）——**非缺陷，未修改**。
`ah1f` 的旋翼问题（桨叶沿机身纵轴排布）已用 Blender 渲染确认，待处理。

审查脚本：`SBW/review/audit_aircraft_v2.py`（43 台飞行器全检）。

## 1.0.0g：从 ashvehicle 搬入 6 台知名固定翼

来源：`ashvehicle-1.21.1-4.6-8.9.jar`（44 台载具 / 33 台飞行器）。选 6 台**不与本包重名**的知名机型：

| id | 机型 | 模型（块） | 武器改写 |
|---|---|---|---|
| `v-22` | V-22 鱼鹰 | 28.4 × 14.1 × 20.4 | 无（原本就用上游弹药） |
| `f-117` | F-117 夜鹰 | 19.2 × 6.3 × 24.0 | 无 |
| `su-57` | 苏-57 | 22.6 × 15.4 × 22.9 | 3 处（R-60 换上游 R-9M336） |
| `f-22` | F-22 猛禽 | 32.3 × 8.0 × 27.7 | 8 处（AIM-9/120 换上游） |
| `f-35a` | F-35A | 9.7 × 5.3 × 25.6 | 9 处（AGM-158/AIM-120 换上游） |
| `b-2` | B-2 幽灵 | 52.9 × 5.2 × 19.3 | 5 处（GBU-57／核弹换 MK-82） |

### 搬迁必须做的事

ashvehicle 用**扁平资产布局**（`assets/ashvehicle/geo/<id>.geo.json` + `textures/entity/<id>.png`），
靠它自己的渲染器解析；上游按约定路径找模型。所以每台都要：

1. geo → `assets/zzyvehicles/models/bedrock/vehicle/<id>.geo.json`
2. 贴图 → `assets/zzyvehicles/textures/bedrock/vehicle/<id>.png`
3. **新建模型索引** `assets/zzyvehicles/sbw/vehicles/<id>.json`（指向上面两个路径）
4. data → `data/zzyvehicles/sbw/vehicles/<id>.json`，并改写：
   - `ID` → `zzyvehicles:<id>`
   - 武器 `Projectile` / `AmmoType` / `Icon` → 上游等价物（见映射表）
   - `SoundInfo`（自有音效）、`EngineSound`（自有引擎声）→ 删除，回落默认
   - 其余 `ashvehicle:` 引用（HUD 图/小图）→ 删键
5. `MergedVehicles.SIZES` 补注实体尺寸（取模型 bbox 的宽高）

验证：6 台 data/索引/geo/贴图齐全，`ashvehicle:` 残留 **0**，飞行器总数 42 → **48**，全量碰撞箱审查 0 ERR。

> 注意：`b-2` 的核弹武器（`nuke_bomb`）被映射成 `mk_82`，因为上游没有对应弹种。
> 如需保留核弹效果，得自行实现投射物。

## 版本号规则

自本版起采用 `1.0.0a → 1.0.0b → 1.0.0c → 1.0.0final` 的阶段性命名，字母表示迭代快照，
`final` 为该轮定稿。此前用的 `2.0.x` 系列已废弃。

## 2026-10-27 新增（9 台，同步现 181 台）

模型来自 War Thunder 客户端导出，逐面与源 OBJ 对照 100% 一致（尺度、轴向、UV、面数）。

| 载具 | 类型 | HP | 武器 | 弹体 |
|---|---|---:|---|---|
| `bmd_4m` | APC | 300 | 100mm 2A70 + 30mm 2A72 + 7.62 PKT | cannon_shell / small_cannon_shell / projectile |
| `bmp_3` | APC | 320 | 100mm 2A70 + 30mm 2A72 + 7.62 PKT | 同上 |
| `zsu_23_4` | APC | 260 | 四管 23mm（真实配置，无导弹） | small_cannon_shell |
| `challenger_2` | Tank | 620 | 120mm L30 + 7.62 同轴 | cannon_shell / projectile |
| `leopard_2a5` | Tank | 560 | 120mm Rh120 + 7.62 同轴 | cannon_shell / projectile |
| `t_80bvm` | Tank | 420 | 125mm 2A46M-4 + 7.62 PKT | cannon_shell / projectile |
| `t_80u` | Tank | 400 | 125mm 2A46M-1 + 7.62 PKT（修正：原为 23mm 高炮占位） | cannon_shell / projectile |
| `iris_slm` | AA | 260 | 8 联装防空弹 | ru_9m336_missile |
| `iris_slm_fcs` | AA | 240 | 无武器（雷达车） | — |

武器弹种全部取自上游 SBW 原生 ID（`superbwarfare:` 与 `@RifleAmmo` 等别名），
不引用第三方命名空间（如 `dragonrise_reforge:`），保证在只装 SBW 的实例上可运行。

### 碰撞箱

- `OBB` 的 `Transform:"Turret"` 坐标是**相对 `TurretPos`** 的（包内 2s25m / 2s38 / 625e 三台为证）。
- 炮塔轴与炮口位置由模型部件分组（`turret` / `gun_barrel` / `gun_mask`）实算，不是克隆旧车坐标。
- 全量 181 台碰撞箱审查：0 个 ERR。修复了 `amx56`（负尺寸盒）、`strv103`（零体积盒）、
  `motuo`（Type=Car 但碰撞盒为空）。遗留 57 台「有主炮无炮塔盒」属数据质量提示，不影响运行。

## 说明

- 多材质载具目前使用单一基色贴图（导出管线不提供材质↔贴图映射），个别部件 UV 可能错位。
- 座位、第三人称相机为按车体尺寸估算，需实测微调。

## 依赖关系

| 方向 | 项目 | 说明 |
|---|---|---|
| 运行依赖 | Superb Warfare (GPL-3.0) | 必需 |
| **必需** | [sbw-radar](https://github.com/zzy25565/sbw-radar) | 防空载具的雷达 HUD、TWS 锁定与近炸引信均由该模组提供；缺失时可生成但无法锁定/引导 |
| 可选搭配 | [sbw-defense](https://github.com/zzy25565/sbw-defense) | 载具的 ERA / APS 数据需要该模组才有实际效果 |
