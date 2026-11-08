package cn.zzy.vehicles.entity;

import com.atsuishio.superbwarfare.entity.projectile.BasicGeoProjectileEntity;
import com.atsuishio.superbwarfare.entity.projectile.MissileProjectile;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * 分体式防空车（山毛榉 / S-400 / VLS）专用防空导弹。
 *
 * 弹道：冷发射 → 大仰角爬升 → 高空巡航 → 俯冲（top-attack）命中目标。
 *   ① 冷发射 10 tick：小下坠，等待点火
 *   ② 爬升：约 80° 仰角，同时朝目标方位修正；目标巡航高度 =
 *      max(沿途地形最高点, 发射点, 目标点) + CLEARANCE（地形适配，避免撞山）
 *   ③ 巡航：保持巡航高度水平接近，每 10 tick 对前方地形动态补高
 *   ④ 俯冲：水平距离 <= DIVE_RADIUS 时全力朝目标，形成大角度下压
 *   ⑤ 目标丢失时不放弃：飞向最后已知点（由基类 targetPos 维护）
 *
 * 雷达联动：继承 SBW MissileProjectile，sbw_radar 的 RadarMissileControl 会每 tick
 * 通过 setTargetUuid / setTargetVec 喂入雷达跟踪目标，本类直接消费这些数据。
 */
public class BukMissileEntity extends MissileProjectile implements BasicGeoProjectileEntity {

    /** 冷发射阶段（下坠）tick 数 */
    private static final int COLD_LAUNCH_TICKS = 10;
    /** 巡航高度余量：路径地形最高点之上多少格 */
    private static final double CRUISE_CLEARANCE = 45.0;
    /** 开始俯冲的水平距离 */
    private static final double DIVE_RADIUS = 70.0;
    /** 巡航高度上限（避免贴近世界顶） */
    private static final double MAX_CRUISE_Y = 300.0;
    /** 爬升段速度（格/tick） */
    private static final double CLIMB_SPEED = 9.0;
    /** 巡航/俯冲段速度（格/tick） */
    private static final double CRUISE_SPEED = 10.5;
    /** 爬升段转向速度（度/tick） */
    private static final float CLIMB_TURN = 3.0f;
    /** 巡航段转向速度（度/tick） */
    private static final float CRUISE_TURN = 6.0f;
    /** 俯冲段转向速度（度/tick） */
    private static final float DIVE_TURN = 12.0f;

    /** 目标巡航高度（首次解析目标时计算，之后动态修正） */
    private double cruiseY = Double.NaN;
    /** 地形采样计时 */
    private int terrainTick = 0;
    /** 当前阶段（仅用于调试/统计） */
    private int phase = 0;

    public BukMissileEntity(EntityType<? extends BukMissileEntity> type, Level level) {
        super(type, level);
        this.setDistracted(false);
    }

    @Override
    public void tick() {
        // ① 冷发射：小下坠，不制导
        if (this.tickCount <= COLD_LAUNCH_TICKS) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.05, 0.0).multiply(0.99, 0.99, 0.99));
            super.tick();
            return;
        }

        if (!this.level().isClientSide) {
            Vec3 tgt = resolveTargetPoint();
            if (tgt != null) {
                if (Double.isNaN(cruiseY)) {
                    cruiseY = computeCruiseAltitude(tgt);
                }
                steer(tgt);
            }
        }
        super.tick();
    }

    /** 目标点解析：优先雷达/制导喂入的实体目标，其次坐标目标（最后已知点） */
    private Vec3 resolveTargetPoint() {
        Entity e = EntityFindUtil.findEntity(this.level(), this.getTargetUUID());
        if (e != null && e.isAlive()) {
            return e.position().add(0.0, e.getBbHeight() * 0.5, 0.0);
        }
        return this.getTargetPos();
    }

    /** 弹道核心：按当前高度与水平距离选择爬升 / 巡航 / 俯冲 */
    private void steer(Vec3 tgt) {
        Vec3 pos = this.position();
        Vec3 toTgt = tgt.subtract(pos);
        double horiz = Math.hypot(toTgt.x, toTgt.z);
        double dy = toTgt.y;
        Vec3 horizDir = horiz > 1.0E-4 ? new Vec3(toTgt.x, 0.0, toTgt.z).normalize() : this.getLookAngle();

        double curY = pos.y;

        if (curY < cruiseY - 6.0 && horiz > DIVE_RADIUS) {
            // ② 爬升段：约 80° 仰角 + 少量方位修正（0.18 : 1 的比例）
            phase = 1;
            Vec3 dir = horizDir.scale(0.18).add(0.0, 1.0, 0.0).normalize();
            this.turn(dir, CLIMB_TURN);
            this.setDeltaMovement(applySpeed(CLIMB_SPEED));
        } else if (horiz > DIVE_RADIUS) {
            // ③ 巡航段：维持巡航高度，水平朝目标；每 10 tick 动态抬高（地形适配）
            phase = 2;
            terrainTick++;
            if (terrainTick % 10 == 0) {
                double ahead = computeCruiseAltitude(tgt);
                if (ahead > cruiseY) cruiseY = Math.min(ahead, MAX_CRUISE_Y);
            }
            double heightErr = cruiseY - curY;
            double pitch = Mth.clamp(heightErr * 0.05, -0.25, 0.35);
            Vec3 dir = new Vec3(horizDir.x, pitch, horizDir.z).normalize();
            this.turn(dir, CRUISE_TURN);
            this.setDeltaMovement(applySpeed(CRUISE_SPEED));
        } else {
            // ④ 俯冲段：全力朝目标（含垂直分量 → 自然形成大角度俯冲）
            phase = 3;
            Vec3 dir = toTgt.normalize();
            this.turn(dir, DIVE_TURN);
            this.setDeltaMovement(applySpeed(CRUISE_SPEED));
        }
    }

    /** 沿当前朝向加速到目标速度（保持 SBW 的速度模型：阻尼 + 视线加速） */
    private Vec3 applySpeed(double speed) {
        Vec3 look = this.getLookAngle();
        Vec3 v = this.getDeltaMovement().scale(0.08).add(look.scale(speed));
        return v.multiply(0.85, 0.85, 0.85);
    }

    /**
     * 地形适配：沿 发射点→目标 连线采样 16 点，取地面最高处 + CLEARANCE，
     * 同时不低于发射点与目标点，且不超过世界高度保护值。
     */
    private double computeCruiseAltitude(Vec3 tgt) {
        double maxGround = Math.max(this.getY(), tgt.y);
        if (this.level() instanceof ServerLevel sl) {
            int samples = 16;
            for (int i = 1; i <= samples; i++) {
                double t = (double) i / samples;
                double x = Mth.lerp(t, this.getX(), tgt.x);
                double z = Mth.lerp(t, this.getZ(), tgt.z);
                int h = sl.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
                if (h > maxGround && h < 320) maxGround = h;
            }
        }
        return Mth.clamp(maxGround + CRUISE_CLEARANCE, this.getY() + 8.0, MAX_CRUISE_Y);
    }

    // ── BasicGeoProjectileEntity 接口方法（Kotlin 默认值在 Java 侧需显式实现）──
    @Override
    public net.minecraft.resources.ResourceLocation getModel() {
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("superbwarfare", "projectile/projectile");
    }

    @Override
    public net.minecraft.resources.ResourceLocation getAnimation() {
        return null;
    }

    @Override
    public com.atsuishio.superbwarfare.client.animation.entity.BasicProjectileAnimationInstance<?> getAnimationInstance() {
        return null;
    }

    @Override
    public net.minecraft.resources.ResourceLocation getEmissiveTexture() {
        return null;
    }

    @Override
    public int getHiddenTicks() {
        return 0;
    }

    @Override
    public int getFlareHiddenTicks() {
        return 3;
    }

    @Override
    public com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance getModelInstance() {
        net.minecraft.resources.ResourceLocation key =
                net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(this.getType());
        if (key == null) return null;
        net.minecraft.resources.ResourceLocation loc = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                key.getNamespace(), "models/bedrock/projectile/" + key.getPath() + ".geo.json");
        com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel model =
                com.atsuishio.superbwarfare.resource.model.ProjectileModelReloadListener.INSTANCE.getModel(loc);
        return model == null ? null : model.createInstance();
    }

    @Override
    public net.minecraft.world.item.Item getDefaultItem() {
        return ModItems.LARGE_ANTI_AIR_MISSILE.get();
    }

    /** 调试用：当前阶段（1 爬升 / 2 巡航 / 3 俯冲） */
    public int getPhase() {
        return phase;
    }
}
