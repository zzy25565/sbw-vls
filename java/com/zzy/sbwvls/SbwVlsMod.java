package com.zzy.sbwvls;

/**
 * Constants holder for the merged build.
 * 1.0.0b 起并入单一命名空间：合并载具与高精载具统一注册在 "zzyvehicles" 下，
 * 资源也统一放 assets/zzyvehicles 与 data/zzyvehicles，不再有 sbw_vls 命名空间。
 */
public final class SbwVlsMod {
    /** Resource namespace for the merged vehicles (NOT a mod id in this jar). */
    public static final String MODID = "zzyvehicles";

    private SbwVlsMod() {}
}
