package livetrain.ui;

import elusive.math.ElusiveMath;

public class NumericEntryParser {
    private double lower, upper, lastValid;
    private boolean bind = false;
    
    public NumericEntryParser(double fallback) {
        lastValid = fallback;
    }
    
    public NumericEntryParser(double lower, double upper, double fallback) {
        this.lower = lower;
        this.upper = upper;
        lastValid = fallback;
        bind = true;
    }
    
    public double parse(String str) {
        try {
            lastValid = Double.parseDouble(str);
        } catch (Exception e) {}
        
        return bind ? ElusiveMath.clamp(lastValid, lower, upper) : lastValid;
    }
}
