package cn.zzy.vehicles.entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
public final class S400RadarEntity extends PlatformVehicle {
    public S400RadarEntity(EntityType<?> type, Level level) { super(type, level); }
    @Override public String modelId() { return "s400_radar"; }
}
