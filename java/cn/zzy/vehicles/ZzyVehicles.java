package cn.zzy.vehicles;

import cn.zzy.vehicles.entity.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

@Mod(ZzyVehicles.ID)
public final class ZzyVehicles {
    public static final String ID = "zzyvehicles";
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ID);
    public static final DeferredHolder<EntityType<?>, EntityType<S400LauncherEntity>> S400_LAUNCHER = ENTITIES.register("s400_launcher",
            () -> EntityType.Builder.of(S400LauncherEntity::new, MobCategory.MISC).sized(3.6f, 3.0f).clientTrackingRange(128).updateInterval(1).build("zzyvehicles:s400_launcher"));
    public static final DeferredHolder<EntityType<?>, EntityType<S400RadarEntity>> S400_RADAR = ENTITIES.register("s400_radar",
            () -> EntityType.Builder.of(S400RadarEntity::new, MobCategory.MISC).sized(3.7f, 3.0f).clientTrackingRange(128).updateInterval(1).build("zzyvehicles:s400_radar"));
    public static final DeferredHolder<EntityType<?>, EntityType<VerticalLauncherEntity>> VLS = ENTITIES.register("vls",
            () -> EntityType.Builder.of(VerticalLauncherEntity::new, MobCategory.MISC).sized(3.8f, 4.7f).clientTrackingRange(128).updateInterval(1).build("zzyvehicles:vls"));
    public static final DeferredHolder<EntityType<?>, EntityType<BukRadarEntity>> BUK_RADAR = ENTITIES.register("buk_radar",
            () -> EntityType.Builder.of(BukRadarEntity::new, MobCategory.MISC).sized(3.5f, 3.2f).clientTrackingRange(128).updateInterval(1).build("zzyvehicles:buk_radar"));
    public static final DeferredHolder<EntityType<?>, EntityType<VisualMissileEntity>> VISUAL_MISSILE = ENTITIES.register("visual_missile",
            () -> EntityType.Builder.of(VisualMissileEntity::new, MobCategory.MISC).sized(.4f,.4f).clientTrackingRange(256).updateInterval(1).noSave().build("zzyvehicles:visual_missile"));
    static {
        ITEMS.register("s400_launcher_platform", () -> new PlatformItem(S400_LAUNCHER));
        ITEMS.register("s400_radar_platform", () -> new PlatformItem(S400_RADAR));
        ITEMS.register("vls_platform", () -> new PlatformItem(VLS));
        ITEMS.register("buk_radar_platform", () -> new PlatformItem(BUK_RADAR));
    }
    public ZzyVehicles(IEventBus bus) {
        bus.addListener(cn.zzy.vehicles.network.LaunchAnimationPayload::register);
        ENTITIES.register(bus); ITEMS.register(bus);
        com.zzy.sbwvls.CreativeTabs.TABS.register(bus);
        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        });
    }
}
