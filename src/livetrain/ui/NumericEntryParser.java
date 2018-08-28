package livetrain.ui;

import elusive.math.ElusiveMath;

/**
 * Used to check input for interface fields and sync the result back with the field
 */
public class NumericEntryParser {
    private double lower, upper, lastValid;
    private boolean bind = false;
    
    /**
     * @param fallback Value to fallback on if the parse is unsatisfactory
     */
    public NumericEntryParser(double fallback) {
        lastValid = fallback;
    }
    
    /**
     * @param lower Lower bound
     * @param upper Upper bound
     * @param fallback Value to fallback on
     */
    public NumericEntryParser(double lower, double upper, double fallback) {
        this.lower = lower;
        this.upper = upper;
        lastValid = fallback;
        bind = true;
    }
    
    /**
     * Attempt to parse a string for a real
     * 
     * @param str String
     * @return Parsed value, whether it be valid or a fallback
     */
    public double parse(String str) {
        try {
            lastValid = Double.parseDouble(str);
        } catch (Exception e) {}
        
        return bind ? ElusiveMath.clamp(lastValid, lower, upper) : lastValid;
    }
}
