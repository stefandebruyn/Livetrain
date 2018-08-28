package livetrain;

/**
 * Generates timestamps for the simulator
 */
public class Clock {
    public static final double EPOCH = System.currentTimeMillis() / 1000.0;
    public static double simulationSpeed = 1;
    public static double timeBank = 0, simEpoch = 0;
    
    private Clock() {}

    /**
     * @return Current time in seconds since program epoch
     */
    public static double timestamp() { return System.currentTimeMillis() / 1000.0 - EPOCH; }
 
    /**
     * @return Current simulation time in seconds
     */
    public static double simulationTime() { return timeBank + (Simulation.instance().run() ? (timestamp() - simEpoch) : 0); }
}
