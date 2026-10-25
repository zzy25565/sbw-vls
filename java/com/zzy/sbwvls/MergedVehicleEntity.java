package com.zzy.sbwvls;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Generic vehicle entity for vehicles merged in from 1.20.1 SBW addons.
 * Behaviour is data driven through assets/data sbw/vehicles/&lt;id&gt;.json.
 *
 * Upgrade hook: if a merged vehicle later needs custom logic, subclass this
 * (or VehicleEntity / ArtilleryEntity) and register that class instead.
 */
public class MergedVehicleEntity extends VehicleEntity {

    public MergedVehicleEntity(EntityType<?> type, Level level) {
        super(type, level);
    }
}
