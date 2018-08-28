package livetrain.physics;

import elusive.geometry.Pose2D;
import elusive.geometry.Vector2D;
import elusive.profiling.motion.MotionState1D;

import livetrain.Clock;

import java.awt.Graphics2D;

/**
 * Parent of all simulated objects. Handles object kinematics. States are openly mutable for
 * instantaneous changes
 */
public abstract class Simulant {
    public MotionState1D xState, yState, thetaState;
    protected volatile double lastUpdateTimestamp = -1;
    
    /**
     * Simulants default to pose <0, 0, 0> with no vectors
     */
    public Simulant() {
        xState = new MotionState1D(0, 0, 0, 0, 0);
        yState = new MotionState1D(0, 0, 0, 0, 0);
        thetaState = new MotionState1D(0, 0, 0, 0, 0);
    }

    /**
     * @param x X position
     * @param y Y position
     * @param theta Heading (radians)
     */
    public Simulant(double x, double y, double theta) {
        xState = new MotionState1D(x, 0, 0, 0, 0);
        yState = new MotionState1D(y, 0, 0, 0, 0);
        thetaState = new MotionState1D(theta, 0, 0, 0, 0);
    }

    /**
     * @return X position
     */
    public double x() { return xState.x; }

    /**
     * @return Y position
     */
    public double y() { return yState.x; }

    /**
     * @return Heading (radians)
     */
    public double theta() { return thetaState.x; }

    /**
     * @return Current pose <x, y, theta>
     */
    public Pose2D pose() { return new Pose2D(xState.x, yState.x, thetaState.x); }

    /**
     * Reset the last update timestamp. The next update cycle will have no effect, but
     * subsequent cycles proceed as normal with the correct dt for physics calculations
     */
    public void resetTimestamp() { lastUpdateTimestamp = -1; }

    /**
     * Forcibly update the robot's pose
     * 
     * @param x X position
     * @param y Y position
     * @param theta Heading (radians)
     */
    public void setPose(double x, double y, double theta) {
        xState.x = x;
        yState.x = y;
        thetaState.x = theta;
    }

    /**
     * Run a single update cycle
     * 
     * @param timestamp Simulation time
     */
    public void update(double timestamp) {
        if (lastUpdateTimestamp != -1) {
            double dt = (timestamp - lastUpdateTimestamp) * Clock.simulationSpeed;

            // Update state
            xState = xState.stateAtTime(dt);
            yState = yState.stateAtTime(dt);
            thetaState = thetaState.stateAtTime(dt);
        }
        
        lastUpdateTimestamp = timestamp;
    }

    /**
     * Draw the object to a graphics surface
     * 
     * @param g Surface
     * @return Orginal surface
     */
    public abstract Graphics2D draw(Graphics2D g);
}
