package cn.zzy.vehicles;

import cn.zzy.vehicles.entity.PlatformVehicle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import java.util.function.Supplier;

/** 普通实体不能使用原版 Mob 刷怪蛋；平台物品在点击位置部署无武器载具。 */
public final class PlatformItem extends Item {
    private final Supplier<? extends EntityType<? extends PlatformVehicle>> type;
    public PlatformItem(Supplier<? extends EntityType<? extends PlatformVehicle>> type) {
        super(new Properties().stacksTo(1)); this.type = type;
    }
    @Override public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            PlatformVehicle vehicle = type.get().create(context.getLevel());
            if (vehicle == null) return InteractionResult.FAIL;
            BlockPos position = context.getClickedPos().relative(context.getClickedFace());
            vehicle.moveTo(position.getX() + .5, position.getY(), position.getZ() + .5,
                    context.getPlayer() == null ? 0 : context.getPlayer().getYRot(), 0);
            if (!context.getLevel().addFreshEntity(vehicle)) return InteractionResult.FAIL;
            if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) context.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
}
