package cn.zzy.vehicles.client;

import cn.zzy.vehicles.ZzyVehicles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import java.util.*;

/** 仅客户端加载渲染类，避免专用服务器访问 Minecraft 客户端。 */
@EventBusSubscriber(modid = ZzyVehicles.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientAssets {
    private static volatile Map<String, MeshAsset> assets = Map.of();
    static MeshAsset get(String id) { return assets.get(id); }
    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event){event.register(LaunchAnimationControls.PLAY);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ZzyVehicles.VISUAL_MISSILE.get(), VisualMissileRenderer::new);
        event.registerEntityRenderer(ZzyVehicles.S400_LAUNCHER.get(), HighDetailVehicleRenderer::new);
        event.registerEntityRenderer(ZzyVehicles.S400_RADAR.get(), HighDetailVehicleRenderer::new);
        event.registerEntityRenderer(ZzyVehicles.VLS.get(), HighDetailVehicleRenderer::new);
        event.registerEntityRenderer(ZzyVehicles.BUK_RADAR.get(), HighDetailVehicleRenderer::new);
        // 弹丸实体渲染器（Dragon_Rise 移植）
        event.registerEntityRenderer(com.zzy.sbwvls.ProjectileEntities.AGM65.get(), com.redabysslucia.dragonrise_reforge.client.renderer.entity.projectile.Agm65CustomRenderer::new);
        event.registerEntityRenderer(com.zzy.sbwvls.ProjectileEntities.AIM120.get(), com.redabysslucia.dragonrise_reforge.client.renderer.entity.projectile.Aim120Renderer::new);
        event.registerEntityRenderer(com.zzy.sbwvls.ProjectileEntities.KD88A.get(), com.redabysslucia.dragonrise_reforge.client.renderer.entity.projectile.Kd88aRenderer::new);
        event.registerEntityRenderer(com.zzy.sbwvls.ProjectileEntities.LS6.get(), com.redabysslucia.dragonrise_reforge.client.renderer.entity.projectile.Ls6Renderer::new);
        event.registerEntityRenderer(com.zzy.sbwvls.ProjectileEntities.LS6_IR.get(), com.redabysslucia.dragonrise_reforge.client.renderer.entity.projectile.Ls6IrRenderer::new);
        event.registerEntityRenderer(com.zzy.sbwvls.ProjectileEntities.JDAM_ER.get(), com.redabysslucia.dragonrise_reforge.client.renderer.entity.projectile.JdamErRenderer::new);
        event.registerEntityRenderer(com.zzy.sbwvls.ProjectileEntities.GBU12.get(), com.redabysslucia.dragonrise_reforge.client.renderer.entity.projectile.Gbu12Renderer::new);
        event.registerEntityRenderer(com.zzy.sbwvls.ProjectileEntities.GBU24.get(), com.redabysslucia.dragonrise_reforge.client.renderer.entity.projectile.Gbu24Renderer::new);
        event.registerEntityRenderer(com.zzy.sbwvls.ProjectileEntities.GB250.get(), com.redabysslucia.dragonrise_reforge.client.renderer.entity.projectile.Gb250Renderer::new);

    }
    @SubscribeEvent public static void reload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((net.minecraft.server.packs.resources.ResourceManagerReloadListener) manager -> {
            Map<String, MeshAsset> next = new HashMap<>();
            for (String id : List.of("buk_m3", "s400_launcher", "s400_radar", "vls", "buk_radar", "buk_visual_missile", "s400_launcher_visual_missile", "vls_visual_missile")) {
                try {
                    MeshAsset asset = MeshAsset.load(manager, id); next.put(id, asset);
                    com.mojang.logging.LogUtils.getLogger().info("ZZY_ASSET_LOADED {} parts={} wheels={}",id,asset.parts().size(),asset.wheels().size());
                }
                catch (Exception error) { throw new IllegalStateException("ZZY mesh load failed: " + id, error); }
            }
            assets = Map.copyOf(next);
        });
    }
}
