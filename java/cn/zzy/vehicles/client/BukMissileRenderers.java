package cn.zzy.vehicles.client;

import com.zzy.sbwvls.ProjectileEntities;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * 分体式防空车导弹（buk_missile_01/02）的渲染器注册。
 * 独立于 ClientAssets，避免与模型/履带渲染链耦合：
 * 模型与贴图由 SBW BasicProjectileRenderer 按实体 id 自动解析:
 *   assets/zzyvehicles/models/bedrock/projectile/buk_missile_01.geo.json
 *   assets/zzyvehicles/textures/bedrock/projectile/buk_missile_01.png
 */
@EventBusSubscriber(modid = cn.zzy.vehicles.ZzyVehicles.ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class BukMissileRenderers {

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ProjectileEntities.BUK_MISSILE_01.get(),
                (EntityRendererProvider<cn.zzy.vehicles.entity.BukMissileEntity>)
                        com.atsuishio.superbwarfare.client.renderer.projectile.BasicProjectileRenderer::new);
        event.registerEntityRenderer(ProjectileEntities.BUK_MISSILE_02.get(),
                (EntityRendererProvider<cn.zzy.vehicles.entity.BukMissileEntity>)
                        com.atsuishio.superbwarfare.client.renderer.projectile.BasicProjectileRenderer::new);
    }

    private BukMissileRenderers() {}
}
