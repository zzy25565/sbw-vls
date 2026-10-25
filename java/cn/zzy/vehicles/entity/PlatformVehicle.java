package cn.zzy.vehicles.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 沿用卓越前线的驾驶、地形适应、OBB、座位和视角；武器配置由用户添加。 */
public abstract class PlatformVehicle extends VehicleEntity {
    protected PlatformVehicle(EntityType<?> type, Level level) { super(type, level); }
    public abstract String modelId();
}
