# SBW VLS 载具包（zzy-vehicles）

Superb Warfare 的自定义载具集合（Minecraft 1.21.1 / NeoForge）。当前收录 **175 台**载具定义。

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
└── data/sbw_vls/sbw/vehicles/    175 个载具定义 JSON
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

## 说明

- 多材质载具目前使用单一基色贴图（导出管线不提供材质↔贴图映射），个别部件 UV 可能错位。
- 武器弹种为占位，需按上游实际投射物 id 调整。
