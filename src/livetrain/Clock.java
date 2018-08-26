package livetrain;

public class Clock {
    public static final double EPOCH = System.currentTimeMillis() / 1000.0;
    public static double simulationSpeed = 1;
    public static double timeBank = 0, simEpoch = 0;
    
    private Clock() {}

    public static double timestamp() { return System.currentTimeMillis() / 1000.0 - EPOCH; }
 
    public static double simulationTime() { return timeBank + (Simulation.instance().run() ? (timestamp() - simEpoch) : 0); }
}
