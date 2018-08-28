package livetrain;

import livetrain.physics.Simulant;
import livetrain.robot.Robot;

import java.util.ArrayList;

/**
 * Contains the looped simulation cycle and relevant robot data
 */
public class Simulation {
    public static double pixelsPerUnit = 2;
    
    private volatile static Simulation instance = null;
    private volatile Robot robot = new Robot(-1, -1);
    private ArrayList<Simulant> objects = new ArrayList<>();
    private volatile boolean run = false, advanceSim = false;
    private double advanceSimTime = 0;
    
    /**
     * Upon construction, the simulation creates an unconfigured robot and adds it to the
     * simulation queue
     */
    private Simulation() {
        addObject(robot);
    }
    
    /**
     * @return Singleton
     */
    public static Simulation instance() {
        if (instance == null)
            instance = new Simulation();
        
        return instance;
    }
    
    /**
     * @return List of simulated objects
     */
    public ArrayList<Simulant> objects() { return objects; }
    
    /**
     * @return Main robot reference
     */
    public static Robot robot() { return instance().robot; }

    /**
     * @return If the simulation is automatically running
     */
    public boolean run() { return run; }
    
    /**
     * @param run If the simulation should automatically run
     */
    public void setRun(boolean run) {
        this.run = run;
        Log.add("Simulation.run set to " + run);
    }
    
    /**
     * Add an object to be simulated
     * 
     * @param object Object
     */
    public void addObject(Simulant object) { objects.add(object); }
    
    /**
     * Run a single update cycle
     */
    public void update() {
        // Incremental advancements
        if (advanceSim) {
            double resolution = 0.01;
            
            for (double t = 0; t < advanceSimTime; t += resolution)
                for (Simulant obj : objects) {
                    Clock.timeBank += resolution;
                    obj.update(Clock.simulationTime());
                }
            
            advanceSim = false;
        }
        
        // Simulation proper is not running
        if (!run)
            return;
        
        // Cycle
        for (Simulant obj : objects)
            obj.update(Clock.simulationTime());
    }

    /**
     * Set the simulation to advance by some amount of time on the next update cycle
     * 
     * @param time Advancement
     */
    public void advanceSim(double time) {
        advanceSim = true;
        advanceSimTime = time;
    }
}
