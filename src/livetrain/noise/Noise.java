package livetrain.noise;

public class Noise {
    public enum Type { SINUSOIDAL, RANDOM };
    private Type type;
    private double lower, upper;
    
    public Noise(Type t, double l, double u) {
        type = t;
        lower = l;
        upper = u;
    }
    
    public double generate(double timestamp) {
        switch (type) {
            case SINUSOIDAL:
                return Math.sin(timestamp) * (upper - lower) + (upper - lower) / 4;
                
            case RANDOM:
                return lower + Math.random() * (upper - lower);
        }
        
        return 0;
    }
    
    @Override public String toString() { return type + "[" + lower + ", " + upper + "]"; }
}
