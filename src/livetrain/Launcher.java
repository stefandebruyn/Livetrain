package livetrain;

import livetrain.Log;
import livetrain.graphics.SimulationRenderer;
import livetrain.ui.SimulationUI;

import java.awt.EventQueue;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Boots the program by creating all threads
 */
public class Launcher {
    public static final String WINDOW_NAME = "Livetrain";
    public static final ImageIcon PROGRAM_ICON = new ImageIcon(Launcher.class.getResource("/image/icon.png"));
    public static final Simulation sim = Simulation.instance();
    
    private Launcher() {}
    
    /**
     * Entry point
     * 
     * @param args 
     */
    public static void main(String[] args) {
        // Set UI theme
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SimulationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SimulationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SimulationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SimulationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        // Graphics and interface get placed in the same thread
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                // Interface
                SimulationUI ui = SimulationUI.instance();
                ui.setVisible(true);
                ui.attachSimulation(sim);
                ui.setIconImage(Launcher.PROGRAM_ICON.getImage());
                
                // Renderer
                JFrame frame = new JFrame(WINDOW_NAME);
                frame.setContentPane(SimulationRenderer.instance());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setIconImage(Launcher.PROGRAM_ICON.getImage());
                
                // Establish default configuration
                SimulationUI.instance().registerDefaults();
            }
        });
        
        // Simulation loop
        while (!Thread.currentThread().isInterrupted())
            sim.update();
   }
}
