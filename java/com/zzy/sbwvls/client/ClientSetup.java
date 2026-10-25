package com.zzy.sbwvls.client;

import com.zzy.sbwvls.MergedVehicles;
import com.zzy.sbwvls.SbwVlsMod;
import com.zzy.sbwvls.VlsEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = cn.zzy.vehicles.ZzyVehicles.ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ClientSetup {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // armed Buk-M3 (sbw_vls:buk_m3) - restored, runs alongside the unarmed zzyvehicles one
        event.registerEntityRenderer(VlsEntities.BUK_M3, cn.zzy.vehicles.client.HighDetailVehicleRenderer::new);

        // VlsTurretRenderer renders VehicleEntity, which is a supertype of VlsTurretEntity
        event.registerEntityRenderer(VlsEntities.VLS_TURRET, VlsTurretRenderer::new);
        event.registerEntityRenderer(VlsEntities.VLS_CRUISE_12, VlsTurretRenderer::new);
        event.registerEntityRenderer(VlsEntities.VLS_CRUISE_24, VlsTurretRenderer::new);
        event.registerEntityRenderer(VlsEntities.VLS_BALLISTIC_8, VlsTurretRenderer::new);
        event.registerEntityRenderer(VlsEntities.VLS_BALLISTIC_16, VlsTurretRenderer::new);

        // Vehicles merged from 1.20.1 addons
        MergedVehicles.TYPES.values().forEach(type ->
                event.registerEntityRenderer(type, MergedVehicleRenderer::new));
    }

    private ClientSetup() {}
}
