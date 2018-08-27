package livetrain.graphics;

import elusive.geometry.Parametric;
import elusive.geometry.Pose2D;
import elusive.trajectory.Trajectory;

import livetrain.Clock;
import livetrain.Simulation;
import livetrain.physics.Simulant;
import livetrain.robot.Drivetrain;
import livetrain.robot.Robot;
import livetrain.ui.SimulationUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JPanel;
import livetrain.Launcher;

public class SimulationRenderer extends JPanel {
    private static SimulationRenderer instance = null;
    public static final int CANVAS_WIDTH = 640;
    public static final int CANVAS_HEIGHT = 480;
    public static final Color BACKGROUND_COLOR = new Color(8, 8, 8);
    public static final Color GRID_COLOR = new Color(15, 15, 15);
    public static final Color AXES_COLOR = new Color(100, 100, 100);
    public static final Color PATH_COLOR = new Color(73, 75, 209);
    public static final double MAX_ARC_LENGTH = 10000;
    private int axisTickSize = 5;
    private int telemetryLineHeight = 13;
    
    private boolean wrnSegmentRenderingProblem = false;
    
    private SimulationRenderer() {
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
    }

    @Override public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

         // Paint the background
         super.paintComponent(g);
         setBackground(BACKGROUND_COLOR);

         // Draw vertical gridlines and horizontal units
         for (int x = 0; x < getWidth(); x += Simulation.pixelsPerUnit * 12) {
             g2d.setColor(x == 0 ? AXES_COLOR : GRID_COLOR);
             g2d.drawLine(x, 0, x, getHeight());

             int feet = (int)(x / Simulation.pixelsPerUnit) / 12;

             if (feet % 2 == 0) {
                 g2d.setColor(AXES_COLOR);
                 g2d.drawString("" + (int)(x / Simulation.pixelsPerUnit), x + 2, getHeight() - 2);
                 g2d.drawLine(x, getHeight() - 1, x, getHeight() - axisTickSize - 1);
             }
         }

         // Draw horizontal gridlines and vertical units
         for (int y = getHeight(); y >= 0; y -= Simulation.pixelsPerUnit * 12) {
             g2d.setColor(y == getHeight() ? AXES_COLOR : GRID_COLOR);
             g2d.drawLine(1, y - 1, getWidth(), y - 1);

             int feet = (int)(y / Simulation.pixelsPerUnit) / 12;

             if (feet % 2 == 0 && (getHeight() - y) != 0) {
                 g2d.setColor(AXES_COLOR);
                 g2d.drawString("" + (int)((getHeight() - y) / Simulation.pixelsPerUnit), 2, y - 3);
                 g2d.drawLine(0, y - 1, axisTickSize, y - 1);
             }
         }

         // Draw path
         drawTrajectory(g2d, SimulationUI.instance().trajectory());
         
         // Draw waypoints
         drawWaypoints(g2d, SimulationUI.instance().waypoints());

         // Draw simulation objects
         for (Simulant obj : Simulation.instance().objects())
             g2d = obj.draw(g2d);
         
         // Draw telemetry
         Robot robot = Simulation.robot();
         Drivetrain dt = robot.drivetrain();
         int ybuffer = 0;
         
         // Warning telemetry
         ArrayList<String> warnings = new ArrayList<>();
         
         if (wrnSegmentRenderingProblem)
             warnings.add("Warning: One or more trajectory segments failed to render");
         
         String[] telemetry = warnings.toArray(new String[0]);
         
         g2d.setColor(Color.RED);
         drawTelemetry(g2d, telemetry, ybuffer);
         ybuffer += telemetryLineHeight * (telemetry.length + 1) * (telemetry.length > 0 ? 1 : 0);
         
         // Time telemetry
         telemetry = new String[] {
             String.format(Locale.getDefault(), "Simulation time: %.4fs", Clock.simulationTime())
         };
         
         g2d.setColor(AXES_COLOR);
         drawTelemetry(g2d, telemetry, ybuffer);
         ybuffer += telemetryLineHeight * (telemetry.length + 1);
         
         // Robot telemetry
         telemetry = new String[] {
             String.format(Locale.getDefault(), "Robot pose: <%.2f, %.2f, %.2f°>", robot.xState.x, robot.yState.x, Math.toDegrees(robot.thetaState.x)),
             String.format(Locale.getDefault(), "Robot velocity: <%.2f, %.2f, %.2f °/s>", robot.xState.v, robot.yState.v, Math.toDegrees(robot.thetaState.v)),
             String.format(Locale.getDefault(), "Drivetrain powers: {%.2f, %.2f, %.2f, %.2f}", dt.power(0), dt.power(1), dt.power(2), dt.power(3)),
         };
    
         g2d.setColor(Robot.color);
         drawTelemetry(g2d, telemetry, ybuffer);
         ybuffer += telemetryLineHeight * (telemetry.length + 1);
         
         // Trajectory telemetry
         telemetry = new String[] {
             "Trajectory pose: " + robot.follower().pathPose(),
             "Trajectory velocity: " + robot.follower().pathVelocity(),
             "Trajectory acceleration: " + robot.follower().pathAcceleration()
         };
         
         g2d.setColor(PATH_COLOR);
         drawTelemetry(g2d, telemetry, ybuffer);

         repaint();
    }

    private void drawWaypoints(Graphics2D g2d, ArrayList<Pose2D> waypoints) {
        g2d.setColor(PATH_COLOR);
        
        int diam = 5;
        int offset = (int)(diam / 2);
        int panHeight = Simulation.instance().renderer().getHeight();
        
        for (Pose2D pose : waypoints)
            g2d.fillOval((int)(pose.x() * Simulation.pixelsPerUnit) - offset, panHeight - (int)(pose.y() * Simulation.pixelsPerUnit) - offset, diam, diam);
    }

    private void drawTrajectory(Graphics2D g2d, Trajectory traj) {
        if (traj == null)
            return;
        
         g2d.setColor(PATH_COLOR);
         
         int panHeight = Simulation.instance().renderer().getHeight();
         int failedRenders = 0;

         for (Parametric p : traj.segments()) {
             double arcLength = p.arcLength();
             double resolution = 2;
             int lastx = -1, lasty = -1;
             
             // Check for problematic segment
             if (arcLength > MAX_ARC_LENGTH || Double.isNaN(arcLength) || arcLength < 0) {
                 failedRenders++;
                 continue;
             }

             for (int i = 0; i <= arcLength; i += resolution) {
                 Pose2D pose = p.poseAt(i);
                 int x = (int)(pose.x() * Simulation.pixelsPerUnit);
                 int y = panHeight - (int)(pose.y() * Simulation.pixelsPerUnit);

                 if (lastx != -1)
                     g2d.drawLine(lastx, lasty, x, y);

                 lastx = x;
                 lasty = y;
             }
             
             Pose2D end = p.poseAt(arcLength);
             g2d.drawLine(lastx, lasty, (int)(end.x() * Simulation.pixelsPerUnit), panHeight - (int)(end.y() * Simulation.pixelsPerUnit));
         }
         
         wrnSegmentRenderingProblem = (failedRenders > 0);
    }

    private void drawTelemetry(Graphics2D g2d, String[] telemetry, int starty) {
        for (int i = 0; i < telemetry.length; i++) {
            String str = telemetry[i];
            FontMetrics fm = getFontMetrics(getFont());
            int width = fm.stringWidth(str);
            
            g2d.drawString(str, getWidth() - width - 2, starty + telemetryLineHeight * (i + 1) + 2);
        }
    }

    public static SimulationRenderer instance() {
        if (instance == null)
            instance = new SimulationRenderer();
        
        return instance;
    }
}