package cn.zzy.vehicles.client;
import cn.zzy.vehicles.entity.VisualMissileEntity;
import cn.zzy.vehicles.mechanics.LaunchSpec;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.*;
import java.lang.Math;

/** 渲染独立弹体和全亮尾焰；平台不在画面内时，离架弹体仍可正常显示。 */
public final class VisualMissileRenderer extends EntityRenderer<VisualMissileEntity> {
    public VisualMissileRenderer(EntityRendererProvider.Context c){super(c);shadowRadius=0;}
    @Override public ResourceLocation getTextureLocation(VisualMissileEntity e){return ResourceLocation.withDefaultNamespace("textures/block/iron_block.png");}
    @Override public boolean shouldRender(VisualMissileEntity e,Frustum f,double x,double y,double z){
        var s=LaunchSpec.get(e.sourceModel());if(s==null)return false;var p=e.tailPosition(1);var q=new Vector3d(p).add(e.direction().mul(s.length()));return e.shouldRenderAtSqrDistance(e.distanceToSqr(x,y,z)) && f.isVisible(new AABB(p.x,p.y,p.z,q.x,q.y,q.z).inflate(3));
    }
    @Override public void render(VisualMissileEntity e,float yaw,float partial,PoseStack stack,MultiBufferSource buffers,int light){
        var asset=ClientAssets.get(LaunchSpec.missileModel(e.sourceModel()));if(asset==null)return;var dir=e.direction();if(dir.lengthSquared()<.1)return;
        stack.pushPose();var p=e.tailPosition(partial);double x=net.minecraft.util.Mth.lerp((double)partial,e.xo,e.getX()),y=net.minecraft.util.Mth.lerp((double)partial,e.yo,e.getY()),z=net.minecraft.util.Mth.lerp((double)partial,e.zo,e.getZ());
        stack.translate(p.x-x,p.y-y,p.z-z);stack.mulPose(new Quaternionf().rotationTo(new Vector3f(0,1,0),new Vector3f((float)dir.x,(float)dir.y,(float)dir.z)));
        for(var part:asset.parts()){
            var consumer=buffers.getBuffer(RenderType.entityCutoutNoCull(part.texture()));float[] a=part.vertices();
            for(int tri=0;tri<a.length;tri+=24)for(int c=0;c<4;c++){
                int i=tri+Math.min(c,2)*8;consumer.addVertex(stack.last(),a[i],a[i+1],a[i+2]).setColor(255,255,255,255).setUv(a[i+6],a[i+7]).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(stack.last(),a[i+3],a[i+4],a[i+5]);
            }
        }
        if(e.engineLit()){
            var consumer=buffers.getBuffer(RenderType.lightning());float radius=e.sourceModel().equals("buk_m3")?.23f:.3f;
            flame(stack,consumer,radius,2.2f+(.3f*(float)Math.sin((e.flightAge(partial))*3)),255,125,20,190);
            flame(stack,consumer,radius*.55f,1.5f,255,247,190,240);
        }
        stack.popPose();super.render(e,yaw,partial,stack,buffers,light);
    }
    private static void flame(PoseStack stack,VertexConsumer c,float r,float length,int red,int green,int blue,int alpha){
        for(int i=0;i<12;i++){
            double a=i*Math.PI/6,b=(i+1)*Math.PI/6;float[][] v={{(float)Math.cos(a)*r,0,(float)Math.sin(a)*r},{(float)Math.cos(b)*r,0,(float)Math.sin(b)*r},{0,-length,0},{0,-length,0}};
            for(float[] p:v)c.addVertex(stack.last(),p[0],p[1],p[2]).setColor(red,green,blue,alpha);
        }
    }
}
