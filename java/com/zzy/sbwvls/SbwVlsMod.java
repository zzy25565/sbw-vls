package com.zzy.sbwvls;

/**
 * Constants holder for the merged build.
 * The 171 integrated vehicles keep the "sbw_vls" RESOURCE namespace
 * (assets/sbw_vls, data/sbw_vls) while the only declared mod in this jar
 * is cn.zzy.vehicles.ZzyVehicles ("zzyvehicles") - same layout as zzy-vehicles-2.0.0.
 */
public final class SbwVlsMod {
    /** Resource namespace for the merged vehicles (NOT a mod id in this jar). */
    public static final String MODID = "sbw_vls";

    private SbwVlsMod() {}
}
