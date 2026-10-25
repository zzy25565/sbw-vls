package cn.zzy.vehicles.client;
import cn.zzy.vehicles.entity.*;
import cn.zzy.vehicles.mechanics.LaunchSpec;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import org.joml.*;
import java.lang.Math;

/** 打开舱盖后露出深色筒口，避免原始封闭模型露出白色实心顶面。 */
final class LaunchMouthRenderer {
    static void draw(PlatformVehicle entity,float partial,PoseStack stack,MultiBufferSource buffers){
        if(!(entity instanceof LaunchAnimationVehicle v))return;float t=v.launchAnimationAge(partial);if(t<5 || t>=76)return;
        var cell=LaunchSpec.get(v.modelId()).cells().get(v.launchCell());var p=cell.mouth();var dir=new Vector3f((float)cell.direction().x,(float)cell.direction().y,(float)cell.direction().z);
        stack.pushPose();if(v instanceof BukM3Entity b)stack.mulPose(new Matrix4f(b.launcherYawTransform(partial)));if(v instanceof S400LauncherEntity s)stack.mulPose(new Matrix4f(s.trailerLocalTransform(partial)));
        stack.translate(p.x+dir.x*.006,p.y+dir.y*.006,p.z+dir.z*.006);stack.mulPose(new Quaternionf().rotationTo(new Vector3f(0,1,0),dir));var consumer=buffers.getBuffer(RenderType.debugQuads());
        float radius=v.modelId().equals("s400_launcher")?.47f:v.modelId().equals("buk_m3")?.24f:.31f;
        if(v.modelId().equals("vls")){
            for(float[] a:new float[][]{{-radius,0,-radius},{radius,0,-radius},{radius,0,radius},{-radius,0,radius}})consumer.addVertex(stack.last(),a[0],a[1],a[2]).setColor(13,17,20,255);
        }else for(int i=0;i<24;i++){
            double a=i*Math.PI/12,b=(i+1)*Math.PI/12;
            for(float[] q:new float[][]{{0,0,0},{(float)Math.cos(a)*radius,0,(float)Math.sin(a)*radius},{(float)Math.cos(b)*radius,0,(float)Math.sin(b)*radius},{(float)Math.cos(b)*radius,0,(float)Math.sin(b)*radius}})consumer.addVertex(stack.last(),q[0],q[1],q[2]).setColor(13,17,20,255);
        }
        stack.popPose();
    }
}
