package cn.zzy.vehicles.entity;
import cn.zzy.vehicles.mechanics.LauncherSlew;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.*;
import java.lang.Math;

/** 整座转台共用水平轴；筒组俯仰和液压动作在转台局部坐标内叠加。 */
public final class BukM3Entity extends DeployableLauncherVehicle {
    private static final EntityDataAccessor<Float> YAW=SynchedEntityData.defineId(BukM3Entity.class,EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> CLOSING=SynchedEntityData.defineId(BukM3Entity.class,EntityDataSerializers.BOOLEAN);
    private float previousYaw,clientYaw,targetYaw;
    public BukM3Entity(EntityType<?> type,Level level){super(type,level);}
    @Override public String modelId(){return "buk_m3";}
    @Override protected void defineSynchedData(SynchedEntityData.Builder b){super.defineSynchedData(b);b.define(YAW,0f);b.define(CLOSING,false);}
    public float launcherYaw(float partial){return Mth.rotLerp(partial,previousYaw,level().isClientSide ? clientYaw : entityData.get(YAW));}
    public Matrix4d launcherYawTransform(float partial){return LauncherSlew.transform(launcherYaw(partial));}
    @Override public boolean isLauncherDeployed(){return super.isLauncherDeployed() && !entityData.get(CLOSING);}
    public boolean isLauncherCentering(){return entityData.get(CLOSING);}
    /** 服务端请求局部回转角，只有完全展开时接受。360 度回转走最短路径。 */
    public void setLauncherYawTarget(float degrees){
        if(!level().isClientSide && isLauncherDeployed() && launcherProgress(0)>=.9999f && Float.isFinite(degrees))targetYaw=Mth.wrapDegrees(degrees);
    }
    @Override public void setLauncherDeployed(boolean open){
        if(level().isClientSide || (!open && isLaunchAnimationActive()))return;
        if(open){entityData.set(CLOSING,false);super.setLauncherDeployed(true);}
        else if(Math.abs(entityData.get(YAW))>.001f){targetYaw=0;entityData.set(CLOSING,true);}
        else {entityData.set(CLOSING,false);targetYaw=0;super.setLauncherDeployed(false);}
    }
    @Override public void baseTick(){
        if(level().isClientSide){previousYaw=clientYaw;clientYaw=entityData.get(YAW);}
        else previousYaw=entityData.get(YAW);
        super.baseTick();
        if(level().isClientSide)return;
        if(isLaunchAnimationActive())return;
        if(entityData.get(CLOSING))targetYaw=0;
        else if(isLauncherDeployed() && launcherProgress(0)>=.9999f && getFirstPassenger() instanceof Player p){
            // 复用原生乘员视角网络同步，转换到随车体倾斜的局部水平面。
            var look=p.getLookAngle();Vector3d local=getVehicleTransform(1).invert().transformDirection(new Vector3d(look.x,look.y,look.z));
            if(local.x*local.x+local.z*local.z>1e-8)targetYaw=(float)Math.toDegrees(Math.atan2(local.x,local.z));
        }
        if(entityData.get(CLOSING) || (isLauncherDeployed() && launcherProgress(0)>=.9999f))entityData.set(YAW,LauncherSlew.advance(entityData.get(YAW),targetYaw));
        if(entityData.get(CLOSING) && Math.abs(entityData.get(YAW))<.001f){entityData.set(CLOSING,false);super.setLauncherDeployed(false);}
    }
    @Override public Matrix4d getTransformFromString(String name,float partial){
        if("BukTurret".equals(name))return getVehicleTransform(partial).mul(launcherYawTransform(partial));
        return super.getTransformFromString(name,partial);
    }
    @Override public Quaterniond getRotationFromString(String name,float partial){
        if("BukTurret".equals(name))return new Quaterniond(super.getRotationFromString("Vehicle",partial)).rotateY(Math.toRadians(launcherYaw(partial)));
        return super.getRotationFromString(name,partial);
    }
    @Override public void addAdditionalSaveData(CompoundTag tag){
        super.addAdditionalSaveData(tag);tag.putFloat("ZzyLauncherYaw",entityData.get(YAW));tag.putFloat("ZzyLauncherYawTarget",targetYaw);tag.putBoolean("ZzyLauncherCentering",entityData.get(CLOSING));
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag){
        super.readAdditionalSaveData(tag);float a=tag.getFloat("ZzyLauncherYaw"),t=tag.getFloat("ZzyLauncherYawTarget");
        entityData.set(YAW,Float.isFinite(a)?Mth.wrapDegrees(a):0);previousYaw=clientYaw=entityData.get(YAW);targetYaw=Float.isFinite(t)?Mth.wrapDegrees(t):0;
        entityData.set(CLOSING,tag.getBoolean("ZzyLauncherCentering"));
    }
}
