package cn.zzy.vehicles.client;
import com.google.gson.JsonObject;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** 发射架和液压杆的外观机构。局部坐标单位米，原模型展开状态为零。 */
public record LauncherAnimation(Vector3f hinge, float stowDegrees, float rackStart, float rackEnd,
                                Vector3f base, Vector3f tip, float supportLift, JsonObject legs) {
    public static LauncherAnimation read(JsonObject p) {
        return new LauncherAnimation(MeshAsset.vec(p.getAsJsonArray("hinge")), p.get("stow_degrees").getAsFloat(),
                p.get("rack_start").getAsFloat(), p.get("rack_end").getAsFloat(),
                MeshAsset.vec(p.getAsJsonArray("base")), MeshAsset.vec(p.getAsJsonArray("tip")), p.get("support_lift").getAsFloat(), p.has("legs") ? p.getAsJsonObject("legs") : null);
    }
    static float smooth(float p, float a, float b) {
        float t = Math.max(0, Math.min(1, (p-a)/(b-a))); return t*t*(3-2*t);
    }
    public Matrix4f transform(String role, float progress) {
        if (role.startsWith("leg_") && legs!=null) {
            String id=role.substring(4,6);JsonObject leg=legs.getAsJsonObject(id);
            Vector3f pivot=MeshAsset.vec(leg.getAsJsonArray("pivot"));
            float fold=(float)Math.toRadians(leg.get("fold_degrees").getAsFloat())*(1-smooth(progress,0,.10f));
            Matrix4f m=new Matrix4f().translation(pivot).rotateY(fold).rotateZ((float)Math.toRadians(leg.get("lift_degrees").getAsFloat())*(1-smooth(progress,0,.10f))).translate(-pivot.x,-pivot.y,-pivot.z);
            // 先转出支臂再放脚；收回时先提脚，最后将斜撑与支臂一并抬起折入车架。
            if (role.endsWith("_lower")) m.translate(0,supportLift*(1-smooth(progress,.10f,.18f)),0);
            return m;
        }
        float angle = (float) Math.toRadians(stowDegrees)*(1-smooth(progress,rackStart,rackEnd));
        Matrix4f rack = new Matrix4f().translation(hinge).rotateX(angle).translate(-hinge.x,-hinge.y,-hinge.z);
        if (role.equals("rack")) return rack;
        if (role.equals("support")) return new Matrix4f().translation(0,supportLift*(1-smooth(progress,0,.18f)),0);
        if (!role.equals("barrel") && !role.equals("piston")) return new Matrix4f();
        Vector3f movingTip = rack.transformPosition(new Vector3f(tip));
        Quaternionf rotation = new Quaternionf().rotationTo(new Vector3f(tip).sub(base).normalize(),new Vector3f(movingTip).sub(base).normalize());
        // 缸筒绕底端转向，活塞跟随上铰点；金属网格不拉长、直径不缩放。
        Vector3f anchor = role.equals("barrel") ? base : movingTip;
        Vector3f old = role.equals("barrel") ? base : tip;
        return new Matrix4f().translation(anchor).rotate(rotation).translate(-old.x,-old.y,-old.z);
    }
}
