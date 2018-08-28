package livetrain.ui;

import livetrain.Clock;
import livetrain.Log;
import livetrain.Registry;
import livetrain.Simulation;
import livetrain.noise.NoiseGenerator;
import livetrain.physics.Simulant;

import elusive.geometry.Pose2D;
import elusive.profiling.motion.MotionProfile;
import elusive.trajectory.TrajectoryBuilder;
import elusive.trajectory.Trajectory;

import java.util.ArrayList;
import livetrain.noise.Noise;

/**
 * Simulation manipulation interface
 */
public class SimulationUI extends javax.swing.JFrame {
    private volatile static SimulationUI instance;
    private volatile ArrayList<Pose2D> waypoints;
    private volatile Trajectory trajectory = null;
    private volatile Simulation sim;

    private SimulationUI() {
        initComponents();
        chkAddNoiseStateChanged(null);
    }
    
    /**
     * @return Singleton
     */
    public static SimulationUI instance() {
        if (instance == null)
            instance = new SimulationUI();
        
        return instance;
    }
    
    /**
     * Designate the simulation associated with this interface
     * 
     * @param sim Simulation
     */
    public void attachSimulation(Simulation sim) {
        this.sim = sim;
        Log.add("Attached " + sim.toString() + " to " + toString());
    }
    
    /**
     * Build a trajectory according to the user's configuration and send it to the robot
     */
    private void updateTrajectory() {
        // Update robot constraints
        updateMotionConstraints();

        // Build knots from table data
        waypoints = new ArrayList<>();
        
        for (int i = 0; i < tblKnots.getRowCount(); i++) {
            Double x = (Double)tblKnots.getValueAt(i, 0);
            Double y = (Double)tblKnots.getValueAt(i, 1);
            Double theta = (Double)tblKnots.getValueAt(i, 2);
            
            if (x == null || y == null || theta == null)
                break;
            
            theta = Math.toRadians(theta);
            waypoints.add(new Pose2D(x, y, theta));
        }
        
        if (waypoints.size() < 2) {
            chkFollowPath.setEnabled(false);
            return;
        }
        
        // Determine profile type
        MotionProfile.Type profileType = MotionProfile.Type.TRIANGULAR;
        
        switch ((String)boxProfileType.getSelectedItem()) {
            case "Triangular":
                profileType = MotionProfile.Type.TRIANGULAR;
                break;
                    
            case "Trapezoidal":
                profileType = MotionProfile.Type.TRAPEZOIDAL;
                break;
                    
            case "S-curve":
                profileType = MotionProfile.Type.S_CURVE;
                break;
        }
        
        // Build the trajectory
        switch ((String)boxPathType.getSelectedItem()) {
            case "Hermite cubic":
                trajectory = TrajectoryBuilder.buildHermiteCubic(Simulation.robot().motionConstraints(), profileType, waypoints.toArray(new Pose2D[0]));
                break;
                
            case "Hermite quintic":
                trajectory = TrajectoryBuilder.buildHermiteQuintic(Simulation.robot().motionConstraints(), profileType, waypoints.toArray(new Pose2D[0]));
                break;
        }
        
        chkFollowPath.setEnabled(true);
        sim.robot().follower().setTrajectory(trajectory);
    }
    
    /**
     * Update the robot's motion constraints
     */
    private void updateMotionConstraints() {
        NumericEntryParser vp = new NumericEntryParser(0);
        NumericEntryParser ap = new NumericEntryParser(0);
        NumericEntryParser jp = new NumericEntryParser(0);
        
        double v = vp.parse(txtMaxVelocity.getText());
        double a = ap.parse(txtMaxAcceleration.getText());
        double j = jp.parse(txtMaxJerk.getText());
        
        txtMaxVelocity.setText("" + v);
        txtMaxAcceleration.setText("" + a);
        txtMaxJerk.setText("" + j);
        
        Simulation.robot().setMotionConstraints(v, a, j);
    }
    
    /**
     * Update the noise generator
     */
    private void updateNoise() {
        // Robot pose static noise
        NumericEntryParser lower = new NumericEntryParser(0);
        NumericEntryParser upper = new NumericEntryParser(0);
        
        Noise.Type t = boxBotPoseStatNoiseType.getSelectedItem().equals("Sinusoidal") ?
                Noise.Type.SINUSOIDAL : Noise.Type.RANDOM;
        double l = lower.parse(txtBotPoseStatNoiseLower.getText());
        double u = upper.parse(txtBotPoseStatNoiseUpper.getText());
        
        txtBotPoseStatNoiseLower.setText("" + l);
        txtBotPoseStatNoiseUpper.setText("" + u);
        
        NoiseGenerator.setRobotPoseStatic(t, l, u);
        
        // Robot pose additive noise
        lower = new NumericEntryParser(0);
        upper = new NumericEntryParser(0);
        
        t = boxBotPoseAddNoiseType.getSelectedItem().equals("Sinusoidal") ?
                Noise.Type.SINUSOIDAL : Noise.Type.RANDOM;
        l = lower.parse(txtBotPoseAddNoiseLower.getText());
        u = upper.parse(txtBotPoseAddNoiseUpper.getText());
        
        txtBotPoseAddNoiseLower.setText("" + l);
        txtBotPoseAddNoiseUpper.setText("" + u);
        
        NoiseGenerator.setRobotPoseAdd(t, l, u);
    }
    
    /**
     * @return Last built trajectory
     */
    public Trajectory trajectory() { return trajectory; }
    
    /**
     * @return Last built set of knots
     */
    public ArrayList<Pose2D> waypoints() { return waypoints; }
    
    /**
     * Set the default robot geometry
     */
    public void registerDefaults() {
        // Default robot configuration
        Registry.edit(Registry.Entry.ROBOT_INIT_X, txtRobotInitialX);
        Registry.edit(Registry.Entry.ROBOT_INIT_Y, txtRobotInitialY);
        Registry.edit(Registry.Entry.ROBOT_INIT_THETA, txtRobotInitialTheta);
        Registry.edit(Registry.Entry.ROBOT_WIDTH, txtRobotWidth);
        Registry.edit(Registry.Entry.ROBOT_HEIGHT, txtRobotHeight);
        Registry.edit(Registry.Entry.ROBOT_POW0, txtRobotPower0);
        Registry.edit(Registry.Entry.ROBOT_POW1, txtRobotPower1);
        Registry.edit(Registry.Entry.ROBOT_POW2, txtRobotPower2);
        Registry.edit(Registry.Entry.ROBOT_POW3, txtRobotPower3);
        Registry.edit(Registry.Entry.ROBOT_WHEEL_RADIUS, txtWheelRadius);
        
        // Default control coefficients
        tblCoefficientsPropertyChange(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabParent = new javax.swing.JTabbedPane();
        panSimulationTab = new javax.swing.JPanel();
        panSimulation = new javax.swing.JPanel();
        chkSimulationRun = new javax.swing.JCheckBox();
        btSimulationReset = new javax.swing.JButton();
        sldSimulationSpeed = new javax.swing.JSlider();
        labSimulationSpeed = new javax.swing.JLabel();
        chkFollowPath = new javax.swing.JCheckBox();
        btAdvanceBy = new javax.swing.JButton();
        txtAdvanceBy = new javax.swing.JTextField();
        labAdvanceByUnit = new javax.swing.JLabel();
        txtPixelsPerUnit = new javax.swing.JTextField();
        labPixelsPerUnit = new javax.swing.JLabel();
        panRobot = new javax.swing.JPanel();
        txtRobotInitialX = new javax.swing.JTextField();
        txtRobotInitialY = new javax.swing.JTextField();
        labRobotInitialX = new javax.swing.JLabel();
        labRobotInitialY = new javax.swing.JLabel();
        txtRobotInitialTheta = new javax.swing.JTextField();
        labRobotInitialTheta = new javax.swing.JLabel();
        txtRobotPower0 = new javax.swing.JTextField();
        txtRobotPower1 = new javax.swing.JTextField();
        txtRobotPower2 = new javax.swing.JTextField();
        txtRobotPower3 = new javax.swing.JTextField();
        txtRobotWidth = new javax.swing.JTextField();
        txtRobotHeight = new javax.swing.JTextField();
        labRobotWidth = new javax.swing.JLabel();
        labRobotHeight = new javax.swing.JLabel();
        txtMaxVelocity = new javax.swing.JTextField();
        txtMaxAcceleration = new javax.swing.JTextField();
        txtMaxJerk = new javax.swing.JTextField();
        boxDrivetrainType = new javax.swing.JComboBox<>();
        labMaxVel = new javax.swing.JLabel();
        labMaxAcc = new javax.swing.JLabel();
        labMaxJerk = new javax.swing.JLabel();
        labInitPow0 = new javax.swing.JLabel();
        labInitPow1 = new javax.swing.JLabel();
        labInitPow2 = new javax.swing.JLabel();
        labInitPow3 = new javax.swing.JLabel();
        txtWheelRadius = new javax.swing.JTextField();
        labWheelRadius = new javax.swing.JLabel();
        panTrajectoryTab = new javax.swing.JPanel();
        panKnots = new javax.swing.JPanel();
        scrKnotTable = new javax.swing.JScrollPane();
        tblKnots = new javax.swing.JTable();
        boxPathType = new javax.swing.JComboBox<>();
        labPathType = new javax.swing.JLabel();
        labProfileType = new javax.swing.JLabel();
        boxProfileType = new javax.swing.JComboBox<>();
        tabControllers = new javax.swing.JPanel();
        srcCoefficientsTable = new javax.swing.JScrollPane();
        tblCoefficients = new javax.swing.JTable();
        txtUpdateFrequency = new javax.swing.JTextField();
        labUpdateFrequency = new javax.swing.JLabel();
        labUpdateFrequencyUnit = new javax.swing.JLabel();
        panNoiseTab = new javax.swing.JPanel();
        chkAddNoise = new javax.swing.JCheckBox();
        panRobotPoseNoise = new javax.swing.JPanel();
        boxBotPoseStatNoiseType = new javax.swing.JComboBox<>();
        txtBotPoseStatNoiseLower = new javax.swing.JTextField();
        labBotPoseStatNoiseType = new javax.swing.JLabel();
        labBotPoseStatNoiseLower = new javax.swing.JLabel();
        labBotPoseStatNoiseUpper = new javax.swing.JLabel();
        txtBotPoseStatNoiseUpper = new javax.swing.JTextField();
        panTargetPoseNoise = new javax.swing.JPanel();
        boxBotPoseAddNoiseType = new javax.swing.JComboBox<>();
        txtBotPoseAddNoiseLower = new javax.swing.JTextField();
        labBotPoseAddNoiseType = new javax.swing.JLabel();
        labBotPoseAddNoiseLower = new javax.swing.JLabel();
        labBotPoseAddNoiseUpper = new javax.swing.JLabel();
        txtBotPoseAddNoiseUpper = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        tabParent.setName("Simulation"); // NOI18N

        panSimulation.setBorder(javax.swing.BorderFactory.createTitledBorder("Simulation"));

        chkSimulationRun.setText("Run");
        chkSimulationRun.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chkSimulationRunMouseClicked(evt);
            }
        });

        btSimulationReset.setText("Reset simulation");
        btSimulationReset.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btSimulationResetMouseClicked(evt);
            }
        });

        sldSimulationSpeed.setMaximum(200);
        sldSimulationSpeed.setValue(100);
        sldSimulationSpeed.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldSimulationSpeedStateChanged(evt);
            }
        });

        labSimulationSpeed.setText("Speed (100%)");

        chkFollowPath.setSelected(true);
        chkFollowPath.setText("Follow path");
        chkFollowPath.setEnabled(false);
        chkFollowPath.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                chkFollowPathMouseReleased(evt);
            }
        });

        btAdvanceBy.setText("Advance by");
        btAdvanceBy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btAdvanceByMouseClicked(evt);
            }
        });

        txtAdvanceBy.setText("0.1");
        txtAdvanceBy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAdvanceByFocusLost(evt);
            }
        });

        labAdvanceByUnit.setText("Time units");

        txtPixelsPerUnit.setText("2");
        txtPixelsPerUnit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPixelsPerUnitFocusLost(evt);
            }
        });

        labPixelsPerUnit.setText("Pixels per unit");

        javax.swing.GroupLayout panSimulationLayout = new javax.swing.GroupLayout(panSimulation);
        panSimulation.setLayout(panSimulationLayout);
        panSimulationLayout.setHorizontalGroup(
            panSimulationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSimulationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panSimulationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panSimulationLayout.createSequentialGroup()
                        .addGroup(panSimulationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sldSimulationSpeed, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                            .addGroup(panSimulationLayout.createSequentialGroup()
                                .addGroup(panSimulationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panSimulationLayout.createSequentialGroup()
                                        .addComponent(chkSimulationRun)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(chkFollowPath))
                                    .addComponent(btSimulationReset, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(panSimulationLayout.createSequentialGroup()
                        .addGroup(panSimulationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labSimulationSpeed)
                            .addGroup(panSimulationLayout.createSequentialGroup()
                                .addComponent(btAdvanceBy)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAdvanceBy, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labAdvanceByUnit))
                            .addGroup(panSimulationLayout.createSequentialGroup()
                                .addComponent(txtPixelsPerUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labPixelsPerUnit)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        panSimulationLayout.setVerticalGroup(
            panSimulationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSimulationLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panSimulationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSimulationRun)
                    .addComponent(chkFollowPath))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btSimulationReset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panSimulationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btAdvanceBy)
                    .addComponent(txtAdvanceBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labAdvanceByUnit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panSimulationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPixelsPerUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labPixelsPerUnit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labSimulationSpeed)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sldSimulationSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panRobot.setBorder(javax.swing.BorderFactory.createTitledBorder("Robot"));

        txtRobotInitialX.setText("24.0");
        txtRobotInitialX.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRobotInitialXFocusLost(evt);
            }
        });

        txtRobotInitialY.setText("24.0");
        txtRobotInitialY.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRobotInitialYFocusLost(evt);
            }
        });

        labRobotInitialX.setText("Initial X");
        labRobotInitialX.setToolTipText("");

        labRobotInitialY.setText("Initial Y");

        txtRobotInitialTheta.setText("0.0");
        txtRobotInitialTheta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRobotInitialThetaFocusLost(evt);
            }
        });

        labRobotInitialTheta.setText("Initial Θ (°)");

        txtRobotPower0.setText("0");
        txtRobotPower0.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRobotPower0FocusLost(evt);
            }
        });

        txtRobotPower1.setText("0");
        txtRobotPower1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRobotPower1FocusLost(evt);
            }
        });

        txtRobotPower2.setText("0");
        txtRobotPower2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRobotPower2FocusLost(evt);
            }
        });

        txtRobotPower3.setText("0");
        txtRobotPower3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRobotPower3FocusLost(evt);
            }
        });

        txtRobotWidth.setText("18.0");
        txtRobotWidth.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRobotWidthFocusLost(evt);
            }
        });

        txtRobotHeight.setText("18.0");
        txtRobotHeight.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRobotHeightFocusLost(evt);
            }
        });

        labRobotWidth.setText("Width");
        labRobotWidth.setToolTipText("");

        labRobotHeight.setText("Height");
        labRobotHeight.setToolTipText("");

        txtMaxVelocity.setText("12.0");
        txtMaxVelocity.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMaxVelocityFocusLost(evt);
            }
        });

        txtMaxAcceleration.setText("6.0");
        txtMaxAcceleration.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMaxAccelerationFocusLost(evt);
            }
        });

        txtMaxJerk.setText("4.0");
        txtMaxJerk.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMaxJerkFocusLost(evt);
            }
        });

        boxDrivetrainType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mecanum", "Tank" }));

        labMaxVel.setText("Maximum velocity");

        labMaxAcc.setText("Maximum acceleration");

        labMaxJerk.setText("Maximum jerk");

        labInitPow0.setText("Front left initial power");

        labInitPow1.setText("Back left initial power");

        labInitPow2.setText("Back right initial power");

        labInitPow3.setText("Front right initial power");

        txtWheelRadius.setText("2.0");
        txtWheelRadius.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtWheelRadiusFocusLost(evt);
            }
        });

        labWheelRadius.setText("Wheel radius");

        javax.swing.GroupLayout panRobotLayout = new javax.swing.GroupLayout(panRobot);
        panRobot.setLayout(panRobotLayout);
        panRobotLayout.setHorizontalGroup(
            panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRobotLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panRobotLayout.createSequentialGroup()
                        .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtRobotInitialTheta)
                            .addComponent(txtRobotInitialY, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                            .addComponent(txtRobotInitialX, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMaxVelocity, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panRobotLayout.createSequentialGroup()
                                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panRobotLayout.createSequentialGroup()
                                        .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(labRobotInitialX)
                                            .addComponent(labRobotInitialY, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                                        .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtRobotHeight, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtRobotWidth, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(panRobotLayout.createSequentialGroup()
                                        .addComponent(labRobotInitialTheta)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtWheelRadius)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labRobotHeight)
                                    .addComponent(labRobotWidth)
                                    .addComponent(labWheelRadius)))
                            .addComponent(labMaxVel))
                        .addContainerGap(20, Short.MAX_VALUE))
                    .addGroup(panRobotLayout.createSequentialGroup()
                        .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panRobotLayout.createSequentialGroup()
                                .addComponent(txtMaxAcceleration, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labMaxAcc))
                            .addGroup(panRobotLayout.createSequentialGroup()
                                .addComponent(txtMaxJerk, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labMaxJerk))
                            .addGroup(panRobotLayout.createSequentialGroup()
                                .addComponent(txtRobotPower1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labInitPow1))
                            .addGroup(panRobotLayout.createSequentialGroup()
                                .addComponent(txtRobotPower0, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labInitPow0))
                            .addGroup(panRobotLayout.createSequentialGroup()
                                .addComponent(txtRobotPower2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labInitPow2))
                            .addGroup(panRobotLayout.createSequentialGroup()
                                .addComponent(txtRobotPower3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labInitPow3))
                            .addComponent(boxDrivetrainType, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        panRobotLayout.setVerticalGroup(
            panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRobotLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRobotInitialX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labRobotInitialX)
                    .addComponent(labRobotWidth)
                    .addComponent(txtRobotWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRobotInitialY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labRobotInitialY)
                    .addComponent(txtRobotHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labRobotHeight))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRobotInitialTheta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labRobotInitialTheta)
                    .addComponent(txtWheelRadius, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labWheelRadius))
                .addGap(18, 18, 18)
                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMaxVelocity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labMaxVel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMaxAcceleration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labMaxAcc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMaxJerk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labMaxJerk))
                .addGap(18, 18, 18)
                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRobotPower0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labInitPow0))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRobotPower1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labInitPow1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRobotPower2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labInitPow2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRobotPower3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labInitPow3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(boxDrivetrainType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panSimulationTabLayout = new javax.swing.GroupLayout(panSimulationTab);
        panSimulationTab.setLayout(panSimulationTabLayout);
        panSimulationTabLayout.setHorizontalGroup(
            panSimulationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSimulationTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panSimulationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panSimulation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panRobot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panSimulationTabLayout.setVerticalGroup(
            panSimulationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSimulationTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panSimulation, javax.swing.GroupLayout.PREFERRED_SIZE, 186, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panRobot, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabParent.addTab("Simulation", panSimulationTab);

        panKnots.setBorder(javax.swing.BorderFactory.createTitledBorder("Knots"));

        tblKnots.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Double(24.0),  new Double(24.0),  new Double(0.0)},
                { new Double(144.0),  new Double(144.0),  new Double(45.0)},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "X", "Y", "Θ (°)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblKnots.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tblKnotsPropertyChange(evt);
            }
        });
        scrKnotTable.setViewportView(tblKnots);
        if (tblKnots.getColumnModel().getColumnCount() > 0) {
            tblKnots.getColumnModel().getColumn(0).setResizable(false);
            tblKnots.getColumnModel().getColumn(1).setResizable(false);
            tblKnots.getColumnModel().getColumn(2).setResizable(false);
        }

        javax.swing.GroupLayout panKnotsLayout = new javax.swing.GroupLayout(panKnots);
        panKnots.setLayout(panKnotsLayout);
        panKnotsLayout.setHorizontalGroup(
            panKnotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panKnotsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrKnotTable, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        panKnotsLayout.setVerticalGroup(
            panKnotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panKnotsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrKnotTable, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addContainerGap())
        );

        boxPathType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hermite cubic", "Hermite quintic" }));
        boxPathType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                boxPathTypeItemStateChanged(evt);
            }
        });

        labPathType.setText("Path type");

        labProfileType.setText("Profile type");

        boxProfileType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Triangular", "Trapezoidal", "S-curve" }));
        boxProfileType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                boxProfileTypeItemStateChanged(evt);
            }
        });

        tabControllers.setBorder(javax.swing.BorderFactory.createTitledBorder("Controllers"));

        tblCoefficients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"P",  new Double(-0.5),  new Double(-0.05),  new Double(-0.05)},
                {"I",  new Double(0.0),  new Double(0.0),  new Double(0.0)},
                {"D",  new Double(0.0),  new Double(0.0),  new Double(0.0)},
                {"V",  new Double(0.2),  new Double(0.02),  new Double(0.02)},
                {"A",  new Double(0.0),  new Double(0.0),  new Double(0.0)},
                {"S",  new Double(0.0),  new Double(0.0),  new Double(0.0)}
            },
            new String [] {
                "", "Heading", "Lateral", "Axial"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCoefficients.getTableHeader().setReorderingAllowed(false);
        tblCoefficients.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tblCoefficientsPropertyChange(evt);
            }
        });
        srcCoefficientsTable.setViewportView(tblCoefficients);
        if (tblCoefficients.getColumnModel().getColumnCount() > 0) {
            tblCoefficients.getColumnModel().getColumn(0).setResizable(false);
            tblCoefficients.getColumnModel().getColumn(0).setPreferredWidth(10);
            tblCoefficients.getColumnModel().getColumn(3).setResizable(false);
        }

        txtUpdateFrequency.setText("100");
        txtUpdateFrequency.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUpdateFrequencyFocusLost(evt);
            }
        });

        labUpdateFrequency.setText("Update frequency");
        labUpdateFrequency.setToolTipText("");

        labUpdateFrequencyUnit.setText("Hz");

        javax.swing.GroupLayout tabControllersLayout = new javax.swing.GroupLayout(tabControllers);
        tabControllers.setLayout(tabControllersLayout);
        tabControllersLayout.setHorizontalGroup(
            tabControllersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabControllersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabControllersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(srcCoefficientsTable, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(tabControllersLayout.createSequentialGroup()
                        .addComponent(labUpdateFrequency)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtUpdateFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labUpdateFrequencyUnit)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabControllersLayout.setVerticalGroup(
            tabControllersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabControllersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(srcCoefficientsTable, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(tabControllersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labUpdateFrequency)
                    .addComponent(txtUpdateFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labUpdateFrequencyUnit))
                .addContainerGap())
        );

        javax.swing.GroupLayout panTrajectoryTabLayout = new javax.swing.GroupLayout(panTrajectoryTab);
        panTrajectoryTab.setLayout(panTrajectoryTabLayout);
        panTrajectoryTabLayout.setHorizontalGroup(
            panTrajectoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTrajectoryTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panTrajectoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panKnots, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panTrajectoryTabLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(panTrajectoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labProfileType)
                            .addComponent(labPathType))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panTrajectoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(boxPathType, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(boxProfileType, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(tabControllers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        panTrajectoryTabLayout.setVerticalGroup(
            panTrajectoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTrajectoryTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panTrajectoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labPathType)
                    .addComponent(boxPathType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panTrajectoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labProfileType)
                    .addComponent(boxProfileType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panKnots, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabControllers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabParent.addTab("Trajectory", panTrajectoryTab);

        chkAddNoise.setText("Add noise");
        chkAddNoise.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkAddNoiseStateChanged(evt);
            }
        });

        panRobotPoseNoise.setBorder(javax.swing.BorderFactory.createTitledBorder("Robot pose static noise"));

        boxBotPoseStatNoiseType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sinusoidal", "Random" }));
        boxBotPoseStatNoiseType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                boxBotPoseStatNoiseTypeItemStateChanged(evt);
            }
        });

        txtBotPoseStatNoiseLower.setText("0.0");
        txtBotPoseStatNoiseLower.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBotPoseStatNoiseLowerFocusLost(evt);
            }
        });

        labBotPoseStatNoiseType.setText("Type");

        labBotPoseStatNoiseLower.setText("Lower");

        labBotPoseStatNoiseUpper.setText("Upper");

        txtBotPoseStatNoiseUpper.setText("0.0");
        txtBotPoseStatNoiseUpper.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBotPoseStatNoiseUpperFocusLost(evt);
            }
        });

        javax.swing.GroupLayout panRobotPoseNoiseLayout = new javax.swing.GroupLayout(panRobotPoseNoise);
        panRobotPoseNoise.setLayout(panRobotPoseNoiseLayout);
        panRobotPoseNoiseLayout.setHorizontalGroup(
            panRobotPoseNoiseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRobotPoseNoiseLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panRobotPoseNoiseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labBotPoseStatNoiseType)
                    .addComponent(labBotPoseStatNoiseLower))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panRobotPoseNoiseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panRobotPoseNoiseLayout.createSequentialGroup()
                        .addComponent(txtBotPoseStatNoiseLower, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labBotPoseStatNoiseUpper)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBotPoseStatNoiseUpper, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(boxBotPoseStatNoiseType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        panRobotPoseNoiseLayout.setVerticalGroup(
            panRobotPoseNoiseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRobotPoseNoiseLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panRobotPoseNoiseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boxBotPoseStatNoiseType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labBotPoseStatNoiseType))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panRobotPoseNoiseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBotPoseStatNoiseLower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labBotPoseStatNoiseLower)
                    .addComponent(txtBotPoseStatNoiseUpper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labBotPoseStatNoiseUpper))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panTargetPoseNoise.setBorder(javax.swing.BorderFactory.createTitledBorder("Robot pose cumulative noise"));

        boxBotPoseAddNoiseType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sinusoidal", "Random" }));
        boxBotPoseAddNoiseType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                boxBotPoseAddNoiseTypeItemStateChanged(evt);
            }
        });

        txtBotPoseAddNoiseLower.setText("0.0");
        txtBotPoseAddNoiseLower.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBotPoseAddNoiseLowerFocusLost(evt);
            }
        });

        labBotPoseAddNoiseType.setText("Type");

        labBotPoseAddNoiseLower.setText("Lower");

        labBotPoseAddNoiseUpper.setText("Upper");

        txtBotPoseAddNoiseUpper.setText("0.0");
        txtBotPoseAddNoiseUpper.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBotPoseAddNoiseUpperFocusLost(evt);
            }
        });

        javax.swing.GroupLayout panTargetPoseNoiseLayout = new javax.swing.GroupLayout(panTargetPoseNoise);
        panTargetPoseNoise.setLayout(panTargetPoseNoiseLayout);
        panTargetPoseNoiseLayout.setHorizontalGroup(
            panTargetPoseNoiseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTargetPoseNoiseLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panTargetPoseNoiseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labBotPoseAddNoiseType)
                    .addComponent(labBotPoseAddNoiseLower))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panTargetPoseNoiseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panTargetPoseNoiseLayout.createSequentialGroup()
                        .addComponent(txtBotPoseAddNoiseLower, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labBotPoseAddNoiseUpper)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBotPoseAddNoiseUpper, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(boxBotPoseAddNoiseType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        panTargetPoseNoiseLayout.setVerticalGroup(
            panTargetPoseNoiseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTargetPoseNoiseLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panTargetPoseNoiseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boxBotPoseAddNoiseType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labBotPoseAddNoiseType))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panTargetPoseNoiseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBotPoseAddNoiseLower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labBotPoseAddNoiseLower)
                    .addComponent(txtBotPoseAddNoiseUpper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labBotPoseAddNoiseUpper))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panNoiseTabLayout = new javax.swing.GroupLayout(panNoiseTab);
        panNoiseTab.setLayout(panNoiseTabLayout);
        panNoiseTabLayout.setHorizontalGroup(
            panNoiseTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panNoiseTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panNoiseTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panNoiseTabLayout.createSequentialGroup()
                        .addComponent(chkAddNoise)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panRobotPoseNoise, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panTargetPoseNoise, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panNoiseTabLayout.setVerticalGroup(
            panNoiseTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panNoiseTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkAddNoise)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panRobotPoseNoise, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panTargetPoseNoise, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(333, Short.MAX_VALUE))
        );

        tabParent.addTab("Noise", panNoiseTab);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabParent, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabParent)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkSimulationRunMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chkSimulationRunMouseClicked
        if (chkSimulationRun.isSelected())
            Clock.simEpoch = Clock.timestamp();
        else
            Clock.timeBank += Clock.timestamp() - Clock.simEpoch;
        
        for (Simulant obj : sim.objects())
                obj.resetTimestamp();
        
        sim.setRun(chkSimulationRun.isSelected());
        btAdvanceBy.setEnabled(!chkSimulationRun.isSelected());
    }//GEN-LAST:event_chkSimulationRunMouseClicked

    private void txtRobotInitialXFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRobotInitialXFocusLost
        Registry.edit(Registry.Entry.ROBOT_INIT_X, txtRobotInitialX);
    }//GEN-LAST:event_txtRobotInitialXFocusLost

    private void txtRobotInitialYFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRobotInitialYFocusLost
        Registry.edit(Registry.Entry.ROBOT_INIT_Y, txtRobotInitialY);
    }//GEN-LAST:event_txtRobotInitialYFocusLost

    private void txtRobotInitialThetaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRobotInitialThetaFocusLost
        Registry.edit(Registry.Entry.ROBOT_INIT_THETA, txtRobotInitialTheta);
    }//GEN-LAST:event_txtRobotInitialThetaFocusLost

    private void txtRobotPower0FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRobotPower0FocusLost
        Registry.edit(Registry.Entry.ROBOT_POW0, txtRobotPower0);
    }//GEN-LAST:event_txtRobotPower0FocusLost

    private void txtRobotPower1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRobotPower1FocusLost
        Registry.edit(Registry.Entry.ROBOT_POW1, txtRobotPower1);
    }//GEN-LAST:event_txtRobotPower1FocusLost

    private void txtRobotPower2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRobotPower2FocusLost
        Registry.edit(Registry.Entry.ROBOT_POW2, txtRobotPower2);
    }//GEN-LAST:event_txtRobotPower2FocusLost

    private void txtRobotPower3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRobotPower3FocusLost
        Registry.edit(Registry.Entry.ROBOT_POW3, txtRobotPower3);
    }//GEN-LAST:event_txtRobotPower3FocusLost

    private void txtRobotWidthFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRobotWidthFocusLost
        Registry.edit(Registry.Entry.ROBOT_WIDTH, txtRobotWidth);
    }//GEN-LAST:event_txtRobotWidthFocusLost

    private void txtRobotHeightFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRobotHeightFocusLost
        Registry.edit(Registry.Entry.ROBOT_HEIGHT, txtRobotHeight);
    }//GEN-LAST:event_txtRobotHeightFocusLost

    private void btSimulationResetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btSimulationResetMouseClicked
        chkSimulationRun.setSelected(false);
        chkSimulationRunMouseClicked(null);
        
        Registry.edit(Registry.Entry.ROBOT_INIT_X, txtRobotInitialX);
        Registry.edit(Registry.Entry.ROBOT_INIT_Y, txtRobotInitialY);
        Registry.edit(Registry.Entry.ROBOT_INIT_THETA, txtRobotInitialTheta);
        Registry.edit(Registry.Entry.ROBOT_POW0, txtRobotPower0);
        Registry.edit(Registry.Entry.ROBOT_POW1, txtRobotPower1);
        Registry.edit(Registry.Entry.ROBOT_POW2, txtRobotPower2);
        Registry.edit(Registry.Entry.ROBOT_POW3, txtRobotPower3);
        Simulation.robot().zeroVectors();
        Clock.timeBank = 0;
        Clock.simEpoch = Clock.timestamp();
    }//GEN-LAST:event_btSimulationResetMouseClicked

    private void sldSimulationSpeedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldSimulationSpeedStateChanged
        double speed = sldSimulationSpeed.getValue() / 100.0;
        Clock.simulationSpeed = speed;
        labSimulationSpeed.setText("Speed (" + sldSimulationSpeed.getValue() + "%)");
    }//GEN-LAST:event_sldSimulationSpeedStateChanged

    private void tblKnotsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tblKnotsPropertyChange
        updateTrajectory();
    }//GEN-LAST:event_tblKnotsPropertyChange

    private void tblCoefficientsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tblCoefficientsPropertyChange
        double[] heading = new double[6], lateral = new double[6], axial = new double[6];
        
        for (int i = 0; i < 6; i++) {
            heading[i] = (Double)tblCoefficients.getValueAt(i, 1);
            lateral[i] = (Double)tblCoefficients.getValueAt(i, 2);
            axial[i] = (Double)tblCoefficients.getValueAt(i, 3);
        }
        
        Simulation.robot().follower().setCoefficients(heading, lateral, axial);
    }//GEN-LAST:event_tblCoefficientsPropertyChange

    private void txtMaxVelocityFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxVelocityFocusLost
        updateMotionConstraints();
    }//GEN-LAST:event_txtMaxVelocityFocusLost

    private void txtMaxAccelerationFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxAccelerationFocusLost
        updateMotionConstraints();
    }//GEN-LAST:event_txtMaxAccelerationFocusLost

    private void txtMaxJerkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxJerkFocusLost
        updateMotionConstraints();
    }//GEN-LAST:event_txtMaxJerkFocusLost

    private void chkFollowPathMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chkFollowPathMouseReleased
        Simulation.robot().setIsFollowingTrajectory(chkFollowPath.isSelected());
    }//GEN-LAST:event_chkFollowPathMouseReleased

    private void txtWheelRadiusFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWheelRadiusFocusLost
        Registry.edit(Registry.Entry.ROBOT_WHEEL_RADIUS, txtWheelRadius);
    }//GEN-LAST:event_txtWheelRadiusFocusLost

    private void boxPathTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_boxPathTypeItemStateChanged
        updateTrajectory();
    }//GEN-LAST:event_boxPathTypeItemStateChanged

    private void boxProfileTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_boxProfileTypeItemStateChanged
        updateTrajectory();
    }//GEN-LAST:event_boxProfileTypeItemStateChanged

    private void txtUpdateFrequencyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUpdateFrequencyFocusLost
        Registry.edit(Registry.Entry.ROBOT_UPDATE_FREQUENCY, txtUpdateFrequency);
    }//GEN-LAST:event_txtUpdateFrequencyFocusLost

    private void txtPixelsPerUnitFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPixelsPerUnitFocusLost
        Registry.edit(Registry.Entry.PIXELS_PER_UNIT, txtPixelsPerUnit);
    }//GEN-LAST:event_txtPixelsPerUnitFocusLost

    private void btAdvanceByMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btAdvanceByMouseClicked
        NumericEntryParser p = new NumericEntryParser(0);
        double time = p.parse(txtAdvanceBy.getText());
        
        for (Simulant obj : sim.objects())
                obj.resetTimestamp();
        
        sim.advanceSim(time);
    }//GEN-LAST:event_btAdvanceByMouseClicked

    private void txtAdvanceByFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAdvanceByFocusLost
        NumericEntryParser p = new NumericEntryParser(0);
        double time = p.parse(txtAdvanceBy.getText());
        txtAdvanceBy.setText("" + time);
    }//GEN-LAST:event_txtAdvanceByFocusLost

    private void boxBotPoseStatNoiseTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_boxBotPoseStatNoiseTypeItemStateChanged
        updateNoise();
    }//GEN-LAST:event_boxBotPoseStatNoiseTypeItemStateChanged

    private void txtBotPoseStatNoiseLowerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBotPoseStatNoiseLowerFocusLost
        updateNoise();
    }//GEN-LAST:event_txtBotPoseStatNoiseLowerFocusLost

    private void txtBotPoseStatNoiseUpperFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBotPoseStatNoiseUpperFocusLost
        updateNoise();
    }//GEN-LAST:event_txtBotPoseStatNoiseUpperFocusLost

    private void boxBotPoseAddNoiseTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_boxBotPoseAddNoiseTypeItemStateChanged
        updateNoise();
    }//GEN-LAST:event_boxBotPoseAddNoiseTypeItemStateChanged

    private void txtBotPoseAddNoiseLowerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBotPoseAddNoiseLowerFocusLost
        updateNoise();
    }//GEN-LAST:event_txtBotPoseAddNoiseLowerFocusLost

    private void txtBotPoseAddNoiseUpperFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBotPoseAddNoiseUpperFocusLost
        updateNoise();
    }//GEN-LAST:event_txtBotPoseAddNoiseUpperFocusLost

    private void chkAddNoiseStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkAddNoiseStateChanged
        NoiseGenerator.setAddNoise(chkAddNoise.isSelected());
    }//GEN-LAST:event_chkAddNoiseStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> boxBotPoseAddNoiseType;
    private javax.swing.JComboBox<String> boxBotPoseStatNoiseType;
    private javax.swing.JComboBox<String> boxDrivetrainType;
    private javax.swing.JComboBox<String> boxPathType;
    private javax.swing.JComboBox<String> boxProfileType;
    private javax.swing.JButton btAdvanceBy;
    private javax.swing.JButton btSimulationReset;
    private javax.swing.JCheckBox chkAddNoise;
    private javax.swing.JCheckBox chkFollowPath;
    private javax.swing.JCheckBox chkSimulationRun;
    private javax.swing.JLabel labAdvanceByUnit;
    private javax.swing.JLabel labBotPoseAddNoiseLower;
    private javax.swing.JLabel labBotPoseAddNoiseType;
    private javax.swing.JLabel labBotPoseAddNoiseUpper;
    private javax.swing.JLabel labBotPoseStatNoiseLower;
    private javax.swing.JLabel labBotPoseStatNoiseType;
    private javax.swing.JLabel labBotPoseStatNoiseUpper;
    private javax.swing.JLabel labInitPow0;
    private javax.swing.JLabel labInitPow1;
    private javax.swing.JLabel labInitPow2;
    private javax.swing.JLabel labInitPow3;
    private javax.swing.JLabel labMaxAcc;
    private javax.swing.JLabel labMaxJerk;
    private javax.swing.JLabel labMaxVel;
    private javax.swing.JLabel labPathType;
    private javax.swing.JLabel labPixelsPerUnit;
    private javax.swing.JLabel labProfileType;
    private javax.swing.JLabel labRobotHeight;
    private javax.swing.JLabel labRobotInitialTheta;
    private javax.swing.JLabel labRobotInitialX;
    private javax.swing.JLabel labRobotInitialY;
    private javax.swing.JLabel labRobotWidth;
    private javax.swing.JLabel labSimulationSpeed;
    private javax.swing.JLabel labUpdateFrequency;
    private javax.swing.JLabel labUpdateFrequencyUnit;
    private javax.swing.JLabel labWheelRadius;
    private javax.swing.JPanel panKnots;
    private javax.swing.JPanel panNoiseTab;
    private javax.swing.JPanel panRobot;
    private javax.swing.JPanel panRobotPoseNoise;
    private javax.swing.JPanel panSimulation;
    private javax.swing.JPanel panSimulationTab;
    private javax.swing.JPanel panTargetPoseNoise;
    private javax.swing.JPanel panTrajectoryTab;
    private javax.swing.JScrollPane scrKnotTable;
    private javax.swing.JSlider sldSimulationSpeed;
    private javax.swing.JScrollPane srcCoefficientsTable;
    private javax.swing.JPanel tabControllers;
    private javax.swing.JTabbedPane tabParent;
    private javax.swing.JTable tblCoefficients;
    private javax.swing.JTable tblKnots;
    private javax.swing.JTextField txtAdvanceBy;
    private javax.swing.JTextField txtBotPoseAddNoiseLower;
    private javax.swing.JTextField txtBotPoseAddNoiseUpper;
    private javax.swing.JTextField txtBotPoseStatNoiseLower;
    private javax.swing.JTextField txtBotPoseStatNoiseUpper;
    private javax.swing.JTextField txtMaxAcceleration;
    private javax.swing.JTextField txtMaxJerk;
    private javax.swing.JTextField txtMaxVelocity;
    private javax.swing.JTextField txtPixelsPerUnit;
    private javax.swing.JTextField txtRobotHeight;
    private javax.swing.JTextField txtRobotInitialTheta;
    private javax.swing.JTextField txtRobotInitialX;
    private javax.swing.JTextField txtRobotInitialY;
    private javax.swing.JTextField txtRobotPower0;
    private javax.swing.JTextField txtRobotPower1;
    private javax.swing.JTextField txtRobotPower2;
    private javax.swing.JTextField txtRobotPower3;
    private javax.swing.JTextField txtRobotWidth;
    private javax.swing.JTextField txtUpdateFrequency;
    private javax.swing.JTextField txtWheelRadius;
    // End of variables declaration//GEN-END:variables
}
