package livetrain.robot;

import elusive.geometry.Pose2D;
import elusive.geometry.Vector2D;
import elusive.profiling.motion.MotionConstraints;

import livetrain.Log;
import livetrain.Simulation;
import livetrain.graphics.SimulationRenderer;
import livetrain.physics.Simulant;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

/**
 * A combined drivetrain, follower, and trajectory
 */
public class Robot extends Simulant {
    private Color color = new Color(204, 71, 71);
    private Drivetrain drivetrain;
    private TrajectoryFollower follower;
    private MotionConstraints constraints;
    private double width, height, updateFrequency = 100;
    private boolean isFollowingTrajectory = true;

    /**
     * @param width Drivetrain width
     * @param height Drivetrain height
     */
    public Robot(int width, int height) {
        this.width = width;
        this.height = height;
        drivetrain = new Drivetrain(Drivetrain.Type.MECANUM, this, 50);
        follower = new TrajectoryFollower();
        constraints = new MotionConstraints(0, 0, 0);
    }
    
    /**
     * @return Color for rendering
     */
    public Color color() { return color; }

    /**
     * @return Drivetrain width
     */
    public double width() { return width; }
    
    /**
     * Set the drivetrain width
     * 
     * @param w Width
     */
    public void setWidth(double w) { width = w; }

    /**
     * @return Drivetrain height
     */
    public double height() { return height; }
    
    /**
     * @param h Set the drivetrain height
     */
    public void setHeight(double h) { height = h; }
    
    /**
     * @return The follower guiding this robot's trajectory
     */
    public TrajectoryFollower follower() { return follower; }

    /**
     * @return If this robot is being guided by its follower
     */
    public boolean isFollowingTrajectory() { return isFollowingTrajectory; }

    /**
     * Set if the robot is being guided
     * 
     * @param f Following?
     */
    public void setIsFollowingTrajectory(boolean f) {
        isFollowingTrajectory = f;
        Log.add("Set Robot.isFollowingTrajectory", "" + isFollowingTrajectory);
    }
    
    /**
     * @return Drivetrain
     */
    public Drivetrain drivetrain() { return drivetrain; }
    
    /**
     * @return The frequency at which the drivetrain is allowed to update motion
     */
    public double updateFrequency() { return updateFrequency; }

    /**
     * Set the drivetrain update frequency
     * 
     * @param f Frequency (Hz)
     */
    public void setUpdateFrequency(double f) {
        updateFrequency = f;
        Log.add("Set Robot.updateFrequency", "" + f);
    }
    
    /**
     * @return The robot's kinematic constraints
     */
    public MotionConstraints motionConstraints() { return constraints; }

    /**
     * Set the robot's kinematic constraints
     * 
     * @param v Max velocity
     * @param a Max acceleration
     * @param j Max jerk
     */
    public void setMotionConstraints(double v, double a, double j) {
        constraints = new MotionConstraints(v, a, j);
        Log.add("Set Robot.constraints", "v=" + v, "a=" + a, "j=" + j);
    }
    
    /**
     * Remove all kinematical vectors applied to the robot
     */
    public void zeroVectors() {
        xState.v = 0;
        yState.v = 0;
        thetaState.v = 0;
        xState.a = 0;
        yState.a = 0;
        thetaState.a = 0;
        xState.j = 0;
        yState.j = 0;
        thetaState.j = 0;
    }

    /**
     * Run a single update cycle. Follower is prompted for an update, the update is passed into
     * the drivetrain, and the drivetrain resolves the kinematics
     * 
     * @param timestamp Simulation time
     */
    @Override public void update(double timestamp) {
        // Follow the trajectory
        if (isFollowingTrajectory && (lastUpdateTimestamp == -1 || timestamp - lastUpdateTimestamp >= 1 / updateFrequency)) {
            Pose2D currentPose = new Pose2D(xState.x, yState.x, thetaState.x);
            double[] powers = follower.update(currentPose, timestamp);
            drivetrain.setPowers(powers[0], powers[1], powers[2], powers[3]);
            Log.append("Drivetrain powers", Arrays.toString(powers));
        }
        
        // Update the state
        Pose2D dtPose = drivetrain.state();
        Vector2D linVel = Vector2D.build(dtPose.x(), dtPose.y());
        linVel = linVel.rotated(thetaState.x);
        
        xState.v = linVel.x();
        yState.v = linVel.y();
        thetaState.v = -dtPose.heading();
        
        super.update(timestamp);
    }

    /**
     * Draw the robot to a graphics surface
     * 
     * @param g Surface
     * @return Original surface
     */
    public Graphics2D draw(Graphics2D g) {
        Graphics2D surface = (Graphics2D)g.create();
        Pose2D pose = pose();
        
        int wpx = (int)(width * Simulation.pixelsPerUnit);
        int hpx = (int)(height * Simulation.pixelsPerUnit);
        
        int xpx = (int)(pose.x() * Simulation.pixelsPerUnit) - (int)(wpx / 2);
        int ypx = SimulationRenderer.instance().getHeight() - (int)(pose.y() * Simulation.pixelsPerUnit) - (int)(hpx / 2);

        surface.setColor(color);
        surface.rotate(-pose.heading(), xpx + wpx / 2, ypx + hpx / 2);
        surface.drawRect(xpx, ypx, (int)wpx, (int)hpx);
        surface.setColor(new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()));
        surface.drawLine(xpx + (int)(wpx / 2), ypx + (int)(hpx / 2), xpx + (int)(wpx * 1.66), ypx + (int)(hpx / 2));
        surface.dispose();
        
        return g;
    }
}
