package livetrain.noise;

import livetrain.Log;

import elusive.geometry.Pose2D;

public class NoiseGenerator {
    public enum Type { ROBOT_POSE_STATIC, ROBOT_POSE_ADD };
    private static Noise robotPoseStatic = new Noise(Noise.Type.RANDOM, 0, 0);
    private static Noise robotPoseAdd = new Noise(Noise.Type.RANDOM, 0, 0);
    private volatile static boolean addNoise = false;
    
    private NoiseGenerator() {}
    
    public static boolean addNoise() { return addNoise; }
    
    public static void setAddNoise(boolean add) {
        addNoise = add;
        Log.add("Set NoiseGenerator.addNoise", "" + add);
    }
    
    public static void setRobotPoseStatic(Noise.Type t, double l, double u) {
        robotPoseStatic = new Noise(t, l, u);
        Log.add("Set robot pose noise", robotPoseStatic.toString());
    }
    
    public static void setRobotPoseAdd(Noise.Type t, double l, double u) {
        robotPoseAdd = new Noise(t, l, u);
        Log.add("Set robot pose additive noise", robotPoseAdd.toString());
    }
    
    public static double generate(Type t, double timestamp) {
        switch (t) {
            case ROBOT_POSE_STATIC:
                return addNoise ? robotPoseStatic.generate(timestamp) : 0;
                
            case ROBOT_POSE_ADD:
                return addNoise ? robotPoseAdd.generate(timestamp) : 0;
        }
        
        return 0;
    }
    
    public static Pose2D generate(Type t, double timestamp, Pose2D pose) {
        return new Pose2D(pose.x() + generate(t, timestamp), pose.y() + generate(t, timestamp),
                pose.heading() + generate(t, timestamp));
    }
}
