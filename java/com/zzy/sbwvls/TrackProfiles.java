package com.zzy.sbwvls;
import net.minecraft.util.Mth;
/** Recovered numeric curves; arithmetic corrections documented in restore_track_profiles.py. */
public final class TrackProfiles {
    public interface Curve {
        float getBoneRotX(float t); float getBoneMoveY(float t); float getBoneMoveZ(float t);
        default float getTrackDistance(){return 2;} default int period(){return 100;}
        default boolean hideForTurretControllerWhileZooming(){return false;}
    }
    public static Curve forId(String id){return switch(id){
        case "abrams_x" -> new AbramsXRenderer();
        case "hyr0" -> new HYR0Renderer();
        case "m10booker" -> new M10BookerRenderer();
        case "m1a1hc" -> new M1a1hcRenderer();
        case "m1a2sepv1" -> new M1a2sepv1Renderer();
        case "m1a2sepv2" -> new M1A2SEPV2Renderer();
        case "rail_tank" -> new RailTankRenderer();
        case "t72b3" -> new T72b3Renderer();
        case "t80b" -> new T80BRenderer();
        case "t90_msa" -> new T90MSRenderer();
        case "t90_msa_keep" -> new T90MSRenderer();
        case "zbd04a" -> new ZBD04ARenderer();
        case "zbd05" -> new Zbd05Renderer();
        case "ztd05" -> new Ztd05Renderer();
        case "ztz59a" -> new ZTZ59ARenderer();
        case "ztz96a" -> new Ztz96aRenderer();
        case "ztz99a" -> new ZTZ99ASbmRenderer();
        case "aavp" -> new AAVPModel();
        case "bmp1" -> new BMP1Model();
        case "bmp1p" -> new BMP1PModel();
        case "bmp1u" -> new BMP1UModel();
        case "bmp2" -> new BMP2Model();
        case "bmp2_noatgm" -> new BMP2NoAtgmModel();
        case "bmp2d" -> new BMP2DModel();
        case "bmp2m" -> new BMP2MModel();
        case "bmp2md" -> new BMP2MDModel();
        case "m109" -> new M109Model();
        case "msta" -> new MstaModel();
        case "ajax" -> new AjaxModel();
        case "bmp_2_bakhcha" -> new Bmp2BakhcaModel();
        case "bmpt_3k" -> new BMPT3KModel();
        case "bradley" -> new BradleyModel();
        case "cv90" -> new CV90Model();
        case "leopard_2a7v" -> new Leopard2A7VModel();
        case "oplot" -> new OplotModel();
        case "puma" -> new PumaModel();
        case "t72_b3m" -> new T72B3MModel();
        case "terminator" -> new TerminatorModel();
        case "m3a3" -> new M3A3Renderer();
        default -> null;
    };}
    private static final class AAVPModel implements Curve {
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{175.86f, 16.04f, 50.24f}, {128.32f, 14.3f, 53.97f}, {79.11f, 10.34f, 55.04f}, {29.89f, 6.96f, 52.7f}, {26.76f, 5.09f, 48.89f}, {26.76f, 3.18f, 45.1f}, {26.76f, 1.27f, 41.31f}, {0.0f, 0.59f, 37.19f}, {0.0f, 0.59f, 32.94f}, {0.0f, 0.59f, 28.7f}, {0.0f, 0.59f, 24.46f}, {0.0f, 0.59f, 20.21f}, {0.0f, 0.59f, 15.97f}, {0.0f, 0.59f, 11.72f}, {0.0f, 0.59f, 7.48f}, {0.0f, 0.59f, 3.23f}, {0.0f, 0.59f, -1.01f}, {0.0f, 0.59f, -5.25f}, {0.0f, 0.59f, -9.5f}, {0.0f, 0.59f, -13.74f}, {0.0f, 0.59f, -17.99f}, {0.0f, 0.59f, -22.23f}, {-0.59f, 0.59f, -26.48f}, {-32.52f, 1.88f, -30.43f}, {-31.58f, 4.1f, -34.05f}, {-31.58f, 6.32f, -37.66f}, {-45.03f, 8.7f, -41.16f}, {-84.29f, 12.5f, -42.88f}, {-123.56f, 16.56f, -41.95f}, {-157.91f, 19.21f, -38.73f}, {178.45f, 19.55f, -34.54f}, {177.12f, 19.33f, -30.3f}, {177.12f, 19.12f, -26.06f}, {177.12f, 18.91f, -21.83f}, {177.12f, 18.69f, -17.59f}, {177.12f, 18.48f, -13.35f}, {177.12f, 18.27f, -9.11f}, {177.12f, 18.05f, -4.87f}, {177.12f, 17.84f, -0.63f}, {177.12f, 17.63f, 3.61f}, {179.28f, 17.42f, 7.85f}, {-180.0f, 17.42f, 12.09f}, {-180.0f, 17.42f, 16.34f}, {-180.0f, 17.42f, 20.58f}, {-180.0f, 17.42f, 24.82f}, {178.93f, 17.42f, 29.07f}, {176.8f, 17.3f, 33.31f}, {175.73f, 16.99f, 37.54f}, {175.73f, 16.67f, 41.77f}, {175.73f, 16.36f, 46.01f}, {175.86f, 16.04f, 50.24f}};
    private static final float START_Y = 16.04f;
    private static final float START_Z = 50.24f;
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)50);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 16.04f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 50.24f;
    }
        public int period(){return 100;}
    }
    private static final class AbramsXRenderer implements Curve {
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    public float getBoneRotX(float t) {
        if (t <= 41.25f) {
            return 0.0f;
        }
        if (t <= 47.25f) {
            return Mth.lerp((float)((t - 41.25f) / 6.0f), (float)0.0f, (float)-147.5f);
        }
        if (t <= 53.9167f) {
            return -147.5f;
        }
        if (t <= 54.5833f) {
            return Mth.lerp((float)((t - 53.9167f) / 0.6666031f), (float)-147.5f, (float)-180.0f);
        }
        if (t <= 85.5f) {
            return -180.0f;
        }
        if (t <= 85.9167f) {
            return -180.0f;
        }
        if (t <= 86.4167f) {
            return Mth.lerp((float)((t - 85.9167f) / 0.5f), (float)-180.0f, (float)-205.0f);
        }
        if (t <= 93.6667f) {
            return -205.0f;
        }
        if (t <= 100.0f) {
            return Mth.lerp((float)((t - 93.6667f) / (100.0f - 93.6667f)), (float)-205.0f, (float)-360.0f);
        }
        return 0.0f;
    }
    public float getBoneMoveY(float t) {
        if (t <= 41.75f) {
            return 0.0f;
        }
        if (t <= 42.5833f) {
            return Mth.lerp((float)((t - 41.75f) / 0.83330154f), (float)0.0f, (float)-0.72f);
        }
        if (t <= 43.5f) {
            return Mth.lerp((float)((t - 42.5833f) / 0.91669846f), (float)-0.72f, (float)-2.175f);
        }
        if (t <= 44.3333f) {
            return Mth.lerp((float)((t - 43.5f) / 0.83330154f), (float)-2.175f, (float)-5.01f);
        }
        if (t <= 45.25f) {
            return Mth.lerp((float)((t - 44.3333f) / 0.91669846f), (float)-5.01f, (float)-7.8f);
        }
        if (t <= 46.0833f) {
            return Mth.lerp((float)((t - 45.25f) / 0.83330154f), (float)-7.8f, (float)-10.6f);
        }
        if (t <= 46.9167f) {
            return Mth.lerp((float)((t - 46.0833f) / 0.8333969f), (float)-10.6f, (float)-13.245f);
        }
        if (t <= 47.8333f) {
            return Mth.lerp((float)((t - 46.9167f) / 0.9166031f), (float)-13.245f, (float)-14.81f);
        }
        if (t <= 53.5833f) {
            return Mth.lerp((float)((t - 47.8333f) / 5.75f), (float)-14.81f, (float)-24.64f);
        }
        if (t <= 54.25f) {
            return Mth.lerp((float)((t - 53.5833f) / 0.66669846f), (float)-24.64f, (float)-25.39f);
        }
        if (t <= 54.9167f) {
            return Mth.lerp((float)((t - 54.25f) / 0.66669846f), (float)-25.39f, (float)-25.74f);
        }
        if (t <= 84.9167f) {
            return Mth.lerp((float)((t - 54.9167f) / 30.000004f), (float)-25.74f, (float)-25.73f);
        }
        if (t <= 85.6667f) {
            return Mth.lerp((float)((t - 84.9167f) / 0.75f), (float)-25.73f, (float)-25.33f);
        }
        if (t <= 86.4167f) {
            return Mth.lerp((float)((t - 85.6667f) / 0.75f), (float)-25.33f, (float)-24.72f);
        }
        if (t <= 93.1667f) {
            return Mth.lerp((float)((t - 86.4167f) / 6.75f), (float)-24.72f, (float)-15.75f);
        }
        if (t <= 93.9167f) {
            return Mth.lerp((float)((t - 93.1667f) / 0.75f), (float)-15.75f, (float)-14.53f);
        }
        if (t <= 94.5833f) {
            return Mth.lerp((float)((t - 93.9167f) / 0.66659546f), (float)-14.53f, (float)-12.57f);
        }
        if (t <= 95.25f) {
            return Mth.lerp((float)((t - 94.5833f) / 0.6667023f), (float)-12.57f, (float)-10.62f);
        }
        if (t <= 95.9167f) {
            return Mth.lerp((float)((t - 95.25f) / 0.6667023f), (float)-10.62f, (float)-8.48f);
        }
        if (t <= 96.6667f) {
            return Mth.lerp((float)((t - 95.9167f) / 0.75f), (float)-8.48f, (float)-6.2f);
        }
        if (t <= 97.25f) {
            return Mth.lerp((float)((t - 96.6667f) / 0.5832977f), (float)-6.2f, (float)-4.35f);
        }
        if (t <= 97.8333f) {
            return Mth.lerp((float)((t - 97.25f) / 0.5832977f), (float)-4.35f, (float)-2.68f);
        }
        if (t <= 98.5f) {
            return Mth.lerp((float)((t - 97.8333f) / 0.6667023f), (float)-2.68f, (float)-1.26f);
        }
        if (t <= 98.8333f) {
            return Mth.lerp((float)((t - 98.5f) / 0.33329773f), (float)-1.26f, (float)-0.68f);
        }
        if (t <= 99.25f) {
            return Mth.lerp((float)((t - 98.8333f) / 0.41670227f), (float)-0.68f, (float)-0.3f);
        }
        if (t <= 99.5833f) {
            return Mth.lerp((float)((t - 99.25f) / 0.33329773f), (float)-0.3f, (float)0.0f);
        }
        return 0.0f;
    }
    public float getBoneMoveZ(float t) {
        if (t <= 41.75f) {
            return Mth.lerp((float)(t / 41.75f), (float)0.0f, (float)126.5f);
        }
        if (t <= 42.5833f) {
            return Mth.lerp((float)((t - 41.75f) / 0.83330154f), (float)126.5f, (float)129.37f);
        }
        if (t <= 43.5f) {
            return Mth.lerp((float)((t - 42.5833f) / 0.91669846f), (float)129.37f, (float)131.74f);
        }
        if (t <= 44.3333f) {
            return Mth.lerp((float)((t - 43.5f) / 0.83330154f), (float)131.74f, (float)133.605f);
        }
        if (t <= 45.25f) {
            return Mth.lerp((float)((t - 44.3333f) / 0.91669846f), (float)133.605f, (float)134.085f);
        }
        if (t <= 46.0833f) {
            return Mth.lerp((float)((t - 45.25f) / 0.83330154f), (float)134.085f, (float)133.565f);
        }
        if (t <= 46.9167f) {
            return Mth.lerp((float)((t - 46.0833f) / 0.8333969f), (float)133.565f, (float)131.6f);
        }
        if (t <= 47.8333f) {
            return Mth.lerp((float)((t - 46.9167f) / 0.9166031f), (float)131.6f, (float)129.55f);
        }
        if (t <= 53.5833f) {
            return Mth.lerp((float)((t - 47.8333f) / 5.75f), (float)129.55f, (float)114.11f);
        }
        if (t <= 54.25f) {
            return Mth.lerp((float)((t - 53.5833f) / 0.66669846f), (float)114.11f, (float)112.38f);
        }
        if (t <= 54.9167f) {
            return Mth.lerp((float)((t - 54.25f) / 0.66669846f), (float)112.38f, (float)110.33f);
        }
        if (t <= 84.9167f) {
            return Mth.lerp((float)((t - 54.9167f) / 30.000004f), (float)110.33f, (float)16.99f);
        }
        if (t <= 85.6667f) {
            return Mth.lerp((float)((t - 84.9167f) / 0.75f), (float)16.99f, (float)14.71f);
        }
        if (t <= 86.4167f) {
            return Mth.lerp((float)((t - 85.6667f) / 0.75f), (float)14.71f, (float)12.51f);
        }
        if (t <= 93.1667f) {
            return Mth.lerp((float)((t - 86.4167f) / 6.75f), (float)12.51f, (float)-6.97f);
        }
        if (t <= 93.9167f) {
            return Mth.lerp((float)((t - 93.1667f) / 0.75f), (float)-6.97f, (float)-8.64f);
        }
        if (t <= 94.5833f) {
            return Mth.lerp((float)((t - 93.9167f) / 0.66659546f), (float)-8.64f, (float)-10.07f);
        }
        if (t <= 95.25f) {
            return Mth.lerp((float)((t - 94.5833f) / 0.6667023f), (float)-10.07f, (float)-10.86f);
        }
        if (t <= 95.9167f) {
            return Mth.lerp((float)((t - 95.25f) / 0.6667023f), (float)-10.86f, (float)-11.18f);
        }
        if (t <= 96.6667f) {
            return Mth.lerp((float)((t - 95.9167f) / 0.75f), (float)-11.18f, (float)-10.8f);
        }
        if (t <= 97.25f) {
            return Mth.lerp((float)((t - 96.6667f) / 0.5832977f), (float)-10.8f, (float)-10.05f);
        }
        if (t <= 97.8333f) {
            return Mth.lerp((float)((t - 97.25f) / 0.5832977f), (float)-10.05f, (float)-8.695f);
        }
        if (t <= 98.5f) {
            return Mth.lerp((float)((t - 97.8333f) / 0.6667023f), (float)-8.695f, (float)-6.38f);
        }
        if (t <= 98.8333f) {
            return Mth.lerp((float)((t - 98.5f) / 0.33329773f), (float)-1.26f, (float)-0.68f);
        }
        if (t <= 99.25f) {
            return Mth.lerp((float)((t - 98.8333f) / 0.41670227f), (float)-0.68f, (float)-0.3f);
        }
        if (t <= 99.5833f) {
            return Mth.lerp((float)((t - 99.25f) / 0.33329773f), (float)-0.3f, (float)0.0f);
        }
        return Mth.lerp((float)((t - 99.5833f) / 0.41670227f), (float)-1.69f, (float)0.0f);
    }
    public float getTrackDistance() {
        return 2.27f;
    }
        public int period(){return 100;}
    }
    private static final class AjaxModel implements Curve {
    private static final int TRACK_COUNT = 56;
    private static final int MAX_IDX = 56;
    private static final float[][] KEYFRAMES = new float[][]{{176.95f, 18.5f, 47.25f}, {143.37f, 17.21f, 50.96f}, {104.31f, 13.89f, 53.08f}, {65.24f, 9.98f, 52.68f}, {29.22f, 7.14f, 49.96f}, {28.61f, 5.23f, 46.44f}, {28.61f, 3.31f, 42.92f}, {28.01f, 1.39f, 39.4f}, {0.0f, 0.5f, 35.55f}, {0.0f, 0.5f, 31.54f}, {0.0f, 0.5f, 27.53f}, {0.0f, 0.5f, 23.53f}, {0.0f, 0.5f, 19.52f}, {0.0f, 0.5f, 15.51f}, {0.0f, 0.5f, 11.5f}, {0.0f, 0.5f, 7.49f}, {0.0f, 0.5f, 3.48f}, {0.0f, 0.5f, -0.53f}, {0.0f, 0.5f, -4.54f}, {0.0f, 0.5f, -8.54f}, {0.0f, 0.5f, -12.55f}, {0.0f, 0.5f, -16.56f}, {0.0f, 0.5f, -20.57f}, {0.0f, 0.5f, -24.58f}, {0.0f, 0.5f, -28.59f}, {0.0f, 0.5f, -32.6f}, {-7.35f, 0.57f, -36.6f}, {-42.8f, 2.48f, -40.01f}, {-42.8f, 5.21f, -42.95f}, {-42.8f, 7.93f, -45.89f}, {-41.56f, 10.56f, -48.9f}, {-90.13f, 14.1f, -50.5f}, {-143.57f, 17.56f, -48.75f}, {-178.04f, 18.49f, -44.95f}, {-179.93f, 18.5f, -40.94f}, {-180.0f, 18.5f, -36.93f}, {-180.0f, 18.5f, -32.93f}, {-180.0f, 18.5f, -28.92f}, {-180.0f, 18.5f, -24.91f}, {-180.0f, 18.5f, -20.9f}, {-180.0f, 18.5f, -16.89f}, {-180.0f, 18.5f, -12.88f}, {-180.0f, 18.5f, -8.87f}, {-180.0f, 18.5f, -4.87f}, {-180.0f, 18.5f, -0.86f}, {-180.0f, 18.5f, 3.15f}, {-180.0f, 18.5f, 7.16f}, {-180.0f, 18.5f, 11.17f}, {-180.0f, 18.5f, 15.18f}, {-180.0f, 18.5f, 19.19f}, {-180.0f, 18.5f, 23.19f}, {-180.0f, 18.5f, 27.2f}, {-180.0f, 18.5f, 31.21f}, {-180.0f, 18.5f, 35.22f}, {-179.99f, 18.5f, 39.23f}, {-179.99f, 18.5f, 43.24f}, {176.95f, 18.5f, 47.25f}};
    private static final float START_Y = 18.5f;
    private static final float START_Z = 47.25f;
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 112;
        float normalized = t / (float)wrapRange * 56.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)56);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)56);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 18.5f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 47.25f;
    }
        public int period(){return 112;}
    }
    private static final class BMP1Model implements Curve {
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{176.22f, 17.36f, 41.51f}, {128.19f, 15.77f, 44.96f}, {78.85f, 12.13f, 45.99f}, {33.07f, 8.97f, 43.9f}, {32.61f, 6.87f, 40.61f}, {32.61f, 4.76f, 37.32f}, {32.61f, 2.65f, 34.03f}, {2.85f, 1.16f, 30.48f}, {0.0f, 1.16f, 26.58f}, {0.0f, 1.16f, 22.67f}, {0.0f, 1.16f, 18.77f}, {0.0f, 1.16f, 14.86f}, {0.0f, 1.16f, 10.95f}, {0.0f, 1.16f, 7.05f}, {0.0f, 1.16f, 3.14f}, {0.0f, 1.16f, -0.77f}, {0.0f, 1.16f, -4.67f}, {0.0f, 1.16f, -8.58f}, {0.0f, 1.16f, -12.49f}, {0.0f, 1.16f, -16.4f}, {0.0f, 1.16f, -20.31f}, {0.0f, 1.16f, -24.21f}, {-21.14f, 1.43f, -28.09f}, {-31.59f, 3.37f, -31.47f}, {-31.59f, 5.42f, -34.79f}, {-31.59f, 7.47f, -38.12f}, {-31.59f, 9.52f, -41.45f}, {-74.57f, 12.41f, -43.92f}, {-118.7f, 16.19f, -43.52f}, {-167.74f, 18.48f, -40.49f}, {178.42f, 18.54f, -36.6f}, {178.26f, 18.42f, -32.69f}, {178.26f, 18.3f, -28.79f}, {178.26f, 18.18f, -24.88f}, {179.56f, 18.07f, -20.98f}, {-180.0f, 18.07f, -17.07f}, {-180.0f, 18.07f, -13.17f}, {-180.0f, 18.07f, -9.26f}, {-180.0f, 18.07f, -5.35f}, {-180.0f, 18.07f, -1.44f}, {-180.0f, 18.07f, 2.46f}, {-180.0f, 18.07f, 6.38f}, {-180.0f, 18.07f, 10.28f}, {-180.0f, 18.07f, 14.19f}, {-180.0f, 18.07f, 18.09f}, {-180.0f, 18.07f, 22.0f}, {179.3f, 18.07f, 25.91f}, {177.21f, 17.93f, 29.81f}, {177.21f, 17.73f, 33.71f}, {177.21f, 17.54f, 37.61f}, {176.22f, 17.36f, 41.51f}};
    private static final float START_Y = 17.36f;
    private static final float START_Z = 41.51f;
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)50);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 17.36f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 41.51f;
    }
        public int period(){return 100;}
    }
    private static final class BMP1PModel implements Curve {
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{176.22f, 17.36f, 41.51f}, {128.19f, 15.77f, 44.96f}, {78.85f, 12.13f, 45.99f}, {33.07f, 8.97f, 43.9f}, {32.61f, 6.87f, 40.61f}, {32.61f, 4.76f, 37.32f}, {32.61f, 2.65f, 34.03f}, {2.85f, 1.16f, 30.48f}, {0.0f, 1.16f, 26.58f}, {0.0f, 1.16f, 22.67f}, {0.0f, 1.16f, 18.77f}, {0.0f, 1.16f, 14.86f}, {0.0f, 1.16f, 10.95f}, {0.0f, 1.16f, 7.05f}, {0.0f, 1.16f, 3.14f}, {0.0f, 1.16f, -0.77f}, {0.0f, 1.16f, -4.67f}, {0.0f, 1.16f, -8.58f}, {0.0f, 1.16f, -12.49f}, {0.0f, 1.16f, -16.4f}, {0.0f, 1.16f, -20.31f}, {0.0f, 1.16f, -24.21f}, {-21.14f, 1.43f, -28.09f}, {-31.59f, 3.37f, -31.47f}, {-31.59f, 5.42f, -34.79f}, {-31.59f, 7.47f, -38.12f}, {-31.59f, 9.52f, -41.45f}, {-74.57f, 12.41f, -43.92f}, {-118.7f, 16.19f, -43.52f}, {-167.74f, 18.48f, -40.49f}, {178.42f, 18.54f, -36.6f}, {178.26f, 18.42f, -32.69f}, {178.26f, 18.3f, -28.79f}, {178.26f, 18.18f, -24.88f}, {179.56f, 18.07f, -20.98f}, {-180.0f, 18.07f, -17.07f}, {-180.0f, 18.07f, -13.17f}, {-180.0f, 18.07f, -9.26f}, {-180.0f, 18.07f, -5.35f}, {-180.0f, 18.07f, -1.44f}, {-180.0f, 18.07f, 2.46f}, {-180.0f, 18.07f, 6.38f}, {-180.0f, 18.07f, 10.28f}, {-180.0f, 18.07f, 14.19f}, {-180.0f, 18.07f, 18.09f}, {-180.0f, 18.07f, 22.0f}, {179.3f, 18.07f, 25.91f}, {177.21f, 17.93f, 29.81f}, {177.21f, 17.73f, 33.71f}, {177.21f, 17.54f, 37.61f}, {176.22f, 17.36f, 41.51f}};
    private static final float START_Y = 17.36f;
    private static final float START_Z = 41.51f;
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)50);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 17.36f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 41.51f;
    }
        public int period(){return 100;}
    }
    private static final class BMP1UModel implements Curve {
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{176.22f, 17.36f, 41.51f}, {128.19f, 15.77f, 44.96f}, {78.85f, 12.13f, 45.99f}, {33.07f, 8.97f, 43.9f}, {32.61f, 6.87f, 40.61f}, {32.61f, 4.76f, 37.32f}, {32.61f, 2.65f, 34.03f}, {2.85f, 1.16f, 30.48f}, {0.0f, 1.16f, 26.58f}, {0.0f, 1.16f, 22.67f}, {0.0f, 1.16f, 18.77f}, {0.0f, 1.16f, 14.86f}, {0.0f, 1.16f, 10.95f}, {0.0f, 1.16f, 7.05f}, {0.0f, 1.16f, 3.14f}, {0.0f, 1.16f, -0.77f}, {0.0f, 1.16f, -4.67f}, {0.0f, 1.16f, -8.58f}, {0.0f, 1.16f, -12.49f}, {0.0f, 1.16f, -16.4f}, {0.0f, 1.16f, -20.31f}, {0.0f, 1.16f, -24.21f}, {-21.14f, 1.43f, -28.09f}, {-31.59f, 3.37f, -31.47f}, {-31.59f, 5.42f, -34.79f}, {-31.59f, 7.47f, -38.12f}, {-31.59f, 9.52f, -41.45f}, {-74.57f, 12.41f, -43.92f}, {-118.7f, 16.19f, -43.52f}, {-167.74f, 18.48f, -40.49f}, {178.42f, 18.54f, -36.6f}, {178.26f, 18.42f, -32.69f}, {178.26f, 18.3f, -28.79f}, {178.26f, 18.18f, -24.88f}, {179.56f, 18.07f, -20.98f}, {-180.0f, 18.07f, -17.07f}, {-180.0f, 18.07f, -13.17f}, {-180.0f, 18.07f, -9.26f}, {-180.0f, 18.07f, -5.35f}, {-180.0f, 18.07f, -1.44f}, {-180.0f, 18.07f, 2.46f}, {-180.0f, 18.07f, 6.38f}, {-180.0f, 18.07f, 10.28f}, {-180.0f, 18.07f, 14.19f}, {-180.0f, 18.07f, 18.09f}, {-180.0f, 18.07f, 22.0f}, {179.3f, 18.07f, 25.91f}, {177.21f, 17.93f, 29.81f}, {177.21f, 17.73f, 33.71f}, {177.21f, 17.54f, 37.61f}, {176.22f, 17.36f, 41.51f}};
    private static final float START_Y = 17.36f;
    private static final float START_Z = 41.51f;
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)50);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 17.36f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 41.51f;
    }
        public int period(){return 100;}
    }
    private static final class BMP2DModel implements Curve {
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{176.22f, 17.36f, 41.51f}, {128.19f, 15.77f, 44.96f}, {78.85f, 12.13f, 45.99f}, {33.07f, 8.97f, 43.9f}, {32.61f, 6.87f, 40.61f}, {32.61f, 4.76f, 37.32f}, {32.61f, 2.65f, 34.03f}, {2.85f, 1.16f, 30.48f}, {0.0f, 1.16f, 26.58f}, {0.0f, 1.16f, 22.67f}, {0.0f, 1.16f, 18.77f}, {0.0f, 1.16f, 14.86f}, {0.0f, 1.16f, 10.95f}, {0.0f, 1.16f, 7.05f}, {0.0f, 1.16f, 3.14f}, {0.0f, 1.16f, -0.77f}, {0.0f, 1.16f, -4.67f}, {0.0f, 1.16f, -8.58f}, {0.0f, 1.16f, -12.49f}, {0.0f, 1.16f, -16.4f}, {0.0f, 1.16f, -20.31f}, {0.0f, 1.16f, -24.21f}, {-21.14f, 1.43f, -28.09f}, {-31.59f, 3.37f, -31.47f}, {-31.59f, 5.42f, -34.79f}, {-31.59f, 7.47f, -38.12f}, {-31.59f, 9.52f, -41.45f}, {-74.57f, 12.41f, -43.92f}, {-118.7f, 16.19f, -43.52f}, {-167.74f, 18.48f, -40.49f}, {178.42f, 18.54f, -36.6f}, {178.26f, 18.42f, -32.69f}, {178.26f, 18.3f, -28.79f}, {178.26f, 18.18f, -24.88f}, {179.56f, 18.07f, -20.98f}, {-180.0f, 18.07f, -17.07f}, {-180.0f, 18.07f, -13.17f}, {-180.0f, 18.07f, -9.26f}, {-180.0f, 18.07f, -5.35f}, {-180.0f, 18.07f, -1.44f}, {-180.0f, 18.07f, 2.46f}, {-180.0f, 18.07f, 6.38f}, {-180.0f, 18.07f, 10.28f}, {-180.0f, 18.07f, 14.19f}, {-180.0f, 18.07f, 18.09f}, {-180.0f, 18.07f, 22.0f}, {179.3f, 18.07f, 25.91f}, {177.21f, 17.93f, 29.81f}, {177.21f, 17.73f, 33.71f}, {177.21f, 17.54f, 37.61f}, {176.22f, 17.36f, 41.51f}};
    private static final float START_Y = 17.36f;
    private static final float START_Z = 41.51f;
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)50);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 17.36f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 41.51f;
    }
        public int period(){return 100;}
    }
    private static final class BMP2MDModel implements Curve {
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{176.22f, 17.36f, 41.51f}, {128.19f, 15.77f, 44.96f}, {78.85f, 12.13f, 45.99f}, {33.07f, 8.97f, 43.9f}, {32.61f, 6.87f, 40.61f}, {32.61f, 4.76f, 37.32f}, {32.61f, 2.65f, 34.03f}, {2.85f, 1.16f, 30.48f}, {0.0f, 1.16f, 26.58f}, {0.0f, 1.16f, 22.67f}, {0.0f, 1.16f, 18.77f}, {0.0f, 1.16f, 14.86f}, {0.0f, 1.16f, 10.95f}, {0.0f, 1.16f, 7.05f}, {0.0f, 1.16f, 3.14f}, {0.0f, 1.16f, -0.77f}, {0.0f, 1.16f, -4.67f}, {0.0f, 1.16f, -8.58f}, {0.0f, 1.16f, -12.49f}, {0.0f, 1.16f, -16.4f}, {0.0f, 1.16f, -20.31f}, {0.0f, 1.16f, -24.21f}, {-21.14f, 1.43f, -28.09f}, {-31.59f, 3.37f, -31.47f}, {-31.59f, 5.42f, -34.79f}, {-31.59f, 7.47f, -38.12f}, {-31.59f, 9.52f, -41.45f}, {-74.57f, 12.41f, -43.92f}, {-118.7f, 16.19f, -43.52f}, {-167.74f, 18.48f, -40.49f}, {178.42f, 18.54f, -36.6f}, {178.26f, 18.42f, -32.69f}, {178.26f, 18.3f, -28.79f}, {178.26f, 18.18f, -24.88f}, {179.56f, 18.07f, -20.98f}, {-180.0f, 18.07f, -17.07f}, {-180.0f, 18.07f, -13.17f}, {-180.0f, 18.07f, -9.26f}, {-180.0f, 18.07f, -5.35f}, {-180.0f, 18.07f, -1.44f}, {-180.0f, 18.07f, 2.46f}, {-180.0f, 18.07f, 6.38f}, {-180.0f, 18.07f, 10.28f}, {-180.0f, 18.07f, 14.19f}, {-180.0f, 18.07f, 18.09f}, {-180.0f, 18.07f, 22.0f}, {179.3f, 18.07f, 25.91f}, {177.21f, 17.93f, 29.81f}, {177.21f, 17.73f, 33.71f}, {177.21f, 17.54f, 37.61f}, {176.22f, 17.36f, 41.51f}};
    private static final float START_Y = 17.36f;
    private static final float START_Z = 41.51f;
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)50);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 17.36f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 41.51f;
    }
        public int period(){return 100;}
    }
    private static final class BMP2MModel implements Curve {
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{176.22f, 17.36f, 41.51f}, {128.19f, 15.77f, 44.96f}, {78.85f, 12.13f, 45.99f}, {33.07f, 8.97f, 43.9f}, {32.61f, 6.87f, 40.61f}, {32.61f, 4.76f, 37.32f}, {32.61f, 2.65f, 34.03f}, {2.85f, 1.16f, 30.48f}, {0.0f, 1.16f, 26.58f}, {0.0f, 1.16f, 22.67f}, {0.0f, 1.16f, 18.77f}, {0.0f, 1.16f, 14.86f}, {0.0f, 1.16f, 10.95f}, {0.0f, 1.16f, 7.05f}, {0.0f, 1.16f, 3.14f}, {0.0f, 1.16f, -0.77f}, {0.0f, 1.16f, -4.67f}, {0.0f, 1.16f, -8.58f}, {0.0f, 1.16f, -12.49f}, {0.0f, 1.16f, -16.4f}, {0.0f, 1.16f, -20.31f}, {0.0f, 1.16f, -24.21f}, {-21.14f, 1.43f, -28.09f}, {-31.59f, 3.37f, -31.47f}, {-31.59f, 5.42f, -34.79f}, {-31.59f, 7.47f, -38.12f}, {-31.59f, 9.52f, -41.45f}, {-74.57f, 12.41f, -43.92f}, {-118.7f, 16.19f, -43.52f}, {-167.74f, 18.48f, -40.49f}, {178.42f, 18.54f, -36.6f}, {178.26f, 18.42f, -32.69f}, {178.26f, 18.3f, -28.79f}, {178.26f, 18.18f, -24.88f}, {179.56f, 18.07f, -20.98f}, {-180.0f, 18.07f, -17.07f}, {-180.0f, 18.07f, -13.17f}, {-180.0f, 18.07f, -9.26f}, {-180.0f, 18.07f, -5.35f}, {-180.0f, 18.07f, -1.44f}, {-180.0f, 18.07f, 2.46f}, {-180.0f, 18.07f, 6.38f}, {-180.0f, 18.07f, 10.28f}, {-180.0f, 18.07f, 14.19f}, {-180.0f, 18.07f, 18.09f}, {-180.0f, 18.07f, 22.0f}, {179.3f, 18.07f, 25.91f}, {177.21f, 17.93f, 29.81f}, {177.21f, 17.73f, 33.71f}, {177.21f, 17.54f, 37.61f}, {176.22f, 17.36f, 41.51f}};
    private static final float START_Y = 17.36f;
    private static final float START_Z = 41.51f;
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)50);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 17.36f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 41.51f;
    }
        public int period(){return 100;}
    }
    private static final class BMP2Model implements Curve {
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{176.22f, 17.36f, 41.51f}, {128.19f, 15.77f, 44.96f}, {78.85f, 12.13f, 45.99f}, {33.07f, 8.97f, 43.9f}, {32.61f, 6.87f, 40.61f}, {32.61f, 4.76f, 37.32f}, {32.61f, 2.65f, 34.03f}, {2.85f, 1.16f, 30.48f}, {0.0f, 1.16f, 26.58f}, {0.0f, 1.16f, 22.67f}, {0.0f, 1.16f, 18.77f}, {0.0f, 1.16f, 14.86f}, {0.0f, 1.16f, 10.95f}, {0.0f, 1.16f, 7.05f}, {0.0f, 1.16f, 3.14f}, {0.0f, 1.16f, -0.77f}, {0.0f, 1.16f, -4.67f}, {0.0f, 1.16f, -8.58f}, {0.0f, 1.16f, -12.49f}, {0.0f, 1.16f, -16.4f}, {0.0f, 1.16f, -20.31f}, {0.0f, 1.16f, -24.21f}, {-21.14f, 1.43f, -28.09f}, {-31.59f, 3.37f, -31.47f}, {-31.59f, 5.42f, -34.79f}, {-31.59f, 7.47f, -38.12f}, {-31.59f, 9.52f, -41.45f}, {-74.57f, 12.41f, -43.92f}, {-118.7f, 16.19f, -43.52f}, {-167.74f, 18.48f, -40.49f}, {178.42f, 18.54f, -36.6f}, {178.26f, 18.42f, -32.69f}, {178.26f, 18.3f, -28.79f}, {178.26f, 18.18f, -24.88f}, {179.56f, 18.07f, -20.98f}, {-180.0f, 18.07f, -17.07f}, {-180.0f, 18.07f, -13.17f}, {-180.0f, 18.07f, -9.26f}, {-180.0f, 18.07f, -5.35f}, {-180.0f, 18.07f, -1.44f}, {-180.0f, 18.07f, 2.46f}, {-180.0f, 18.07f, 6.38f}, {-180.0f, 18.07f, 10.28f}, {-180.0f, 18.07f, 14.19f}, {-180.0f, 18.07f, 18.09f}, {-180.0f, 18.07f, 22.0f}, {179.3f, 18.07f, 25.91f}, {177.21f, 17.93f, 29.81f}, {177.21f, 17.73f, 33.71f}, {177.21f, 17.54f, 37.61f}, {176.22f, 17.36f, 41.51f}};
    private static final float START_Y = 17.36f;
    private static final float START_Z = 41.51f;
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)50);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 17.36f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 41.51f;
    }
        public int period(){return 100;}
    }
    private static final class BMP2NoAtgmModel implements Curve {
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{176.22f, 17.36f, 41.51f}, {128.19f, 15.77f, 44.96f}, {78.85f, 12.13f, 45.99f}, {33.07f, 8.97f, 43.9f}, {32.61f, 6.87f, 40.61f}, {32.61f, 4.76f, 37.32f}, {32.61f, 2.65f, 34.03f}, {2.85f, 1.16f, 30.48f}, {0.0f, 1.16f, 26.58f}, {0.0f, 1.16f, 22.67f}, {0.0f, 1.16f, 18.77f}, {0.0f, 1.16f, 14.86f}, {0.0f, 1.16f, 10.95f}, {0.0f, 1.16f, 7.05f}, {0.0f, 1.16f, 3.14f}, {0.0f, 1.16f, -0.77f}, {0.0f, 1.16f, -4.67f}, {0.0f, 1.16f, -8.58f}, {0.0f, 1.16f, -12.49f}, {0.0f, 1.16f, -16.4f}, {0.0f, 1.16f, -20.31f}, {0.0f, 1.16f, -24.21f}, {-21.14f, 1.43f, -28.09f}, {-31.59f, 3.37f, -31.47f}, {-31.59f, 5.42f, -34.79f}, {-31.59f, 7.47f, -38.12f}, {-31.59f, 9.52f, -41.45f}, {-74.57f, 12.41f, -43.92f}, {-118.7f, 16.19f, -43.52f}, {-167.74f, 18.48f, -40.49f}, {178.42f, 18.54f, -36.6f}, {178.26f, 18.42f, -32.69f}, {178.26f, 18.3f, -28.79f}, {178.26f, 18.18f, -24.88f}, {179.56f, 18.07f, -20.98f}, {-180.0f, 18.07f, -17.07f}, {-180.0f, 18.07f, -13.17f}, {-180.0f, 18.07f, -9.26f}, {-180.0f, 18.07f, -5.35f}, {-180.0f, 18.07f, -1.44f}, {-180.0f, 18.07f, 2.46f}, {-180.0f, 18.07f, 6.38f}, {-180.0f, 18.07f, 10.28f}, {-180.0f, 18.07f, 14.19f}, {-180.0f, 18.07f, 18.09f}, {-180.0f, 18.07f, 22.0f}, {179.3f, 18.07f, 25.91f}, {177.21f, 17.93f, 29.81f}, {177.21f, 17.73f, 33.71f}, {177.21f, 17.54f, 37.61f}, {176.22f, 17.36f, 41.51f}};
    private static final float START_Y = 17.36f;
    private static final float START_Z = 41.51f;
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)50);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 17.36f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 41.51f;
    }
        public int period(){return 100;}
    }
    private static final class BMPT3KModel implements Curve {
    private static final int TRACK_COUNT = 53;
    private static final int MAX_IDX = 53;
    private static final float[][] KEYFRAMES = new float[][]{{176.78f, 21.97f, 51.55f}, {128.03f, 19.86f, 55.87f}, {78.54f, 15.17f, 56.88f}, {31.65f, 11.5f, 53.78f}, {31.65f, 8.89f, 49.54f}, {31.65f, 6.28f, 45.31f}, {33.51f, 3.67f, 41.08f}, {6.9f, 1.72f, 36.59f}, {0.0f, 1.69f, 31.62f}, {0.0f, 1.69f, 26.65f}, {0.0f, 1.69f, 21.67f}, {0.0f, 1.69f, 16.7f}, {0.0f, 1.69f, 11.73f}, {0.0f, 1.69f, 6.75f}, {0.0f, 1.69f, 1.78f}, {0.0f, 1.69f, -3.19f}, {0.0f, 1.69f, -8.16f}, {0.0f, 1.69f, -13.14f}, {0.0f, 1.69f, -18.11f}, {0.0f, 1.69f, -23.08f}, {0.0f, 1.69f, -28.06f}, {0.0f, 1.69f, -33.03f}, {0.0f, 1.69f, -38.0f}, {0.0f, 1.69f, -42.97f}, {-0.57f, 1.69f, -47.95f}, {-34.42f, 3.17f, -52.6f}, {-43.88f, 6.6f, -56.2f}, {-43.88f, 10.05f, -59.78f}, {-43.88f, 13.49f, -63.37f}, {-92.83f, 17.95f, -65.12f}, {-147.62f, 22.07f, -62.65f}, {-178.31f, 22.94f, -57.85f}, {179.48f, 22.91f, -52.87f}, {179.48f, 22.86f, -47.9f}, {179.48f, 22.82f, -42.93f}, {179.48f, 22.77f, -37.96f}, {179.48f, 22.73f, -32.98f}, {179.48f, 22.68f, -28.01f}, {179.48f, 22.64f, -23.04f}, {179.48f, 22.6f, -18.07f}, {179.48f, 22.55f, -13.09f}, {179.48f, 22.51f, -8.12f}, {179.48f, 22.46f, -3.15f}, {179.48f, 22.42f, 1.82f}, {179.48f, 22.37f, 6.8f}, {179.48f, 22.33f, 11.77f}, {179.48f, 22.28f, 16.74f}, {179.48f, 22.24f, 21.72f}, {179.48f, 22.19f, 26.69f}, {179.48f, 22.15f, 31.66f}, {179.48f, 22.1f, 36.63f}, {179.48f, 22.06f, 41.61f}, {179.48f, 22.01f, 46.58f}, {176.78f, 21.97f, 51.55f}};
    private static final float START_Y = 21.97f;
    private static final float START_Z = 51.55f;
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 106;
        float normalized = t / (float)wrapRange * 53.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)53);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)53);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 21.97f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 51.55f;
    }
        public int period(){return 106;}
    }
    private static final class Bmp2BakhcaModel implements Curve {
    private static final int TRACK_COUNT = 62;
    private static final int MAX_IDX = 62;
    private static final float[][] KEYFRAMES = new float[][]{{176.06f, 21.18f, 53.69f}, {143.44f, 20.06f, 57.17f}, {109.31f, 17.13f, 59.35f}, {75.19f, 13.48f, 59.44f}, {35.61f, 10.46f, 57.39f}, {28.97f, 8.61f, 54.18f}, {28.97f, 6.81f, 50.93f}, {28.97f, 5.02f, 47.68f}, {28.97f, 3.22f, 44.43f}, {7.23f, 1.74f, 41.06f}, {0.0f, 1.68f, 37.35f}, {0.0f, 1.68f, 33.64f}, {0.0f, 1.68f, 29.92f}, {0.0f, 1.68f, 26.21f}, {0.0f, 1.68f, 22.5f}, {0.0f, 1.68f, 18.79f}, {0.0f, 1.68f, 15.08f}, {0.0f, 1.68f, 11.36f}, {0.0f, 1.68f, 7.65f}, {0.0f, 1.68f, 3.94f}, {0.0f, 1.68f, 0.23f}, {0.0f, 1.68f, -3.49f}, {0.0f, 1.68f, -7.2f}, {0.0f, 1.68f, -10.91f}, {0.0f, 1.68f, -14.62f}, {0.0f, 1.68f, -18.34f}, {0.0f, 1.68f, -22.05f}, {0.0f, 1.68f, -25.76f}, {-20.81f, 2.01f, -29.43f}, {-36.24f, 4.08f, -32.5f}, {-36.24f, 6.27f, -35.5f}, {-36.24f, 8.46f, -38.49f}, {-36.24f, 10.66f, -41.49f}, {-34.73f, 12.85f, -44.48f}, {-74.4f, 15.63f, -46.78f}, {-128.43f, 19.12f, -46.0f}, {-175.02f, 20.62f, -42.74f}, {-175.73f, 20.89f, -39.04f}, {-175.68f, 21.17f, -35.33f}, {-175.68f, 21.45f, -31.63f}, {-175.68f, 21.73f, -27.93f}, {-175.68f, 22.01f, -24.23f}, {-176.76f, 22.29f, -20.53f}, {-180.0f, 22.3f, -16.81f}, {-180.0f, 22.3f, -13.1f}, {-180.0f, 22.3f, -9.39f}, {-180.0f, 22.3f, -5.68f}, {-180.0f, 22.3f, -1.96f}, {-180.0f, 22.3f, 1.75f}, {-180.0f, 22.3f, 5.46f}, {-180.0f, 22.3f, 9.17f}, {-180.0f, 22.3f, 12.89f}, {-180.0f, 22.3f, 16.6f}, {-180.0f, 22.3f, 20.31f}, {-180.0f, 22.3f, 24.02f}, {-180.0f, 22.3f, 27.74f}, {-180.0f, 22.3f, 31.45f}, {179.11f, 22.3f, 35.16f}, {176.44f, 22.1f, 38.87f}, {176.44f, 21.87f, 42.57f}, {176.44f, 21.64f, 46.28f}, {176.44f, 21.41f, 49.98f}, {176.06f, 21.18f, 53.69f}};
    private static final float START_Y = 21.18f;
    private static final float START_Z = 53.69f;
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 124;
        float normalized = t / (float)wrapRange * 62.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)62);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)62);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 21.18f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 53.69f;
    }
        public int period(){return 124;}
    }
    private static final class BradleyModel implements Curve {
    private static final int TRACK_COUNT = 63;
    private static final int MAX_IDX = 63;
    private static final float[][] KEYFRAMES = new float[][]{{175.55f, 17.49f, 45.29f}, {123.17f, 15.66f, 48.65f}, {63.86f, 11.86f, 48.95f}, {37.4f, 9.32f, 46.02f}, {37.4f, 6.91f, 42.85f}, {37.4f, 4.49f, 39.69f}, {37.4f, 2.07f, 36.53f}, {0.53f, 0.7f, 32.95f}, {0.0f, 0.7f, 28.97f}, {0.0f, 0.7f, 24.99f}, {0.0f, 0.7f, 21.01f}, {0.0f, 0.7f, 17.03f}, {0.0f, 0.7f, 13.05f}, {0.0f, 0.7f, 9.07f}, {0.0f, 0.7f, 5.09f}, {0.0f, 0.7f, 1.11f}, {0.0f, 0.7f, -2.87f}, {0.0f, 0.7f, -6.85f}, {0.0f, 0.7f, -10.83f}, {0.0f, 0.7f, -14.81f}, {0.0f, 0.7f, -18.79f}, {0.0f, 0.7f, -22.77f}, {0.0f, 0.7f, -26.75f}, {0.0f, 0.7f, -30.73f}, {0.0f, 0.7f, -34.71f}, {0.0f, 0.7f, -38.69f}, {0.0f, 0.7f, -42.67f}, {-15.23f, 0.97f, -46.63f}, {-24.82f, 2.59f, -50.26f}, {-24.82f, 4.26f, -53.88f}, {-24.82f, 5.93f, -57.49f}, {-24.82f, 7.6f, -61.1f}, {-29.17f, 9.27f, -64.71f}, {-63.79f, 12.09f, -67.41f}, {-103.35f, 15.97f, -67.81f}, {-142.91f, 19.28f, -65.72f}, {-177.0f, 20.6f, -62.05f}, {179.64f, 20.58f, -58.07f}, {179.64f, 20.55f, -54.09f}, {179.64f, 20.53f, -50.11f}, {179.64f, 20.5f, -46.13f}, {179.64f, 20.48f, -42.15f}, {179.64f, 20.45f, -38.17f}, {179.64f, 20.43f, -34.19f}, {179.73f, 20.4f, -30.21f}, {-180.0f, 20.4f, -26.23f}, {-180.0f, 20.4f, -22.25f}, {-180.0f, 20.4f, -18.27f}, {-180.0f, 20.4f, -14.29f}, {-180.0f, 20.4f, -10.31f}, {-180.0f, 20.4f, -6.33f}, {179.63f, 20.4f, -2.35f}, {178.52f, 20.3f, 1.63f}, {178.52f, 20.2f, 5.61f}, {178.52f, 20.1f, 9.59f}, {178.52f, 19.99f, 13.57f}, {178.52f, 19.89f, 17.55f}, {175.54f, 19.76f, 21.52f}, {174.54f, 19.38f, 25.49f}, {174.54f, 19.0f, 29.45f}, {174.54f, 18.63f, 33.41f}, {174.54f, 18.25f, 37.37f}, {174.54f, 17.87f, 41.33f}, {175.55f, 17.49f, 45.29f}};
    private static final float START_Y = 17.49f;
    private static final float START_Z = 45.29f;
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 126;
        float normalized = t / (float)wrapRange * 63.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)63);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)63);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 17.49f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 45.29f;
    }
        public int period(){return 126;}
    }
    private static final class CV90Model implements Curve {
    private static final int MAX_IDX = 71;
    private static final float[][] KEYFRAMES = new float[][]{{178.43f, 14.5f, 42.0f}, {143.02f, 13.55f, 44.76f}, {105.04f, 11.11f, 46.36f}, {67.06f, 8.2f, 46.12f}, {29.08f, 6.05f, 44.15f}, {24.81f, 4.85f, 41.43f}, {24.81f, 3.6f, 38.73f}, {24.81f, 2.35f, 36.03f}, {24.81f, 1.1f, 33.33f}, {0.0f, 0.5f, 30.46f}, {0.0f, 0.5f, 27.49f}, {0.0f, 0.5f, 24.51f}, {0.0f, 0.5f, 21.54f}, {0.0f, 0.5f, 18.56f}, {0.0f, 0.5f, 15.59f}, {0.0f, 0.5f, 12.62f}, {0.0f, 0.5f, 9.64f}, {0.0f, 0.5f, 6.67f}, {0.0f, 0.5f, 3.69f}, {0.0f, 0.5f, 0.72f}, {0.0f, 0.5f, -2.25f}, {0.0f, 0.5f, -5.23f}, {0.0f, 0.5f, -8.2f}, {0.0f, 0.5f, -11.18f}, {0.0f, 0.5f, -14.15f}, {0.0f, 0.5f, -17.13f}, {0.0f, 0.5f, -20.1f}, {0.0f, 0.5f, -23.07f}, {0.0f, 0.5f, -26.05f}, {0.0f, 0.5f, -29.02f}, {0.0f, 0.5f, -32.0f}, {-0.62f, 0.5f, -34.97f}, {-28.74f, 1.32f, -37.79f}, {-28.74f, 2.75f, -40.39f}, {-28.74f, 4.18f, -43.0f}, {-28.74f, 5.61f, -45.61f}, {-28.74f, 7.04f, -48.22f}, {-37.69f, 8.48f, -50.81f}, {-75.51f, 10.95f, -52.38f}, {-115.31f, 13.86f, -52.1f}, {-153.13f, 15.98f, -50.1f}, {179.85f, 16.48f, -47.21f}, {178.76f, 16.42f, -44.23f}, {178.73f, 16.35f, -41.26f}, {178.73f, 16.28f, -38.29f}, {178.73f, 16.22f, -35.31f}, {178.73f, 16.15f, -32.34f}, {178.73f, 16.09f, -29.37f}, {178.73f, 16.02f, -26.39f}, {178.73f, 15.95f, -23.42f}, {178.73f, 15.89f, -20.44f}, {178.73f, 15.82f, -17.47f}, {178.73f, 15.76f, -14.5f}, {178.73f, 15.69f, -11.52f}, {178.73f, 15.62f, -8.55f}, {178.73f, 15.56f, -5.58f}, {178.73f, 15.49f, -2.6f}, {178.73f, 15.43f, 0.37f}, {178.73f, 15.36f, 3.34f}, {178.73f, 15.29f, 6.32f}, {178.73f, 15.23f, 9.29f}, {178.73f, 15.16f, 12.26f}, {178.73f, 15.09f, 15.24f}, {178.73f, 15.03f, 18.21f}, {178.73f, 14.96f, 21.19f}, {178.73f, 14.9f, 24.16f}, {178.73f, 14.83f, 27.13f}, {178.73f, 14.76f, 30.11f}, {178.73f, 14.7f, 33.08f}, {178.73f, 14.63f, 36.05f}, {178.73f, 14.57f, 39.03f}, {178.43f, 14.5f, 42.0f}};
    private static final float START_Y = 14.5f;
    private static final float START_Z = 42.0f;
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 142;
        float normalized = t / (float)wrapRange * 71.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)71);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)71);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 14.5f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 42.0f;
    }
        public int period(){return 142;}
    }
    private static final class HYR0Renderer implements Curve {
    private static final float[] MOVE_Y = new float[]{6.531f, 6.006f, 4.665f, 3.104f, 2.007f, 1.209f, 0.411f, -0.387f, -1.185f, -1.983f, -2.781f, -3.579f, -4.377f, -5.175f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.518f, -5.509f, -4.992f, -3.8f, -2.246f, -0.737f, 0.331f, 0.684f, 0.746f, 0.808f, 0.871f, 0.933f, 0.995f, 1.057f, 1.119f, 1.182f, 1.244f, 1.306f, 1.368f, 1.431f, 1.493f, 1.555f, 1.617f, 1.679f, 1.742f, 1.804f, 1.866f, 1.928f, 1.99f, 2.053f, 2.115f, 2.177f, 2.239f, 2.301f, 2.364f, 2.426f, 2.488f, 2.55f, 2.612f, 2.675f, 2.737f, 2.799f, 2.861f, 2.923f, 2.986f, 3.048f, 3.11f, 3.172f, 3.234f, 3.297f, 3.359f, 3.421f, 3.483f, 3.545f, 3.608f, 3.67f, 3.732f, 3.794f, 3.856f, 3.919f, 3.981f, 4.043f, 4.105f, 4.168f, 4.23f, 4.292f, 4.354f, 4.416f, 4.479f, 4.541f, 4.603f, 4.665f, 4.727f, 4.79f, 4.852f, 4.914f, 4.976f, 5.038f, 5.101f, 5.163f, 5.225f, 5.287f, 5.349f, 5.412f, 5.474f, 5.536f, 5.598f, 5.66f, 5.723f, 5.785f, 5.847f, 5.909f, 5.971f, 6.034f, 6.096f, 6.158f, 6.22f, 6.282f, 6.345f, 6.407f, 6.469f, 6.531f};
    private static final float[] MOVE_Z = new float[]{168.859f, 170.343f, 171.168f, 170.966f, 169.832f, 168.44f, 167.048f, 165.655f, 164.263f, 162.871f, 161.478f, 160.086f, 158.694f, 157.302f, 155.788f, 154.183f, 152.578f, 150.973f, 149.369f, 147.764f, 146.159f, 144.554f, 142.949f, 141.344f, 139.74f, 138.135f, 136.53f, 134.925f, 133.32f, 131.715f, 130.111f, 128.506f, 126.901f, 125.296f, 123.691f, 122.087f, 120.482f, 118.877f, 117.272f, 115.667f, 114.062f, 112.458f, 110.853f, 109.248f, 107.643f, 106.038f, 104.433f, 102.829f, 101.224f, 99.619f, 98.014f, 96.409f, 94.804f, 93.2f, 91.595f, 89.99f, 88.385f, 86.78f, 85.176f, 83.571f, 81.966f, 80.361f, 78.756f, 77.151f, 75.547f, 73.942f, 72.337f, 70.732f, 69.127f, 67.522f, 65.918f, 64.313f, 62.708f, 61.103f, 59.498f, 57.894f, 56.289f, 54.684f, 53.079f, 51.474f, 49.869f, 48.265f, 46.66f, 45.055f, 43.45f, 41.845f, 40.24f, 38.636f, 37.031f, 35.426f, 33.821f, 32.216f, 30.611f, 29.007f, 27.402f, 25.797f, 24.192f, 22.587f, 20.983f, 19.378f, 17.773f, 16.273f, 15.225f, 14.905f, 15.396f, 16.57f, 18.119f, 19.723f, 21.326f, 22.93f, 24.534f, 26.137f, 27.741f, 29.344f, 30.948f, 32.552f, 34.155f, 35.759f, 37.363f, 38.966f, 40.57f, 42.173f, 43.777f, 45.381f, 46.984f, 48.588f, 50.191f, 51.795f, 53.399f, 55.002f, 56.606f, 58.21f, 59.813f, 61.417f, 63.02f, 64.624f, 66.228f, 67.831f, 69.435f, 71.039f, 72.642f, 74.246f, 75.849f, 77.453f, 79.057f, 80.66f, 82.264f, 83.867f, 85.471f, 87.075f, 88.678f, 90.282f, 91.886f, 93.489f, 95.093f, 96.696f, 98.3f, 99.904f, 101.507f, 103.111f, 104.715f, 106.318f, 107.922f, 109.525f, 111.129f, 112.733f, 114.336f, 115.94f, 117.543f, 119.147f, 120.751f, 122.354f, 123.958f, 125.562f, 127.165f, 128.769f, 130.372f, 131.976f, 133.58f, 135.183f, 136.787f, 138.391f, 139.994f, 141.598f, 143.201f, 144.805f, 146.409f, 148.012f, 149.616f, 151.219f, 152.823f, 154.427f, 156.03f, 157.634f, 159.238f, 160.841f, 162.445f, 164.048f, 165.652f, 167.256f, 168.859f};
    private static final float[] ROT_X = new float[]{0.0f, -38.95f, -77.9f, -116.85f, -150.18f, -150.18f, -150.18f, -150.18f, -150.18f, -150.18f, -150.18f, -150.18f, -150.18f, -150.18f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -184.17f, -213.85f, -243.53f, -273.2f, -302.88f, -332.55f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f, -357.78f};
    private static final float[] TIMES = new float[]{0.0f, 0.4f, 0.8f, 1.2f, 1.6f, 2.0f, 2.4f, 2.8f, 3.2f, 3.6f, 4.0f, 4.4f, 4.8f, 5.2f, 5.6f, 6.0f, 6.4f, 6.8f, 7.2f, 7.6f, 8.0f, 8.4f, 8.8f, 9.2f, 9.6f, 10.0f, 10.4f, 10.8f, 11.2f, 11.6f, 12.0f, 12.4f, 12.8f, 13.2f, 13.6f, 14.0f, 14.4f, 14.8f, 15.2f, 15.6f, 16.0f, 16.4f, 16.8f, 17.2f, 17.6f, 18.0f, 18.4f, 18.8f, 19.2f, 19.6f, 20.0f, 20.4f, 20.8f, 21.2f, 21.6f, 22.0f, 22.4f, 22.8f, 23.2f, 23.6f, 24.0f, 24.4f, 24.8f, 25.2f, 25.6f, 26.0f, 26.4f, 26.8f, 27.2f, 27.6f, 28.0f, 28.4f, 28.8f, 29.2f, 29.6f, 30.0f, 30.4f, 30.8f, 31.2f, 31.6f, 32.0f, 32.4f, 32.8f, 33.2f, 33.6f, 34.0f, 34.4f, 34.8f, 35.2f, 35.6f, 36.0f, 36.4f, 36.8f, 37.2f, 37.6f, 38.0f, 38.4f, 38.8f, 39.2f, 39.6f, 40.0f, 40.4f, 40.8f, 41.2f, 41.6f, 42.0f, 42.4f, 42.8f, 43.2f, 43.6f, 44.0f, 44.4f, 44.8f, 45.2f, 45.6f, 46.0f, 46.4f, 46.8f, 47.2f, 47.6f, 48.0f, 48.4f, 48.8f, 49.2f, 49.6f, 50.0f, 50.4f, 50.8f, 51.2f, 51.6f, 52.0f, 52.4f, 52.8f, 53.2f, 53.6f, 54.0f, 54.4f, 54.8f, 55.2f, 55.6f, 56.0f, 56.4f, 56.8f, 57.2f, 57.6f, 58.0f, 58.4f, 58.8f, 59.2f, 59.6f, 60.0f, 60.4f, 60.8f, 61.2f, 61.6f, 62.0f, 62.4f, 62.8f, 63.2f, 63.6f, 64.0f, 64.4f, 64.8f, 65.2f, 65.6f, 66.0f, 66.4f, 66.8f, 67.2f, 67.6f, 68.0f, 68.4f, 68.8f, 69.2f, 69.6f, 70.0f, 70.4f, 70.8f, 71.2f, 71.6f, 72.0f, 72.4f, 72.8f, 73.2f, 73.6f, 74.0f, 74.4f, 74.8f, 75.2f, 75.6f, 76.0f, 76.4f, 76.8f, 77.2f, 77.6f, 78.0f, 78.4f, 78.8f, 79.2f, 79.6f, 80.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.4f, 0.8f, 1.2f, 1.6f, 2.0f, 2.4f, 2.8f, 3.2f, 3.6f, 4.0f, 4.4f, 4.8f, 5.2f, 5.6f, 6.0f, 6.4f, 6.8f, 7.2f, 7.6f, 8.0f, 8.4f, 8.8f, 9.2f, 9.6f, 10.0f, 10.4f, 10.8f, 11.2f, 11.6f, 12.0f, 12.4f, 12.8f, 13.2f, 13.6f, 14.0f, 14.4f, 14.8f, 15.2f, 15.6f, 16.0f, 16.4f, 16.8f, 17.2f, 17.6f, 18.0f, 18.4f, 18.8f, 19.2f, 19.6f, 20.0f, 20.4f, 20.8f, 21.2f, 21.6f, 22.0f, 22.4f, 22.8f, 23.2f, 23.6f, 24.0f, 24.4f, 24.8f, 25.2f, 25.6f, 26.0f, 26.4f, 26.8f, 27.2f, 27.6f, 28.0f, 28.4f, 28.8f, 29.2f, 29.6f, 30.0f, 30.4f, 30.8f, 31.2f, 31.6f, 32.0f, 32.4f, 32.8f, 33.2f, 33.6f, 34.0f, 34.4f, 34.8f, 35.2f, 35.6f, 36.0f, 36.4f, 36.8f, 37.2f, 37.6f, 38.0f, 38.4f, 38.8f, 39.2f, 39.6f, 40.0f, 40.4f, 40.8f, 41.2f, 41.6f, 42.0f, 42.4f, 42.8f, 43.2f, 43.6f, 44.0f, 44.4f, 44.8f, 45.2f, 45.6f, 46.0f, 46.4f, 46.8f, 47.2f, 47.6f, 48.0f, 48.4f, 48.8f, 49.2f, 49.6f, 50.0f, 50.4f, 50.8f, 51.2f, 51.6f, 52.0f, 52.4f, 52.8f, 53.2f, 53.6f, 54.0f, 54.4f, 54.8f, 55.2f, 55.6f, 56.0f, 56.4f, 56.8f, 57.2f, 57.6f, 58.0f, 58.4f, 58.8f, 59.2f, 59.6f, 60.0f, 60.4f, 60.8f, 61.2f, 61.6f, 62.0f, 62.4f, 62.8f, 63.2f, 63.6f, 64.0f, 64.4f, 64.8f, 65.2f, 65.6f, 66.0f, 66.4f, 66.8f, 67.2f, 67.6f, 68.0f, 68.4f, 68.8f, 69.2f, 69.6f, 70.0f, 70.4f, 70.8f, 71.2f, 71.6f, 72.0f, 72.4f, 72.8f, 73.2f, 73.6f, 74.0f, 74.4f, 74.8f, 75.2f, 75.6f, 76.0f, 76.4f, 76.8f, 77.2f, 77.6f, 78.0f, 78.4f, 78.8f, 79.2f, 79.6f, 80.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return HYR0Renderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return HYR0Renderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return HYR0Renderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.7391304f;
    }
        public int period(){return 80;}
    }
    private static final class Leopard2A7VModel implements Curve {
    private static final int TRACK_COUNT = 71;
    private static final int MAX_IDX = 71;
    private static final float[][] KEYFRAMES = new float[][]{{176.79f, 22.5f, 59.75f}, {143.01f, 21.27f, 63.38f}, {103.55f, 18.1f, 65.53f}, {69.02f, 14.28f, 65.32f}, {38.21f, 11.35f, 62.85f}, {39.45f, 8.87f, 59.84f}, {39.45f, 6.39f, 56.83f}, {39.45f, 3.92f, 53.82f}, {31.02f, 1.47f, 50.79f}, {0.57f, 0.5f, 47.07f}, {0.0f, 0.5f, 43.17f}, {0.0f, 0.5f, 39.27f}, {0.0f, 0.5f, 35.37f}, {0.0f, 0.5f, 31.47f}, {0.0f, 0.5f, 27.57f}, {0.0f, 0.5f, 23.67f}, {0.0f, 0.5f, 19.78f}, {0.0f, 0.5f, 15.88f}, {0.0f, 0.5f, 11.98f}, {0.0f, 0.5f, 8.08f}, {0.0f, 0.5f, 4.18f}, {0.0f, 0.5f, 0.28f}, {0.0f, 0.5f, -3.62f}, {0.0f, 0.5f, -7.51f}, {0.0f, 0.5f, -11.41f}, {0.0f, 0.5f, -15.31f}, {0.0f, 0.5f, -19.21f}, {0.0f, 0.5f, -23.11f}, {0.0f, 0.5f, -27.01f}, {0.0f, 0.5f, -30.9f}, {0.0f, 0.5f, -34.8f}, {0.0f, 0.5f, -38.7f}, {-17.34f, 0.74f, -42.58f}, {-35.92f, 2.66f, -45.94f}, {-35.92f, 4.95f, -49.09f}, {-35.92f, 7.24f, -52.25f}, {-35.92f, 9.52f, -55.41f}, {-35.92f, 11.81f, -58.57f}, {-61.23f, 14.43f, -61.4f}, {-95.17f, 18.17f, -62.21f}, {-133.95f, 21.64f, -60.59f}, {-172.73f, 23.42f, -57.2f}, {-179.76f, 23.47f, -53.3f}, {179.51f, 23.44f, -49.41f}, {179.51f, 23.41f, -45.51f}, {179.51f, 23.37f, -41.61f}, {179.51f, 23.34f, -37.71f}, {179.51f, 23.31f, -33.81f}, {179.51f, 23.27f, -29.91f}, {179.51f, 23.24f, -26.01f}, {179.51f, 23.21f, -22.12f}, {179.51f, 23.17f, -18.22f}, {179.51f, 23.14f, -14.32f}, {179.51f, 23.1f, -10.42f}, {179.51f, 23.07f, -6.52f}, {179.51f, 23.04f, -2.62f}, {179.51f, 23.0f, 1.27f}, {179.51f, 22.97f, 5.17f}, {179.51f, 22.94f, 9.07f}, {179.51f, 22.9f, 12.97f}, {179.51f, 22.87f, 16.87f}, {179.51f, 22.84f, 20.77f}, {179.51f, 22.8f, 24.66f}, {179.51f, 22.77f, 28.56f}, {179.51f, 22.74f, 32.46f}, {179.51f, 22.7f, 36.36f}, {179.51f, 22.67f, 40.26f}, {179.51f, 22.63f, 44.16f}, {179.51f, 22.6f, 48.05f}, {179.51f, 22.57f, 51.95f}, {179.51f, 22.53f, 55.85f}, {176.79f, 22.5f, 59.75f}};
    private static final float START_Y = 22.5f;
    private static final float START_Z = 59.75f;
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 142;
        float normalized = t / (float)wrapRange * 71.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)71);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)71);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 22.5f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 59.75f;
    }
        public int period(){return 142;}
    }
    private static final class M109Model implements Curve {
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{-177.02f, 11.4f, -41.51f}, {-118.82f, 9.48f, -44.74f}, {-55.2f, 5.73f, -44.62f}, {-26.48f, 3.63f, -41.37f}, {-26.48f, 1.88f, -37.85f}, {-6.8f, 0.25f, -34.28f}, {0.0f, 0.2f, -30.35f}, {0.0f, 0.2f, -26.41f}, {0.0f, 0.2f, -22.47f}, {0.0f, 0.2f, -18.53f}, {0.0f, 0.2f, -14.59f}, {0.0f, 0.2f, -10.65f}, {0.0f, 0.2f, -6.71f}, {0.0f, 0.2f, -2.77f}, {0.0f, 0.2f, 1.16f}, {0.0f, 0.2f, 5.1f}, {0.0f, 0.2f, 9.04f}, {0.0f, 0.2f, 12.98f}, {0.0f, 0.2f, 16.92f}, {0.0f, 0.2f, 20.86f}, {0.0f, 0.2f, 24.79f}, {0.0f, 0.2f, 28.74f}, {17.32f, 0.59f, 32.62f}, {17.32f, 1.76f, 36.39f}, {17.32f, 2.93f, 40.15f}, {17.32f, 4.11f, 43.9f}, {54.98f, 6.01f, 47.24f}, {108.91f, 9.76f, 47.81f}, {162.84f, 12.41f, 45.1f}, {-178.29f, 12.5f, 41.18f}, {-178.22f, 12.38f, 37.25f}, {-178.36f, 12.27f, 33.31f}, {-178.42f, 12.16f, 29.38f}, {-178.56f, 12.04f, 25.43f}, {-178.62f, 11.95f, 21.5f}, {-178.76f, 11.87f, 17.56f}, {-178.89f, 11.78f, 13.63f}, {-178.96f, 11.7f, 9.68f}, {-179.09f, 11.64f, 5.75f}, {-179.16f, 11.58f, 1.81f}, {-179.3f, 11.53f, -2.13f}, {-179.36f, 11.48f, -6.07f}, {-179.5f, 11.44f, -10.0f}, {-179.63f, 11.41f, -13.95f}, {-179.7f, 11.39f, -17.88f}, {-179.83f, 11.37f, -21.82f}, {-179.9f, 11.36f, -25.76f}, {179.97f, 11.36f, -29.7f}, {179.9f, 11.36f, -33.63f}, {179.77f, 11.37f, -37.58f}, {-177.02f, 11.4f, -41.51f}};
    private static final float START_Y = 11.4f;
    private static final float START_Z = -41.51f;
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)50);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 11.4f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - -41.51f;
    }
        public int period(){return 100;}
    }
    private static final class M10BookerRenderer implements Curve {
    private static final float[] MOVE_Y = new float[]{-0.047f, -0.179f, -0.569f, -1.195f, -2.022f, -3.002f, -4.082f, -5.2f, -6.294f, -7.302f, -8.167f, -8.848f, -9.469f, -10.09f, -10.711f, -11.332f, -11.953f, -12.574f, -13.195f, -13.816f, -14.437f, -15.058f, -15.679f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.972f, -15.406f, -14.804f, -14.202f, -13.6f, -12.999f, -12.397f, -11.795f, -11.193f, -10.591f, -9.989f, -9.387f, -8.785f, -8.111f, -7.249f, -6.244f, -5.151f, -4.033f, -2.952f, -1.969f, -1.139f, -0.508f, -0.113f, 0.025f, 0.024f, 0.023f, 0.022f, 0.021f, 0.021f, 0.02f, 0.019f, 0.018f, 0.017f, 0.016f, 0.015f, 0.015f, 0.014f, 0.013f, 0.012f, 0.011f, 0.01f, 0.01f, 0.009f, 0.008f, 0.007f, 0.006f, 0.005f, 0.005f, 0.004f, 0.003f, 0.002f, 0.001f, 0.0f, 0.0f, -0.001f, -0.002f, -0.003f, -0.004f, -0.005f, -0.005f, -0.006f, -0.007f, -0.008f, -0.009f, -0.01f, -0.01f, -0.011f, -0.012f, -0.013f, -0.014f, -0.015f, -0.016f, -0.016f, -0.017f, -0.018f, -0.019f, -0.02f, -0.021f, -0.021f, -0.022f, -0.023f, -0.024f, -0.025f, -0.026f, -0.026f, -0.027f, -0.028f, -0.029f, -0.03f, -0.031f, -0.031f, -0.032f, -0.033f, -0.034f, -0.035f, -0.036f, -0.036f, -0.037f, -0.038f, -0.039f, -0.04f, -0.041f, -0.042f, -0.042f, -0.043f, -0.044f, -0.045f, -0.046f, -0.047f};
    private static final float[] MOVE_Z = new float[]{81.576f, 82.686f, 83.734f, 84.661f, 85.415f, 85.953f, 86.245f, 86.275f, 86.041f, 85.556f, 84.847f, 83.959f, 83.026f, 82.092f, 81.159f, 80.225f, 79.292f, 78.358f, 77.425f, 76.491f, 75.558f, 74.624f, 73.691f, 72.658f, 71.537f, 70.416f, 69.295f, 68.174f, 67.053f, 65.931f, 64.81f, 63.689f, 62.568f, 61.447f, 60.326f, 59.205f, 58.083f, 56.962f, 55.841f, 54.72f, 53.599f, 52.478f, 51.357f, 50.235f, 49.114f, 47.993f, 46.872f, 45.751f, 44.63f, 43.509f, 42.387f, 41.266f, 40.145f, 39.024f, 37.903f, 36.782f, 35.661f, 34.539f, 33.418f, 32.297f, 31.176f, 30.055f, 28.934f, 27.813f, 26.691f, 25.57f, 24.449f, 23.328f, 22.207f, 21.086f, 19.965f, 18.843f, 17.722f, 16.601f, 15.48f, 14.359f, 13.238f, 12.117f, 10.995f, 9.874f, 8.753f, 7.632f, 6.511f, 5.39f, 4.269f, 3.147f, 2.026f, 0.905f, -0.216f, -1.337f, -2.458f, -3.579f, -4.701f, -5.657f, -6.603f, -7.549f, -8.495f, -9.44f, -10.386f, -11.332f, -12.278f, -13.224f, -14.17f, -15.116f, -16.062f, -16.955f, -17.667f, -18.157f, -18.396f, -18.372f, -18.085f, -17.552f, -16.802f, -15.878f, -14.831f, -13.721f, -12.6f, -11.479f, -10.358f, -9.237f, -8.116f, -6.995f, -5.873f, -4.752f, -3.631f, -2.51f, -1.389f, -0.268f, 0.853f, 1.975f, 3.096f, 4.217f, 5.338f, 6.459f, 7.58f, 8.701f, 9.823f, 10.944f, 12.065f, 13.186f, 14.307f, 15.428f, 16.549f, 17.67f, 18.792f, 19.913f, 21.034f, 22.155f, 23.276f, 24.397f, 25.518f, 26.64f, 27.761f, 28.882f, 30.003f, 31.124f, 32.245f, 33.366f, 34.488f, 35.609f, 36.73f, 37.851f, 38.972f, 40.093f, 41.214f, 42.336f, 43.457f, 44.578f, 45.699f, 46.82f, 47.941f, 49.062f, 50.184f, 51.305f, 52.426f, 53.547f, 54.668f, 55.789f, 56.91f, 58.032f, 59.153f, 60.274f, 61.395f, 62.516f, 63.637f, 64.758f, 65.88f, 67.001f, 68.122f, 69.243f, 70.364f, 71.485f, 72.606f, 73.728f, 74.849f, 75.97f, 77.091f, 78.212f, 79.333f, 80.454f, 81.576f};
    private static final float[] ROT_X = new float[]{0.0f, -13.61f, -27.22f, -40.83f, -54.45f, -68.06f, -81.67f, -95.28f, -108.89f, -122.5f, -136.11f, -146.37f, -146.37f, -146.37f, -146.37f, -146.37f, -146.37f, -146.37f, -146.37f, -146.37f, -146.37f, -146.37f, -146.37f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -212.47f, -212.47f, -212.47f, -212.47f, -212.47f, -212.47f, -212.47f, -212.47f, -212.47f, -212.47f, -212.47f, -212.47f, -223.62f, -237.23f, -250.84f, -264.45f, -278.06f, -291.67f, -305.28f, -318.9f, -332.51f, -346.12f, -359.73f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f, -360.04f};
    private static final float[] TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return M10BookerRenderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return M10BookerRenderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return M10BookerRenderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.754386f;
    }
        public int period(){return 100;}
    }
    private static final class M1A2SEPV2Renderer implements Curve {
    private static final float[] MOVE_Y = new float[]{21.9215f, 21.7503f, 21.2463f, 20.437f, 19.367f, 18.095f, 16.691f, 15.2321f, 13.7985f, 12.4689f, 11.3164f, 10.4027f, 9.5904f, 8.778f, 7.9657f, 7.1533f, 6.3409f, 5.5286f, 4.7162f, 3.9039f, 3.0915f, 2.2792f, 1.518f, 1.0373f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.883f, 0.8832f, 1.0631f, 1.5745f, 2.1769f, 2.7792f, 3.3816f, 3.9839f, 4.5863f, 5.1886f, 5.791f, 6.3933f, 6.9957f, 7.598f, 8.2004f, 8.8028f, 9.4051f, 10.0075f, 10.6098f, 11.204f, 12.0097f, 13.1014f, 14.4056f, 15.8346f, 17.2919f, 18.6794f, 19.9036f, 20.8821f, 21.5489f, 21.8592f, 21.8793f, 21.8798f, 21.8803f, 21.8808f, 21.8813f, 21.8818f, 21.8823f, 21.8828f, 21.8833f, 21.8838f, 21.8843f, 21.8848f, 21.8853f, 21.8858f, 21.8863f, 21.8868f, 21.8873f, 21.8878f, 21.8883f, 21.8888f, 21.8893f, 21.8898f, 21.8902f, 21.8907f, 21.8912f, 21.8917f, 21.8922f, 21.8927f, 21.8932f, 21.8937f, 21.8942f, 21.8947f, 21.8952f, 21.8957f, 21.8962f, 21.8967f, 21.8972f, 21.8977f, 21.8982f, 21.8987f, 21.8992f, 21.8997f, 21.9002f, 21.9007f, 21.9012f, 21.9017f, 21.9022f, 21.9027f, 21.9031f, 21.9036f, 21.9041f, 21.9046f, 21.9051f, 21.9056f, 21.9061f, 21.9066f, 21.9071f, 21.9076f, 21.9081f, 21.9086f, 21.9091f, 21.9096f, 21.9101f, 21.9106f, 21.9111f, 21.9116f, 21.9121f, 21.9126f, 21.9131f, 21.9136f, 21.9141f, 21.9146f, 21.9151f, 21.9155f, 21.916f, 21.9165f, 21.917f, 21.9175f, 21.918f, 21.9185f, 21.919f, 21.9195f, 21.92f, 21.9205f, 21.921f, 21.9215f};
    private static final float[] MOVE_Z = new float[]{56.389f, 57.8392f, 59.2098f, 60.4253f, 61.419f, 62.1364f, 62.5379f, 62.6015f, 62.3237f, 61.7199f, 60.8231f, 59.6837f, 58.4661f, 57.2486f, 56.0311f, 54.8136f, 53.596f, 52.3785f, 51.161f, 49.9435f, 48.7259f, 47.5084f, 46.2601f, 44.881f, 43.4287f, 41.9651f, 40.5014f, 39.0378f, 37.5741f, 36.1104f, 34.6468f, 33.1831f, 31.7195f, 30.2558f, 28.7922f, 27.3285f, 25.8648f, 24.4012f, 22.9375f, 21.4739f, 20.0102f, 18.5466f, 17.0829f, 15.6192f, 14.1556f, 12.6919f, 11.2283f, 9.7646f, 8.301f, 6.8373f, 5.3736f, 3.91f, 2.4463f, 0.9827f, -0.481f, -1.9446f, -3.4083f, -4.8719f, -6.3356f, -7.7993f, -9.2629f, -10.7266f, -12.1902f, -13.6539f, -15.1175f, -16.5812f, -18.0449f, -19.5085f, -20.9722f, -22.4358f, -23.8995f, -25.3631f, -26.8268f, -28.2905f, -29.7541f, -31.2178f, -32.6814f, -34.1451f, -35.6087f, -37.0724f, -38.5361f, -39.9997f, -41.4634f, -42.927f, -44.3907f, -45.8543f, -47.318f, -48.7673f, -50.1352f, -51.4691f, -52.8031f, -54.1371f, -55.471f, -56.805f, -58.139f, -59.4729f, -60.8069f, -62.1409f, -63.4748f, -64.8088f, -66.1428f, -67.4767f, -68.8107f, -70.1447f, -71.482f, -72.699f, -73.6677f, -74.3228f, -74.6202f, -74.5398f, -74.087f, -73.2924f, -72.2094f, -70.9111f, -69.4849f, -68.0218f, -66.5582f, -65.0945f, -63.6309f, -62.1672f, -60.7035f, -59.2399f, -57.7762f, -56.3126f, -54.8489f, -53.3853f, -51.9216f, -50.4579f, -48.9943f, -47.5306f, -46.067f, -44.6033f, -43.1397f, -41.676f, -40.2124f, -38.7487f, -37.285f, -35.8214f, -34.3577f, -32.8941f, -31.4304f, -29.9668f, -28.5031f, -27.0394f, -25.5758f, -24.1121f, -22.6485f, -21.1848f, -19.7212f, -18.2575f, -16.7938f, -15.3302f, -13.8665f, -12.4029f, -10.9392f, -9.4756f, -8.0119f, -6.5482f, -5.0846f, -3.6209f, -2.1573f, -0.6936f, 0.77f, 2.2337f, 3.6974f, 5.161f, 6.6247f, 8.0883f, 9.552f, 11.0156f, 12.4793f, 13.943f, 15.4066f, 16.8703f, 18.3339f, 19.7976f, 21.2612f, 22.7249f, 24.1885f, 25.6522f, 27.1159f, 28.5795f, 30.0432f, 31.5068f, 32.9705f, 34.4341f, 35.8978f, 37.3615f, 38.8251f, 40.2888f, 41.7524f, 43.2161f, 44.6797f, 46.1434f, 47.6071f, 49.0707f, 50.5344f, 51.998f, 53.4617f, 54.9253f, 56.389f};
    private static final float[] ROT_X = new float[]{0.0f, -13.462f, -26.9239f, -40.3859f, -53.8479f, -67.3099f, -80.7718f, -94.2338f, -107.6958f, -121.1578f, -134.6197f, -146.288f, -146.288f, -146.288f, -146.288f, -146.288f, -146.288f, -146.288f, -146.288f, -146.288f, -146.288f, -146.288f, -154.2062f, -167.365f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.4945f, -193.6533f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -204.3016f, -206.0457f, -220.9597f, -235.8737f, -250.7876f, -265.7016f, -280.6156f, -295.5296f, -310.4436f, -325.3576f, -340.2716f, -355.1856f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f, -359.9806f};
    private static final float[] TIMES = new float[]{0.0f, 0.4f, 0.8f, 1.2f, 1.6f, 2.0f, 2.4f, 2.8f, 3.2f, 3.6f, 4.0f, 4.4f, 4.8f, 5.2f, 5.6f, 6.0f, 6.4f, 6.8f, 7.2f, 7.6f, 8.0f, 8.4f, 8.8f, 9.2f, 9.6f, 10.0f, 10.4f, 10.8f, 11.2f, 11.6f, 12.0f, 12.4f, 12.8f, 13.2f, 13.6f, 14.0f, 14.4f, 14.8f, 15.2f, 15.6f, 16.0f, 16.4f, 16.8f, 17.2f, 17.6f, 18.0f, 18.4f, 18.8f, 19.2f, 19.6f, 20.0f, 20.4f, 20.8f, 21.2f, 21.6f, 22.0f, 22.4f, 22.8f, 23.2f, 23.6f, 24.0f, 24.4f, 24.8f, 25.2f, 25.6f, 26.0f, 26.4f, 26.8f, 27.2f, 27.6f, 28.0f, 28.4f, 28.8f, 29.2f, 29.6f, 30.0f, 30.4f, 30.8f, 31.2f, 31.6f, 32.0f, 32.4f, 32.8f, 33.2f, 33.6f, 34.0f, 34.4f, 34.8f, 35.2f, 35.6f, 36.0f, 36.4f, 36.8f, 37.2f, 37.6f, 38.0f, 38.4f, 38.8f, 39.2f, 39.6f, 40.0f, 40.4f, 40.8f, 41.2f, 41.6f, 42.0f, 42.4f, 42.8f, 43.2f, 43.6f, 44.0f, 44.4f, 44.8f, 45.2f, 45.6f, 46.0f, 46.4f, 46.8f, 47.2f, 47.6f, 48.0f, 48.4f, 48.8f, 49.2f, 49.6f, 50.0f, 50.4f, 50.8f, 51.2f, 51.6f, 52.0f, 52.4f, 52.8f, 53.2f, 53.6f, 54.0f, 54.4f, 54.8f, 55.2f, 55.6f, 56.0f, 56.4f, 56.8f, 57.2f, 57.6f, 58.0f, 58.4f, 58.8f, 59.2f, 59.6f, 60.0f, 60.4f, 60.8f, 61.2f, 61.6f, 62.0f, 62.4f, 62.8f, 63.2f, 63.6f, 64.0f, 64.4f, 64.8f, 65.2f, 65.6f, 66.0f, 66.4f, 66.8f, 67.2f, 67.6f, 68.0f, 68.4f, 68.8f, 69.2f, 69.6f, 70.0f, 70.4f, 70.8f, 71.2f, 71.6f, 72.0f, 72.4f, 72.8f, 73.2f, 73.6f, 74.0f, 74.4f, 74.8f, 75.2f, 75.6f, 76.0f, 76.4f, 76.8f, 77.2f, 77.6f, 78.0f, 78.4f, 78.8f, 79.2f, 79.6f, 80.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.4f, 0.8f, 1.2f, 1.6f, 2.0f, 2.4f, 2.8f, 3.2f, 3.6f, 4.0f, 4.4f, 4.8f, 5.2f, 5.6f, 6.0f, 6.4f, 6.8f, 7.2f, 7.6f, 8.0f, 8.4f, 8.8f, 9.2f, 9.6f, 10.0f, 10.4f, 10.8f, 11.2f, 11.6f, 12.0f, 12.4f, 12.8f, 13.2f, 13.6f, 14.0f, 14.4f, 14.8f, 15.2f, 15.6f, 16.0f, 16.4f, 16.8f, 17.2f, 17.6f, 18.0f, 18.4f, 18.8f, 19.2f, 19.6f, 20.0f, 20.4f, 20.8f, 21.2f, 21.6f, 22.0f, 22.4f, 22.8f, 23.2f, 23.6f, 24.0f, 24.4f, 24.8f, 25.2f, 25.6f, 26.0f, 26.4f, 26.8f, 27.2f, 27.6f, 28.0f, 28.4f, 28.8f, 29.2f, 29.6f, 30.0f, 30.4f, 30.8f, 31.2f, 31.6f, 32.0f, 32.4f, 32.8f, 33.2f, 33.6f, 34.0f, 34.4f, 34.8f, 35.2f, 35.6f, 36.0f, 36.4f, 36.8f, 37.2f, 37.6f, 38.0f, 38.4f, 38.8f, 39.2f, 39.6f, 40.0f, 40.4f, 40.8f, 41.2f, 41.6f, 42.0f, 42.4f, 42.8f, 43.2f, 43.6f, 44.0f, 44.4f, 44.8f, 45.2f, 45.6f, 46.0f, 46.4f, 46.8f, 47.2f, 47.6f, 48.0f, 48.4f, 48.8f, 49.2f, 49.6f, 50.0f, 50.4f, 50.8f, 51.2f, 51.6f, 52.0f, 52.4f, 52.8f, 53.2f, 53.6f, 54.0f, 54.4f, 54.8f, 55.2f, 55.6f, 56.0f, 56.4f, 56.8f, 57.2f, 57.6f, 58.0f, 58.4f, 58.8f, 59.2f, 59.6f, 60.0f, 60.4f, 60.8f, 61.2f, 61.6f, 62.0f, 62.4f, 62.8f, 63.2f, 63.6f, 64.0f, 64.4f, 64.8f, 65.2f, 65.6f, 66.0f, 66.4f, 66.8f, 67.2f, 67.6f, 68.0f, 68.4f, 68.8f, 69.2f, 69.6f, 70.0f, 70.4f, 70.8f, 71.2f, 71.6f, 72.0f, 72.4f, 72.8f, 73.2f, 73.6f, 74.0f, 74.4f, 74.8f, 75.2f, 75.6f, 76.0f, 76.4f, 76.8f, 77.2f, 77.6f, 78.0f, 78.4f, 78.8f, 79.2f, 79.6f, 80.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return M1A2SEPV2Renderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return M1A2SEPV2Renderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return M1A2SEPV2Renderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.4285715f;
    }
        public int period(){return 80;}
    }
    private static final class M1a1hcRenderer implements Curve {
    private static final float[] MOVE_Y = new float[]{0.0f, 0.11f, 0.29f, 1.11f, 3.01f, 5.11f, 7.11f, 9.21f, 10.36f, 11.91f, 13.58f, 15.81f, 18.08f, 19.71f, 21.12f, 21.41f, 21.41f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.71f, 21.86f, 21.81f, 21.46f, 20.11f, 18.61f, 16.31f, 14.01f, 12.31f, 9.41f, 7.01f, 4.51f, 2.1135f, 1.09f, 0.4f, -0.1f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    private static final float[] MOVE_Z = new float[]{0.0f, -4.49f, -6.69f, -9.09f, -13.39f, -17.69f, -21.89f, -26.19f, -28.29f, -29.99f, -31.31f, -31.49f, -31.27f, -29.99f, -28.37f, -26.19f, -21.69f, -17.29f, -12.79f, -8.29f, -3.79f, 0.71f, 5.21f, 9.71f, 14.21f, 18.71f, 23.21f, 27.71f, 32.21f, 36.71f, 41.21f, 45.71f, 50.21f, 54.71f, 59.21f, 63.71f, 68.21f, 72.71f, 77.21f, 81.71f, 86.21f, 90.61f, 92.86f, 95.11f, 97.46f, 99.21f, 100.46f, 100.91f, 100.66f, 99.81f, 96.61f, 92.81f, 89.01f, 85.2095f, 83.29f, 81.0f, 78.75f, 76.5f, 72.0f, 67.5f, 63.0f, 58.5f, 54.0f, 49.5f, 45.0f, 40.5f, 36.0f, 31.5f, 27.0f, 22.5f, 18.0f, 13.5f, 9.0f, 4.5f, 0.0f};
    private static final float[] ROT_X = new float[]{360.0f, 357.0f, 339.0f, 334.0f, 334.0f, 315.0f, 270.0f, 225.0f, 181.0f, 177.0f, 180.0f, 180.0f, 182.5f, 186.5f, 176.5f, 138.5f, 92.5f, 54.0f, 33.0f, 33.0f, 13.0f, 0.0f, 0.0f};
    private static final float[] TIMES = new float[]{0.0f, 1.5833f, 2.3333f, 3.1667f, 4.75f, 6.3333f, 7.9167f, 9.5417f, 10.3333f, 11.125f, 11.875f, 12.7083f, 13.4583f, 14.2917f, 15.0417f, 15.875f, 17.4583f, 19.0417f, 20.625f, 22.2083f, 23.7917f, 25.4167f, 27.0f, 28.5833f, 30.1667f, 31.75f, 33.3333f, 34.9167f, 36.5f, 38.0833f, 39.6667f, 41.25f, 42.875f, 44.4583f, 46.0417f, 47.625f, 49.2083f, 50.7917f, 52.375f, 53.9583f, 55.5417f, 57.125f, 57.9167f, 58.75f, 59.5417f, 60.3333f, 61.125f, 61.9167f, 62.7083f, 63.5f, 65.0833f, 66.6667f, 68.25f, 69.8333f, 70.5833f, 71.4167f, 72.2083f, 73.0f, 74.5833f, 76.2083f, 77.7917f, 79.375f, 80.9583f, 82.5417f, 84.125f, 85.7083f, 87.2917f, 88.875f, 90.4583f, 92.0833f, 93.6667f, 95.25f, 96.8333f, 98.4167f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 1.5833f, 3.1667f, 4.75f, 9.5417f, 11.125f, 12.7083f, 14.2917f, 15.875f, 17.4583f, 19.0417f, 53.9583f, 55.5417f, 57.125f, 58.75f, 60.3333f, 61.9167f, 63.5f, 65.0833f, 69.8333f, 71.4167f, 73.0f, 100.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return M1a1hcRenderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return M1a1hcRenderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return M1a1hcRenderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.7857143f;
    }
        public int period(){return 100;}
    }
    private static final class M1a2sepv1Renderer implements Curve {
    private static final float[] MOVE_Y = new float[]{24.4269f, 24.2922f, 23.8926f, 23.2422f, 22.3635f, 21.287f, 20.0502f, 18.6962f, 17.2719f, 15.8269f, 14.4115f, 13.0749f, 11.8635f, 10.8195f, 9.9858f, 9.2663f, 8.5467f, 7.8271f, 7.1076f, 6.388f, 5.6684f, 4.9489f, 4.2293f, 3.5098f, 2.7902f, 2.0706f, 1.3511f, 0.6988f, 0.311f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2009f, 0.2701f, 0.6076f, 1.2074f, 1.8472f, 2.4871f, 3.1269f, 3.7668f, 4.4066f, 5.0464f, 5.6863f, 6.3261f, 6.966f, 7.6058f, 8.2456f, 8.8855f, 9.5253f, 10.1631f, 11.0003f, 12.0687f, 13.3195f, 14.6954f, 16.1334f, 17.5676f, 18.9324f, 20.1651f, 21.2095f, 22.0176f, 22.5525f, 22.7897f, 22.8153f, 22.8349f, 22.8546f, 22.8742f, 22.8939f, 22.9135f, 22.9332f, 22.9528f, 22.9725f, 22.9922f, 23.0118f, 23.0315f, 23.0511f, 23.0708f, 23.0904f, 23.1101f, 23.1297f, 23.1494f, 23.169f, 23.1887f, 23.2083f, 23.228f, 23.2477f, 23.2673f, 23.287f, 23.3066f, 23.3263f, 23.3459f, 23.3656f, 23.3852f, 23.4049f, 23.4245f, 23.4442f, 23.4638f, 23.4835f, 23.5032f, 23.5228f, 23.5425f, 23.5621f, 23.5818f, 23.6014f, 23.6211f, 23.6407f, 23.6604f, 23.68f, 23.6997f, 23.7194f, 23.739f, 23.7587f, 23.7783f, 23.798f, 23.8176f, 23.8373f, 23.8569f, 23.8766f, 23.8962f, 23.9159f, 23.9355f, 23.9552f, 23.9749f, 23.9945f, 24.0142f, 24.0338f, 24.0535f, 24.0731f, 24.0928f, 24.1124f, 24.1321f, 24.1517f, 24.1714f, 24.1911f, 24.2107f, 24.2304f, 24.25f, 24.2697f, 24.2893f, 24.309f, 24.3286f, 24.3483f, 24.3679f, 24.3876f, 24.4072f, 24.4269f};
    private static final float[] MOVE_Z = new float[]{57.5271f, 58.966f, 60.3548f, 61.6453f, 62.7926f, 63.7568f, 64.5044f, 65.0094f, 65.2542f, 65.2304f, 64.9386f, 64.3892f, 63.6011f, 62.6019f, 61.4222f, 60.1665f, 58.9108f, 57.6551f, 56.3994f, 55.1437f, 53.888f, 52.6323f, 51.3766f, 50.1209f, 48.8652f, 47.6095f, 46.3538f, 45.0644f, 43.6725f, 42.2316f, 40.7843f, 39.3371f, 37.8898f, 36.4426f, 34.9953f, 33.548f, 32.1008f, 30.6535f, 29.2063f, 27.759f, 26.3117f, 24.8645f, 23.4172f, 21.97f, 20.5227f, 19.0754f, 17.6282f, 16.1809f, 14.7337f, 13.2864f, 11.8391f, 10.3919f, 8.9446f, 7.4974f, 6.0501f, 4.6028f, 3.1556f, 1.7083f, 0.2611f, -1.1862f, -2.6335f, -4.0807f, -5.528f, -6.9752f, -8.4225f, -9.8698f, -11.317f, -12.7643f, -14.2115f, -15.6588f, -17.1061f, -18.5533f, -20.0006f, -21.4478f, -22.8951f, -24.3424f, -25.7896f, -27.2369f, -28.6841f, -30.1314f, -31.5787f, -33.0259f, -34.4732f, -35.9204f, -37.3677f, -38.815f, -40.2622f, -41.7095f, -43.1567f, -44.6008f, -46.0058f, -47.3213f, -48.6195f, -49.9176f, -51.2158f, -52.5139f, -53.812f, -55.1102f, -56.4083f, -57.7064f, -59.0046f, -60.3027f, -61.6009f, -62.899f, -64.1971f, -65.4959f, -66.673f, -67.6451f, -68.3676f, -68.8074f, -68.9444f, -68.7723f, -68.299f, -67.5461f, -66.5482f, -65.3509f, -64.0091f, -62.5842f, -61.1373f, -59.6901f, -58.243f, -56.7959f, -55.3488f, -53.9016f, -52.4545f, -51.0074f, -49.5603f, -48.1131f, -46.666f, -45.2189f, -43.7717f, -42.3246f, -40.8775f, -39.4304f, -37.9832f, -36.5361f, -35.089f, -33.6419f, -32.1947f, -30.7476f, -29.3005f, -27.8534f, -26.4062f, -24.9591f, -23.512f, -22.0649f, -20.6177f, -19.1706f, -17.7235f, -16.2763f, -14.8292f, -13.3821f, -11.935f, -10.4878f, -9.0407f, -7.5936f, -6.1465f, -4.6993f, -3.2522f, -1.8051f, -0.358f, 1.0892f, 2.5363f, 3.9834f, 5.4305f, 6.8777f, 8.3248f, 9.7719f, 11.2191f, 12.6662f, 14.1133f, 15.5604f, 17.0076f, 18.4547f, 19.9018f, 21.3489f, 22.7961f, 24.2432f, 25.6903f, 27.1374f, 28.5846f, 30.0317f, 31.4788f, 32.926f, 34.3731f, 35.8202f, 37.2673f, 38.7145f, 40.1616f, 41.6087f, 43.0558f, 44.503f, 45.9501f, 47.3972f, 48.8443f, 50.2915f, 51.7386f, 53.1857f, 54.6328f, 56.08f, 57.5271f};
    private static final float[] ROT_X = new float[]{0.0f, -10.6996f, -21.3992f, -32.0988f, -42.7984f, -53.498f, -64.1976f, -74.8972f, -85.5968f, -96.2964f, -106.996f, -117.6956f, -128.3952f, -139.0948f, -150.1857f, -150.1857f, -150.1857f, -150.1857f, -150.1857f, -150.1857f, -150.1857f, -150.1857f, -150.1857f, -150.1857f, -150.1857f, -150.1857f, -150.1857f, -158.7888f, -170.0707f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -187.8663f, -199.1482f, -206.2383f, -206.2383f, -206.2383f, -206.2383f, -206.2383f, -206.2383f, -206.2383f, -206.2383f, -206.2383f, -206.2383f, -206.2383f, -206.2383f, -206.2383f, -206.2383f, -209.2769f, -221.5616f, -233.8463f, -246.1311f, -258.4158f, -270.7005f, -282.9852f, -295.27f, -307.5547f, -319.8394f, -332.1241f, -344.4089f, -356.6936f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f, -359.2219f};
    private static final float[] TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return M1a2sepv1Renderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return M1a2sepv1Renderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return M1a2sepv1Renderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.7857143f;
    }
        public int period(){return 100;}
    }
    private static final class M3A3Renderer implements Curve {
    private static final float[] MOVE_Y = new float[]{18.4688f, 18.3352f, 17.9403f, 17.302f, 16.4488f, 15.4191f, 14.2592f, 13.0213f, 11.7608f, 10.5346f, 9.3976f, 8.4009f, 7.5894f, 6.9956f, 6.4825f, 5.9693f, 5.4561f, 4.943f, 4.4298f, 3.9167f, 3.4035f, 2.8903f, 2.3772f, 1.864f, 1.3508f, 0.91f, 0.7206f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7843f, 1.1076f, 1.6244f, 2.1532f, 2.6821f, 3.2109f, 3.7397f, 4.2685f, 4.7974f, 5.3262f, 5.855f, 6.3838f, 6.9127f, 7.4415f, 7.9703f, 8.592f, 9.4295f, 10.4469f, 11.5983f, 12.8321f, 14.0929f, 15.3238f, 16.4698f, 17.4792f, 18.3067f, 18.9152f, 19.2772f, 19.3774f, 19.3666f, 19.3558f, 19.345f, 19.3341f, 19.3233f, 19.3125f, 19.3017f, 19.2909f, 19.2801f, 19.2692f, 19.2584f, 19.2476f, 19.2368f, 19.226f, 19.2152f, 19.2043f, 19.1935f, 19.1827f, 19.1719f, 19.1611f, 19.1503f, 19.1394f, 19.1286f, 19.1178f, 19.107f, 19.0962f, 19.0854f, 19.0745f, 19.0637f, 19.0529f, 19.0421f, 19.0313f, 19.0205f, 19.0096f, 18.9988f, 18.988f, 18.9772f, 18.9664f, 18.9556f, 18.9447f, 18.9339f, 18.9231f, 18.9123f, 18.9015f, 18.8907f, 18.8798f, 18.869f, 18.8582f, 18.8474f, 18.8366f, 18.8258f, 18.8149f, 18.8041f, 18.7933f, 18.7825f, 18.7717f, 18.7609f, 18.75f, 18.7392f, 18.7284f, 18.7176f, 18.7068f, 18.696f, 18.6851f, 18.6743f, 18.6635f, 18.6527f, 18.6419f, 18.6311f, 18.6202f, 18.6094f, 18.5986f, 18.5878f, 18.577f, 18.5662f, 18.5553f, 18.5445f, 18.5337f, 18.5229f, 18.5121f, 18.5013f, 18.4904f, 18.4796f, 18.4688f};
    private static final float[] MOVE_Z = new float[]{53.4636f, 54.7172f, 55.9145f, 57.0017f, 57.9299f, 58.6574f, 59.1516f, 59.3901f, 59.3624f, 59.0696f, 58.5249f, 57.7528f, 56.788f, 55.6754f, 54.5212f, 53.3671f, 52.2129f, 51.0587f, 49.9045f, 48.7504f, 47.5962f, 46.442f, 45.2878f, 44.1337f, 42.9795f, 41.7977f, 40.5513f, 39.2882f, 38.0251f, 36.762f, 35.4989f, 34.2358f, 32.9727f, 31.7096f, 30.4464f, 29.1833f, 27.9202f, 26.6571f, 25.394f, 24.1309f, 22.8678f, 21.6047f, 20.3415f, 19.0784f, 17.8153f, 16.5522f, 15.2891f, 14.026f, 12.7629f, 11.4997f, 10.2366f, 8.9735f, 7.7104f, 6.4473f, 5.1842f, 3.9211f, 2.658f, 1.3948f, 0.1317f, -1.1314f, -2.3945f, -3.6576f, -4.9207f, -6.1838f, -7.447f, -8.7101f, -9.9732f, -11.2363f, -12.4994f, -13.7625f, -15.0256f, -16.2888f, -17.5519f, -18.815f, -20.0781f, -21.3412f, -22.6043f, -23.8674f, -25.1305f, -26.3937f, -27.6568f, -28.9199f, -30.183f, -31.4461f, -32.7092f, -33.9723f, -35.2355f, -36.4986f, -37.7617f, -39.0212f, -40.2398f, -41.3921f, -42.5392f, -43.6863f, -44.8334f, -45.9805f, -47.1276f, -48.2746f, -49.4217f, -50.5688f, -51.7159f, -52.863f, -54.01f, -55.1571f, -56.2541f, -57.1965f, -57.9411f, -58.4545f, -58.7138f, -58.7071f, -58.4348f, -57.9092f, -57.1539f, -56.2027f, -55.0985f, -53.8909f, -52.6341f, -51.371f, -50.1079f, -48.8449f, -47.5818f, -46.3187f, -45.0556f, -43.7926f, -42.5295f, -41.2664f, -40.0034f, -38.7403f, -37.4772f, -36.2142f, -34.9511f, -33.688f, -32.425f, -31.1619f, -29.8988f, -28.6358f, -27.3727f, -26.1096f, -24.8466f, -23.5835f, -22.3204f, -21.0574f, -19.7943f, -18.5312f, -17.2682f, -16.0051f, -14.742f, -13.479f, -12.2159f, -10.9528f, -9.6898f, -8.4267f, -7.1636f, -5.9006f, -4.6375f, -3.3744f, -2.1114f, -0.8483f, 0.4148f, 1.6778f, 2.9409f, 4.204f, 5.467f, 6.7301f, 7.9932f, 9.2562f, 10.5193f, 11.7824f, 13.0454f, 14.3085f, 15.5716f, 16.8346f, 18.0977f, 19.3608f, 20.6239f, 21.8869f, 23.15f, 24.4131f, 25.6761f, 26.9392f, 28.2023f, 29.4653f, 30.7284f, 31.9915f, 33.2545f, 34.5176f, 35.7807f, 37.0437f, 38.3068f, 39.5699f, 40.8329f, 42.096f, 43.3591f, 44.6221f, 45.8852f, 47.1483f, 48.4113f, 49.6744f, 50.9375f, 52.2005f, 53.4636f};
    private static final float[] ROT_X = new float[]{0.0f, -12.1681f, -24.3362f, -36.5043f, -48.6725f, -60.8406f, -73.0087f, -85.1768f, -97.3449f, -109.513f, -121.6811f, -133.8493f, -146.0174f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -165.2765f, -177.4446f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -188.7751f, -200.9432f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -215.5466f, -227.7147f, -239.8829f, -252.051f, -264.2191f, -276.3872f, -288.5553f, -300.7234f, -312.8916f, -325.0597f, -337.2278f, -349.3959f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f};
    private static final float[] TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return M3A3Renderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return M3A3Renderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return M3A3Renderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.7857143f;
    }
        public int period(){return 100;}
    }
    private static final class MstaModel implements Curve {
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{176.83f, 16.02f, 48.08f}, {127.69f, 14.1f, 51.84f}, {72.89f, 9.93f, 52.5f}, {33.3f, 6.8f, 49.6f}, {33.3f, 4.39f, 45.93f}, {33.3f, 1.98f, 42.27f}, {2.73f, 0.21f, 38.34f}, {0.0f, 0.2f, 33.95f}, {0.0f, 0.2f, 29.56f}, {0.0f, 0.2f, 25.19f}, {0.0f, 0.2f, 20.8f}, {0.0f, 0.2f, 16.41f}, {0.0f, 0.2f, 12.03f}, {0.0f, 0.2f, 7.65f}, {0.0f, 0.2f, 3.26f}, {0.0f, 0.2f, -1.12f}, {0.0f, 0.2f, -5.51f}, {0.0f, 0.2f, -9.89f}, {0.0f, 0.2f, -14.27f}, {0.0f, 0.2f, -18.66f}, {0.0f, 0.2f, -23.04f}, {0.0f, 0.2f, -27.42f}, {0.0f, 0.2f, -31.81f}, {-36.13f, 1.21f, -35.97f}, {-37.69f, 3.89f, -39.44f}, {-37.69f, 6.57f, -42.91f}, {-37.69f, 9.25f, -46.38f}, {-75.27f, 12.58f, -49.05f}, {-133.72f, 16.63f, -47.96f}, {-179.56f, 17.99f, -43.96f}, {177.84f, 17.84f, -39.58f}, {177.9f, 17.68f, -35.2f}, {178.01f, 17.52f, -30.82f}, {178.13f, 17.37f, -26.44f}, {178.24f, 17.23f, -22.06f}, {178.3f, 17.1f, -17.67f}, {178.41f, 16.97f, -13.29f}, {178.52f, 16.86f, -8.91f}, {178.64f, 16.74f, -4.52f}, {178.7f, 16.64f, -0.14f}, {178.81f, 16.55f, 4.24f}, {178.92f, 16.46f, 8.63f}, {179.04f, 16.38f, 13.01f}, {179.09f, 16.31f, 17.39f}, {179.21f, 16.24f, 21.78f}, {179.32f, 16.19f, 26.15f}, {179.43f, 16.14f, 30.54f}, {179.49f, 16.1f, 34.93f}, {179.6f, 16.06f, 39.3f}, {179.72f, 16.04f, 43.69f}, {176.83f, 16.02f, 48.08f}};
    private static final float START_Y = 16.02f;
    private static final float START_Z = 48.08f;
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)50);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 16.02f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 48.08f;
    }
        public int period(){return 100;}
    }
    private static final class OplotModel implements Curve {
    private static final int TRACK_COUNT = 61;
    private static final int MAX_IDX = 61;
    private static final float[][] KEYFRAMES = new float[][]{{176.97f, 19.4f, 73.22f}, {138.47f, 18.02f, 76.86f}, {99.37f, 14.57f, 78.68f}, {55.4f, 10.79f, 77.75f}, {20.17f, 8.57f, 74.56f}, {26.89f, 6.77f, 71.0f}, {26.89f, 4.97f, 67.45f}, {26.89f, 3.17f, 63.9f}, {6.93f, 1.54f, 60.3f}, {0.0f, 1.45f, 56.32f}, {0.0f, 1.45f, 52.34f}, {0.0f, 1.45f, 48.36f}, {0.0f, 1.45f, 44.38f}, {0.0f, 1.45f, 40.4f}, {0.0f, 1.45f, 36.42f}, {0.0f, 1.45f, 32.44f}, {0.0f, 1.45f, 28.46f}, {0.0f, 1.45f, 24.48f}, {0.0f, 1.45f, 20.5f}, {0.0f, 1.45f, 16.52f}, {0.0f, 1.45f, 12.54f}, {0.0f, 1.45f, 8.55f}, {0.0f, 1.45f, 4.57f}, {0.0f, 1.45f, 0.59f}, {0.0f, 1.45f, -3.39f}, {0.0f, 1.45f, -7.37f}, {0.0f, 1.45f, -11.35f}, {0.0f, 1.45f, -15.33f}, {-17.24f, 1.65f, -19.29f}, {-33.51f, 3.79f, -22.64f}, {-33.51f, 5.99f, -25.96f}, {-33.51f, 8.19f, -29.27f}, {-37.5f, 10.16f, -32.71f}, {-82.5f, 13.51f, -34.66f}, {-127.5f, 17.25f, -33.65f}, {-173.02f, 19.16f, -30.27f}, {-179.81f, 19.21f, -26.29f}, {-179.89f, 19.22f, -22.31f}, {-179.89f, 19.22f, -18.33f}, {-179.89f, 19.23f, -14.35f}, {-179.89f, 19.24f, -10.37f}, {-179.89f, 19.25f, -6.39f}, {-179.89f, 19.25f, -2.41f}, {-179.89f, 19.26f, 1.57f}, {-179.89f, 19.27f, 5.55f}, {-179.89f, 19.28f, 9.53f}, {-179.89f, 19.28f, 13.51f}, {-179.89f, 19.29f, 17.49f}, {-179.89f, 19.3f, 21.47f}, {-179.89f, 19.31f, 25.45f}, {-179.89f, 19.32f, 29.43f}, {-179.89f, 19.32f, 33.41f}, {-179.89f, 19.33f, 37.39f}, {-179.89f, 19.34f, 41.37f}, {-179.89f, 19.35f, 45.35f}, {-179.89f, 19.35f, 49.33f}, {-179.89f, 19.36f, 53.32f}, {-179.89f, 19.37f, 57.3f}, {-179.89f, 19.38f, 61.28f}, {-179.89f, 19.38f, 65.26f}, {-179.89f, 19.39f, 69.24f}, {176.97f, 19.4f, 73.22f}};
    private static final float START_Y = 19.4f;
    private static final float START_Z = 73.22f;
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 122;
        float normalized = t / (float)wrapRange * 61.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)61);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)61);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 19.4f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 73.22f;
    }
        public int period(){return 122;}
    }
    private static final class PumaModel implements Curve {
    private static final int TRACK_COUNT = 61;
    private static final int MAX_IDX = 61;
    private static final float[][] KEYFRAMES = new float[][]{{176.89f, 22.52f, 52.45f}, {138.8f, 21.19f, 56.14f}, {100.02f, 17.82f, 58.14f}, {61.24f, 13.95f, 57.54f}, {39.42f, 11.13f, 54.75f}, {39.42f, 8.59f, 51.66f}, {39.42f, 6.05f, 48.57f}, {39.42f, 3.51f, 45.48f}, {7.41f, 1.96f, 41.87f}, {0.0f, 1.93f, 37.87f}, {0.0f, 1.93f, 33.87f}, {0.0f, 1.93f, 29.87f}, {0.0f, 1.93f, 25.87f}, {0.0f, 1.93f, 21.87f}, {0.0f, 1.93f, 17.87f}, {0.0f, 1.93f, 13.87f}, {0.0f, 1.93f, 9.87f}, {0.0f, 1.93f, 5.86f}, {0.0f, 1.93f, 1.86f}, {0.0f, 1.93f, -2.14f}, {0.0f, 1.93f, -6.14f}, {0.0f, 1.93f, -10.14f}, {0.0f, 1.93f, -14.14f}, {0.0f, 1.93f, -18.14f}, {0.0f, 1.93f, -22.14f}, {0.0f, 1.93f, -26.14f}, {0.0f, 1.93f, -30.14f}, {-7.15f, 2.02f, -34.13f}, {-32.43f, 3.71f, -37.72f}, {-32.43f, 5.85f, -41.1f}, {-32.43f, 8.0f, -44.47f}, {-32.43f, 10.14f, -47.85f}, {-32.61f, 12.29f, -51.23f}, {-70.12f, 15.27f, -53.77f}, {-109.19f, 19.19f, -53.84f}, {-148.26f, 22.25f, -51.4f}, {-178.23f, 23.1f, -47.56f}, {179.76f, 23.09f, -43.56f}, {179.66f, 23.07f, -39.56f}, {179.66f, 23.05f, -35.56f}, {179.66f, 23.02f, -31.56f}, {179.66f, 23.0f, -27.56f}, {179.66f, 22.97f, -23.56f}, {179.66f, 22.95f, -19.56f}, {179.66f, 22.93f, -15.56f}, {179.66f, 22.9f, -11.56f}, {179.66f, 22.88f, -7.56f}, {179.66f, 22.86f, -3.56f}, {179.66f, 22.83f, 0.44f}, {179.66f, 22.81f, 4.44f}, {179.66f, 22.78f, 8.44f}, {179.66f, 22.76f, 12.44f}, {179.66f, 22.74f, 16.45f}, {179.66f, 22.71f, 20.45f}, {179.66f, 22.69f, 24.45f}, {179.66f, 22.66f, 28.45f}, {179.66f, 22.64f, 32.45f}, {179.66f, 22.62f, 36.45f}, {179.66f, 22.59f, 40.45f}, {179.66f, 22.57f, 44.45f}, {179.66f, 22.55f, 48.45f}, {176.89f, 22.52f, 52.45f}};
    private static final float START_Y = 22.52f;
    private static final float START_Z = 52.45f;
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 122;
        float normalized = t / (float)wrapRange * 61.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)61);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)61);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 22.52f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 52.45f;
    }
        public int period(){return 122;}
    }
    private static final class RailTankRenderer implements Curve {
    public float getBoneRotX(float t) {
        if (t <= 37.6667f) {
            return 0.0f;
        }
        if (t <= 38.5833f) {
            return Mth.lerp((float)((t - 37.6667f) / 0.9166031f), (float)0.0f, (float)-45.0f);
        }
        if (t <= 39.75f) {
            return -45.0f;
        }
        if (t <= 40.6667f) {
            return Mth.lerp((float)((t - 39.75f) / 0.91669846f), (float)-45.0f, (float)-90.0f);
        }
        if (t <= 41.6667f) {
            return -90.0f;
        }
        if (t <= 42.5f) {
            return -90.0f;
        }
        if (t <= 43.5f) {
            return Mth.lerp((float)(t - 42.5f), (float)-90.0f, (float)-135.0f);
        }
        if (t <= 44.5833f) {
            return -135.0f;
        }
        if (t <= 45.0833f) {
            return Mth.lerp((float)((t - 44.5833f) / 0.5f), (float)-135.0f, (float)-150.0f);
        }
        if (t <= 52.25f) {
            return -150.0f;
        }
        if (t <= 52.75f) {
            return Mth.lerp((float)((t - 52.25f) / 0.5f), (float)-150.0f, (float)-180.0f);
        }
        if (t <= 84.3333f) {
            return -180.0f;
        }
        if (t <= 84.9167f) {
            return Mth.lerp((float)((t - 84.3333f) / 0.58340454f), (float)-180.0f, (float)-210.0f);
        }
        if (t <= 91.75f) { return -210.0f; }
        if (t <= 92.5833f) {
            return Mth.lerp((float)((t - 91.75f) / (92.5833f - 91.75f)), (float)-210.0f, (float)-220.0f);
        }
        if (t <= 94.25f) {
            return -220.0f;
        }
        if (t <= 94.9167f) {
            return Mth.lerp((float)((t - 94.25f) / (94.9167f - 94.25f)), (float)-220.0f, (float)-243.33f);
        }
        if (t <= 95.75f) {
            return Mth.lerp((float)((t - 94.9167f) / 0.8332977f), (float)-243.33f, (float)-270.0f);
        }
        if (t <= 96.8333f) {
            return -270.0f;
        }
        if (t <= 97.5833f) {
            return Mth.lerp((float)((t - 96.8333f) / 0.75f), (float)-270.0f, (float)-315.0f);
        }
        if (t <= 98.8333f) {
            return -315.0f;
        }
        if (t <= 99.5833f) {
            return Mth.lerp((float)((t - 98.8333f) / 0.75f), (float)-315.0f, (float)-360.0f);
        }
        return 0.0f;
    }
    public float getBoneMoveY(float t) {
        if (t <= 37.6667f) {
            return 0.0f;
        }
        if (t <= 38.5833f) {
            return Mth.lerp((float)((t - 37.6667f) / 0.9166031f), (float)0.0f, (float)-1.8f);
        }
        if (t <= 40.3333f) {
            return Mth.lerp((float)((t - 38.5833f) / 1.75f), (float)-1.8f, (float)-4.1f);
        }
        if (t <= 42.9167f) {
            return Mth.lerp((float)((t - 40.3333f) / 2.583397f), (float)-4.1f, (float)-10.3f);
        }
        if (t <= 44.25f) {
            return Mth.lerp((float)((t - 42.9167f) / (44.25f - 42.9167f)), (float)-10.3f, (float)-12.9f);
        }
        if (t <= 52.4167f) {
            return Mth.lerp((float)((t - 44.25f) / 8.166698f), (float)-12.9f, (float)-23.96f);
        }
        if (t <= 84.5833f) {
            return -23.96f;
        }
        if (t <= 93.0f) {
            return Mth.lerp((float)((t - 84.5833f) / 8.416702f), (float)-23.96f, (float)-12.93f);
        }
        if (t <= 95.25f) {
            return Mth.lerp((float)((t - 93.0f) / 2.25f), (float)-12.93f, (float)-10.085f);
        }
        if (t <= 97.5f) {
            return Mth.lerp((float)((t - 95.25f) / 2.25f), (float)-10.085f, (float)-4.585f);
        }
        if (t <= 98.8333f) {
            return Mth.lerp((float)((t - 97.5f) / 1.3332977f), (float)-4.585f, (float)-1.165f);
        }
        if (t <= 99.25f) {
            return Mth.lerp((float)((t - 98.8333f) / 0.41670227f), (float)-1.165f, (float)-0.25f);
        }
        return Mth.lerp((float)((t - 99.25f) / 0.75f), (float)-0.25f, (float)0.0f);
    }
    public float getBoneMoveZ(float t) {
        if (t <= 37.6667f) {
            return Mth.lerp((float)(t / 37.6667f), (float)0.0f, (float)111.6f);
        }
        if (t <= 38.5833f) {
            return Mth.lerp((float)((t - 37.6667f) / 0.9166031f), (float)111.6f, (float)113.25f);
        }
        if (t <= 40.3333f) {
            return Mth.lerp((float)((t - 38.5833f) / 1.75f), (float)113.25f, (float)116.0f);
        }
        if (t <= 42.9167f) {
            return 116.0f;
        }
        if (t <= 44.25f) {
            return Mth.lerp((float)((t - 42.9167f) / (44.25f - 42.9167f)), (float)116.0f, (float)113.5f);
        }
        if (t <= 52.4167f) {
            return Mth.lerp((float)((t - 44.25f) / 8.166698f), (float)113.5f, (float)96.25f);
        }
        if (t <= 84.5833f) {
            return Mth.lerp((float)((t - 52.4167f) / 32.1666f), (float)96.25f, (float)14.095f);
        }
        if (t <= 93.0f) {
            return Mth.lerp((float)((t - 84.5833f) / 8.416702f), (float)14.095f, (float)-3.565f);
        }
        if (t <= 95.25f) {
            return Mth.lerp((float)((t - 93.0f) / 2.25f), (float)-3.565f, (float)-6.35f);
        }
        if (t <= 97.5f) {
            return Mth.lerp((float)((t - 95.25f) / 2.25f), (float)-6.35f, (float)-6.39f);
        }
        if (t <= 98.8333f) {
            return Mth.lerp((float)((t - 97.5f) / 1.3332977f), (float)-6.39f, (float)-3.03f);
        }
        if (t <= 99.25f) {
            return Mth.lerp((float)((t - 98.8333f) / 0.41670227f), (float)-3.03f, (float)-1.95f);
        }
        return Mth.lerp((float)((t - 99.25f) / 0.75f), (float)-1.95f, (float)0.0f);
    }
        public int period(){return 100;}
    }
    private static final class T72B3MModel implements Curve {
    private static final int TRACK_COUNT = 90;
    private static final int MAX_IDX = 90;
    private static final float[][] KEYFRAMES = new float[][]{{177.18f, 22.0f, 48.75f}, {157.56f, 21.4f, 51.67f}, {132.62f, 19.71f, 54.14f}, {112.67f, 17.2f, 55.74f}, {87.74f, 14.25f, 56.24f}, {62.8f, 11.35f, 55.55f}, {42.86f, 8.94f, 53.78f}, {21.04f, 7.41f, 51.21f}, {20.41f, 6.37f, 48.4f}, {20.41f, 5.32f, 45.58f}, {20.41f, 4.27f, 42.76f}, {20.41f, 3.22f, 39.94f}, {20.41f, 2.17f, 37.13f}, {10.21f, 1.25f, 34.27f}, {0.0f, 1.14f, 31.27f}, {0.0f, 1.14f, 28.26f}, {0.0f, 1.14f, 25.26f}, {0.0f, 1.14f, 22.25f}, {0.0f, 1.14f, 19.25f}, {0.0f, 1.14f, 16.24f}, {0.0f, 1.14f, 13.23f}, {0.0f, 1.14f, 10.23f}, {0.0f, 1.14f, 7.22f}, {0.0f, 1.14f, 4.21f}, {0.0f, 1.14f, 1.21f}, {0.0f, 1.14f, -1.8f}, {0.0f, 1.14f, -4.81f}, {0.0f, 1.14f, -7.81f}, {0.0f, 1.14f, -10.82f}, {0.0f, 1.14f, -13.82f}, {0.0f, 1.14f, -16.83f}, {0.0f, 1.14f, -19.84f}, {0.0f, 1.14f, -22.84f}, {0.0f, 1.14f, -25.85f}, {0.0f, 1.14f, -28.86f}, {0.0f, 1.14f, -31.86f}, {0.0f, 1.14f, -34.87f}, {0.0f, 1.14f, -37.88f}, {0.0f, 1.14f, -40.88f}, {0.0f, 1.14f, -43.89f}, {0.0f, 1.14f, -46.9f}, {-0.55f, 1.14f, -49.9f}, {-32.03f, 1.75f, -52.82f}, {-36.06f, 3.52f, -55.25f}, {-36.06f, 5.29f, -57.68f}, {-36.06f, 7.06f, -60.11f}, {-36.06f, 8.83f, -62.54f}, {-36.06f, 10.6f, -64.97f}, {-39.32f, 12.24f, -67.48f}, {-68.93f, 14.67f, -69.18f}, {-103.49f, 17.63f, -69.38f}, {-133.11f, 20.26f, -68.0f}, {-162.72f, 21.8f, -65.47f}, {-178.82f, 21.95f, -62.48f}, {178.89f, 21.9f, -59.47f}, {178.75f, 21.84f, -56.47f}, {178.75f, 21.77f, -53.46f}, {178.75f, 21.71f, -50.45f}, {178.75f, 21.64f, -47.45f}, {178.75f, 21.57f, -44.44f}, {179.07f, 21.51f, -41.44f}, {179.69f, 21.5f, -38.43f}, {-180.0f, 21.5f, -35.42f}, {-180.0f, 21.5f, -32.42f}, {-180.0f, 21.5f, -29.41f}, {-180.0f, 21.5f, -26.4f}, {-180.0f, 21.5f, -23.4f}, {-180.0f, 21.5f, -20.39f}, {-180.0f, 21.5f, -17.39f}, {-180.0f, 21.5f, -14.38f}, {-180.0f, 21.5f, -11.37f}, {-180.0f, 21.5f, -8.37f}, {-180.0f, 21.5f, -5.36f}, {-180.0f, 21.5f, -2.35f}, {-180.0f, 21.5f, 0.65f}, {-180.0f, 21.5f, 3.66f}, {-180.0f, 21.5f, 6.67f}, {-180.0f, 21.5f, 9.67f}, {-180.0f, 21.5f, 12.68f}, {-180.0f, 21.5f, 15.68f}, {-180.0f, 21.5f, 18.69f}, {-179.7f, 21.5f, 21.7f}, {-179.11f, 21.5f, 24.7f}, {-178.82f, 21.57f, 27.71f}, {-178.82f, 21.63f, 30.72f}, {-178.82f, 21.69f, 33.72f}, {-178.82f, 21.75f, 36.73f}, {-178.82f, 21.81f, 39.73f}, {-178.82f, 21.88f, 42.74f}, {-178.82f, 21.94f, 45.74f}, {177.18f, 22.0f, 48.75f}};
    private static final float START_Y = 22.0f;
    private static final float START_Z = 48.75f;
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 180;
        float normalized = t / (float)wrapRange * 90.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)90);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)90);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 22.0f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 48.75f;
    }
        public int period(){return 180;}
    }
    private static final class T72b3Renderer implements Curve {
    private static final float[] MOVE_Y = new float[]{20.189f, 20.0542f, 19.6559f, 19.0119f, 18.1512f, 17.1124f, 15.9423f, 14.6935f, 13.4219f, 12.1848f, 11.0378f, 10.0324f, 9.2137f, 8.6186f, 8.1582f, 7.6982f, 7.2382f, 6.7782f, 6.3182f, 5.8582f, 5.3981f, 4.9381f, 4.4781f, 4.0181f, 3.5581f, 3.098f, 2.638f, 2.2845f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1914f, 2.1999f, 2.4018f, 2.8843f, 3.5683f, 4.2523f, 4.9364f, 5.6204f, 6.3045f, 6.9885f, 7.6725f, 8.3566f, 9.0406f, 9.7247f, 10.4087f, 11.0927f, 11.7447f, 12.5866f, 13.6412f, 14.8405f, 16.107f, 17.3589f, 18.5153f, 19.5016f, 20.254f, 20.7238f, 20.8808f, 20.8726f, 20.8645f, 20.8564f, 20.8482f, 20.8401f, 20.832f, 20.8238f, 20.8157f, 20.8075f, 20.7994f, 20.7913f, 20.7831f, 20.775f, 20.7668f, 20.7587f, 20.7506f, 20.7424f, 20.7343f, 20.7262f, 20.718f, 20.7099f, 20.7017f, 20.6936f, 20.6855f, 20.6773f, 20.6692f, 20.661f, 20.6529f, 20.6448f, 20.6366f, 20.6285f, 20.6203f, 20.6122f, 20.6041f, 20.5959f, 20.5878f, 20.5797f, 20.5715f, 20.5634f, 20.5552f, 20.5471f, 20.539f, 20.5308f, 20.5227f, 20.5145f, 20.5064f, 20.4983f, 20.4901f, 20.482f, 20.4739f, 20.4657f, 20.4576f, 20.4494f, 20.4413f, 20.4332f, 20.425f, 20.4169f, 20.4087f, 20.4006f, 20.3925f, 20.3843f, 20.3762f, 20.3681f, 20.3599f, 20.3518f, 20.3436f, 20.3355f, 20.3274f, 20.3192f, 20.3111f, 20.3029f, 20.2948f, 20.2867f, 20.2785f, 20.2704f, 20.2622f, 20.2541f, 20.246f, 20.2378f, 20.2297f, 20.2216f, 20.2134f, 20.2053f, 20.1971f, 20.189f};
    private static final float[] MOVE_Z = new float[]{48.7265f, 49.9912f, 51.1991f, 52.2958f, 53.2322f, 53.9661f, 54.4646f, 54.7053f, 54.6773f, 54.3819f, 53.8324f, 53.0535f, 52.0801f, 50.9561f, 49.7679f, 48.5796f, 47.3913f, 46.203f, 45.0146f, 43.8263f, 42.638f, 41.4497f, 40.2614f, 39.0731f, 37.8848f, 36.6965f, 35.5081f, 34.2863f, 33.0175f, 31.7433f, 30.469f, 29.1948f, 27.9206f, 26.6463f, 25.3721f, 24.0978f, 22.8236f, 21.5493f, 20.2751f, 19.0008f, 17.7266f, 16.4523f, 15.1781f, 13.9038f, 12.6296f, 11.3554f, 10.0811f, 8.8069f, 7.5326f, 6.2584f, 4.9841f, 3.7099f, 2.4356f, 1.1614f, -0.1129f, -1.3871f, -2.6614f, -3.9356f, -5.2099f, -6.4841f, -7.7583f, -9.0326f, -10.3068f, -11.5811f, -12.8553f, -14.1296f, -15.4038f, -16.6781f, -17.9523f, -19.2266f, -20.5008f, -21.7751f, -23.0493f, -24.3235f, -25.5978f, -26.872f, -28.1463f, -29.4205f, -30.6948f, -31.969f, -33.2433f, -34.5175f, -35.7918f, -37.066f, -38.3403f, -39.6145f, -40.8887f, -42.163f, -43.4372f, -44.7115f, -45.9856f, -47.2413f, -48.4148f, -49.4898f, -50.5649f, -51.64f, -52.7151f, -53.7902f, -54.8652f, -55.9403f, -57.0154f, -58.0905f, -59.1655f, -60.2406f, -61.3157f, -62.4092f, -63.3612f, -64.0702f, -64.4905f, -64.5948f, -64.3765f, -63.8496f, -63.0482f, -62.024f, -60.8433f, -59.5822f, -58.308f, -57.0338f, -55.7596f, -54.4854f, -53.2111f, -51.9369f, -50.6627f, -49.3885f, -48.1142f, -46.84f, -45.5658f, -44.2916f, -43.0174f, -41.7431f, -40.4689f, -39.1947f, -37.9205f, -36.6463f, -35.372f, -34.0978f, -32.8236f, -31.5494f, -30.2752f, -29.0009f, -27.7267f, -26.4525f, -25.1783f, -23.9041f, -22.6298f, -21.3556f, -20.0814f, -18.8072f, -17.533f, -16.2587f, -14.9845f, -13.7103f, -12.4361f, -11.1619f, -9.8876f, -8.6134f, -7.3392f, -6.065f, -4.7908f, -3.5165f, -2.2423f, -0.9681f, 0.3061f, 1.5803f, 2.8546f, 4.1288f, 5.403f, 6.6772f, 7.9514f, 9.2257f, 10.4999f, 11.7741f, 13.0483f, 14.3225f, 15.5968f, 16.871f, 18.1452f, 19.4194f, 20.6937f, 21.9679f, 23.2421f, 24.5163f, 25.7905f, 27.0648f, 28.339f, 29.6132f, 30.8874f, 32.1616f, 33.4359f, 34.7101f, 35.9843f, 37.2585f, 38.5327f, 39.807f, 41.0812f, 42.3554f, 43.6296f, 44.9038f, 46.1781f, 47.4523f, 48.7265f};
    private static final float[] ROT_X = new float[]{0.0f, -12.1682f, -24.3363f, -36.5045f, -48.6726f, -60.8408f, -73.0089f, -85.1771f, -97.3453f, -109.5134f, -121.6816f, -133.8497f, -146.0179f, -158.186f, -158.8376f, -158.8376f, -158.8376f, -158.8376f, -158.8376f, -158.8376f, -158.8376f, -158.8376f, -158.8376f, -158.8376f, -158.8376f, -158.8376f, -158.8376f, -169.8951f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -183.0494f, -195.2176f, -212.4674f, -212.4674f, -212.4674f, -212.4674f, -212.4674f, -212.4674f, -212.4674f, -212.4674f, -212.4674f, -212.4674f, -212.4674f, -212.4674f, -212.4674f, -214.1846f, -228.7864f, -243.3882f, -257.9899f, -272.5917f, -287.1935f, -301.7953f, -316.3971f, -330.9989f, -345.6007f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f, -360.366f};
    private static final float[] TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return T72b3Renderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return T72b3Renderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return T72b3Renderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.7857143f;
    }
        public int period(){return 100;}
    }
    private static final class T80BRenderer implements Curve {
    private static final float[] MOVE_Y = new float[]{19.0633f, 18.9299f, 18.5361f, 17.9011f, 17.0556f, 16.0406f, 14.9055f, 13.7053f, 12.4982f, 11.3427f, 10.295f, 9.4059f, 8.7184f, 8.0298f, 7.2753f, 6.5208f, 5.7663f, 5.0118f, 4.2573f, 3.5028f, 2.7483f, 1.9938f, 1.2393f, 0.6358f, 0.3332f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2374f, 0.2575f, 0.4538f, 0.9014f, 1.5332f, 2.165f, 2.7968f, 3.4285f, 4.0603f, 4.6921f, 5.3239f, 5.9556f, 6.5874f, 7.2192f, 7.8509f, 8.4827f, 9.1145f, 9.7173f, 10.3187f, 11.1358f, 12.1288f, 13.2497f, 14.444f, 15.6538f, 16.8204f, 17.8872f, 18.8024f, 19.5217f, 20.0101f, 20.2439f, 20.2544f, 20.2401f, 20.2257f, 20.2114f, 20.197f, 20.1827f, 20.1683f, 20.154f, 20.1396f, 20.1252f, 20.1109f, 20.0965f, 20.0822f, 20.0678f, 20.0535f, 20.0391f, 20.0248f, 20.0104f, 19.9961f, 19.9817f, 19.9674f, 19.953f, 19.9387f, 19.9243f, 19.91f, 19.8956f, 19.8813f, 19.8669f, 19.8526f, 19.8382f, 19.8239f, 19.8095f, 19.7952f, 19.7808f, 19.7665f, 19.7521f, 19.7378f, 19.7234f, 19.7091f, 19.6947f, 19.6804f, 19.666f, 19.6517f, 19.6373f, 19.623f, 19.6086f, 19.5943f, 19.5799f, 19.5656f, 19.5512f, 19.5369f, 19.5225f, 19.5082f, 19.4938f, 19.4795f, 19.4651f, 19.4508f, 19.4364f, 19.4221f, 19.4077f, 19.3934f, 19.379f, 19.3647f, 19.3503f, 19.336f, 19.3216f, 19.3073f, 19.2929f, 19.2786f, 19.2642f, 19.2499f, 19.2355f, 19.2212f, 19.2068f, 19.1925f, 19.1781f, 19.1638f, 19.1494f, 19.1351f, 19.1207f, 19.1064f, 19.092f, 19.0777f, 19.0633f};
    private static final float[] MOVE_Z = new float[]{52.3197f, 53.5238f, 54.6694f, 55.7011f, 56.5687f, 57.2302f, 57.6534f, 57.8179f, 57.7156f, 57.3515f, 56.7433f, 55.9205f, 54.923f, 53.9303f, 52.9794f, 52.0284f, 51.0775f, 50.1265f, 49.1755f, 48.2246f, 47.2736f, 46.3227f, 45.3717f, 44.3332f, 43.1592f, 41.9506f, 40.7367f, 39.5227f, 38.3088f, 37.0949f, 35.881f, 34.6671f, 33.4532f, 32.2393f, 31.0254f, 29.8114f, 28.5975f, 27.3836f, 26.1697f, 24.9558f, 23.7419f, 22.528f, 21.3141f, 20.1002f, 18.8862f, 17.6723f, 16.4584f, 15.2445f, 14.0306f, 12.8167f, 11.6028f, 10.3889f, 9.1749f, 7.961f, 6.7471f, 5.5332f, 4.3193f, 3.1054f, 1.8915f, 0.6776f, -0.5363f, -1.7503f, -2.9642f, -4.1781f, -5.392f, -6.6059f, -7.8198f, -9.0337f, -10.2476f, -11.4616f, -12.6755f, -13.8894f, -15.1033f, -16.3172f, -17.5311f, -18.745f, -19.9589f, -21.1728f, -22.3868f, -23.6007f, -24.8146f, -26.0285f, -27.2424f, -28.4563f, -29.6702f, -30.8841f, -32.0981f, -33.312f, -34.5254f, -35.7218f, -36.8419f, -37.8785f, -38.915f, -39.9516f, -40.9881f, -42.0247f, -43.0613f, -44.0978f, -45.1344f, -46.1709f, -47.2075f, -48.244f, -49.2806f, -50.3171f, -51.3684f, -52.42f, -53.3144f, -54.0083f, -54.4679f, -54.671f, -54.6077f, -54.2811f, -53.7071f, -52.9134f, -51.9385f, -50.8299f, -49.6413f, -48.4279f, -47.2141f, -46.0003f, -44.7865f, -43.5726f, -42.3588f, -41.145f, -39.9311f, -38.7173f, -37.5035f, -36.2897f, -35.0758f, -33.862f, -32.6482f, -31.4344f, -30.2205f, -29.0067f, -27.7929f, -26.5791f, -25.3652f, -24.1514f, -22.9376f, -21.7237f, -20.5099f, -19.2961f, -18.0823f, -16.8684f, -15.6546f, -14.4408f, -13.227f, -12.0131f, -10.7993f, -9.5855f, -8.3716f, -7.1578f, -5.944f, -4.7302f, -3.5163f, -2.3025f, -1.0887f, 0.1251f, 1.339f, 2.5528f, 3.7666f, 4.9804f, 6.1943f, 7.4081f, 8.6219f, 9.8358f, 11.0496f, 12.2634f, 13.4772f, 14.6911f, 15.9049f, 17.1187f, 18.3325f, 19.5464f, 20.7602f, 21.974f, 23.1879f, 24.4017f, 25.6155f, 26.8293f, 28.0432f, 29.257f, 30.4708f, 31.6846f, 32.8985f, 34.1123f, 35.3261f, 36.5399f, 37.7538f, 38.9676f, 40.1814f, 41.3953f, 42.6091f, 43.8229f, 45.0367f, 46.2506f, 47.4644f, 48.6782f, 49.892f, 51.1059f, 52.3197f};
    private static final float[] ROT_X = new float[]{0.0f, -12.6458f, -25.2916f, -37.9375f, -50.5833f, -63.2291f, -75.8749f, -88.5208f, -101.1666f, -113.8124f, -126.4582f, -139.104f, -151.7499f, -141.5714f, -141.5714f, -141.5714f, -141.5714f, -141.5714f, -141.5714f, -141.5714f, -141.5714f, -141.5714f, -141.5714f, -160.5752f, -170.5112f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -184.3469f, -194.2829f, -211.362f, -211.362f, -211.362f, -211.362f, -211.362f, -211.362f, -211.362f, -211.362f, -211.362f, -211.362f, -211.362f, -211.362f, -211.362f, -211.362f, -203.4425f, -216.0884f, -228.7342f, -241.38f, -254.0258f, -266.6717f, -279.3175f, -291.9633f, -304.6091f, -317.2549f, -329.9008f, -342.5466f, -355.1924f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f, -360.6774f};
    private static final float[] TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return T80BRenderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return T80BRenderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return T80BRenderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.7857143f;
    }
        public int period(){return 100;}
    }
    private static final class T90MSRenderer implements Curve {
    public float getTrackDistance() {
        return 1.15f;
    }
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    public float getBoneRotX(float t) {
        if ((t = (100.0f - t % 100.0f) % 100.0f) <= 1.15f) {
            return Mth.lerp((float)(t / 1.15f), (float)2.0f, (float)1.5f);
        }
        if (t <= 2.3f) {
            return Mth.lerp((float)((t - 1.15f) / 1.15f), (float)1.5f, (float)2.0f);
        }
        if (t <= 3.45f) {
            return Mth.lerp((float)((t - 2.3f) / 1.15f), (float)2.0f, (float)1.5f);
        }
        if (t <= 4.6f) {
            return Mth.lerp((float)((t - 3.45f) / 1.15f), (float)1.5f, (float)1.0f);
        }
        if (t <= 5.75f) {
            return Mth.lerp((float)((t - 4.6f) / 1.15f), (float)1.0f, (float)3.0f);
        }
        if (t <= 6.9f) {
            return Mth.lerp((float)((t - 5.75f) / 1.15f), (float)3.0f, (float)2.0f);
        }
        if (t <= 8.05f) {
            return Mth.lerp((float)((t - 6.9f) / 1.15f), (float)2.0f, (float)1.0f);
        }
        if (t <= 9.2f) {
            return Mth.lerp((float)((t - 8.05f) / 1.15f), (float)1.0f, (float)2.0f);
        }
        if (t <= 10.34f) {
            return Mth.lerp((float)((t - 9.2f) / 1.15f), (float)2.0f, (float)2.0f);
        }
        if (t <= 11.49f) {
            return Mth.lerp((float)((t - 10.34f) / 1.15f), (float)2.0f, (float)1.0f);
        }
        if (t <= 12.64f) {
            return Mth.lerp((float)((t - 11.49f) / 1.15f), (float)1.0f, (float)3.0f);
        }
        if (t <= 13.79f) {
            return Mth.lerp((float)((t - 12.64f) / 1.15f), (float)3.0f, (float)2.0f);
        }
        if (t <= 14.94f) {
            return Mth.lerp((float)((t - 13.79f) / 1.15f), (float)2.0f, (float)1.0f);
        }
        if (t <= 16.09f) {
            return Mth.lerp((float)((t - 14.94f) / 1.15f), (float)1.0f, (float)2.0f);
        }
        if (t <= 17.24f) {
            return Mth.lerp((float)((t - 16.09f) / 1.15f), (float)2.0f, (float)2.0f);
        }
        if (t <= 18.39f) {
            return Mth.lerp((float)((t - 17.24f) / 1.15f), (float)2.0f, (float)1.0f);
        }
        if (t <= 19.54f) {
            return Mth.lerp((float)((t - 18.39f) / 1.15f), (float)1.0f, (float)1.0f);
        }
        if (t <= 20.69f) {
            return Mth.lerp((float)((t - 19.54f) / 1.15f), (float)1.0f, (float)0.0f);
        }
        if (t <= 21.84f) {
            return Mth.lerp((float)((t - 20.69f) / 1.15f), (float)0.0f, (float)1.0f);
        }
        if (t <= 22.99f) {
            return Mth.lerp((float)((t - 21.84f) / 1.15f), (float)1.0f, (float)1.0f);
        }
        if (t <= 24.14f) {
            return Mth.lerp((float)((t - 22.99f) / 1.15f), (float)1.0f, (float)2.0f);
        }
        if (t <= 25.29f) {
            return Mth.lerp((float)((t - 24.14f) / 1.15f), (float)2.0f, (float)1.0f);
        }
        if (t <= 26.44f) {
            return Mth.lerp((float)((t - 25.29f) / 1.15f), (float)1.0f, (float)1.0f);
        }
        if (t <= 27.59f) {
            return Mth.lerp((float)((t - 26.44f) / 1.15f), (float)1.0f, (float)1.5f);
        }
        if (t <= 28.74f) {
            return Mth.lerp((float)((t - 27.59f) / 1.15f), (float)1.5f, (float)1.0f);
        }
        if (t <= 29.89f) {
            return Mth.lerp((float)((t - 28.74f) / 1.15f), (float)1.0f, (float)1.0f);
        }
        if (t <= 31.03f) {
            return Mth.lerp((float)((t - 29.89f) / 1.15f), (float)1.0f, (float)1.5f);
        }
        if (t <= 32.18f) {
            return Mth.lerp((float)((t - 31.03f) / 1.15f), (float)1.5f, (float)1.0f);
        }
        if (t <= 33.33f) {
            return Mth.lerp((float)((t - 32.18f) / 1.15f), (float)1.0f, (float)0.0f);
        }
        if (t <= 34.48f) {
            return Mth.lerp((float)((t - 33.33f) / 1.15f), (float)0.0f, (float)1.0f);
        }
        if (t <= 35.63f) {
            return Mth.lerp((float)((t - 34.48f) / 1.15f), (float)1.0f, (float)1.0f);
        }
        if (t <= 36.78f) {
            return Mth.lerp((float)((t - 35.63f) / 1.15f), (float)1.0f, (float)1.0f);
        }
        if (t <= 37.93f) {
            return Mth.lerp((float)((t - 36.78f) / 1.15f), (float)1.0f, (float)3.0f);
        }
        if (t <= 39.08f) {
            return Mth.lerp((float)((t - 37.93f) / 1.15f), (float)3.0f, (float)0.0f);
        }
        if (t <= 40.23f) {
            return Mth.lerp((float)((t - 39.08f) / 1.15f), (float)0.0f, (float)-39.0f);
        }
        if (t <= 41.38f) {
            return Mth.lerp((float)((t - 40.23f) / 1.15f), (float)-39.0f, (float)-72.5f);
        }
        if (t <= 42.53f) {
            return Mth.lerp((float)((t - 41.38f) / 1.15f), (float)-72.5f, (float)-95.0f);
        }
        if (t <= 43.68f) {
            return Mth.lerp((float)((t - 42.53f) / 1.15f), (float)-95.0f, (float)-127.5f);
        }
        if (t <= 44.83f) {
            return Mth.lerp((float)((t - 43.68f) / 1.15f), (float)-127.5f, (float)-139.5f);
        }
        if (t <= 45.98f) {
            return Mth.lerp((float)((t - 44.83f) / 1.15f), (float)-139.5f, (float)-142.0f);
        }
        if (t <= 47.13f) {
            return Mth.lerp((float)((t - 45.98f) / 1.15f), (float)-142.0f, (float)-143.0f);
        }
        if (t <= 48.28f) {
            return Mth.lerp((float)((t - 47.13f) / 1.15f), (float)-143.0f, (float)-145.0f);
        }
        if (t <= 49.43f) {
            return Mth.lerp((float)((t - 48.28f) / 1.15f), (float)-145.0f, (float)-148.5f);
        }
        if (t <= 50.57f) {
            return Mth.lerp((float)((t - 49.43f) / 1.15f), (float)-148.5f, (float)-157.5f);
        }
        if (t <= 51.72f) {
            return Mth.lerp((float)((t - 50.57f) / 1.15f), (float)-157.5f, (float)-180.0f);
        }
        if (t <= 83.91f) {
            return -180.0f;
        }
        if (t <= 85.06f) {
            return Mth.lerp((float)((t - 83.91f) / 1.15f), (float)-180.0f, (float)-192.5f);
        }
        if (t <= 86.21f) {
            return Mth.lerp((float)((t - 85.06f) / 1.15f), (float)-192.5f, (float)-203.0f);
        }
        if (t <= 87.36f) {
            return Mth.lerp((float)((t - 86.21f) / 1.15f), (float)-203.0f, (float)-203.5f);
        }
        if (t <= 88.51f) {
            return Mth.lerp((float)((t - 87.36f) / 1.15f), (float)-203.5f, (float)-205.0f);
        }
        if (t <= 89.66f) {
            return Mth.lerp((float)((t - 88.51f) / 1.15f), (float)-205.0f, (float)-207.5f);
        }
        if (t <= 90.8f) {
            return Mth.lerp((float)((t - 89.66f) / 1.15f), (float)-207.5f, (float)-209.0f);
        }
        if (t <= 91.95f) {
            return Mth.lerp((float)((t - 90.8f) / 1.15f), (float)-209.0f, (float)-232.0f);
        }
        if (t <= 93.1f) {
            return Mth.lerp((float)((t - 91.95f) / 1.15f), (float)-232.0f, (float)-240.5f);
        }
        if (t <= 94.25f) {
            return Mth.lerp((float)((t - 93.1f) / 1.15f), (float)-240.5f, (float)-272.5f);
        }
        if (t <= 95.4f) {
            return Mth.lerp((float)((t - 94.25f) / 1.15f), (float)-272.5f, (float)-312.5f);
        }
        if (t <= 96.55f) {
            return Mth.lerp((float)((t - 95.4f) / 1.15f), (float)-312.5f, (float)-337.5f);
        }
        if (t <= 97.7f) {
            return Mth.lerp((float)((t - 96.55f) / 1.15f), (float)-337.5f, (float)-360.5f);
        }
        return Mth.lerp((float)((t - 97.7f) / 2.3f), (float)-360.5f, (float)-358.5f);
    }
    public float getBoneMoveY(float t) {
        if ((t = (100.0f - t % 100.0f) % 100.0f) <= 40.23f) {
            return Mth.lerp((float)(t / 40.23f), (float)0.0f, (float)2.3f);
        }
        if (t <= 41.38f) {
            return Mth.lerp((float)((t - 40.23f) / 1.1500015f), (float)2.3f, (float)2.2f);
        }
        if (t <= 42.53f) {
            return Mth.lerp((float)((t - 41.38f) / 1.1499977f), (float)2.2f, (float)0.4f);
        }
        if (t <= 43.68f) {
            return Mth.lerp((float)((t - 42.53f) / 1.1500015f), (float)0.4f, (float)-2.3f);
        }
        if (t <= 44.83f) {
            return Mth.lerp((float)((t - 43.68f) / 1.1500015f), (float)-2.3f, (float)-5.4f);
        }
        if (t <= 45.98f) {
            return Mth.lerp((float)((t - 44.83f) / 1.1499977f), (float)-5.4f, (float)-7.6f);
        }
        if (t <= 47.13f) {
            return Mth.lerp((float)((t - 45.98f) / 1.1500015f), (float)-7.6f, (float)-9.5f);
        }
        if (t <= 48.28f) {
            return Mth.lerp((float)((t - 47.13f) / 1.1499977f), (float)-9.5f, (float)-11.3f);
        }
        if (t <= 49.43f) {
            return Mth.lerp((float)((t - 48.28f) / 1.1500015f), (float)-11.3f, (float)-13.1f);
        }
        if (t <= 50.57f) {
            return Mth.lerp((float)((t - 49.43f) / 1.1399994f), (float)-13.1f, (float)-14.8f);
        }
        if (t <= 51.72f) {
            return Mth.lerp((float)((t - 50.57f) / 1.1500015f), (float)-14.8f, (float)-16.3f);
        }
        if (t <= 83.91f) {
            return -17.3f;
        }
        if (t <= 85.06f) {
            return Mth.lerp((float)((t - 83.91f) / 1.1499939f), (float)-17.3f, (float)-16.6f);
        }
        if (t <= 86.21f) {
            return Mth.lerp((float)((t - 85.06f) / 1.1500015f), (float)-16.6f, (float)-15.5f);
        }
        if (t <= 87.36f) {
            return Mth.lerp((float)((t - 86.21f) / 1.1500015f), (float)-15.5f, (float)-14.3f);
        }
        if (t <= 88.51f) {
            return Mth.lerp((float)((t - 87.36f) / 1.1500015f), (float)-14.3f, (float)-13.3f);
        }
        if (t <= 89.66f) {
            return Mth.lerp((float)((t - 88.51f) / 1.1500015f), (float)-13.3f, (float)-12.1f);
        }
        if (t <= 90.8f) {
            return Mth.lerp((float)((t - 89.66f) / 1.1399994f), (float)-12.1f, (float)-10.7f);
        }
        if (t <= 91.95f) {
            return Mth.lerp((float)((t - 90.8f) / 1.1499939f), (float)-10.7f, (float)-8.5f);
        }
        if (t <= 93.1f) {
            return Mth.lerp((float)((t - 91.95f) / 1.1500015f), (float)-8.5f, (float)-5.95f);
        }
        if (t <= 94.25f) {
            return Mth.lerp((float)((t - 93.1f) / 1.1500015f), (float)-5.95f, (float)-3.05f);
        }
        if (t <= 95.4f) {
            return Mth.lerp((float)((t - 94.25f) / 1.1500015f), (float)-3.05f, (float)-0.95f);
        }
        if (t <= 96.55f) {
            return Mth.lerp((float)((t - 95.4f) / 1.1500015f), (float)-0.95f, (float)0.05f);
        }
        return Mth.lerp((float)((t - 96.55f) / 3.449997f), (float)0.05f, (float)0.0f);
    }
    public float getBoneMoveZ(float t) {
        if ((t = (100.0f - t % 100.0f) % 100.0f) <= 1.15f) {
            return Mth.lerp((float)(t / 1.15f), (float)0.0f, (float)3.0f);
        }
        if (t <= 2.3f) {
            return Mth.lerp((float)((t - 1.15f) / 1.15f), (float)3.0f, (float)6.0f);
        }
        if (t <= 3.45f) {
            return Mth.lerp((float)((t - 2.3f) / 1.15f), (float)6.0f, (float)9.0f);
        }
        if (t <= 4.6f) {
            return Mth.lerp((float)((t - 3.45f) / 1.15f), (float)9.0f, (float)12.0f);
        }
        if (t <= 5.75f) {
            return Mth.lerp((float)((t - 4.6f) / 1.15f), (float)12.0f, (float)15.0f);
        }
        if (t <= 6.9f) {
            return Mth.lerp((float)((t - 5.75f) / 1.15f), (float)15.0f, (float)18.0f);
        }
        if (t <= 8.05f) {
            return Mth.lerp((float)((t - 6.9f) / 1.15f), (float)18.0f, (float)21.0f);
        }
        if (t <= 9.2f) {
            return Mth.lerp((float)((t - 8.05f) / 1.15f), (float)21.0f, (float)24.0f);
        }
        if (t <= 10.34f) {
            return Mth.lerp((float)((t - 9.2f) / 1.15f), (float)24.0f, (float)27.0f);
        }
        if (t <= 11.49f) {
            return Mth.lerp((float)((t - 10.34f) / 1.15f), (float)27.0f, (float)30.0f);
        }
        if (t <= 12.64f) {
            return Mth.lerp((float)((t - 11.49f) / 1.15f), (float)30.0f, (float)33.0f);
        }
        if (t <= 13.79f) {
            return Mth.lerp((float)((t - 12.64f) / 1.15f), (float)33.0f, (float)36.0f);
        }
        if (t <= 14.94f) {
            return Mth.lerp((float)((t - 13.79f) / 1.15f), (float)36.0f, (float)39.0f);
        }
        if (t <= 16.09f) {
            return Mth.lerp((float)((t - 14.94f) / 1.15f), (float)39.0f, (float)42.0f);
        }
        if (t <= 17.24f) {
            return Mth.lerp((float)((t - 16.09f) / 1.15f), (float)42.0f, (float)45.0f);
        }
        if (t <= 18.39f) {
            return Mth.lerp((float)((t - 17.24f) / 1.15f), (float)45.0f, (float)48.0f);
        }
        if (t <= 19.54f) {
            return Mth.lerp((float)((t - 18.39f) / 1.15f), (float)48.0f, (float)51.0f);
        }
        if (t <= 20.69f) {
            return Mth.lerp((float)((t - 19.54f) / 1.15f), (float)51.0f, (float)54.0f);
        }
        if (t <= 21.84f) {
            return Mth.lerp((float)((t - 20.69f) / 1.15f), (float)54.0f, (float)57.0f);
        }
        if (t <= 22.99f) {
            return Mth.lerp((float)((t - 21.84f) / 1.15f), (float)57.0f, (float)60.0f);
        }
        if (t <= 24.14f) {
            return Mth.lerp((float)((t - 22.99f) / 1.15f), (float)60.0f, (float)63.0f);
        }
        if (t <= 25.29f) {
            return Mth.lerp((float)((t - 24.14f) / 1.15f), (float)63.0f, (float)66.0f);
        }
        if (t <= 26.44f) {
            return Mth.lerp((float)((t - 25.29f) / 1.15f), (float)66.0f, (float)69.0f);
        }
        if (t <= 27.59f) {
            return Mth.lerp((float)((t - 26.44f) / 1.15f), (float)69.0f, (float)72.0f);
        }
        if (t <= 28.74f) {
            return Mth.lerp((float)((t - 27.59f) / 1.15f), (float)72.0f, (float)75.0f);
        }
        if (t <= 29.89f) {
            return Mth.lerp((float)((t - 28.74f) / 1.15f), (float)75.0f, (float)78.0f);
        }
        if (t <= 31.03f) {
            return Mth.lerp((float)((t - 29.89f) / 1.14f), (float)78.0f, (float)81.0f);
        }
        if (t <= 32.18f) {
            return Mth.lerp((float)((t - 31.03f) / 1.15f), (float)81.0f, (float)84.0f);
        }
        if (t <= 33.33f) {
            return Mth.lerp((float)((t - 32.18f) / 1.15f), (float)84.0f, (float)87.0f);
        }
        if (t <= 34.48f) {
            return Mth.lerp((float)((t - 33.33f) / 1.15f), (float)87.0f, (float)90.0f);
        }
        if (t <= 35.63f) {
            return Mth.lerp((float)((t - 34.48f) / 1.15f), (float)90.0f, (float)93.0f);
        }
        if (t <= 36.78f) {
            return Mth.lerp((float)((t - 35.63f) / 1.15f), (float)93.0f, (float)96.0f);
        }
        if (t <= 37.93f) {
            return Mth.lerp((float)((t - 36.78f) / 1.15f), (float)96.0f, (float)99.0f);
        }
        if (t <= 39.08f) {
            return Mth.lerp((float)((t - 37.93f) / 1.15f), (float)99.0f, (float)102.0f);
        }
        if (t <= 40.23f) {
            return Mth.lerp((float)((t - 39.08f) / 1.15f), (float)102.0f, (float)105.0f);
        }
        if (t <= 41.38f) {
            return Mth.lerp((float)((t - 40.23f) / 1.15f), (float)105.0f, (float)107.85f);
        }
        if (t <= 42.53f) {
            return Mth.lerp((float)((t - 41.38f) / 1.15f), (float)107.85f, (float)110.0f);
        }
        if (t <= 43.68f) {
            return Mth.lerp((float)((t - 42.53f) / 1.15f), (float)110.0f, (float)110.8f);
        }
        if (t <= 44.83f) {
            return Mth.lerp((float)((t - 43.68f) / 1.15f), (float)110.8f, (float)110.65f);
        }
        if (t <= 45.98f) {
            return Mth.lerp((float)((t - 44.83f) / 1.15f), (float)110.65f, (float)108.8f);
        }
        if (t <= 47.13f) {
            return Mth.lerp((float)((t - 45.98f) / 1.15f), (float)108.8f, (float)106.55f);
        }
        if (t <= 48.28f) {
            return Mth.lerp((float)((t - 47.13f) / 1.15f), (float)106.55f, (float)104.25f);
        }
        if (t <= 49.43f) {
            return Mth.lerp((float)((t - 48.28f) / 1.15f), (float)104.25f, (float)101.9f);
        }
        if (t <= 50.57f) {
            return Mth.lerp((float)((t - 49.43f) / 1.15f), (float)101.9f, (float)99.5f);
        }
        if (t <= 51.72f) {
            return Mth.lerp((float)((t - 50.57f) / 1.15f), (float)99.5f, (float)96.9f);
        }
        if (t <= 52.87f) {
            return Mth.lerp((float)((t - 51.72f) / 1.15f), (float)96.9f, (float)94.3f);
        }
        if (t <= 83.91f) {
            return Mth.lerp((float)((t - 52.87f) / 31.04f), (float)94.3f, (float)13.5f);
        }
        if (t <= 85.06f) {
            return Mth.lerp((float)((t - 83.91f) / 1.15f), (float)13.5f, (float)10.5f);
        }
        if (t <= 86.21f) {
            return Mth.lerp((float)((t - 85.06f) / 1.15f), (float)10.5f, (float)7.7f);
        }
        if (t <= 87.36f) {
            return Mth.lerp((float)((t - 86.21f) / 1.15f), (float)7.7f, (float)5.0f);
        }
        if (t <= 88.51f) {
            return Mth.lerp((float)((t - 87.36f) / 1.15f), (float)5.0f, (float)2.3f);
        }
        if (t <= 89.66f) {
            return Mth.lerp((float)((t - 88.51f) / 1.15f), (float)2.3f, (float)0.1f);
        }
        if (t <= 90.8f) {
            return Mth.lerp((float)((t - 89.66f) / 1.15f), (float)0.1f, (float)-2.3f);
        }
        if (t <= 91.95f) {
            return Mth.lerp((float)((t - 90.8f) / 1.15f), (float)-2.3f, (float)-4.9f);
        }
        if (t <= 93.1f) {
            return Mth.lerp((float)((t - 91.95f) / 1.15f), (float)-4.9f, (float)-6.6f);
        }
        if (t <= 94.25f) {
            return Mth.lerp((float)((t - 93.1f) / 1.15f), (float)-6.6f, (float)-7.95f);
        }
        if (t <= 95.4f) {
            return Mth.lerp((float)((t - 94.25f) / 1.15f), (float)-7.95f, (float)-7.85f);
        }
        if (t <= 96.55f) {
            return Mth.lerp((float)((t - 95.4f) / 1.15f), (float)-7.85f, (float)-5.95f);
        }
        if (t <= 97.7f) {
            return Mth.lerp((float)((t - 96.55f) / 1.15f), (float)-5.95f, (float)-3.15f);
        }
        return Mth.lerp((float)((t - 97.7f) / 2.3f), (float)-3.15f, (float)0.0f);
    }
        public int period(){return 100;}
    }
    private static final class TerminatorModel implements Curve {
    private static final int TRACK_COUNT = 53;
    private static final int MAX_IDX = 53;
    private static final float[][] KEYFRAMES = new float[][]{{176.78f, 21.97f, 51.55f}, {128.03f, 19.86f, 55.87f}, {78.54f, 15.17f, 56.88f}, {31.65f, 11.5f, 53.78f}, {31.65f, 8.89f, 49.54f}, {31.65f, 6.28f, 45.31f}, {33.51f, 3.67f, 41.08f}, {6.9f, 1.72f, 36.59f}, {0.0f, 1.69f, 31.62f}, {0.0f, 1.69f, 26.65f}, {0.0f, 1.69f, 21.67f}, {0.0f, 1.69f, 16.7f}, {0.0f, 1.69f, 11.73f}, {0.0f, 1.69f, 6.75f}, {0.0f, 1.69f, 1.78f}, {0.0f, 1.69f, -3.19f}, {0.0f, 1.69f, -8.16f}, {0.0f, 1.69f, -13.14f}, {0.0f, 1.69f, -18.11f}, {0.0f, 1.69f, -23.08f}, {0.0f, 1.69f, -28.06f}, {0.0f, 1.69f, -33.03f}, {0.0f, 1.69f, -38.0f}, {0.0f, 1.69f, -42.97f}, {-0.57f, 1.69f, -47.95f}, {-34.42f, 3.17f, -52.6f}, {-43.88f, 6.6f, -56.2f}, {-43.88f, 10.05f, -59.78f}, {-43.88f, 13.49f, -63.37f}, {-92.83f, 17.95f, -65.12f}, {-147.62f, 22.07f, -62.65f}, {-178.31f, 22.94f, -57.85f}, {179.48f, 22.91f, -52.87f}, {179.48f, 22.86f, -47.9f}, {179.48f, 22.82f, -42.93f}, {179.48f, 22.77f, -37.96f}, {179.48f, 22.73f, -32.98f}, {179.48f, 22.68f, -28.01f}, {179.48f, 22.64f, -23.04f}, {179.48f, 22.6f, -18.07f}, {179.48f, 22.55f, -13.09f}, {179.48f, 22.51f, -8.12f}, {179.48f, 22.46f, -3.15f}, {179.48f, 22.42f, 1.82f}, {179.48f, 22.37f, 6.8f}, {179.48f, 22.33f, 11.77f}, {179.48f, 22.28f, 16.74f}, {179.48f, 22.24f, 21.72f}, {179.48f, 22.19f, 26.69f}, {179.48f, 22.15f, 31.66f}, {179.48f, 22.1f, 36.63f}, {179.48f, 22.06f, 41.61f}, {179.48f, 22.01f, 46.58f}, {176.78f, 21.97f, 51.55f}};
    private static final float START_Y = 21.97f;
    private static final float START_Z = 51.55f;
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    private float getKeyframeValue(float t, int component) {
        int wrapRange = 106;
        float normalized = t / (float)wrapRange * 53.0f;
        int idx1 = Mth.clamp((int)((int)normalized), (int)0, (int)53);
        int idx2 = Mth.clamp((int)(idx1 + 1), (int)0, (int)53);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.lerp((float)frac, (float)p1, (float)p2);
    }
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 21.97f;
    }
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 51.55f;
    }
        public int period(){return 106;}
    }
    private static final class ZBD04ARenderer implements Curve {
    private static final float[] MOVE_Y = new float[]{17.1544f, 17.0117f, 16.5921f, 15.9209f, 15.0381f, 13.9966f, 12.8588f, 11.6928f, 10.5684f, 9.5529f, 8.7071f, 8.0773f, 7.5283f, 6.9793f, 6.4303f, 5.8813f, 5.3323f, 4.7833f, 4.2343f, 3.6853f, 3.1363f, 2.5873f, 2.0383f, 1.4893f, 1.0499f, 0.885f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8841f, 0.8856f, 1.0568f, 1.4861f, 1.9695f, 2.4529f, 2.9363f, 3.4198f, 3.9032f, 4.3866f, 4.87f, 5.3534f, 5.8368f, 6.3202f, 6.8037f, 7.2871f, 7.8899f, 8.7178f, 9.7209f, 10.8388f, 12.004f, 13.1464f, 14.197f, 15.0925f, 15.7789f, 16.2149f, 16.3741f, 16.3832f, 16.3922f, 16.4013f, 16.4104f, 16.4195f, 16.4285f, 16.4376f, 16.4467f, 16.4557f, 16.4648f, 16.4739f, 16.483f, 16.492f, 16.5011f, 16.5102f, 16.5193f, 16.5283f, 16.5374f, 16.5465f, 16.5555f, 16.5646f, 16.5737f, 16.5828f, 16.5918f, 16.6009f, 16.61f, 16.6191f, 16.6281f, 16.6372f, 16.6463f, 16.6553f, 16.6644f, 16.6735f, 16.6826f, 16.6916f, 16.7007f, 16.7098f, 16.7189f, 16.7279f, 16.737f, 16.7461f, 16.7551f, 16.7642f, 16.7733f, 16.7824f, 16.7914f, 16.8005f, 16.8096f, 16.8187f, 16.8277f, 16.8368f, 16.8459f, 16.8549f, 16.864f, 16.8731f, 16.8822f, 16.8912f, 16.9003f, 16.9094f, 16.9185f, 16.9275f, 16.9366f, 16.9457f, 16.9547f, 16.9638f, 16.9729f, 16.982f, 16.991f, 17.0001f, 17.0092f, 17.0183f, 17.0273f, 17.0364f, 17.0455f, 17.0545f, 17.0636f, 17.0727f, 17.0818f, 17.0908f, 17.0999f, 17.109f, 17.1181f, 17.1271f, 17.1362f, 17.1453f, 17.1544f};
    private static final float[] MOVE_Z = new float[]{48.9828f, 50.1404f, 51.2286f, 52.1825f, 52.9447f, 53.4698f, 53.7262f, 53.6986f, 53.3887f, 52.8151f, 52.012f, 51.0297f, 49.9974f, 48.965f, 47.9326f, 46.9003f, 45.8679f, 44.8355f, 43.8032f, 42.7708f, 41.7384f, 40.706f, 39.6737f, 38.6413f, 37.5608f, 36.4062f, 35.2369f, 34.0676f, 32.8984f, 31.7291f, 30.5598f, 29.3906f, 28.2213f, 27.052f, 25.8828f, 24.7135f, 23.5442f, 22.375f, 21.2057f, 20.0364f, 18.8672f, 17.6979f, 16.5286f, 15.3594f, 14.1901f, 13.0208f, 11.8516f, 10.6823f, 9.513f, 8.3438f, 7.1745f, 6.0052f, 4.836f, 3.6667f, 2.4974f, 1.3282f, 0.1589f, -1.0104f, -2.1796f, -3.3489f, -4.5182f, -5.6874f, -6.8567f, -8.026f, -9.1952f, -10.3645f, -11.5338f, -12.703f, -13.8723f, -15.0416f, -16.2108f, -17.3801f, -18.5494f, -19.7186f, -20.8879f, -22.0572f, -23.2264f, -24.3957f, -25.565f, -26.7342f, -27.9035f, -29.0728f, -30.242f, -31.4113f, -32.5806f, -33.7498f, -34.9191f, -36.0884f, -37.2576f, -38.4269f, -39.5806f, -40.6665f, -41.7312f, -42.7958f, -43.8605f, -44.9251f, -45.9898f, -47.0544f, -48.1191f, -49.1838f, -50.2484f, -51.3131f, -52.3777f, -53.4424f, -54.4408f, -55.2623f, -55.8574f, -56.19f, -56.2402f, -56.005f, -55.4985f, -54.7512f, -53.8083f, -52.7265f, -51.5711f, -50.4019f, -49.2327f, -48.0634f, -46.8942f, -45.725f, -44.5557f, -43.3865f, -42.2173f, -41.0481f, -39.8788f, -38.7096f, -37.5404f, -36.3711f, -35.2019f, -34.0327f, -32.8634f, -31.6942f, -30.525f, -29.3557f, -28.1865f, -27.0173f, -25.848f, -24.6788f, -23.5096f, -22.3403f, -21.1711f, -20.0019f, -18.8326f, -17.6634f, -16.4942f, -15.325f, -14.1557f, -12.9865f, -11.8173f, -10.648f, -9.4788f, -8.3096f, -7.1403f, -5.9711f, -4.8019f, -3.6326f, -2.4634f, -1.2942f, -0.1249f, 1.0443f, 2.2135f, 3.3828f, 4.552f, 5.7212f, 6.8905f, 8.0597f, 9.2289f, 10.3981f, 11.5674f, 12.7366f, 13.9058f, 15.0751f, 16.2443f, 17.4135f, 18.5828f, 19.752f, 20.9212f, 22.0905f, 23.2597f, 24.4289f, 25.5982f, 26.7674f, 27.9366f, 29.1059f, 30.2751f, 31.4443f, 32.6136f, 33.7828f, 34.952f, 36.1212f, 37.2905f, 38.4597f, 39.6289f, 40.7982f, 41.9674f, 43.1366f, 44.3059f, 45.4751f, 46.6443f, 47.8136f, 48.9828f};
    private static final float[] ROT_X = new float[]{0.0f, -14.0545f, -28.1089f, -42.1634f, -56.2178f, -70.2723f, -84.3267f, -98.3812f, -112.4356f, -126.4901f, -140.5445f, -151.9967f, -151.9967f, -151.9967f, -151.9967f, -151.9967f, -151.9967f, -151.9967f, -151.9967f, -151.9967f, -151.9967f, -151.9967f, -151.9967f, -151.9967f, -164.8445f, -178.9003f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -181.4138f, -195.4696f, -204.4207f, -204.4207f, -204.4207f, -204.4207f, -204.4207f, -204.4207f, -204.4207f, -204.4207f, -204.4207f, -204.4207f, -204.4207f, -204.4207f, -204.4207f, -218.1704f, -232.2738f, -246.3773f, -260.4807f, -274.5841f, -288.6875f, -302.7909f, -316.8944f, -330.9978f, -345.1012f, -359.2046f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f, -359.5554f};
    private static final float[] TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return ZBD04ARenderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return ZBD04ARenderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return ZBD04ARenderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.7857143f;
    }
        public int period(){return 100;}
    }
    private static final class ZTZ59ARenderer implements Curve {
    private static final float[] MOVE_Y = new float[]{16.75f, 16.7243f, 16.6474f, 16.5202f, 16.3439f, 16.1204f, 15.8519f, 15.5413f, 15.1917f, 14.8066f, 14.3902f, 13.9466f, 13.4804f, 12.9964f, 12.4996f, 11.9951f, 11.488f, 10.9837f, 10.4872f, 10.0038f, 9.5382f, 9.0955f, 8.68f, 8.2961f, 7.9477f, 7.6384f, 7.3714f, 7.1494f, 6.9748f, 6.8492f, 6.6041f, 6.3507f, 6.0973f, 5.8439f, 5.5905f, 5.3371f, 5.0837f, 4.8303f, 4.5769f, 4.3235f, 4.0701f, 3.8167f, 3.5634f, 3.31f, 3.0566f, 2.8032f, 2.5498f, 2.2964f, 2.043f, 1.7896f, 1.5362f, 1.2828f, 1.0294f, 0.776f, 0.5226f, 0.2693f, 0.0159f, -0.1575f, -0.2207f, -0.2485f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.2469f, -0.2145f, -0.1467f, -0.0439f, 0.1781f, 0.47f, 0.7618f, 1.0537f, 1.3455f, 1.6374f, 1.9292f, 2.221f, 2.5129f, 2.8047f, 3.0966f, 3.3884f, 3.6803f, 3.9721f, 4.264f, 4.5558f, 4.8476f, 5.1395f, 5.4313f, 5.7232f, 6.015f, 6.3069f, 6.5987f, 6.8906f, 7.1824f, 7.4743f, 7.7216f, 7.8952f, 8.1162f, 8.3823f, 8.6907f, 9.0383f, 9.4215f, 9.8363f, 10.2786f, 10.7437f, 11.2268f, 11.7231f, 12.2273f, 12.7343f, 13.2389f, 13.736f, 14.2203f, 14.6869f, 15.131f, 15.5481f, 15.9338f, 16.2843f, 16.5958f, 16.8652f, 17.0897f, 17.267f, 17.3952f, 17.4732f, 17.5f, 17.4919f, 17.4837f, 17.4726f, 17.4575f, 17.4424f, 17.422f, 17.4007f, 17.3779f, 17.351f, 17.324f, 17.2938f, 17.262f, 17.2297f, 17.1936f, 17.1574f, 17.1194f, 17.0795f, 17.0397f, 16.9969f, 16.954f, 16.9101f, 16.8646f, 16.819f, 16.7719f, 16.7242f, 16.6763f, 16.6271f, 16.578f, 16.5281f, 16.4778f, 16.4276f, 16.3766f, 16.3257f, 16.2747f, 16.2236f, 16.1724f, 16.1215f, 16.0705f, 16.0197f, 15.9693f, 15.919f, 15.8692f, 15.8198f, 15.7705f, 15.7223f, 15.6742f, 15.6268f, 15.5803f, 15.5337f, 15.4889f, 15.4442f, 15.4003f, 15.3579f, 15.3154f, 15.2747f, 15.2347f, 15.1951f, 15.1577f, 15.1203f, 15.0847f, 15.0502f, 15.0158f, 14.9843f, 14.9529f, 14.9229f, 14.8947f, 14.8665f, 14.8413f, 14.8164f, 14.7927f, 14.7714f, 14.7501f, 14.7314f, 14.7137f, 14.6966f, 14.6826f, 14.6686f, 14.6568f, 14.6466f, 14.6364f, 14.6299f, 14.6235f, 14.6187f, 14.6161f, 14.6134f, 14.6141f, 14.6152f, 14.6175f, 14.6224f, 14.6273f, 14.6348f, 14.6434f, 14.6526f, 14.6648f, 14.677f, 14.6913f, 14.7071f, 14.7228f, 14.742f, 14.7611f, 14.7816f, 14.804f, 14.8264f, 14.8514f, 14.8769f, 14.9032f, 14.9315f, 14.9599f, 14.9901f, 15.0212f, 15.0525f, 15.086f, 15.1194f, 15.1541f, 15.1898f, 15.2254f, 15.2629f, 15.3004f, 15.3386f, 15.3778f, 15.4169f, 15.4571f, 15.4976f, 15.5382f, 15.5796f, 15.621f, 15.6627f, 15.7047f, 15.7467f, 15.7888f, 15.8309f, 15.873f, 15.9149f, 15.9568f, 15.9981f, 16.0394f, 16.0803f, 16.1205f, 16.1607f, 16.1996f, 16.2382f, 16.2765f, 16.313f, 16.3495f, 16.3845f, 16.4184f, 16.4522f, 16.483f, 16.5138f, 16.5429f, 16.57f, 16.597f, 16.6204f, 16.6431f, 16.6644f, 16.6823f, 16.7001f, 16.7139f, 16.7262f, 16.7376f, 16.7436f, 16.7497f, 16.7517f, 16.7509f, 16.75f};
    private static final float[] MOVE_Z = new float[]{40.5f, 41.0064f, 41.5076f, 41.9984f, 42.4738f, 42.9289f, 43.359f, 43.7598f, 44.127f, 44.4569f, 44.7461f, 44.9917f, 45.191f, 45.3422f, 45.4435f, 45.494f, 45.4931f, 45.4409f, 45.3379f, 45.1852f, 44.9842f, 44.7372f, 44.4465f, 44.1153f, 43.7469f, 43.3451f, 42.9141f, 42.4582f, 41.9822f, 41.491f, 41.0485f, 40.609f, 40.1696f, 39.7302f, 39.2907f, 38.8513f, 38.4119f, 37.9724f, 37.533f, 37.0936f, 36.6542f, 36.2147f, 35.7753f, 35.3359f, 34.8964f, 34.457f, 34.0176f, 33.5781f, 33.1387f, 32.6993f, 32.2598f, 31.8204f, 31.381f, 30.9416f, 30.5021f, 30.0627f, 29.6233f, 29.1546f, 28.6514f, 28.145f, 27.6378f, 27.1305f, 26.6233f, 26.116f, 25.6088f, 25.1015f, 24.5943f, 24.087f, 23.5798f, 23.0725f, 22.5653f, 22.058f, 21.5507f, 21.0435f, 20.5362f, 20.029f, 19.5217f, 19.0145f, 18.5072f, 18.0f, 17.4927f, 16.9855f, 16.4782f, 15.971f, 15.4637f, 14.9564f, 14.4492f, 13.9419f, 13.4347f, 12.9274f, 12.4202f, 11.9129f, 11.4057f, 10.8984f, 10.3912f, 9.8839f, 9.3767f, 8.8694f, 8.3621f, 7.8549f, 7.3476f, 6.8404f, 6.3331f, 5.8259f, 5.3186f, 4.8114f, 4.3041f, 3.7969f, 3.2896f, 2.7824f, 2.2751f, 1.7678f, 1.2606f, 0.7533f, 0.2461f, -0.2612f, -0.7684f, -1.2757f, -1.7829f, -2.2902f, -2.7974f, -3.3047f, -3.8119f, -4.3192f, -4.8265f, -5.3337f, -5.841f, -6.3482f, -6.8555f, -7.3627f, -7.87f, -8.3772f, -8.8845f, -9.3917f, -9.899f, -10.4063f, -10.9135f, -11.4208f, -11.928f, -12.4353f, -12.9425f, -13.4498f, -13.957f, -14.4643f, -14.9715f, -15.4788f, -15.986f, -16.4933f, -17.0006f, -17.5078f, -18.0151f, -18.5223f, -19.0296f, -19.5368f, -20.0441f, -20.5513f, -21.0586f, -21.5658f, -22.0731f, -22.5803f, -23.0876f, -23.5949f, -24.1021f, -24.6094f, -25.1166f, -25.6239f, -26.1311f, -26.6384f, -27.1456f, -27.6529f, -28.1601f, -28.6674f, -29.1746f, -29.6819f, -30.1892f, -30.6964f, -31.2037f, -31.7109f, -32.217f, -32.7196f, -33.2162f, -33.6634f, -34.0783f, -34.4932f, -34.9081f, -35.323f, -35.7379f, -36.1527f, -36.5676f, -36.9825f, -37.3974f, -37.8123f, -38.2272f, -38.6421f, -39.057f, -39.4719f, -39.8867f, -40.3016f, -40.7165f, -41.1314f, -41.5463f, -41.9612f, -42.3761f, -42.791f, -43.2059f, -43.6208f, -44.0356f, -44.4719f, -44.9483f, -45.4047f, -45.8363f, -46.2387f, -46.6079f, -46.9399f, -47.2314f, -47.4794f, -47.6814f, -47.8352f, -47.9393f, -47.9926f, -47.9945f, -47.9451f, -47.8448f, -47.6947f, -47.4964f, -47.2518f, -46.9635f, -46.6344f, -46.2679f, -45.8679f, -45.4383f, -44.9837f, -44.5087f, -44.0181f, -43.5171f, -43.0108f, -42.5036f, -41.9964f, -41.4893f, -40.9822f, -40.4752f, -39.9684f, -39.4616f, -38.9548f, -38.4483f, -37.9418f, -37.4354f, -36.9292f, -36.4229f, -35.917f, -35.411f, -34.9052f, -34.3995f, -33.8938f, -33.3884f, -32.8829f, -32.3776f, -31.8724f, -31.3672f, -30.8621f, -30.3571f, -29.8521f, -29.3472f, -28.8424f, -28.3376f, -27.8328f, -27.3281f, -26.8234f, -26.3187f, -25.814f, -25.3093f, -24.8047f, -24.3f, -23.7953f, -23.2906f, -22.7858f, -22.2811f, -21.7763f, -21.2714f, -20.7666f, -20.2616f, -19.7567f, -19.2516f, -18.7465f, -18.2414f, -17.7361f, -17.2308f, -16.7255f, -16.22f, -15.7146f, -15.2089f, -14.7033f, -14.1976f, -13.6917f, -13.1858f, -12.6798f, -12.1737f, -11.6676f, -11.1614f, -10.6551f, -10.1487f, -9.6423f, -9.1358f, -8.6292f, -8.1225f, -7.6158f, -7.109f, -6.6022f, -6.0953f, -5.5884f, -5.0814f, -4.5743f, -4.0673f, -3.5602f, -3.053f, -2.5459f, -2.0386f, -1.5314f, -1.0242f, -0.517f, -0.0097f, 0.4975f, 1.0048f, 1.512f, 2.0193f, 2.5265f, 3.0337f, 3.5409f, 4.0481f, 4.5552f, 5.0623f, 5.5693f, 6.0763f, 6.5833f, 7.0902f, 7.5971f, 8.104f, 8.6107f, 9.1175f, 9.6241f, 10.1307f, 10.6373f, 11.1438f, 11.6502f, 12.1566f, 12.6629f, 13.1692f, 13.6753f, 14.1815f, 14.6875f, 15.1935f, 15.6995f, 16.2054f, 16.7112f, 17.2171f, 17.7228f, 18.2285f, 18.7342f, 19.2398f, 19.7455f, 20.251f, 20.7566f, 21.2621f, 21.7676f, 22.2732f, 22.7787f, 23.2842f, 23.7897f, 24.2952f, 24.8007f, 25.3063f, 25.8118f, 26.3174f, 26.8231f, 27.3288f, 27.8345f, 28.3403f, 28.8461f, 29.3521f, 29.858f, 30.364f, 30.8702f, 31.3763f, 31.8826f, 32.3889f, 32.8953f, 33.4019f, 33.9084f, 34.4151f, 34.9219f, 35.4287f, 35.9356f, 36.4425f, 36.9496f, 37.4567f, 37.9638f, 38.471f, 38.9783f, 39.4855f, 39.9927f, 40.5f};
    private static final float[] ROT_X = new float[]{0.0f, -5.8127f, -11.6254f, -17.4381f, -23.2508f, -29.0635f, -34.8762f, -40.6889f, -46.5016f, -52.3143f, -58.127f, -63.9397f, -69.7524f, -75.5651f, -81.3778f, -87.1905f, -93.0032f, -98.8159f, -104.6286f, -110.4413f, -116.254f, -122.0668f, -127.8795f, -133.6922f, -139.5049f, -145.3176f, -151.1303f, -156.943f, -162.7557f, -168.5684f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -150.0306f, -170.8362f, -174.8449f, -178.8537f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -181.6669f, -185.6756f, -189.6844f, -193.6931f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -215.1236f, -197.1209f, -202.9336f, -208.7463f, -214.559f, -220.3717f, -226.1844f, -231.9971f, -237.8098f, -243.6225f, -249.4352f, -255.2479f, -261.0606f, -266.8733f, -272.686f, -278.4987f, -284.3114f, -290.1241f, -295.9368f, -301.7495f, -307.5622f, -313.3749f, -319.1876f, -325.0003f, -330.813f, -336.6257f, -342.4384f, -348.2512f, -354.0639f, -359.8766f, -360.931f, -360.931f, -361.7109f, -361.7109f, -361.7109f, -362.4135f, -362.4135f, -363.0414f, -363.0414f, -363.0414f, -363.5971f, -363.5971f, -364.0831f, -364.0831f, -364.0831f, -364.5023f, -364.5023f, -364.5023f, -364.8573f, -364.8573f, -365.1507f, -365.1507f, -365.1507f, -365.3852f, -365.3852f, -365.5634f, -365.5634f, -365.5634f, -365.6878f, -365.6878f, -365.7612f, -365.7612f, -365.7612f, -365.7859f, -365.7859f, -365.7859f, -365.7645f, -365.7645f, -365.6995f, -365.6995f, -365.6995f, -365.5933f, -365.5933f, -365.4484f, -365.4484f, -365.4484f, -365.2672f, -365.2672f, -365.2672f, -365.0522f, -365.0522f, -364.8058f, -364.8058f, -364.8058f, -364.5305f, -364.5305f, -364.2286f, -364.2286f, -364.2286f, -363.9027f, -363.9027f, -363.5552f, -363.5552f, -363.5552f, -363.1887f, -363.1887f, -363.1887f, -362.8055f, -362.8055f, -362.4083f, -362.4083f, -362.4083f, -361.9995f, -361.9995f, -361.5818f, -361.5818f, -361.5818f, -361.1575f, -361.1575f, -360.7294f, -360.7294f, -360.7294f, -360.2999f, -360.2999f, -360.2999f, -359.8716f, -359.8716f, -359.4471f, -359.4471f, -359.4471f, -359.029f, -359.029f, -358.6198f, -358.6198f, -358.6198f, -358.2221f, -358.2221f, -357.8383f, -357.8383f, -357.8383f, -357.4711f, -357.4711f, -357.4711f, -357.1229f, -357.1229f, -356.7963f, -356.7963f, -356.7963f, -356.4937f, -356.4937f, -356.2176f, -356.2176f, -356.2176f, -355.9706f, -355.9706f, -355.9706f, -355.7549f, -355.7549f, -355.5732f, -355.5732f, -355.5732f, -355.4279f, -355.4279f, -355.3213f, -355.3213f, -355.3213f, -355.2561f, -355.2561f, -355.2346f, -355.2346f, -355.2346f, -355.2594f, -355.2594f, -355.2594f, -355.333f, -355.333f, -355.4579f, -355.4579f, -355.4579f, -355.6366f, -355.6366f, -355.8718f, -355.8718f, -355.8718f, -356.166f, -356.166f, -356.5219f, -356.5219f, -356.5219f, -356.9421f, -356.9421f, -356.9421f, -357.4292f, -357.4292f, -357.9858f, -357.9858f, -357.9858f, -358.6146f, -358.6146f, -359.3179f, -359.3179f, -359.3179f, -360.0982f, -360.0982f, -360.0982f};
    private static final float[] TIMES = new float[]{0.0f, 0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 2.25f, 2.5f, 2.75f, 3.0f, 3.25f, 3.5f, 3.75f, 4.0f, 4.25f, 4.5f, 4.75f, 5.0f, 5.25f, 5.5f, 5.75f, 6.0f, 6.25f, 6.5f, 6.75f, 7.0f, 7.25f, 7.5f, 7.75f, 8.0f, 8.25f, 8.5f, 8.75f, 9.0f, 9.25f, 9.5f, 9.75f, 10.0f, 10.25f, 10.5f, 10.75f, 11.0f, 11.25f, 11.5f, 11.75f, 12.0f, 12.25f, 12.5f, 12.75f, 13.0f, 13.25f, 13.5f, 13.75f, 14.0f, 14.25f, 14.5f, 14.75f, 15.0f, 15.25f, 15.5f, 15.75f, 16.0f, 16.25f, 16.5f, 16.75f, 17.0f, 17.25f, 17.5f, 17.75f, 18.0f, 18.25f, 18.5f, 18.75f, 19.0f, 19.25f, 19.5f, 19.75f, 20.0f, 20.25f, 20.5f, 20.75f, 21.0f, 21.25f, 21.5f, 21.75f, 22.0f, 22.25f, 22.5f, 22.75f, 23.0f, 23.25f, 23.5f, 23.75f, 24.0f, 24.25f, 24.5f, 24.75f, 25.0f, 25.25f, 25.5f, 25.75f, 26.0f, 26.25f, 26.5f, 26.75f, 27.0f, 27.25f, 27.5f, 27.75f, 28.0f, 28.25f, 28.5f, 28.75f, 29.0f, 29.25f, 29.5f, 29.75f, 30.0f, 30.25f, 30.5f, 30.75f, 31.0f, 31.25f, 31.5f, 31.75f, 32.0f, 32.25f, 32.5f, 32.75f, 33.0f, 33.25f, 33.5f, 33.75f, 34.0f, 34.25f, 34.5f, 34.75f, 35.0f, 35.25f, 35.5f, 35.75f, 36.0f, 36.25f, 36.5f, 36.75f, 37.0f, 37.25f, 37.5f, 37.75f, 38.0f, 38.25f, 38.5f, 38.75f, 39.0f, 39.25f, 39.5f, 39.75f, 40.0f, 40.25f, 40.5f, 40.75f, 41.0f, 41.25f, 41.5f, 41.75f, 42.0f, 42.25f, 42.5f, 42.75f, 43.0f, 43.25f, 43.5f, 43.75f, 44.0f, 44.25f, 44.5f, 44.75f, 45.0f, 45.25f, 45.5f, 45.75f, 46.0f, 46.25f, 46.5f, 46.75f, 47.0f, 47.25f, 47.5f, 47.75f, 48.0f, 48.25f, 48.5f, 48.75f, 49.0f, 49.25f, 49.5f, 49.75f, 50.0f, 50.25f, 50.5f, 50.75f, 51.0f, 51.25f, 51.5f, 51.75f, 52.0f, 52.25f, 52.5f, 52.75f, 53.0f, 53.25f, 53.5f, 53.75f, 54.0f, 54.25f, 54.5f, 54.75f, 55.0f, 55.25f, 55.5f, 55.75f, 56.0f, 56.25f, 56.5f, 56.75f, 57.0f, 57.25f, 57.5f, 57.75f, 58.0f, 58.25f, 58.5f, 58.75f, 59.0f, 59.25f, 59.5f, 59.75f, 60.0f, 60.25f, 60.5f, 60.75f, 61.0f, 61.25f, 61.5f, 61.75f, 62.0f, 62.25f, 62.5f, 62.75f, 63.0f, 63.25f, 63.5f, 63.75f, 64.0f, 64.25f, 64.5f, 64.75f, 65.0f, 65.25f, 65.5f, 65.75f, 66.0f, 66.25f, 66.5f, 66.75f, 67.0f, 67.25f, 67.5f, 67.75f, 68.0f, 68.25f, 68.5f, 68.75f, 69.0f, 69.25f, 69.5f, 69.75f, 70.0f, 70.25f, 70.5f, 70.75f, 71.0f, 71.25f, 71.5f, 71.75f, 72.0f, 72.25f, 72.5f, 72.75f, 73.0f, 73.25f, 73.5f, 73.75f, 74.0f, 74.25f, 74.5f, 74.75f, 75.0f, 75.25f, 75.5f, 75.75f, 76.0f, 76.25f, 76.5f, 76.75f, 77.0f, 77.25f, 77.5f, 77.75f, 78.0f, 78.25f, 78.5f, 78.75f, 79.0f, 79.25f, 79.5f, 79.75f, 80.0f, 80.25f, 80.5f, 80.75f, 81.0f, 81.25f, 81.5f, 81.75f, 82.0f, 82.25f, 82.5f, 82.75f, 83.0f, 83.25f, 83.5f, 83.75f, 84.0f, 84.25f, 84.5f, 84.75f, 85.0f, 85.25f, 85.5f, 85.75f, 86.0f, 86.25f, 86.5f, 86.75f, 87.0f, 87.25f, 87.5f, 87.75f, 88.0f, 88.25f, 88.5f, 88.75f, 89.0f, 89.25f, 89.5f, 89.75f, 90.0f, 90.25f, 90.5f, 90.75f, 91.0f, 91.25f, 91.5f, 91.75f, 92.0f, 92.25f, 92.5f, 92.75f, 93.0f, 93.25f, 93.5f, 93.75f, 94.0f, 94.25f, 94.5f, 94.75f, 95.0f, 95.25f, 95.5f, 95.75f, 96.0f, 96.25f, 96.5f, 96.75f, 97.0f, 97.25f, 97.5f, 97.75f, 98.0f, 98.25f, 98.5f, 98.75f, 99.0f, 99.25f, 99.5f, 99.75f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 2.25f, 2.5f, 2.75f, 3.0f, 3.25f, 3.5f, 3.75f, 4.0f, 4.25f, 4.5f, 4.75f, 5.0f, 5.25f, 5.5f, 5.75f, 6.0f, 6.25f, 6.5f, 6.75f, 7.0f, 7.25f, 7.5f, 7.75f, 8.0f, 8.25f, 8.5f, 8.75f, 9.0f, 9.25f, 9.5f, 9.75f, 10.0f, 10.25f, 10.5f, 10.75f, 11.0f, 11.25f, 11.5f, 11.75f, 12.0f, 12.25f, 12.5f, 12.75f, 13.0f, 13.25f, 13.5f, 13.75f, 14.0f, 14.25f, 14.5f, 14.75f, 15.0f, 15.25f, 15.5f, 15.75f, 16.0f, 16.25f, 16.5f, 16.75f, 17.0f, 17.25f, 17.5f, 17.75f, 18.0f, 18.25f, 18.5f, 18.75f, 19.0f, 19.25f, 19.5f, 19.75f, 20.0f, 20.25f, 20.5f, 20.75f, 21.0f, 21.25f, 21.5f, 21.75f, 22.0f, 22.25f, 22.5f, 22.75f, 23.0f, 23.25f, 23.5f, 23.75f, 24.0f, 24.25f, 24.5f, 24.75f, 25.0f, 25.25f, 25.5f, 25.75f, 26.0f, 26.25f, 26.5f, 26.75f, 27.0f, 27.25f, 27.5f, 27.75f, 28.0f, 28.25f, 28.5f, 28.75f, 29.0f, 29.25f, 29.5f, 29.75f, 30.0f, 30.25f, 30.5f, 30.75f, 31.0f, 31.25f, 31.5f, 31.75f, 32.0f, 32.25f, 32.5f, 32.75f, 33.0f, 33.25f, 33.5f, 33.75f, 34.0f, 34.25f, 34.5f, 34.75f, 35.0f, 35.25f, 35.5f, 35.75f, 36.0f, 36.25f, 36.5f, 36.75f, 37.0f, 37.25f, 37.5f, 37.75f, 38.0f, 38.25f, 38.5f, 38.75f, 39.0f, 39.25f, 39.5f, 39.75f, 40.0f, 40.25f, 40.5f, 40.75f, 41.0f, 41.25f, 41.5f, 41.75f, 42.0f, 42.25f, 42.5f, 42.75f, 43.0f, 43.25f, 43.5f, 43.75f, 44.0f, 44.25f, 44.5f, 44.75f, 45.0f, 45.25f, 45.5f, 45.75f, 46.0f, 46.25f, 46.5f, 46.75f, 47.0f, 47.25f, 47.5f, 47.75f, 48.0f, 48.25f, 48.5f, 48.75f, 49.0f, 49.25f, 49.5f, 49.75f, 50.0f, 50.25f, 50.5f, 50.75f, 51.0f, 51.25f, 51.5f, 51.75f, 52.0f, 52.25f, 52.5f, 52.75f, 53.0f, 53.25f, 53.5f, 53.75f, 54.0f, 54.25f, 54.5f, 54.75f, 55.0f, 55.25f, 55.5f, 55.75f, 56.0f, 56.25f, 56.5f, 56.75f, 57.0f, 57.25f, 57.5f, 57.75f, 58.0f, 58.25f, 58.5f, 58.75f, 59.0f, 59.25f, 59.5f, 59.75f, 60.0f, 60.25f, 60.5f, 60.75f, 61.0f, 61.25f, 61.5f, 61.75f, 62.0f, 62.25f, 62.5f, 62.75f, 63.0f, 63.25f, 63.5f, 63.75f, 64.0f, 64.25f, 64.5f, 64.75f, 65.0f, 65.25f, 65.5f, 65.75f, 66.0f, 66.25f, 66.5f, 66.75f, 67.0f, 67.25f, 67.5f, 67.75f, 68.0f, 68.25f, 68.5f, 68.75f, 69.0f, 69.25f, 69.5f, 69.75f, 70.0f, 70.25f, 70.5f, 70.75f, 71.0f, 71.25f, 71.5f, 71.75f, 72.0f, 72.25f, 72.5f, 72.75f, 73.0f, 73.25f, 73.5f, 73.75f, 74.0f, 74.25f, 74.5f, 74.75f, 75.0f, 75.25f, 75.5f, 75.75f, 76.0f, 76.25f, 76.5f, 76.75f, 77.0f, 77.25f, 77.5f, 77.75f, 78.0f, 78.25f, 78.5f, 78.75f, 79.0f, 79.25f, 79.5f, 79.75f, 80.0f, 80.25f, 80.5f, 80.75f, 81.0f, 81.25f, 81.5f, 81.75f, 82.0f, 82.25f, 82.5f, 82.75f, 83.0f, 83.25f, 83.5f, 83.75f, 84.0f, 84.25f, 84.5f, 84.75f, 85.0f, 85.25f, 85.5f, 85.75f, 86.0f, 86.25f, 86.5f, 86.75f, 87.0f, 87.25f, 87.5f, 87.75f, 88.0f, 88.25f, 88.5f, 88.75f, 89.0f, 89.25f, 89.5f, 89.75f, 90.0f, 90.25f, 90.5f, 90.75f, 91.0f, 91.25f, 91.5f, 91.75f, 92.0f, 92.25f, 92.5f, 92.75f, 93.0f, 93.25f, 93.5f, 93.75f, 94.0f, 94.25f, 94.5f, 94.75f, 95.0f, 95.25f, 95.5f, 95.75f, 96.0f, 96.25f, 96.5f, 96.75f, 97.0f, 97.25f, 97.5f, 97.75f, 98.0f, 98.25f, 98.5f, 98.75f, 99.0f, 99.25f, 99.5f, 99.75f, 100.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return ZTZ59ARenderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return ZTZ59ARenderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return ZTZ59ARenderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 2.631579f;
    }
        public int period(){return 100;}
    }
    private static final class ZTZ99ASbmRenderer implements Curve {
    private static final float[] MOVE_Y = new float[]{16.749f, 16.552f, 15.9769f, 15.0702f, 13.9052f, 12.576f, 11.1902f, 9.8598f, 8.6922f, 7.782f, 7.2027f, 6.7803f, 6.2711f, 5.7618f, 5.2526f, 4.7433f, 4.234f, 3.7248f, 3.2155f, 2.7062f, 2.197f, 1.7828f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.751f, 1.8171f, 2.1647f, 2.8677f, 3.6825f, 4.4973f, 5.3121f, 6.127f, 6.9418f, 7.7566f, 8.5714f, 9.3863f, 10.2011f, 11.0159f, 11.8308f, 12.6456f, 13.4127f, 14.4516f, 15.7066f, 17.0763f, 18.4499f, 19.7164f, 20.7735f, 21.5355f, 21.9411f, 21.9714f, 21.9114f, 21.8514f, 21.7914f, 21.7313f, 21.6713f, 21.6113f, 21.5512f, 21.4912f, 21.4312f, 21.3712f, 21.3111f, 21.2511f, 21.1911f, 21.1311f, 21.071f, 21.011f, 20.951f, 20.8909f, 20.8309f, 20.7709f, 20.7109f, 20.6508f, 20.5908f, 20.5308f, 20.4707f, 20.4107f, 20.3507f, 20.2907f, 20.2306f, 20.1706f, 20.1106f, 20.0505f, 19.9905f, 19.9305f, 19.8705f, 19.8104f, 19.7504f, 19.6904f, 19.6303f, 19.5703f, 19.5103f, 19.4503f, 19.3902f, 19.3302f, 19.2702f, 19.2102f, 19.1501f, 19.0901f, 19.0301f, 18.97f, 18.91f, 18.85f, 18.79f, 18.7299f, 18.6699f, 18.6099f, 18.5498f, 18.4898f, 18.4298f, 18.3698f, 18.3097f, 18.2497f, 18.1897f, 18.1296f, 18.0696f, 18.0096f, 17.9496f, 17.8895f, 17.8295f, 17.7695f, 17.7094f, 17.6494f, 17.5894f, 17.5294f, 17.4693f, 17.4093f, 17.3493f, 17.2893f, 17.2292f, 17.1692f, 17.1092f, 17.0491f, 16.9891f, 16.9291f, 16.8691f, 16.809f, 16.749f};
    private static final float[] MOVE_Z = new float[]{61.516f, 62.8877f, 64.1486f, 65.1966f, 65.9471f, 66.3393f, 66.3417f, 65.9539f, 65.2073f, 64.1624f, 62.9035f, 61.5831f, 60.2892f, 58.9952f, 57.7013f, 56.4074f, 55.1135f, 53.8196f, 52.5257f, 51.2318f, 49.9379f, 48.6195f, 47.23f, 45.8395f, 44.449f, 43.0585f, 41.6679f, 40.2774f, 38.8869f, 37.4964f, 36.1059f, 34.7154f, 33.3248f, 31.9343f, 30.5438f, 29.1533f, 27.7628f, 26.3722f, 24.9817f, 23.5912f, 22.2007f, 20.8102f, 19.4197f, 18.0291f, 16.6386f, 15.2481f, 13.8576f, 12.4671f, 11.0766f, 9.686f, 8.2955f, 6.905f, 5.5145f, 4.124f, 2.7334f, 1.3429f, -0.0476f, -1.4381f, -2.8286f, -4.2191f, -5.6097f, -7.0002f, -8.3907f, -9.7812f, -11.1717f, -12.5623f, -13.9528f, -15.3433f, -16.7338f, -18.1243f, -19.5148f, -20.9054f, -22.2959f, -23.6864f, -25.0769f, -26.4674f, -27.8579f, -29.2485f, -30.639f, -32.0295f, -33.42f, -34.8105f, -36.2011f, -37.5916f, -38.9821f, -40.3726f, -41.7631f, -43.1536f, -44.5442f, -45.9315f, -47.275f, -48.4652f, -49.592f, -50.7188f, -51.8455f, -52.9723f, -54.099f, -55.2258f, -56.3526f, -57.4793f, -58.6061f, -59.7329f, -60.8596f, -61.9864f, -63.1422f, -64.0594f, -64.647f, -64.8578f, -64.6745f, -64.112f, -63.2158f, -62.0584f, -60.7332f, -59.3463f, -57.9571f, -56.5678f, -55.1786f, -53.7894f, -52.4002f, -51.0109f, -49.6217f, -48.2325f, -46.8433f, -45.4541f, -44.0648f, -42.6756f, -41.2864f, -39.8972f, -38.508f, -37.1187f, -35.7295f, -34.3403f, -32.9511f, -31.5618f, -30.1726f, -28.7834f, -27.3942f, -26.005f, -24.6157f, -23.2265f, -21.8373f, -20.4481f, -19.0588f, -17.6696f, -16.2804f, -14.8912f, -13.502f, -12.1127f, -10.7235f, -9.3343f, -7.9451f, -6.5559f, -5.1666f, -3.7774f, -2.3882f, -0.999f, 0.3903f, 1.7795f, 3.1687f, 4.5579f, 5.9471f, 7.3364f, 8.7256f, 10.1148f, 11.504f, 12.8932f, 14.2825f, 15.6717f, 17.0609f, 18.4501f, 19.8394f, 21.2286f, 22.6178f, 24.007f, 25.3962f, 26.7855f, 28.1747f, 29.5639f, 30.9531f, 32.3423f, 33.7316f, 35.1208f, 36.51f, 37.8992f, 39.2885f, 40.6777f, 42.0669f, 43.4561f, 44.8453f, 46.2346f, 47.6238f, 49.013f, 50.4022f, 51.7914f, 53.1807f, 54.5699f, 55.9591f, 57.3483f, 58.7376f, 60.1268f, 61.516f};
    private static final float[] ROT_X = new float[]{0.0f, -16.3461f, -32.6922f, -49.0382f, -65.3843f, -81.7304f, -98.0765f, -114.4226f, -130.7687f, -147.1147f, -163.4608f, -158.516f, -158.516f, -158.516f, -158.516f, -158.516f, -158.516f, -158.516f, -158.516f, -158.516f, -158.516f, -174.2782f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -188.258f, -200.7573f, -215.8728f, -215.8728f, -215.8728f, -215.8728f, -215.8728f, -215.8728f, -215.8728f, -215.8728f, -215.8728f, -215.8728f, -215.8728f, -215.8728f, -215.8728f, -220.389f, -236.7351f, -253.0811f, -269.4272f, -285.7733f, -302.1194f, -318.4655f, -334.8116f, -351.1576f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f, -362.4742f};
    private static final float[] TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return ZTZ99ASbmRenderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return ZTZ99ASbmRenderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return ZTZ99ASbmRenderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.7857143f;
    }
        public int period(){return 100;}
    }
    private static final class Zbd05Renderer implements Curve {
    private static final float[] MOVE_Y = new float[]{16.0493f, 15.8406f, 15.2377f, 14.3077f, 13.1542f, 11.9055f, 10.7006f, 9.6737f, 8.9391f, 8.2742f, 7.5747f, 6.8752f, 6.1756f, 5.4761f, 4.7766f, 4.0771f, 3.3776f, 2.678f, 1.9785f, 1.4443f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2969f, 1.5501f, 2.1518f, 2.7991f, 3.4464f, 4.0937f, 4.741f, 5.3883f, 6.0356f, 6.6829f, 7.3302f, 7.9775f, 8.6248f, 9.2721f, 9.9075f, 10.5888f, 11.5774f, 12.7632f, 14.0143f, 15.1913f, 16.1633f, 16.8221f, 17.0943f, 17.0893f, 17.0775f, 17.0657f, 17.0539f, 17.042f, 17.0302f, 17.0184f, 17.0066f, 16.9948f, 16.9829f, 16.9711f, 16.9593f, 16.9475f, 16.9357f, 16.9238f, 16.912f, 16.9002f, 16.8884f, 16.8766f, 16.8648f, 16.8529f, 16.8411f, 16.8293f, 16.8175f, 16.8057f, 16.7938f, 16.782f, 16.7702f, 16.7584f, 16.7466f, 16.7348f, 16.7229f, 16.7111f, 16.6993f, 16.6875f, 16.6757f, 16.6638f, 16.652f, 16.6402f, 16.6284f, 16.6166f, 16.6048f, 16.5929f, 16.5811f, 16.5693f, 16.5575f, 16.5457f, 16.5338f, 16.522f, 16.5102f, 16.4984f, 16.4866f, 16.4748f, 16.4629f, 16.4511f, 16.4393f, 16.4275f, 16.4157f, 16.4038f, 16.392f, 16.3802f, 16.3684f, 16.3566f, 16.3448f, 16.3329f, 16.3211f, 16.3093f, 16.2975f, 16.2857f, 16.2738f, 16.262f, 16.2502f, 16.2384f, 16.2266f, 16.2148f, 16.2029f, 16.1911f, 16.1793f, 16.1675f, 16.1557f, 16.1438f, 16.132f, 16.1202f, 16.1084f, 16.0966f, 16.0848f, 16.0729f, 16.0611f, 16.0493f};
    private static final float[] MOVE_Z = new float[]{44.3666f, 45.6002f, 46.6965f, 47.5334f, 48.0179f, 48.0959f, 47.7587f, 47.044f, 46.0313f, 44.9676f, 43.9233f, 42.8789f, 41.8345f, 40.7901f, 39.7457f, 38.7013f, 37.6569f, 36.6125f, 35.5681f, 34.443f, 33.1998f, 31.9428f, 30.6858f, 29.4288f, 28.1718f, 26.9148f, 25.6578f, 24.4007f, 23.1437f, 21.8867f, 20.6297f, 19.3727f, 18.1157f, 16.8587f, 15.6017f, 14.3447f, 13.0877f, 11.8306f, 10.5736f, 9.3166f, 8.0596f, 6.8026f, 5.5456f, 4.2886f, 3.0316f, 1.7746f, 0.5176f, -0.7394f, -1.9965f, -3.2535f, -4.5105f, -5.7675f, -7.0245f, -8.2815f, -9.5385f, -10.7955f, -12.0525f, -13.3095f, -14.5666f, -15.8236f, -17.0806f, -18.3376f, -19.5946f, -20.8516f, -22.1086f, -23.3656f, -24.6226f, -25.8796f, -27.1367f, -28.3937f, -29.6507f, -30.9077f, -32.1647f, -33.4217f, -34.6787f, -35.9357f, -37.1927f, -38.4497f, -39.7067f, -40.9638f, -42.2208f, -43.4778f, -44.7348f, -45.9918f, -47.2488f, -48.5058f, -49.7628f, -51.0198f, -52.2768f, -53.5335f, -54.7613f, -55.8608f, -56.9383f, -58.0158f, -59.0934f, -60.1709f, -61.2484f, -62.326f, -63.4035f, -64.4811f, -65.5586f, -66.6361f, -67.7137f, -68.7972f, -69.8465f, -70.6134f, -71.0123f, -70.999f, -70.5748f, -69.7871f, -68.7234f, -67.5022f, -66.2454f, -64.9884f, -63.7315f, -62.4745f, -61.2175f, -59.9606f, -58.7036f, -57.4467f, -56.1897f, -54.9328f, -53.6758f, -52.4189f, -51.1619f, -49.905f, -48.648f, -47.3911f, -46.1341f, -44.8771f, -43.6202f, -42.3632f, -41.1063f, -39.8493f, -38.5924f, -37.3354f, -36.0785f, -34.8215f, -33.5646f, -32.3076f, -31.0506f, -29.7937f, -28.5367f, -27.2798f, -26.0228f, -24.7659f, -23.5089f, -22.252f, -20.995f, -19.7381f, -18.4811f, -17.2242f, -15.9672f, -14.7102f, -13.4533f, -12.1963f, -10.9394f, -9.6824f, -8.4255f, -7.1685f, -5.9116f, -4.6546f, -3.3977f, -2.1407f, -0.8837f, 0.3732f, 1.6302f, 2.8871f, 4.1441f, 5.401f, 6.658f, 7.9149f, 9.1719f, 10.4288f, 11.6858f, 12.9427f, 14.1997f, 15.4567f, 16.7136f, 17.9706f, 19.2275f, 20.4845f, 21.7414f, 22.9984f, 24.2553f, 25.5123f, 26.7692f, 28.0262f, 29.2832f, 30.5401f, 31.7971f, 33.054f, 34.311f, 35.5679f, 36.8249f, 38.0818f, 39.3388f, 40.5957f, 41.8527f, 43.1096f, 44.3666f};
    private static final float[] ROT_X = new float[]{0.0f, -19.2057f, -38.4114f, -57.6171f, -76.8228f, -96.0285f, -115.2342f, -134.4399f, -153.6455f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -165.3572f, -179.7615f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -184.4456f, -198.8499f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -203.3915f, -222.5972f, -241.8029f, -261.0086f, -280.2143f, -299.42f, -318.6257f, -337.8314f, -357.037f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f};
    private static final float[] TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return Zbd05Renderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return Zbd05Renderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return Zbd05Renderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.7857143f;
    }
        public int period(){return 100;}
    }
    private static final class Ztd05Renderer implements Curve {
    private static final float[] MOVE_Y = new float[]{16.0493f, 15.8406f, 15.2377f, 14.3077f, 13.1542f, 11.9055f, 10.7006f, 9.6737f, 8.9391f, 8.2742f, 7.5747f, 6.8752f, 6.1756f, 5.4761f, 4.7766f, 4.0771f, 3.3776f, 2.678f, 1.9785f, 1.4443f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2819f, 1.2969f, 1.5501f, 2.1518f, 2.7991f, 3.4464f, 4.0937f, 4.741f, 5.3883f, 6.0356f, 6.6829f, 7.3302f, 7.9775f, 8.6248f, 9.2721f, 9.9075f, 10.5888f, 11.5774f, 12.7632f, 14.0143f, 15.1913f, 16.1633f, 16.8221f, 17.0943f, 17.0893f, 17.0775f, 17.0657f, 17.0539f, 17.042f, 17.0302f, 17.0184f, 17.0066f, 16.9948f, 16.9829f, 16.9711f, 16.9593f, 16.9475f, 16.9357f, 16.9238f, 16.912f, 16.9002f, 16.8884f, 16.8766f, 16.8648f, 16.8529f, 16.8411f, 16.8293f, 16.8175f, 16.8057f, 16.7938f, 16.782f, 16.7702f, 16.7584f, 16.7466f, 16.7348f, 16.7229f, 16.7111f, 16.6993f, 16.6875f, 16.6757f, 16.6638f, 16.652f, 16.6402f, 16.6284f, 16.6166f, 16.6048f, 16.5929f, 16.5811f, 16.5693f, 16.5575f, 16.5457f, 16.5338f, 16.522f, 16.5102f, 16.4984f, 16.4866f, 16.4748f, 16.4629f, 16.4511f, 16.4393f, 16.4275f, 16.4157f, 16.4038f, 16.392f, 16.3802f, 16.3684f, 16.3566f, 16.3448f, 16.3329f, 16.3211f, 16.3093f, 16.2975f, 16.2857f, 16.2738f, 16.262f, 16.2502f, 16.2384f, 16.2266f, 16.2148f, 16.2029f, 16.1911f, 16.1793f, 16.1675f, 16.1557f, 16.1438f, 16.132f, 16.1202f, 16.1084f, 16.0966f, 16.0848f, 16.0729f, 16.0611f, 16.0493f};
    private static final float[] MOVE_Z = new float[]{44.3666f, 45.6002f, 46.6965f, 47.5334f, 48.0179f, 48.0959f, 47.7587f, 47.044f, 46.0313f, 44.9676f, 43.9233f, 42.8789f, 41.8345f, 40.7901f, 39.7457f, 38.7013f, 37.6569f, 36.6125f, 35.5681f, 34.443f, 33.1998f, 31.9428f, 30.6858f, 29.4288f, 28.1718f, 26.9148f, 25.6578f, 24.4007f, 23.1437f, 21.8867f, 20.6297f, 19.3727f, 18.1157f, 16.8587f, 15.6017f, 14.3447f, 13.0877f, 11.8306f, 10.5736f, 9.3166f, 8.0596f, 6.8026f, 5.5456f, 4.2886f, 3.0316f, 1.7746f, 0.5176f, -0.7394f, -1.9965f, -3.2535f, -4.5105f, -5.7675f, -7.0245f, -8.2815f, -9.5385f, -10.7955f, -12.0525f, -13.3095f, -14.5666f, -15.8236f, -17.0806f, -18.3376f, -19.5946f, -20.8516f, -22.1086f, -23.3656f, -24.6226f, -25.8796f, -27.1367f, -28.3937f, -29.6507f, -30.9077f, -32.1647f, -33.4217f, -34.6787f, -35.9357f, -37.1927f, -38.4497f, -39.7067f, -40.9638f, -42.2208f, -43.4778f, -44.7348f, -45.9918f, -47.2488f, -48.5058f, -49.7628f, -51.0198f, -52.2768f, -53.5335f, -54.7613f, -55.8608f, -56.9383f, -58.0158f, -59.0934f, -60.1709f, -61.2484f, -62.326f, -63.4035f, -64.4811f, -65.5586f, -66.6361f, -67.7137f, -68.7972f, -69.8465f, -70.6134f, -71.0123f, -70.999f, -70.5748f, -69.7871f, -68.7234f, -67.5022f, -66.2454f, -64.9884f, -63.7315f, -62.4745f, -61.2175f, -59.9606f, -58.7036f, -57.4467f, -56.1897f, -54.9328f, -53.6758f, -52.4189f, -51.1619f, -49.905f, -48.648f, -47.3911f, -46.1341f, -44.8771f, -43.6202f, -42.3632f, -41.1063f, -39.8493f, -38.5924f, -37.3354f, -36.0785f, -34.8215f, -33.5646f, -32.3076f, -31.0506f, -29.7937f, -28.5367f, -27.2798f, -26.0228f, -24.7659f, -23.5089f, -22.252f, -20.995f, -19.7381f, -18.4811f, -17.2242f, -15.9672f, -14.7102f, -13.4533f, -12.1963f, -10.9394f, -9.6824f, -8.4255f, -7.1685f, -5.9116f, -4.6546f, -3.3977f, -2.1407f, -0.8837f, 0.3732f, 1.6302f, 2.8871f, 4.1441f, 5.401f, 6.658f, 7.9149f, 9.1719f, 10.4288f, 11.6858f, 12.9427f, 14.1997f, 15.4567f, 16.7136f, 17.9706f, 19.2275f, 20.4845f, 21.7414f, 22.9984f, 24.2553f, 25.5123f, 26.7692f, 28.0262f, 29.2832f, 30.5401f, 31.7971f, 33.054f, 34.311f, 35.5679f, 36.8249f, 38.0818f, 39.3388f, 40.5957f, 41.8527f, 43.1096f, 44.3666f};
    private static final float[] ROT_X = new float[]{0.0f, -19.2057f, -38.4114f, -57.6171f, -76.8228f, -96.0285f, -115.2342f, -134.4399f, -153.6455f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -146.1864f, -165.3572f, -179.7615f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -184.4456f, -198.8499f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -210.9939f, -203.3915f, -222.5972f, -241.8029f, -261.0086f, -280.2143f, -299.42f, -318.6257f, -337.8314f, -357.037f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f, -360.5387f};
    private static final float[] TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return Ztd05Renderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return Ztd05Renderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return Ztd05Renderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.7857143f;
    }
        public int period(){return 100;}
    }
    private static final class Ztz96aRenderer implements Curve {
    private static final float[] MOVE_Y = new float[]{19.7449f, 19.5969f, 19.161f, 18.4605f, 17.5332f, 16.4289f, 15.2072f, 13.9336f, 12.6768f, 11.5044f, 10.4794f, 9.6571f, 9.0678f, 8.5129f, 7.9581f, 7.4032f, 6.8484f, 6.2935f, 5.7387f, 5.1838f, 4.629f, 4.0741f, 3.5193f, 2.9644f, 2.4096f, 1.9702f, 1.8057f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8041f, 1.8954f, 2.2496f, 2.8872f, 3.5901f, 4.2931f, 4.996f, 5.699f, 6.402f, 7.1049f, 7.8079f, 8.5108f, 9.2138f, 9.9167f, 10.6197f, 11.3226f, 12.0256f, 12.7178f, 13.6328f, 14.7427f, 15.9754f, 17.2506f, 18.4854f, 19.5995f, 20.5203f, 21.1881f, 21.5594f, 21.6218f, 21.5997f, 21.5776f, 21.5555f, 21.5335f, 21.5114f, 21.4893f, 21.4672f, 21.4451f, 21.4231f, 21.401f, 21.3789f, 21.3568f, 21.3347f, 21.3127f, 21.2906f, 21.2685f, 21.2464f, 21.2243f, 21.2022f, 21.1802f, 21.1581f, 21.136f, 21.1139f, 21.0918f, 21.0698f, 21.0477f, 21.0256f, 21.0035f, 20.9814f, 20.9594f, 20.9373f, 20.9152f, 20.8931f, 20.871f, 20.849f, 20.8269f, 20.8048f, 20.7827f, 20.7606f, 20.7385f, 20.7165f, 20.6944f, 20.6723f, 20.6502f, 20.6281f, 20.6061f, 20.584f, 20.5619f, 20.5398f, 20.5177f, 20.4957f, 20.4736f, 20.4515f, 20.4294f, 20.4073f, 20.3852f, 20.3632f, 20.3411f, 20.319f, 20.2969f, 20.2748f, 20.2528f, 20.2307f, 20.2086f, 20.1865f, 20.1644f, 20.1424f, 20.1203f, 20.0982f, 20.0761f, 20.054f, 20.032f, 20.0099f, 19.9878f, 19.9657f, 19.9436f, 19.9215f, 19.8995f, 19.8774f, 19.8553f, 19.8332f, 19.8111f, 19.7891f, 19.767f, 19.7449f};
    private static final float[] MOVE_Z = new float[]{55.2236f, 56.4908f, 57.6898f, 58.7561f, 59.6324f, 60.2714f, 60.6388f, 60.7148f, 60.4953f, 59.9922f, 59.2325f, 58.2571f, 57.1244f, 55.9724f, 54.8203f, 53.6683f, 52.5162f, 51.3642f, 50.2122f, 49.0601f, 47.9081f, 46.756f, 45.604f, 44.452f, 43.2999f, 42.1027f, 40.8371f, 39.5584f, 38.2797f, 37.001f, 35.7223f, 34.4436f, 33.1649f, 31.8862f, 30.6075f, 29.3288f, 28.0501f, 26.7715f, 25.4928f, 24.2141f, 22.9354f, 21.6567f, 20.378f, 19.0993f, 17.8206f, 16.5419f, 15.2632f, 13.9845f, 12.7058f, 11.4272f, 10.1485f, 8.8698f, 7.5911f, 6.3124f, 5.0337f, 3.755f, 2.4763f, 1.1976f, -0.0811f, -1.3598f, -2.6385f, -3.9171f, -5.1958f, -6.4745f, -7.7532f, -9.0319f, -10.3106f, -11.5893f, -12.868f, -14.1467f, -15.4254f, -16.7041f, -17.9828f, -19.2615f, -20.5401f, -21.8188f, -23.0975f, -24.3762f, -25.6549f, -26.9336f, -28.2123f, -29.491f, -30.7697f, -32.0484f, -33.3271f, -34.6058f, -35.8844f, -37.1631f, -38.4418f, -39.7152f, -40.9413f, -42.0455f, -43.1136f, -44.1817f, -45.2499f, -46.318f, -47.3861f, -48.4543f, -49.5224f, -50.5905f, -51.6587f, -52.7268f, -53.7949f, -54.8631f, -55.9312f, -57.0042f, -57.8924f, -58.5203f, -58.8469f, -58.8512f, -58.5327f, -57.9122f, -57.0301f, -55.9437f, -54.7237f, -53.449f, -52.1705f, -50.892f, -49.6135f, -48.335f, -47.0565f, -45.778f, -44.4995f, -43.221f, -41.9425f, -40.664f, -39.3855f, -38.107f, -36.8285f, -35.55f, -34.2715f, -32.993f, -31.7145f, -30.436f, -29.1575f, -27.879f, -26.6005f, -25.322f, -24.0435f, -22.765f, -21.4865f, -20.208f, -18.9295f, -17.651f, -16.3725f, -15.094f, -13.8155f, -12.537f, -11.2585f, -9.98f, -8.7015f, -7.423f, -6.1445f, -4.866f, -3.5875f, -2.309f, -1.0305f, 0.248f, 1.5265f, 2.805f, 4.0836f, 5.3621f, 6.6406f, 7.9191f, 9.1976f, 10.4761f, 11.7546f, 13.0331f, 14.3116f, 15.5901f, 16.8686f, 18.1471f, 19.4256f, 20.7041f, 21.9826f, 23.2611f, 24.5396f, 25.8181f, 27.0966f, 28.3751f, 29.6536f, 30.9321f, 32.2106f, 33.4891f, 34.7676f, 36.0461f, 37.3246f, 38.6031f, 39.8816f, 41.1601f, 42.4386f, 43.7171f, 44.9956f, 46.2741f, 47.5526f, 48.8311f, 50.1096f, 51.3881f, 52.6666f, 53.9451f, 55.2236f};
    private static final float[] ROT_X = new float[]{0.0f, -13.3207f, -26.6413f, -39.962f, -53.2827f, -66.6033f, -79.924f, -93.2446f, -106.5653f, -119.886f, -133.2066f, -146.5273f, -154.2834f, -154.2834f, -154.2834f, -154.2834f, -154.2834f, -154.2834f, -154.2834f, -154.2834f, -154.2834f, -154.2834f, -154.2834f, -154.2834f, -154.2834f, -166.4853f, -178.6959f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -190.0069f, -202.2175f, -213.3495f, -213.3495f, -213.3495f, -213.3495f, -213.3495f, -213.3495f, -213.3495f, -213.3495f, -213.3495f, -213.3495f, -213.3495f, -213.3495f, -213.3495f, -213.3495f, -218.5257f, -233.1785f, -247.8312f, -262.4839f, -277.1367f, -291.7894f, -306.4421f, -321.0949f, -335.7476f, -350.4003f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f, -360.9895f};
    private static final float[] TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.lerp((float)f, (float)keys[lo], (float)keys[hi]);
    }
    public float getBoneMoveY(float t) {
        return Ztz96aRenderer.sample(TIMES, MOVE_Y, t);
    }
    public float getBoneMoveZ(float t) {
        return Ztz96aRenderer.sample(TIMES, MOVE_Z, t);
    }
    public float getBoneRotX(float t) {
        return Ztz96aRenderer.sample(ROT_TIMES, ROT_X, t);
    }
    public float getTrackDistance() {
        return 1.7857143f;
    }
        public int period(){return 100;}
    }
}
