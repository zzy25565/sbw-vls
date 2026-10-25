package cn.zzy.vehicles.entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** 服务端保存收放状态，客户端按游戏时间求进度；支持中途连续反向。 */
public abstract class DeployableLauncherVehicle extends LaunchAnimationVehicle {
    private static final EntityDataAccessor<Float> FROM = SynchedEntityData.defineId(DeployableLauncherVehicle.class,EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> OPEN = SynchedEntityData.defineId(DeployableLauncherVehicle.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Long> START = SynchedEntityData.defineId(DeployableLauncherVehicle.class,EntityDataSerializers.LONG);
    protected DeployableLauncherVehicle(EntityType<?> type,Level level) { super(type,level); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder); builder.define(FROM,0f); builder.define(OPEN,false); builder.define(START,0L);
    }
    protected int deploymentTicks() { return modelId().equals("s400_launcher") ? 240 : 200; }
    public float launcherProgress(float partial) {
        float from=entityData.get(FROM), target=entityData.get(OPEN) ? 1 : 0;
        float elapsed=Math.max(0,(float)(level().getGameTime()-entityData.get(START))+partial);
        return from+Math.signum(target-from)*Math.min(Math.abs(target-from),elapsed/deploymentTicks());
    }
    public boolean isLauncherDeployed() { return entityData.get(OPEN); }
    public void setLauncherDeployed(boolean open) {
        if (level().isClientSide || open==entityData.get(OPEN) || (!open && isLaunchAnimationActive())) return;
        float progress=launcherProgress(0);
        entityData.set(FROM,progress); entityData.set(START,level().getGameTime()); entityData.set(OPEN,open);
    }
    @Override public InteractionResult interact(Player player,InteractionHand hand) {
        if (hand==InteractionHand.MAIN_HAND && player.isShiftKeyDown() && player.getMainHandItem().isEmpty() && !getLocked()) {
            if (!level().isClientSide) {
                if (isLaunchAnimationActive()) { player.displayClientMessage(Component.translatable("message.zzyvehicles.launch_busy"),true); return InteractionResult.SUCCESS; }
                if (getDeltaMovement().horizontalDistanceSqr()>.0004 || !getPassengers().isEmpty())
                    player.displayClientMessage(Component.translatable("message.zzyvehicles.launcher_park_first"),true);
                else {
                    boolean open=!isLauncherDeployed(); setLauncherDeployed(open);
                    player.displayClientMessage(Component.translatable(open ? "message.zzyvehicles.launcher_deploy" : "message.zzyvehicles.launcher_retract"),true);
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.interact(player,hand);
    }
    @Override public void travel() {
        // 发射架或支腿尚未收妥时锁住驾驶，收妥后恢复原生底盘。
        if (launcherProgress(0)>.0001f || entityData.get(OPEN)) { setDeltaMovement(0,getDeltaMovement().y,0); return; }
        super.travel();
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putFloat("ZzyLauncherProgress",launcherProgress(0)); tag.putBoolean("ZzyLauncherOpen",entityData.get(OPEN));
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // 旧存档默认收拢；中途存档保留进度，加载后继续向原目标运动。
        entityData.set(FROM,Math.max(0,Math.min(1,tag.getFloat("ZzyLauncherProgress"))));
        entityData.set(OPEN,tag.getBoolean("ZzyLauncherOpen")); entityData.set(START,level().getGameTime());
    }
}
