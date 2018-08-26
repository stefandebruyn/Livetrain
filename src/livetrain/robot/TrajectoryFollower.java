package livetrain.robot;

import elusive.control.PIDFController;
import elusive.geometry.Pose2D;
import elusive.geometry.Vector2D;
import elusive.trajectory.Trajectory;

import livetrain.Log;

import java.util.Arrays;

public class TrajectoryFollower {
    private PIDFController headingController, lateralController, axialController;
    private Trajectory trajectory;
    private Pose2D pathPose, pathVelocity, pathAcceleration;
    
    public Pose2D pathPose() { return pathPose; }
    
    public Pose2D pathVelocity() { return pathVelocity; }
    
    public Pose2D pathAcceleration() { return pathAcceleration; }

    public void setCoefficients(double[] heading, double[] lateral, double[] axial) {
        if (heading.length != 6 || lateral.length != 6 || axial.length != 6)
            throw new IllegalArgumentException("Coefficient sets must be 6 in length");
        
        headingController = new PIDFController(heading[0], heading[1], heading[2],
                heading[3], heading[4], heading[5]);
        lateralController = new PIDFController(lateral[0], lateral[1], lateral[2],
                lateral[3], lateral[4], lateral[5]);
        axialController = new PIDFController(axial[0], axial[1], axial[2],
                axial[3], axial[4], axial[5]);
        
        Log.add("Set TrajectoryFollower controller coefficients", Arrays.toString(heading), Arrays.toString(lateral), Arrays.toString(axial));
    }

    public void setTrajectory(Trajectory t) {
        trajectory = t;
        Log.add("Set TrajectoryFollower.trajectory", trajectory.toString(), "Motion profile", trajectory.profile().toString());
    }
    
    public double[] update(Pose2D estimatedPose, double t) {
        Log.add("Trajectory follower update @ t=" + t);
        
        pathPose = trajectory.poseAtTime(t);
        pathVelocity = trajectory.velocityAtTime(t);
        pathAcceleration = trajectory.accelerationAtTime(t);
        
        Log.append("Trajectory poses", "p=" + pathPose.toString(), "v=" + pathVelocity.toString(), "a=" + pathAcceleration.toString());
        
        double headingError = estimatedPose.heading() - pathPose.heading();
        double headingUpdate = headingController.update(headingError, t);
        
        Log.append("Heading update", "err=" + headingError, "upd=" + headingUpdate);
        
        Vector2D poseError = estimatedPose.pose().added(pathPose.pose().negated());
        poseError = poseError.rotated(-estimatedPose.heading());
        
        double axialError = poseError.x();
        double lateralError = poseError.y();
        
        Log.append("Spatial update", "err=" + poseError.toString());
        
        Vector2D robotVelocity = new Vector2D(pathVelocity.x(), pathVelocity.y());
        robotVelocity = robotVelocity.rotated(-estimatedPose.heading());
        
        Vector2D robotAcceleration = new Vector2D(pathAcceleration.x(), pathAcceleration.y());
        robotAcceleration = robotAcceleration.rotated(-estimatedPose.heading());
        
        double axialUpdate = axialController.update(axialError, t, robotVelocity.x(), robotAcceleration.x());
        double lateralUpdate = lateralController.update(lateralError, t, robotVelocity.y(), robotAcceleration.y());
        
        Log.append("upd_axial=" + axialUpdate, "upd_lateral=" + lateralUpdate);
        
        Pose2D correction = new Pose2D(axialUpdate, lateralUpdate, headingUpdate);
        Vector2D targetVelocity = correction.pose();
        double targetOmega = correction.heading();
        
        Log.append("Final update", correction.toString());
        
        double[] powers = new double[] {
            targetVelocity.x() - targetVelocity.y() - targetOmega,
            targetVelocity.x() + targetVelocity.y() - targetOmega,
            targetVelocity.x() - targetVelocity.y() + targetOmega,
            targetVelocity.x() + targetVelocity.y() + targetOmega
        };
        
        return powers;
    }
}
