// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * Robot is the "main control" of the program. The robot can be in different modes (disabled,
 * autonomous, teleop, and test), and this class has methods that run automatically when each mode
 * starts and while it is running.
 *
 * <p>Most of the time you will NOT need to change this file. In command-based robots, the real work
 * happens in RobotContainer.java and in the subsystems and commands folders. The most important job
 * of this file is calling the CommandScheduler (see robotPeriodic below), which is the part that
 * actually runs your commands.
 *
 * <p>If you change the name of this class or the package after creating this project, you must also
 * update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private final RobotContainer m_robotContainer;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  public Robot() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  // "Disabled" mode means the robot is on but NOT allowed to move. This is the
  // safe, resting state.

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  // Runs over and over while the robot is disabled.
  @Override
  public void disabledPeriodic() {}

  // "Autonomous" mode is when the robot drives ITSELF with no driver, using the
  // command returned by RobotContainer.getAutonomousCommand().

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      CommandScheduler.getInstance().schedule(m_autonomousCommand);
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  // "Teleop" (teleoperated) mode is when a human driver controls the robot with
  // a controller. This is usually where your driving code runs.

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }

    // Start IMU bias calibration. The robot must stay still for 3 seconds.
    // Calibration removes the gyro's zero-rate drift so the heading stays
    // accurate while driving straight.
    m_robotContainer.startCalibration();
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {}

  // "Test" mode is a special mode used for checking and testing the robot.

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
