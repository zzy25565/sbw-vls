package com.zzy.sbwvls.client;

import com.atsuishio.superbwarfare.client.renderer.entity.BasicVehicleRenderer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * BasicVehicleRenderer resolves geo/texture from the vehicle JSON by registry id
 * (sbw_vls:vls_turret -> assets/sbw_vls/models/bedrock/vehicle/vls_turret.geo.json etc.).
 */
public class VlsTurretRenderer extends BasicVehicleRenderer {
    public VlsTurretRenderer(EntityRendererProvider.Context manager) {
        super(manager);
    }
}
