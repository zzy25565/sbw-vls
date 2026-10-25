package cn.zzy.vehicles.entity;
import cn.zzy.vehicles.mechanics.LaunchSpec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import org.joml.Vector3d;

/** 短时可视弹体：无碰撞、无伤害、无锁定，60 刻后消失，也不写入区块存档。 */
public final class VisualMissileEntity extends Entity {
    private static final EntityDataAccessor<CompoundTag> FLIGHT=SynchedEntityData.defineId(VisualMissileEntity.class,EntityDataSerializers.COMPOUND_TAG);
    public VisualMissileEntity(EntityType<?> type,Level level){super(type,level);noPhysics=true;setNoGravity(true);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder b){b.define(FLIGHT,new CompoundTag());}
    public void configure(String source,Vector3d mouth,Vector3d dir){
        LaunchSpec spec=LaunchSpec.get(source);Vector3d tail=new Vector3d(mouth).sub(new Vector3d(dir).mul(spec.length()));CompoundTag t=new CompoundTag();t.putString("Model",source);t.putLong("Start",level().getGameTime());
        t.putDouble("X",tail.x);t.putDouble("Y",tail.y);t.putDouble("Z",tail.z);t.putDouble("DX",dir.x);t.putDouble("DY",dir.y);t.putDouble("DZ",dir.z);entityData.set(FLIGHT,t);setPos(tail.x,tail.y,tail.z);
    }
    public String sourceModel(){return entityData.get(FLIGHT).getString("Model");}
    public float flightAge(float partial){return Math.max(0,(float)(level().getGameTime()-entityData.get(FLIGHT).getLong("Start"))+partial);}
    public Vector3d direction(){var t=entityData.get(FLIGHT);return new Vector3d(t.getDouble("DX"),t.getDouble("DY"),t.getDouble("DZ"));}
    public Vector3d tailPosition(float partial){var t=entityData.get(FLIGHT);var spec=LaunchSpec.get(sourceModel());return new Vector3d(t.getDouble("X"),t.getDouble("Y"),t.getDouble("Z")).add(direction().mul(spec==null?0:spec.distance(flightAge(partial))));}
    public boolean engineLit(){var s=LaunchSpec.get(sourceModel());return s!=null && flightAge(0)>=s.ejectionTicks();}
    @Override public void tick(){
        super.tick();var s=LaunchSpec.get(sourceModel());if(s==null)return;float age=flightAge(0);if(age>=60){discard();return;}
        Vector3d p=tailPosition(0);setPos(p.x,p.y,p.z);
        if(level().isClientSide && engineLit()){
            Vector3d d=direction();for(int i=0;i<4;i++)level().addParticle(ParticleTypes.FLAME,p.x-d.x*i*.2,p.y-d.y*i*.2,p.z-d.z*i*.2,-d.x*.15,-d.y*.15,-d.z*.15);
            for(int i=0;i<3;i++)level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,p.x+(random.nextDouble()-.5)*.15,p.y,p.z+(random.nextDouble()-.5)*.15,-d.x*.09,-d.y*.09,-d.z*.09);
        }
    }
    @Override public boolean hurt(DamageSource source,float amount){return false;}
    @Override public boolean isPickable(){return false;}
    @Override public boolean canBeCollidedWith(){return false;}
    @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<512*512;}
    @Override protected void readAdditionalSaveData(CompoundTag tag){discard();}
    @Override protected void addAdditionalSaveData(CompoundTag tag){}
}
