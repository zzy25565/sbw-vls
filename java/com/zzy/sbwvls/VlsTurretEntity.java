package com.zzy.sbwvls;

import com.atsuishio.superbwarfare.entity.vehicle.base.ArtilleryEntity;
import com.atsuishio.superbwarfare.item.misc.FiringParametersItem;
import com.atsuishio.superbwarfare.item.misc.FiringParametersItemKt;
import com.atsuishio.superbwarfare.tools.VectorToolKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * VLS turret: vertical-launch missiles only, no seats, fired exclusively via Artillery Indicator.
 * Ported from Superb Warfare HappiestGhastEntity.
 */
public class VlsTurretEntity extends ArtilleryEntity {

    public VlsTurretEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    /** Allow binding with the Artillery Indicator. */
    @Override
    public boolean canBind() {
        return true;
    }

    /** Block crowbar direct fire; keep shift+crowbar (dismantle etc.) behavior. */
    @Override
    public InteractionResult onCrowbarInteract(ItemStack stack, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown() && !this.isWreck()) {
            return InteractionResult.SUCCESS; // swallow the fire action
        }
        return super.onCrowbarInteract(stack, player, hand);
    }

    /** Same aiming logic as the ghast: no ballistic pitch check, straight vertical launch. */
    @Override
    public void setTarget(ItemStack stack, @Nullable Entity entity, String weaponName) {
        FiringParametersItem.Parameters parameters = FiringParametersItemKt.getFiringParameters(stack);
        this.setRadius(parameters.radius());
        this.setOriginPos(parameters.pos());
        Vec3 randomPos = VectorToolKt.randomPos(parameters.pos().getCenter(), this.getRadius()).add(0.0, -1.0, 0.0);
        this.setTargetPos(BlockPos.containing(randomPos));
    }

    @Override
    public void resetTarget(String weaponName) {
        if (this.isWreck()) return;
        Vec3 randomPos = VectorToolKt.randomPos(this.getOriginPos().getCenter(), this.getRadius()).add(0.0, -1.0, 0.0);
        this.setTargetPos(BlockPos.containing(randomPos));
    }

    /** Vertical missile launch smoke column, ported from the ghast. */
    @Override
    public void beforeShoot(@Nullable LivingEntity living, @Nullable String weaponName) {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        if (!"Main".equals(weaponName) && !"AAMissile".equals(weaponName)) return;

        Vec3 pos = this.getShootPos(weaponName, 1.0f);
        Vec3 direct = this.getShootVec(weaponName, 1.0f).normalize();
        for (int i = 0; i <= 10; i++) {
            double s = 0.03 * i;
            Vec3 p = pos.add(direct.scale(1.2 * i));
            final int fi = i;
            serverLevel.getServer().execute(() ->
                    serverLevel.sendParticles(ParticleTypes.CLOUD, p.x, p.y, p.z,
                            (int) (1 + 1.3 * fi), s, s, s, 0.002 * fi));
        }
    }
}
