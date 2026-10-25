package cn.zzy.vehicles.entity;
import cn.zzy.vehicles.mechanics.TrailerKinematics;
import cn.zzy.vehicles.mechanics.TrailerSpec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import java.util.*;
import java.lang.Math;

/** 同一实体内的鞍座铰接；半挂网格、接地采样和交互盒共用同一变换。 */
public final class S400LauncherEntity extends DeployableLauncherVehicle {
    private static final EntityDataAccessor<Float> ANGLE=SynchedEntityData.defineId(S400LauncherEntity.class,EntityDataSerializers.FLOAT);
    private Vector3d lastHitch;
    private float lastTractorYaw,previousAngle,clientAngle;
    public S400LauncherEntity(EntityType<?> type, Level level) { super(type,level); }
    @Override public String modelId() { return "s400_launcher"; }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder);builder.define(ANGLE,0f); }
    public float trailerAngle(float partial) { return level().isClientSide ? Mth.lerp(partial,previousAngle,clientAngle) : entityData.get(ANGLE); }
    public Matrix4d trailerLocalTransform(float partial) {
        Vector3d h=TrailerSpec.HITCH;
        return new Matrix4d().translation(h).rotateY(trailerAngle(partial)).translate(-h.x,-h.y,-h.z);
    }
    @Override public void baseTick() {
        if(level().isClientSide) { previousAngle=clientAngle; clientAngle=entityData.get(ANGLE); }
        super.baseTick();
        if(level().isClientSide) return;
        Vector3d hitch=getVehicleFlatTransform(1f).transformPosition(new Vector3d(TrailerSpec.HITCH));
        float yaw=(float)Math.toRadians(getYRot());
        if(lastHitch!=null && hitch.distanceSquared(lastHitch)<64) {
            float delta=(float)Math.toRadians(Mth.wrapDegrees(getYRot()-(float)Math.toDegrees(lastTractorYaw)));
            entityData.set(ANGLE,TrailerKinematics.advance(entityData.get(ANGLE),lastTractorYaw,delta,
                    hitch.x-lastHitch.x,hitch.z-lastHitch.z,TrailerSpec.AXLE_DISTANCE,TrailerSpec.LIMIT));
        }
        lastHitch=hitch;lastTractorYaw=yaw;
    }
    @Override public Matrix4d getTransformFromString(String name,float partial) {
        if("Trailer".equals(name)) return getVehicleTransform(partial).mul(trailerLocalTransform(partial));
        return super.getTransformFromString(name,partial);
    }
    @Override public Quaterniond getRotationFromString(String name,float partial) {
        if("Trailer".equals(name)) return new Quaterniond(super.getRotationFromString("Vehicle",partial)).rotateY(trailerAngle(partial));
        return super.getRotationFromString(name,partial);
    }
    @Override public void terrainCompact(List<Vec3> positions) {
        Matrix4d trailer=trailerLocalTransform(1);List<Vec3> points=new ArrayList<>(positions.size());
        for(Vec3 p:positions) {
            if(p.z<0) { Vector3d q=trailer.transformPosition(new Vector3d(p.x,p.y,p.z));points.add(new Vec3(q.x,q.y,q.z)); }
            else points.add(p);
        }
        super.terrainCompact(points);
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag);tag.putFloat("ZzyTrailerAngle",entityData.get(ANGLE)); }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);float a=tag.getFloat("ZzyTrailerAngle");
        entityData.set(ANGLE,Float.isFinite(a) ? Mth.clamp(a,-TrailerSpec.LIMIT,TrailerSpec.LIMIT) : 0);
        previousAngle=clientAngle=entityData.get(ANGLE);lastHitch=null;
    }
}
