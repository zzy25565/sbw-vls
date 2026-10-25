package cn.zzy.vehicles.client;

import cn.zzy.vehicles.entity.PlatformVehicle;
import cn.zzy.vehicles.entity.DeployableLauncherVehicle;
import cn.zzy.vehicles.entity.S400LauncherEntity;
import cn.zzy.vehicles.entity.BukM3Entity;
import cn.zzy.vehicles.entity.LaunchAnimationVehicle;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.*;
import java.lang.Math;
import java.util.*;

/** 原生方块渲染器不能呈现三角网格，此适配层保留全部面并复用原生载具实体。 */
public final class HighDetailVehicleRenderer<T extends PlatformVehicle> extends EntityRenderer<T> {
    private final Map<PlatformVehicle, ChassisPose> poses = new WeakHashMap<>();
    public HighDetailVehicleRenderer(EntityRendererProvider.Context context) { super(context); this.shadowRadius = 1.8f; }
    /** 20 游戏刻/秒，10 秒一周；只是展示角度，不表示实际搜索能力。 */
    static float radarAngleDegrees(int ticks, float partial) { return ((ticks % 200) + partial) * 1.8f; }
    @Override public ResourceLocation getTextureLocation(T entity) { return ResourceLocation.fromNamespaceAndPath("zzyvehicles", "textures/vehicles/" + entity.modelId() + "/material_00.png"); }
    @Override public void render(T entity, float yaw, float partial, PoseStack stack, MultiBufferSource buffers, int light) {
        MeshAsset asset = ClientAssets.get(entity.modelId()); if (asset == null) return;
        ChassisPose pose = poses.computeIfAbsent(entity, key -> new ChassisPose()); pose.update(entity, asset);
        stack.pushPose();
        // 世界平移由 Minecraft 完成；本体的同一变换用于模型、地形射线与碰撞。
        Matrix4d local = entity.getVehicleTransform(partial);
        local.m30(0).m31(0).m32(0);
        // getVehicleTransform 的平移还含旋转中心，必须保留相对实体原点的偏移。
        Matrix4d world = entity.getVehicleTransform(partial);
        double ex = net.minecraft.util.Mth.lerp((double) partial, entity.xo, entity.getX());
        double ey = net.minecraft.util.Mth.lerp((double) partial, entity.yo, entity.getY());
        double ez = net.minecraft.util.Mth.lerp((double) partial, entity.zo, entity.getZ());
        local.m30(world.m30() - ex).m31(world.m31() - ey).m32(world.m32() - ez);
        stack.mulPose(new Matrix4f(local));
        // 透明玻璃最后绘制。资源重新加载时资产整体替换，无静态世界引用。
        for (boolean transparent : new boolean[]{false, true}) for (MeshAsset.Part part : asset.parts()) {
            if (part.translucent() != transparent) continue;
            stack.pushPose();
            if (part.trailer() && entity instanceof S400LauncherEntity s400) {
                stack.mulPose(new Matrix4f(s400.trailerLocalTransform(partial)));
            }
            if (part.slew() && entity instanceof BukM3Entity buk) {
                stack.mulPose(new Matrix4f(buk.launcherYawTransform(partial)));
            }
            if (part.role().equals("radar")) {
                Vector3f pivot = part.pivot();
                stack.translate(pivot.x, pivot.y, pivot.z);
                stack.mulPose(Axis.YP.rotationDegrees(radarAngleDegrees(entity.tickCount, partial)));
                stack.translate(-pivot.x, -pivot.y, -pivot.z);
            }
            if (asset.launcher() != null && !part.launcherRole().isEmpty() && entity instanceof DeployableLauncherVehicle launcher) {
                stack.mulPose(asset.launcher().transform(part.launcherRole(), launcher.launcherProgress(partial)));
            }
            if(part.launchCell()>=0 && entity instanceof LaunchAnimationVehicle launcher){
                Matrix4f cap=LaunchCapAnimation.transform(launcher,part.launchCell(),partial);
                if(cap==null){stack.popPose();continue;}stack.mulPose(cap);
            }
            MeshAsset.Wheel wheel = part.wheel();
            if (wheel != null) {
                ChassisPose.WheelState state = pose.state(wheel.id());
                float compression = state.compression(partial);
                if (part.role().equals("wheel")) {
                    stack.translate(0, compression, 0);
                    Vector3f p = part.pivot(); stack.translate(p.x, p.y, p.z);
                    if (wheel.steer()) stack.mulPose(Axis.YP.rotation(net.minecraft.util.Mth.lerp(partial, entity.getRudderRotO(), entity.getRudderRot())));
                    // +Z 行驶时绕 +X 滚动，接地面的相对速度指向 -Z。
                    stack.mulPose(Axis.XP.rotation((float) (state.distance(partial) / wheel.radius() % (Math.PI * 2))));
                    stack.translate(-p.x, -p.y, -p.z);
                } else if (part.role().equals("suspension")) {
                    Vector3f p = part.pivot(); float dy = wheel.pivot().y - p.y, dz = wheel.pivot().z - p.z;
                    float angle = (float) (-Math.atan2(dy + compression, dz) + Math.atan2(dy, dz));
                    stack.translate(p.x, p.y, p.z); stack.mulPose(Axis.XP.rotation(angle)); stack.translate(-p.x, -p.y, -p.z);
                }
            }
            VertexConsumer consumer = buffers.getBuffer(transparent ? RenderType.entityTranslucent(part.texture()) : RenderType.entityCutoutNoCull(part.texture()));
            float scroll = part.trackSide().isEmpty() ? 0 : (float) ((pose.sideDistance(asset, part.trackSide(), partial) * part.uvPerMeter()) % 1.0);
            float[] v = part.vertices();
            // Minecraft QUADS 格式：三角形最后一个顶点重复一次，几何不增加可见面。
            for (int tri = 0; tri < v.length; tri += 24) {
                for (int corner = 0; corner < 4; corner++) {
                    int i = tri + Math.min(corner, 2) * 8; float y = v[i + 1];
                    if (!part.trackSide().isEmpty()) y += pose.beltOffset(asset, part.trackSide(), y, v[i + 2], partial);
                    consumer.addVertex(stack.last(), v[i], y, v[i + 2]).setColor(255, 255, 255, 255)
                            .setUv(v[i + 6] + (part.uvAxis() == 0 ? scroll : 0), v[i + 7] + (part.uvAxis() == 1 ? scroll : 0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
                            .setNormal(stack.last(), v[i + 3], v[i + 4], v[i + 5]);
                }
            }
            stack.popPose();
        }
        LaunchMouthRenderer.draw(entity,partial,stack,buffers);
        stack.popPose(); super.render(entity, yaw, partial, stack, buffers, light);
    }
}
