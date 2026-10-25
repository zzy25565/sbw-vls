package cn.zzy.vehicles.client;
import cn.zzy.vehicles.entity.LaunchAnimationVehicle;
import cn.zzy.vehicles.mechanics.LaunchSpec;
import org.joml.*;
import java.lang.Math;

/** 相同单元的所有材质共享一个盖板动作，随后由平台原有的俯仰/水平机构带动。 */
public final class LaunchCapAnimation {
    public static Matrix4f transform(LaunchAnimationVehicle v,int cell,float partial){
        float t=v.launchAnimationAge(partial);if(cell<0 || cell!=v.launchCell() || t<0 || t>=80)return new Matrix4f();
        var c=LaunchSpec.get(v.modelId()).cells().get(cell);
        if(c.capKind().equals("hinge")){
            float open=LauncherAnimation.smooth(t,0,10)*(1-LauncherAnimation.smooth(t,60,80));var h=c.hinge();
            return new Matrix4f().translation((float)h.x,(float)h.y,(float)h.z).rotateX((float)Math.toRadians(c.angle()*open)).translate((float)-h.x,(float)-h.y,(float)-h.z);
        }
        if(t>=18)return null;var d=c.direction();var p=c.mouth();
        float distance=.045f*t*t;float side=(cell%2==0?-1:1)*.07f*t;
        return new Matrix4f().translation((float)(d.x*distance)+side,(float)(d.y*distance),(float)(d.z*distance)).translate((float)p.x,(float)p.y,(float)p.z).rotateZ(t*.075f).translate((float)-p.x,(float)-p.y,(float)-p.z);
    }
    private LaunchCapAnimation(){}
}
