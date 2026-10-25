package cn.zzy.vehicles.mechanics;
import com.google.gson.*;
import org.joml.Vector3d;
import java.io.*;
import java.nio.charset.StandardCharsets;

/** 服务端与 Blender 共用交付参数。鞍座位置和轴距按原模型拟合，单位米。 */
public final class TrailerSpec {
    public static final Vector3d HITCH;
    public static final double AXLE_DISTANCE;
    public static final float LIMIT;
    static {
        try (InputStream stream=TrailerSpec.class.getResourceAsStream("/assets/zzyvehicles/animations/launcher_profiles.json")) {
            if(stream==null) throw new IOException("Missing launcher profiles");
            JsonObject p=JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject()
                    .getAsJsonObject("s400_launcher").getAsJsonObject("articulation");
            JsonArray h=p.getAsJsonArray("hitch"); HITCH=new Vector3d(h.get(0).getAsDouble(),h.get(1).getAsDouble(),h.get(2).getAsDouble());
            AXLE_DISTANCE=p.get("axle_distance").getAsDouble(); LIMIT=(float)Math.toRadians(p.get("limit_degrees").getAsFloat());
        } catch(IOException ex) { throw new ExceptionInInitializerError(ex); }
    }
    private TrailerSpec() {}
}
