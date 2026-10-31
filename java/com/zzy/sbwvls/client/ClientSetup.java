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
        var detailed=java.util.Set.of("bmd_4m","bmp_3","challenger_2","iris_slm_fcs","iris_slm","leopard_2a5","t_80bvm","t_80u","zsu_23_4");
        MergedVehicles.TYPES.forEach((id,type) -> {
            if(detailed.contains(id))event.registerEntityRenderer(type, cn.zzy.vehicles.client.HighDetailVehicleRenderer::new);
            else event.registerEntityRenderer(type, context -> new MergedVehicleRenderer(context,id));
        });
    }

    private ClientSetup() {}
}
