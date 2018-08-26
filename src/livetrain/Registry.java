package livetrain;

import livetrain.ui.NumericEntryParser;

import javax.swing.JTextField;

public class Registry {
    public enum Entry {
        ROBOT_INIT_X, ROBOT_INIT_Y, ROBOT_INIT_THETA, ROBOT_WIDTH, ROBOT_HEIGHT,
        ROBOT_POW0, ROBOT_POW1, ROBOT_POW2, ROBOT_POW3, ROBOT_WHEEL_RADIUS,
        ROBOT_UPDATE_FREQUENCY, PIXELS_PER_UNIT
    }
    
    private Registry() {}
    
    public static void edit(Entry ent, JTextField src) {
        NumericEntryParser p;
        double n = Double.NaN;
        
        switch (ent) {
            case ROBOT_INIT_X:
                p = new NumericEntryParser(Simulation.robot().xState.x);
                n = p.parse(src.getText());
                Simulation.robot().xState.x = n;
                break;
                
            case ROBOT_INIT_Y:
                p = new NumericEntryParser(Simulation.robot().yState.x);
                n = p.parse(src.getText());
                Simulation.robot().yState.x = n;
                break;
                
            case ROBOT_INIT_THETA:
                p = new NumericEntryParser(Simulation.robot().thetaState.x);
                n = p.parse(src.getText());
                Simulation.robot().thetaState.x = Math.toRadians(n);
                break;
                
            case ROBOT_WIDTH:
                p = new NumericEntryParser(Simulation.robot().width());
                n = p.parse(src.getText());
                Simulation.robot().setWidth((int)n);
                break;
                
            case ROBOT_HEIGHT:
                p = new NumericEntryParser(Simulation.robot().height());
                n = p.parse(src.getText());
                Simulation.robot().setHeight((int)n);
                break;
                
            case ROBOT_POW0:
                p = new NumericEntryParser(-1, 1, Simulation.robot().drivetrain().power(0));
                n = p.parse(src.getText());
                Simulation.robot().drivetrain().setPower(0, n);
                break;
                
            case ROBOT_POW1:
                p = new NumericEntryParser(-1, 1, Simulation.robot().drivetrain().power(1));
                n = p.parse(src.getText());
                Simulation.robot().drivetrain().setPower(1, n);
                break;
                
            case ROBOT_POW2:
                p = new NumericEntryParser(-1, 1, Simulation.robot().drivetrain().power(2));
                n = p.parse(src.getText());
                Simulation.robot().drivetrain().setPower(2, n);
                break;
                
            case ROBOT_POW3:
                p = new NumericEntryParser(-1, 1, Simulation.robot().drivetrain().power(3));
                n = p.parse(src.getText());
                Simulation.robot().drivetrain().setPower(3, n);
                break;
                
            case ROBOT_WHEEL_RADIUS:
                p = new NumericEntryParser(0.01, Double.POSITIVE_INFINITY, Simulation.robot().drivetrain().wheelRadius());
                n = p.parse(src.getText());
                Simulation.robot().drivetrain().setWheelRadius(n);
                break;
                
            case ROBOT_UPDATE_FREQUENCY:
                p = new NumericEntryParser(1, Double.POSITIVE_INFINITY, Simulation.robot().updateFrequency());
                n = p.parse(src.getText());
                Simulation.robot().setUpdateFrequency(n);
                break;
                
            case PIXELS_PER_UNIT:
                p = new NumericEntryParser(1, Double.POSITIVE_INFINITY, Simulation.pixelsPerUnit);
                n = p.parse(src.getText());
                Simulation.pixelsPerUnit = (int)n;
                break;
        }
        
        src.setText("" + n);
    }
}
