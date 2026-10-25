package cn.zzy.vehicles.client;
import cn.zzy.vehicles.ZzyVehicles;
import cn.zzy.vehicles.entity.LaunchAnimationVehicle;
import cn.zzy.vehicles.network.LaunchAnimationPayload;
import net.minecraft.client.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid=ZzyVehicles.ID,value=Dist.CLIENT)
public final class LaunchAnimationControls {
    public static final KeyMapping PLAY=new KeyMapping("key.zzyvehicles.launch_animation",GLFW.GLFW_KEY_B,"key.categories.zzyvehicles");
    @SubscribeEvent public static void tick(ClientTickEvent.Post event){
        var mc=Minecraft.getInstance();while(PLAY.consumeClick())if(mc.screen==null && mc.player!=null && mc.player.getVehicle() instanceof LaunchAnimationVehicle)PacketDistributor.sendToServer(new LaunchAnimationPayload());
    }
}
