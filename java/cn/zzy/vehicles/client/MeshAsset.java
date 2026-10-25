package cn.zzy.vehicles.client;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Vector3f;
import java.io.*;
import java.util.*;

/** 标准 Bedrock poly_mesh + 每骨骼材质/轮组元数据；不体素化、不减面。 */
public record MeshAsset(List<Part> parts, Map<String, Wheel> wheels, LauncherAnimation launcher) {
    public record Wheel(String id, String side, int index, boolean steer, boolean road, Vector3f pivot, float radius, float travel) {}
    public record Part(String name, String role, ResourceLocation texture, boolean translucent,
                       Vector3f pivot, Wheel wheel, String trackSide, int uvAxis, float uvPerMeter, String launcherRole, boolean trailer, boolean slew, int launchCell, float[] vertices) {}
    public static Vector3f vec(JsonArray a) { return new Vector3f(a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat()); }
    public static MeshAsset load(ResourceManager manager, String name) throws IOException {
        ResourceLocation path = ResourceLocation.fromNamespaceAndPath("zzyvehicles", "models/bedrock/vehicle/" + name + ".geo.json");
        try (Reader reader = manager.openAsReader(path)) {
            JsonArray bones = JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject().getAsJsonArray("bones");
            List<Part> parts = new ArrayList<>(); Map<String, Wheel> wheels = new LinkedHashMap<>();
            for (JsonElement element : bones) {
                JsonObject b = element.getAsJsonObject(); if (!b.has("poly_mesh")) continue;
                JsonObject mesh = b.getAsJsonObject("poly_mesh"), material = b.getAsJsonObject("zzy_material");
                Wheel wheel = null;
                if (b.has("zzy_wheel")) {
                    JsonObject w = b.getAsJsonObject("zzy_wheel"); String id = w.get("id").getAsString();
                    wheel = new Wheel(id, w.get("side").getAsString(), w.get("index").getAsInt(), w.get("steer").getAsBoolean(), w.get("road").getAsBoolean(), vec(w.getAsJsonArray("pivot")), w.get("radius").getAsFloat(), w.get("travel").getAsFloat());
                    wheels.put(id, wheel);
                }
                JsonArray pos = mesh.getAsJsonArray("positions"), norms = mesh.getAsJsonArray("normals"), uvs = mesh.getAsJsonArray("uvs"), polys = mesh.getAsJsonArray("polys");
                float[] vertices = new float[polys.size() * 3 * 8]; int offset = 0;
                for (JsonElement polygon : polys) {
                    JsonArray triangle = polygon.getAsJsonArray();
                    if (triangle.size() != 3) throw new IOException("Expected triangulated poly_mesh: " + name);
                    for (JsonElement corner : triangle) {
                        JsonArray indices = corner.getAsJsonArray();
                        Vector3f p = vec(pos.get(indices.get(0).getAsInt()).getAsJsonArray()).div(16);
                        Vector3f n = vec(norms.get(indices.get(1).getAsInt()).getAsJsonArray());
                        JsonArray uv = uvs.get(indices.get(2).getAsInt()).getAsJsonArray();
                        vertices[offset++] = p.x; vertices[offset++] = p.y; vertices[offset++] = p.z;
                        vertices[offset++] = n.x; vertices[offset++] = n.y; vertices[offset++] = n.z;
                        vertices[offset++] = uv.get(0).getAsFloat(); vertices[offset++] = uv.get(1).getAsFloat();
                    }
                }
                String trackSide = b.has("zzy_track") ? b.getAsJsonObject("zzy_track").get("side").getAsString() : "";
                int uvAxis = b.has("zzy_track") ? b.getAsJsonObject("zzy_track").get("uv_axis").getAsInt() : 1;
                float uvPerMeter = b.has("zzy_track") ? b.getAsJsonObject("zzy_track").get("uv_per_meter").getAsFloat() : 0;
                parts.add(new Part(b.get("name").getAsString(), b.get("zzy_role").getAsString(), ResourceLocation.parse(material.get("texture").getAsString()), material.get("translucent").getAsBoolean(), vec(b.getAsJsonArray("pivot")).div(16), wheel, trackSide, uvAxis, uvPerMeter, b.has("zzy_launcher") ? b.get("zzy_launcher").getAsString() : "", b.has("zzy_articulation") && b.get("zzy_articulation").getAsString().equals("trailer"), b.has("zzy_slew") && b.get("zzy_slew").getAsBoolean(), b.has("zzy_launch_cell") ? b.get("zzy_launch_cell").getAsInt() : -1, vertices));
            }
            LauncherAnimation launcher = null;
            if (name.equals("buk_m3") || name.equals("s400_launcher")) {
                try (Reader profile = manager.openAsReader(ResourceLocation.fromNamespaceAndPath("zzyvehicles", "animations/launcher_profiles.json"))) {
                    launcher = LauncherAnimation.read(JsonParser.parseReader(profile).getAsJsonObject().getAsJsonObject(name));
                }
            }
            return new MeshAsset(List.copyOf(parts), Collections.unmodifiableMap(wheels), launcher);
        }
    }
}
