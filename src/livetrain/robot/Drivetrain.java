package livetrain.robot;

import elusive.geometry.Pose2D;
import elusive.math.ElusiveMath;

import livetrain.Log;

/**
 * Provides the inverse kinematics that govern the robot's pose velocities. Wheel indices
 * begin at the front left and go counter-clockwise
 */
public class Drivetrain {
    public enum Type { MECANUM, TANK };
    private Type type;
    private Robot robot;
    private double[] powers = new double[4]; // TODO: Add support for variable number of wheels?
    private double wheelRadius, wheelSeparationWidth, wheelSeparationLength;
    private double maxVelocity;

    /**
     * @param type Drivetrain type
     * @param robot Parent robot
     * @param maxVel Maximum velocity
     */
    public Drivetrain(Type type, Robot robot, double maxVel) {
        this.type = type;
        this.robot = robot;
        maxVelocity = maxVel;
        wheelSeparationWidth = robot.width() / 2;
        wheelSeparationLength = robot.height() / 2;
        wheelRadius = 2;
    }
    
    /**
     * @param index Wheel index
     * @return Power of wheel at index
     */
    public double power(int index) { return powers[index]; }
    
    /**
     * @param index Wheel index
     * @param pow Power to provide that wheel on [-1, 1]
     */
    public void setPower(int index, double pow) { powers[index] = ElusiveMath.clamp(pow, -1, 1); }
    
    /**
     * @return Wheel powers [0, 1, 2, 3]
     */
    public double[] powers() { return powers; }
    
    /**
     * @param a Wheel 0 power
     * @param b Wheel 1 power
     * @param c Wheel 2 power
     * @param d Wheel 3 power
     */
    public void setPowers(double a, double b, double c, double d) {
        powers[0] = ElusiveMath.clamp(a, -1, 1);
        powers[1] = ElusiveMath.clamp(b, -1, 1);
        powers[2] = ElusiveMath.clamp(c, -1, 1);
        powers[3] = ElusiveMath.clamp(d, -1, 1);
    }

    /**
     * @param a Wheel 0 power increment
     * @param b Wheel 1 power increment
     * @param c Wheel 2 power increment
     * @param d Wheel 3 power increment
     */
    public void updatePowers(double a, double b, double c, double d) {
        powers[0] = ElusiveMath.clamp(powers[0] + a, -1, 1);
        powers[1] = ElusiveMath.clamp(powers[1] + b, -1, 1);
        powers[2] = ElusiveMath.clamp(powers[2] + c, -1, 1);
        powers[3] = ElusiveMath.clamp(powers[3] + d, -1, 1);
    }

    /**
     * @return Drivetrain type
     */
    public Type type() { return type; }

    /**
     * Set drivetrain type
     * 
     * @param type Drivetrain type
     */
    public void setType(Type type) { this.type = type; }
    
    /**
     * @return Wheel radius
     */
    public double wheelRadius() { return wheelRadius; }

    /**
     * Set the wheel radius
     * 
     * @param radius Wheel radius
     */
    public void setWheelRadius(double radius) {
        wheelRadius = radius;
        Log.add("Set Drivetrain.wheelRadius", "" + radius);
    }
 
    /**
     * Get the velocity pose of the drivetrain according to its type and wheel powers
     * 
     * @return Velocity pose
     */
    public Pose2D state() {
        double xVel = 0, yVel = 0, thetaVel = 0;
        
        switch (type) {
            case MECANUM:
                xVel = (powers[0] + powers[3] + powers[1] + powers[2]) * (wheelRadius / 4);
                yVel = (-powers[0] + powers[3] + powers[1] - powers[2]) * (wheelRadius / 4);
                thetaVel = (-powers[0] + powers[3] - powers[1] + powers[2]) * (wheelRadius / (4 * (wheelSeparationWidth + wheelSeparationLength)));
                break;
                
            case TANK:
                break;
        }
    
        return new Pose2D(xVel * maxVelocity, yVel * maxVelocity, thetaVel);
    }
}
