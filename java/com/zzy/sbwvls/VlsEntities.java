package com.zzy.sbwvls;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@EventBusSubscriber(modid = cn.zzy.vehicles.ZzyVehicles.ID, bus = EventBusSubscriber.Bus.MOD)
public final class VlsEntities {

    public static EntityType<cn.zzy.vehicles.entity.BukM3Entity> BUK_M3;
    public static EntityType<VlsTurretEntity> VLS_TURRET;

    public static EntityType<VlsTurretEntity> VLS_CRUISE_12;
    public static EntityType<VlsTurretEntity> VLS_CRUISE_24;
    public static EntityType<VlsTurretEntity> VLS_BALLISTIC_8;
    public static EntityType<VlsTurretEntity> VLS_BALLISTIC_16;

    // Same factory pattern as SBW ModEntities.vehicle(): MISC + tracking 512 + fireImmune
    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(Registries.ENTITY_TYPE, helper -> {
            BUK_M3 = EntityType.Builder
                    .<cn.zzy.vehicles.entity.BukM3Entity>of(cn.zzy.vehicles.entity.BukM3Entity::new, MobCategory.MISC)
                    .sized(3.5f, 3.2f).clientTrackingRange(128).updateInterval(1)
                    .build("zzyvehicles:buk_m3");
            helper.register(ResourceLocation.fromNamespaceAndPath(SbwVlsMod.MODID, "buk_m3"), BUK_M3);
            VLS_TURRET = turret(helper, "vls_turret", 3.0f, 3.0f);

            VLS_CRUISE_12 = turret(helper, "vls_cruise_12", 3.0f, 3.0f);
            VLS_CRUISE_24 = turret(helper, "vls_cruise_24", 4.0f, 3.6f);
            VLS_BALLISTIC_8 = turret(helper, "vls_ballistic_8", 2.6f, 2.6f);
            VLS_BALLISTIC_16 = turret(helper, "vls_ballistic_16", 3.4f, 3.0f);
        });
    }

    private static EntityType<VlsTurretEntity> turret(RegisterEvent.RegisterHelper<EntityType<?>> helper,
                                                      String id, float width, float height) {
        EntityType<VlsTurretEntity> type = EntityType.Builder
                .<VlsTurretEntity>of(VlsTurretEntity::new, MobCategory.MISC)
                .setTrackingRange(512)
                .setUpdateInterval(1)
                .fireImmune()
                .sized(width, height)
                .build(id);
        helper.register(ResourceLocation.fromNamespaceAndPath(SbwVlsMod.MODID, id), type);
        return type;
    }

    private VlsEntities() {}
}
