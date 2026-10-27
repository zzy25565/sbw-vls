# SBW VLS 载具包（zzy-vehicles）

Superb Warfare 的自定义载具集合（Minecraft 1.21.1 / NeoForge）。当前收录 **185 台**载具定义（180 台合并载具 + 5 台高精平台）。版本 **1.0.0c**。

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
└── data/zzyvehicles/sbw/vehicles/    185 个载具定义 JSON
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
