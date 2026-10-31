package com.zzy.sbwvls;

import cn.zzy.vehicles.entity.PlatformVehicle;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Generic vehicle entity for vehicles merged in from 1.20.1 SBW addons.
 * Behaviour is data driven through assets/data sbw/vehicles/&lt;id&gt;.json.
 *
 * Upgrade hook: if a merged vehicle later needs custom logic, subclass this
 * (or VehicleEntity / ArtilleryEntity) and register that class instead.
 */
public class MergedVehicleEntity extends PlatformVehicle {
    private final TrackProfiles.Curve trackProfile;

    public MergedVehicleEntity(EntityType<?> type, Level level) {
        super(type, level);
        trackProfile = TrackProfiles.forId(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath());
    }

    @Override public int getTrackAnimationLength() {
        return trackProfile == null ? super.getTrackAnimationLength() : trackProfile.period();
    }
    @Override public String modelId(){return net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath();}
}
