package cn.zzy.vehicles.mechanics;
import com.google.gson.*;
import org.joml.Vector3d;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** 各发射单元的位置、盖板与演出时间；单位米和游戏刻，不参与弹道或伤害计算。 */
public record LaunchSpec(List<Cell> cells,double length,int ejectionTicks,double ejectionSpeed,int launchTick,int cycleTicks) {
    public record Cell(Vector3d mouth,Vector3d direction,String capKind,Vector3d hinge,float angle) {}
    private static final Map<String,LaunchSpec> SPECS=load();
    private static Vector3d vec(JsonArray a){return new Vector3d(a.get(0).getAsDouble(),a.get(1).getAsDouble(),a.get(2).getAsDouble());}
    private static Map<String,LaunchSpec> load(){
        try(InputStream in=LaunchSpec.class.getResourceAsStream("/assets/zzyvehicles/animations/launch_profiles.json")){
            if(in==null)throw new IOException("Missing launch profiles");Map<String,LaunchSpec> map=new HashMap<>();
            for(var e:JsonParser.parseReader(new InputStreamReader(in,StandardCharsets.UTF_8)).getAsJsonObject().entrySet()){
                JsonObject p=e.getValue().getAsJsonObject();List<Cell> cells=new ArrayList<>();
                for(var item:p.getAsJsonArray("cells")){
                    var c=item.getAsJsonObject();cells.add(new Cell(vec(c.getAsJsonArray("mouth")),vec(c.getAsJsonArray("direction")).normalize(),c.get("cap_kind").getAsString(),c.has("hinge")?vec(c.getAsJsonArray("hinge")):new Vector3d(),c.has("angle_degrees")?c.get("angle_degrees").getAsFloat():0));
                }
                map.put(e.getKey(),new LaunchSpec(List.copyOf(cells),p.get("missile_length").getAsDouble(),p.get("ejection_ticks").getAsInt(),p.get("ejection_speed").getAsDouble(),p.get("launch_tick").getAsInt(),p.get("cycle_ticks").getAsInt()));
            }return Map.copyOf(map);
        }catch(IOException ex){throw new ExceptionInInitializerError(ex);}
    }
    public static LaunchSpec get(String model){return SPECS.get(model);}
    public static String missileModel(String model){return model.equals("buk_m3")?"buk_visual_missile":model+"_visual_missile";}
    /** 纯演出位移：冷发射先匀速弹出，弹体清筒后再加速。 */
    public double distance(float age){double t=Math.max(0,age);if(t<ejectionTicks)return ejectionSpeed*t;double u=t-ejectionTicks;return ejectionSpeed*ejectionTicks+(ejectionTicks==0?.06:ejectionSpeed)*u+.04*u*u;}
}
