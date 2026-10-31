package com.zzy.sbwvls;

import cn.zzy.vehicles.ZzyVehicles;
import com.redabysslucia.dragonrise_reforge.entities.projectile.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 9 种载具弹丸实体注册（自 Dragon_Rise 1.21.1/NeoForge 源码移植，MIT）。
 * 实体 id 保持原命名 (agm65/aim120/...)，命名空间统一为 zzyvehicles。
 * 模型/贴图由 BasicProjectileRenderer 按 id 自动解析:
 *   assets/zzyvehicles/models/bedrock/projectile/&lt;id&gt;.geo.json
 *   assets/zzyvehicles/textures/bedrock/projectile/&lt;id&gt;.png
 */
public final class ProjectileEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ZzyVehicles.ID);

    public static final DeferredHolder<EntityType<?>, EntityType<Agm65CustomEntity>> AGM65 = reg("agm65", Agm65CustomEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<Aim120Entity>> AIM120 = reg("aim120", Aim120Entity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<Kd88aEntity>> KD88A = reg("kd_88a", Kd88aEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<Ls6Entity>> LS6 = reg("ls_6", Ls6Entity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<Ls6IrEntity>> LS6_IR = reg("ls_6_ir", Ls6IrEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<JdamErEntity>> JDAM_ER = reg("jdam_er", JdamErEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<Gbu12Entity>> GBU12 = reg("gbu_12", Gbu12Entity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<Gbu24Entity>> GBU24 = reg("gbu_24", Gbu24Entity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<Gb250Entity>> GB250 = reg("gb_250", Gb250Entity::new);

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> reg(String id, EntityType.EntityFactory<T> factory) {
        return ENTITIES.register(id, () -> EntityType.Builder.of(factory, MobCategory.MISC)
                .setTrackingRange(512)
                .setUpdateInterval(1)
                .fireImmune()
                .sized(0.5f, 0.5f)
                .build(ZzyVehicles.ID + ":" + id));
    }

    private ProjectileEntities() {}
}
