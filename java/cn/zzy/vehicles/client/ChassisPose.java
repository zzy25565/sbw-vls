package cn.zzy.vehicles.client;

import cn.zzy.vehicles.entity.PlatformVehicle;
import cn.zzy.vehicles.entity.S400LauncherEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.joml.*;
import java.lang.Math;
import java.util.*;

/** 悬挂仅驱动外观；承重/牵引/爬坡仍由卓越前线物理负责。长度单位为格，角度为弧度。 */
public final class ChassisPose {
    public static final class WheelState {
        public double distance, previousDistance;
        public float compression, previousCompression;
        private Vector3d lastWorldPosition;
        public float compression(float partial) { return Mth.lerp(partial, previousCompression, compression); }
        public double distance(float partial) { return Mth.lerp((double) partial, previousDistance, distance); }
    }
    private final Map<String, WheelState> states = new HashMap<>();
    private final Map<String, List<MeshAsset.Wheel>> sideWheels = new HashMap<>();
    private MeshAsset lastAsset;
    private Vec3 lastPosition;
    private float lastYaw;
    private int tick = Integer.MIN_VALUE;
    public WheelState state(String id) { return states.computeIfAbsent(id, key -> new WheelState()); }

    public void update(PlatformVehicle vehicle, MeshAsset asset) {
        if (asset != lastAsset) {
            lastAsset = asset; sideWheels.clear();
            for (String side : List.of("L", "R")) sideWheels.put(side, asset.wheels().values().stream()
                    .filter(w -> w.road() && w.side().equals(side)).sorted(Comparator.comparingDouble(w -> w.pivot().z)).toList());
        }
        if (tick == vehicle.tickCount) return; tick = vehicle.tickCount;
        Vec3 position = vehicle.position(); double distance = 0, turn = 0;
        if (lastPosition != null && position.distanceToSqr(lastPosition) < 64) {
            double yaw = Math.toRadians(vehicle.getYRot());
            distance = (position.x - lastPosition.x) * -Math.sin(yaw) + (position.z - lastPosition.z) * Math.cos(yaw);
            turn = Math.toRadians(Mth.wrapDegrees(vehicle.getYRot() - lastYaw));
        }
        lastPosition = position; lastYaw = vehicle.getYRot();
        Matrix4d transform = vehicle.getVehicleTransform(1f); Matrix4d inverse = new Matrix4d(transform).invert();
        for (MeshAsset.Wheel wheel : asset.wheels().values()) {
            WheelState s = state(wheel.id()); s.previousCompression = s.compression; s.previousDistance = s.distance;
            // 用实际行程计算轮子相位；不同轮径各自换算，转向时两侧行程不同。
            s.distance += distance + turn * wheel.pivot().x;
            Matrix4d wheelTransform=new Matrix4d(transform);
            if (vehicle instanceof S400LauncherEntity trailer && wheel.pivot().z<0) {
                wheelTransform.mul(trailer.trailerLocalTransform(1f));
                Vector3d center=wheelTransform.transformPosition(new Vector3d(wheel.pivot()));
                Vector3d forward=wheelTransform.transformDirection(new Vector3d(0,0,1)).normalize();
                // 半挂轮按自己的实际轨迹滚动，不能照抄牵引车的左右轮行程。
                s.distance-=distance+turn*wheel.pivot().x;
                if(s.lastWorldPosition!=null && center.distanceSquared(s.lastWorldPosition)<64)
                    s.distance+=new Vector3d(center).sub(s.lastWorldPosition).dot(forward);
                s.lastWorldPosition=center;
            }
            Matrix4d wheelInverse=new Matrix4d(wheelTransform).invert();
            if (!wheel.road()) continue;
            // 履带负重轮的轮缘高于履带接地面，保留源模型的这段厚度，避免平地时整带下沉。
            float clearance = Math.max(0, wheel.pivot().y - wheel.radius());
            Vector3d p = wheelTransform.transformPosition(new Vector3d(wheel.pivot().x, wheel.pivot().y - wheel.radius() - clearance, wheel.pivot().z));
            Vec3 top = new Vec3(p.x, p.y + wheel.travel() + .35, p.z), bottom = new Vec3(p.x, p.y - wheel.travel() - .35, p.z);
            BlockHitResult hit = vehicle.level().clip(new ClipContext(top, bottom, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, vehicle));
            float target = -wheel.travel();
            if (hit.getType() != HitResult.Type.MISS) {
                Vector3d local = wheelInverse.transformPosition(new Vector3d(hit.getLocation().x, hit.getLocation().y, hit.getLocation().z));
                target = Mth.clamp((float) local.y + wheel.radius() + clearance - wheel.pivot().y, -wheel.travel(), wheel.travel());
            }
            s.compression += (target - s.compression) * .4f;
        }
    }
    public double sideDistance(MeshAsset asset, String side, float partial) {
        List<MeshAsset.Wheel> wheels = sideWheels.get(side); if (wheels == null || wheels.isEmpty()) return 0;
        double sum = 0; for (MeshAsset.Wheel wheel : wheels) sum += state(wheel.id()).distance(partial);
        return sum / wheels.size();
    }
    /** 下履带按相邻负重轮插值，上履带保留原始下垂；不把整条履带当刚体旋转。 */
    public float beltOffset(MeshAsset asset, String side, float y, float z, float partial) {
        List<MeshAsset.Wheel> wheels = sideWheels.get(side);
        if (wheels == null || wheels.isEmpty()) return 0;
        MeshAsset.Wheel left = wheels.get(0), right = wheels.get(wheels.size() - 1);
        if (z < left.pivot().z - left.radius() || z > right.pivot().z + right.radius()) return 0;
        float endFade = Mth.clamp((z - left.pivot().z + left.radius()) / left.radius(), 0, 1) * Mth.clamp((right.pivot().z + right.radius() - z) / right.radius(), 0, 1);
        if (z <= left.pivot().z) right = left;
        else if (z >= right.pivot().z) left = right;
        else for (int i = 0; i + 1 < wheels.size(); i++) {
            if (z >= wheels.get(i).pivot().z && z <= wheels.get(i + 1).pivot().z) { left = wheels.get(i); right = wheels.get(i + 1); break; }
        }
        float t = Mth.clamp((z - left.pivot().z) / Math.max(.001f, right.pivot().z - left.pivot().z), 0, 1);
        float bottomWeight = 1 - Mth.clamp((y - .12f) / .55f, 0, 1);
        return Mth.lerp(t, state(left.id()).compression(partial), state(right.id()).compression(partial)) * bottomWeight * endFade;
    }
}
