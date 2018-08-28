package livetrain;

import elusive.geometry.Pose2D;
import elusive.profiling.motion.MotionState1D;

public class Util {
    
    public static Pose2D poseDifference(Pose2D a, Pose2D b) {
        if (a == null || b == null)
            return null;
        
        return new Pose2D(a.x() - b.x(), a.y() - b.y(), a.heading() - b.heading());
    }
    
    public static Pose2D poseSum(Pose2D a, Pose2D b) {
        if (a == null || b == null)
            return null;
        
        return new Pose2D(a.x() + b.x(), a.y() + b.y(), a.heading() + b.heading());
    }
    
    public static Pose2D stateToPose(MotionState1D x, MotionState1D y, MotionState1D theta) {
        return new Pose2D(x.x, y.x, theta.x);
    }
}
