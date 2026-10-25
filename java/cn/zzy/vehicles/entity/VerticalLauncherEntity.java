package cn.zzy.vehicles.entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
public final class VerticalLauncherEntity extends LaunchAnimationVehicle {
    public VerticalLauncherEntity(EntityType<?> type, Level level) { super(type, level); }
    @Override public String modelId() { return "vls"; }
}
