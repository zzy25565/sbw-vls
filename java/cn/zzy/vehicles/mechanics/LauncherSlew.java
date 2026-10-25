package cn.zzy.vehicles.mechanics;
import com.google.gson.*;
import net.minecraft.util.Mth;
import org.joml.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.lang.Math;

/** 转台轴心按模型圆环拟合，米；速度为外观动画参数，度/秒。 */
public final class LauncherSlew {
    public static final Vector3d PIVOT;
    public static final float DEGREES_PER_TICK;
    static {
        try(InputStream in=LauncherSlew.class.getResourceAsStream("/assets/zzyvehicles/animations/launcher_profiles.json")) {
            if(in==null)throw new IOException("Missing launcher profile");
            JsonObject p=JsonParser.parseReader(new InputStreamReader(in,StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject("buk_m3").getAsJsonObject("slew");
            JsonArray v=p.getAsJsonArray("pivot");PIVOT=new Vector3d(v.get(0).getAsDouble(),v.get(1).getAsDouble(),v.get(2).getAsDouble());
            DEGREES_PER_TICK=p.get("degrees_per_second").getAsFloat()/20;
        }catch(IOException e){throw new ExceptionInInitializerError(e);}
    }
    public static float advance(float angle,float target) {
        return Mth.wrapDegrees(angle+Mth.clamp(Mth.wrapDegrees(target-angle),-DEGREES_PER_TICK,DEGREES_PER_TICK));
    }
    public static Matrix4d transform(float degrees) {
        return new Matrix4d().translation(PIVOT).rotateY(Math.toRadians(degrees)).translate(-PIVOT.x,-PIVOT.y,-PIVOT.z);
    }
    private LauncherSlew() {}
}
