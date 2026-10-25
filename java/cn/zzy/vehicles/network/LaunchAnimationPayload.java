package cn.zzy.vehicles.network;
import cn.zzy.vehicles.ZzyVehicles;
import cn.zzy.vehicles.entity.LaunchAnimationVehicle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** 客户端只请求播放；目标限定为自己的驾驶席，单元、方向、时机均由服务端决定。 */
public record LaunchAnimationPayload() implements CustomPacketPayload {
    public static final Type<LaunchAnimationPayload> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath(ZzyVehicles.ID,"launch_animation"));
    public static final StreamCodec<RegistryFriendlyByteBuf,LaunchAnimationPayload> CODEC=StreamCodec.unit(new LaunchAnimationPayload());
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(msg,ctx)->ctx.enqueueWork(()->{
        var player=ctx.player();if(player.getVehicle() instanceof LaunchAnimationVehicle v && v.getFirstPassenger()==player)v.requestLaunchAnimation(player);
    }));}
}
