package livetrain;

import livetrain.graphics.SimulationRenderer;
import livetrain.ui.SimulationUI;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Launcher {
    public static final String WINDOW_NAME = "Livetrain";
    public static final ImageIcon PROGRAM_ICON = new ImageIcon(Launcher.class.getResource("/image/icon.png"));
    
    private Launcher() {}
    
    public static void main(String[] args) {
        // Create the simulation
        Simulation sim = Simulation.instance();
        
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
        
        // Create the UI
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SimulationUI.instance().setVisible(true);
            }
        });
        
        // Create the simulation window
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(WINDOW_NAME);
                frame.setContentPane(SimulationRenderer.instance());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                // Set simulation window icon
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
