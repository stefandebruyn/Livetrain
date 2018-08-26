package livetrain.robot;

import elusive.geometry.Pose2D;
import elusive.math.ElusiveMath;

import livetrain.Log;

public class Drivetrain {
    public enum Type { MECANUM, TANK };
    private Type type;
    private Robot robot;
    private double[] powers = new double[4]; // TODO: Add support for variable number of wheels?
    private double wheelRadius, wheelSeparationWidth, wheelSeparationLength;
    private double maxVelocity;

    public Drivetrain(Type type, Robot robot, double maxVel) {
        this.type = type;
        this.robot = robot;
        maxVelocity = maxVel;
        wheelSeparationWidth = robot.width() / 2;
        wheelSeparationLength = robot.height() / 2;
        wheelRadius = 2;
    }
    
    public double power(int index) { return powers[index]; }
    
    public void setPower(int index, double pow) { powers[index] = pow; }
    
    public double[] powers() { return powers; }
    
    public void setPowers(double a, double b, double c, double d) {
        powers[0] = ElusiveMath.clamp(a, -1, 1);
        powers[1] = ElusiveMath.clamp(b, -1, 1);
        powers[2] = ElusiveMath.clamp(c, -1, 1);
        powers[3] = ElusiveMath.clamp(d, -1, 1);
    }

    public void updatePowers(double a, double b, double c, double d) {
        powers[0] = ElusiveMath.clamp(powers[0] + a, -1, 1);
        powers[1] = ElusiveMath.clamp(powers[1] + b, -1, 1);
        powers[2] = ElusiveMath.clamp(powers[2] + c, -1, 1);
        powers[3] = ElusiveMath.clamp(powers[3] + d, -1, 1);
    }

    public Type type() { return type; }

    public void setType(Type type) { this.type = type; }
    
    public double wheelRadius() { return wheelRadius; }

    public void setWheelRadius(double radius) {
        wheelRadius = radius;
        Log.add("Set Drivetrain.wheelRadius", "" + radius);
    }
 
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
    
        Pose2D p = new Pose2D(xVel * maxVelocity, yVel * maxVelocity, thetaVel);
        return p;
    }
}
