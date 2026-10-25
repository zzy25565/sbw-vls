package cn.zzy.vehicles.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 9S18 展示平台，仅复用原模组底盘；不接入搜索、锁定或武器逻辑。 */
public final class BukRadarEntity extends PlatformVehicle {
    public BukRadarEntity(EntityType<?> type, Level level) { super(type, level); }
    @Override public String modelId() { return "buk_radar"; }
}
