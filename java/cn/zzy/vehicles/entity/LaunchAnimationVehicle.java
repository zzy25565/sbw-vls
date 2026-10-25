package cn.zzy.vehicles.entity;
import cn.zzy.vehicles.ZzyVehicles;
import cn.zzy.vehicles.mechanics.LaunchSpec;
import cn.zzy.vehicles.mechanics.LaunchEffectCleanup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.joml.*;
import java.lang.Math;

/** 服务端触发单发演出，盖板时间同步给所有观众；不连接 Weapons 或伤害系统。 */
public abstract class LaunchAnimationVehicle extends PlatformVehicle {
    private static final EntityDataAccessor<Long> LAUNCH_START=SynchedEntityData.defineId(LaunchAnimationVehicle.class,EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Integer> LAUNCH_CELL=SynchedEntityData.defineId(LaunchAnimationVehicle.class,EntityDataSerializers.INT);
    private int nextCell;
    protected LaunchAnimationVehicle(EntityType<?> type,Level level){super(type,level);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder b){super.defineSynchedData(b);b.define(LAUNCH_START,-1L);b.define(LAUNCH_CELL,0);}
    public float launchAnimationAge(float partial){long start=entityData.get(LAUNCH_START);return start<0?-1:Math.max(0,(float)(level().getGameTime()-start)+partial);}
    public int launchCell(){return entityData.get(LAUNCH_CELL);}
    public boolean isLaunchAnimationActive(){float a=launchAnimationAge(0);return a>=0 && a<LaunchSpec.get(modelId()).cycleTicks();}
    public String launchReadiness(){
        if(isRemoved() || isWreck())return "damaged";
        if(getLocked())return "locked";if(isLaunchAnimationActive())return "busy";
        if(getDeltaMovement().horizontalDistanceSqr()>.0004)return "park";
        if(this instanceof DeployableLauncherVehicle d && (!d.isLauncherDeployed() || d.launcherProgress(0)<.9999f))return "deploy";
        return "ready";
    }
    public boolean triggerLaunchAnimation(){
        if(level().isClientSide || !launchReadiness().equals("ready"))return false;
        var spec=LaunchSpec.get(modelId());entityData.set(LAUNCH_CELL,nextCell);nextCell=(nextCell+1)%spec.cells().size();entityData.set(LAUNCH_START,level().getGameTime());
        level().playSound(null,blockPosition(),SoundEvents.IRON_TRAPDOOR_OPEN,SoundSource.BLOCKS,1f,.65f);return true;
    }
    /** 已完全展开的模型坐标，与渲染器的转台/鞍座变换顺序一致。 */
    public Matrix4d launchWorldTransform(){
        Matrix4d m=getVehicleTransform(1);
        if(this instanceof BukM3Entity b)m.mul(b.launcherYawTransform(1));
        if(this instanceof S400LauncherEntity s)m.mul(s.trailerLocalTransform(1));return m;
    }
    public void requestLaunchAnimation(Player player){
        if(level().isClientSide)return;String ready=launchReadiness();
        if(triggerLaunchAnimation())player.displayClientMessage(Component.translatable("message.zzyvehicles.launch_play",launchCell()+1),true);
        else player.displayClientMessage(Component.translatable("message.zzyvehicles.launch_"+ready),true);
    }
    @Override public InteractionResult interact(Player player,InteractionHand hand){
        if(hand==InteractionHand.MAIN_HAND && player.getMainHandItem().is(Items.BLAZE_ROD)){
            requestLaunchAnimation(player);return InteractionResult.sidedSuccess(level().isClientSide);
        }return super.interact(player,hand);
    }
    @Override public void baseTick(){
        super.baseTick();if(!(level() instanceof ServerLevel server))return;
        if(isWreck() || isRemoved()){entityData.set(LAUNCH_START,-1L);return;}float a=launchAnimationAge(0);var spec=LaunchSpec.get(modelId());
        if(a==spec.launchTick()){
            var c=spec.cells().get(launchCell());Matrix4d m=launchWorldTransform();Vector3d mouth=m.transformPosition(new Vector3d(c.mouth()));Vector3d dir=m.transformDirection(new Vector3d(c.direction())).normalize();
            var missile=ZzyVehicles.VISUAL_MISSILE.get().create(level());if(missile!=null){missile.configure(modelId(),mouth,dir);if(server.addFreshEntity(missile))LaunchEffectCleanup.register(server,missile.getUUID());}
            server.playSound(null,mouth.x,mouth.y,mouth.z,SoundEvents.FIREWORK_ROCKET_LAUNCH,SoundSource.BLOCKS,3f,spec.ejectionTicks()>0?.55f:.8f);
            server.sendParticles(ParticleTypes.CLOUD,mouth.x,mouth.y,mouth.z,24,.3,.2,.3,.10);
        }
        if(a>=spec.launchTick() && a<spec.launchTick()+18){
            Vector3d p=launchWorldTransform().transformPosition(new Vector3d(spec.cells().get(launchCell()).mouth()));
            server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,p.x,p.y,p.z,3,.25,.15,.25,.035);
            if(spec.ejectionTicks()==0)server.sendParticles(ParticleTypes.FLAME,p.x,p.y,p.z,3,.12,.12,.12,.06);
        }
    }
    @Override public void travel(){if(isLaunchAnimationActive()){setDeltaMovement(0,getDeltaMovement().y,0);return;}super.travel();}
    @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("ZzyNextLaunchCell",nextCell);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){
        super.readAdditionalSaveData(tag);nextCell=Math.floorMod(tag.getInt("ZzyNextLaunchCell"),LaunchSpec.get(modelId()).cells().size());
        // 临时演出随卸载取消，保留单元顺序；读档不会重新生成可视弹体。
        entityData.set(LAUNCH_START,-1L);
    }
}
