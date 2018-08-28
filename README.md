# Livetrain

A tool for generating, tuning, and simulating wheeled mobile robot trajectories. **Currently in heavy alpha.**

## Key features

* Tank and mecanum drivetrain support
* PIDVA tuning for spline following
* Realtime telemetry tracking
* Noise simulation for realistic testing

![Realtime trajectory simulation](https://i.imgur.com/7cNuy8v.png)

![Spline trajectory generation](https://i.imgur.com/vCr29I5.png)

## Installation

None required! Download or zip the repo and run `dist/Livetrain.jar`.

## Quickstart

### Simulation

* Run the simulation automatically with the `Run` checkbox or incrementally with `Advance by`
* Change the zoom level by specifying a `Pixels per unit` ratio
* Configure the drivetrain geometry by specifying dimensions and wheel type. These metrics factor into the robot's kinematics

### Trajectory

* Design a path for the robot to follow by specifying poses in the data table
* Choose the path and motion profile type for the trajectory
* Tune the trajectory follower's PIDVA coefficients in the `Controllers` table
* The `Update frequency` field specifies how many times per second the drivetrain receives an update from the controllers

### Noise

* Noise can be factored into the physics to emulate real life electromechanical unpredictability
* "Static" noise is newly generated each update cycle, whereas "cumulative" builds up over time
* Sinusoidal noise oscillates with time between its bounds
* Random noise creates random values between its bounds

## Library

Livetrain's mathematical backend uses the [Elusive](https://github.com/stefandebruyn/Elusive) library, another of Team 7797's toys.
