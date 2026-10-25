package cn.zzy.vehicles.mechanics;

/** 单位米/弧度。无侧滑半挂的平面运动近似；不创建第二个物理实体。 */
public final class TrailerKinematics {
    private TrailerKinematics() {}
    /** angle = 牵引车航向 - 半挂航向；距离为鞍座实际位移，倒车无需另设方向开关。 */
    public static float advance(float angle, float previousYaw, float yawDelta, double dx, double dz, double axleDistance, float limit) {
        double a=angle;
        for (int i=0;i<8;i++) {
            a+=yawDelta/8.0;
            double trailerYaw=previousYaw+yawDelta*(i+1)/8.0-a;
            a+=(dx*Math.cos(trailerYaw)+dz*Math.sin(trailerYaw))/(8*axleDistance);
            a=Math.max(-limit,Math.min(limit,a));
        }
        return (float)a;
    }
}
