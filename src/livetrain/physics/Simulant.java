package livetrain.physics;

import elusive.geometry.Pose2D;
import elusive.geometry.Vector2D;
import elusive.profiling.motion.MotionState1D;

import livetrain.Clock;

import java.awt.Graphics2D;

public abstract class Simulant {
    public MotionState1D xState, yState, thetaState;
    protected volatile double lastUpdateTimestamp = -1;
    
    public Simulant() {
        xState = new MotionState1D(0, 0, 0, 0, 0);
        yState = new MotionState1D(0, 0, 0, 0, 0);
        thetaState = new MotionState1D(0, 0, 0, 0, 0);
    }

    public Simulant(double x, double y, double theta) {
        xState = new MotionState1D(x, 0, 0, 0, 0);
        yState = new MotionState1D(y, 0, 0, 0, 0);
        thetaState = new MotionState1D(theta, 0, 0, 0, 0);
    }

    public double x() { return xState.x; }

    public double y() { return yState.x; }

    public double theta() { return thetaState.x; }

    public MotionState1D xState() { return xState; }

    public MotionState1D yState() { return yState; }

    public MotionState1D thetaState() { return thetaState; }

    public Pose2D pose() { return new Pose2D(xState.x, yState.x, thetaState.x); }

    public void resetTimestamp() { lastUpdateTimestamp = -1; }

    public void setPose(double x, double y, double theta) {
        xState.x = x;
        yState.x = y;
        thetaState.x = theta;
    }

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

    public abstract Graphics2D draw(Graphics2D g);
}
