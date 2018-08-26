package livetrain;

import livetrain.graphics.SimulationRenderer;
import livetrain.physics.Simulant;
import livetrain.robot.Robot;

import java.util.ArrayList;

public class Simulation {
    public static double pixelsPerUnit = 2;
    private static Simulation instance = null;
    private SimulationRenderer renderer = SimulationRenderer.instance();
    private ArrayList<Simulant> objects = new ArrayList<>();
    private Robot robot = new Robot(-1, -1);
    private volatile boolean run = false, advanceSim = false;
    private volatile double advanceSimTime = 0;
    
    private Simulation() { addObject(robot); }
    
    public static Simulation instance() {
        if (instance == null)
            instance = new Simulation();
        
        return instance;
    }
    
    public SimulationRenderer renderer() { return renderer; }
    
    public ArrayList<Simulant> objects() { return objects; }
    
    public static Robot robot() { return instance().robot; }

    public boolean run() { return run; }
    
    public void setRun(boolean run) { this.run = run; }
    
    public void addObject(Simulant object) { objects.add(object); }
    
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

    public void advanceSim(double time) {
        advanceSim = true;
        advanceSimTime = time;
    }
}
