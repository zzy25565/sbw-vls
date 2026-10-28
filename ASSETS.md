# 资产入库范围

本仓库以**源码与数据**为主，不追求能单独打包出完整 jar。哪些资产进来、哪些不进，按体积和可复现性划界：

## 已入库

| 路径 | 内容 | 体积 |
|---|---|---:|
| `resources/data/zzyvehicles/sbw/vehicles/*.json` | 185 份载具定义（碰撞箱、武器、座席、动力） | ≈ 1.4 MB |
| `resources/assets/zzyvehicles/models/bedrock/vehicle/{is2,tiger,m4a2,type97q,m808}.geo.json` | 5 台**重划分炮塔骨骼**的模型（cubes 格式） | ≈ 0.85 MB |
| `java/` | 35 个 Java 源文件 | ≈ 0.2 MB |

这 5 台模型入库的理由：它们的骨骼归属是**手工/脚本重划分**的成果（见 README 1.0.0d），
不重新跑一遍导出流程无法复现，属于不可再生的源资产。

## 未入库

| 资产 | 体积 | 原因 |
|---|---:|---|
| 9 台 WT 导出的 poly_mesh 模型（`bmd_4m` / `bmp_3` / `challenger_2` / `iris_slm` / `iris_slm_fcs` / `leopard_2a5` / `t_80bvm` / `t_80u` / `zsu_23_4`） | 约 200 MB | 由 OBJ 经转换脚本可再生成；单体最大 40 MB，入库会让仓库膨胀且难以 diff |
| 其余 176 台的 cubes 模型 | 约 60 MB | 上游整合包原有资产，非本项目产出 |
| 贴图 | 约 85 MB | 同上 |
| `zzy-vehicles-*.jar` | 226 MB | 构建产物，不入库 |

## 完整包在哪

- 本地构建产物：`D:\WorkBuddyProjects\2026-10-26-20-02-34\dist\zzy-vehicles-1.0.0e.jar`
- 模型转换与审查脚本：`SBW/review/`（`build_1_0_0d.py`、`build_1_0_0e_base_rename.py`、`audit_obb.py`、`verify_merged.py`）

## 复现路径

1. 拿上游整合包与 9 台的源 OBJ
2. 用 `SBW/review/pack_vehicles_into_addon.py` 转出 poly_mesh（16 单位/米，×1.1919 对齐包内尺度）
3. 用 `merge_namespace_100b.py` 合并命名空间
4. 用 `build_1_0_0d.py` 接入重划骨骼的 5 台 + 修正 OBB 半尺寸
5. 用 `build_1_0_0e_base_rename.py` 修根骨骼名
