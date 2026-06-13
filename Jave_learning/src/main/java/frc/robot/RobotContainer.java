// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.ArcadeDriveCommand;
import frc.robot.subsystems.Drivetrain;

/**
 * RobotContainer is the "wiring" class — it connects everything together.
 *
 * This is where you:
 *   1. Create subsystem instances (e.g., new Drivetrain())
 *   2. Create controller instances (e.g., new CommandXboxController(...))
 *   3. Assign default commands to subsystems
 *   4. Bind controller buttons to commands
 *   5. Configure autonomous routines
 *
 * RobotContainer is created once in Robot.java's constructor and lives for the entire
 * robot program. Think of it as the robot's "bill of materials and wiring diagram."
 *
 * The Robot.java class handles the robot lifecycle (teleop, auto, disabled modes) but
 * delegates all subsystem/command setup to this class. Keeping them separate makes each
 * class focused on one job — a core principle of clean software design.
 */
public class RobotContainer {

  // ---- SUBSYSTEMS ----
  // Create the Drivetrain subsystem. This calls new Drivetrain(), which:
  //   - Creates the two XRPMotor objects for left and right wheels
  //   - Inverts the right motor so positive = forward on both sides
  //   - Registers the subsystem with the CommandScheduler
  //
  // 'private final' means this field is only accessible within RobotContainer, and the
  // reference is set once here and never reassigned. Good practice for subsystems.
  private final Drivetrain m_drivetrain = new Drivetrain();

  // ---- CONTROLLERS ----
  // CommandXboxController is WPILib's Xbox controller class built for the command-based framework.
  // It wraps the raw XboxController class and adds the ability to bind commands directly
  // to button presses using trigger methods like .onTrue(), .whileTrue(), etc.
  //
  // The constructor argument is the USB port number on the Driver Station laptop.
  //   Port 0 = first controller plugged in (check the Driver Station USB tab if needed).
  //
  // Key axis getters on CommandXboxController:
  //   getLeftX()            → left stick horizontal  (axis 0)
  //   getLeftY()            → left stick vertical    (axis 1)  ← our speed input
  //   getRightX()           → right stick horizontal (axis 4)  ← our turn input
  //   getRightY()           → right stick vertical   (axis 5)
  //   getLeftTriggerAxis()  → left trigger  (0.0 to 1.0)
  //   getRightTriggerAxis() → right trigger (0.0 to 1.0)
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  /**
   * RobotContainer constructor — runs once at robot startup.
   *
   * The order matters: we set up default commands BEFORE button bindings so the subsystems
   * have a fallback behavior established before any additional button commands are configured.
   */
  public RobotContainer() {
    configureDefaultCommands();
    configureButtonBindings();
  }

  /**
   * Assigns default commands to subsystems.
   *
   * A default command is the command that a subsystem runs when NO OTHER command is
   * currently requiring that subsystem. As soon as all other commands finish or are
   * interrupted, the scheduler automatically restarts the default command.
   *
   * For the drivetrain, the default command is ArcadeDriveCommand — so the driver
   * always has control unless an autonomous command takes over. When auto finishes,
   * the scheduler automatically resumes ArcadeDriveCommand.
   *
   * --- Lambda Syntax Explanation ---
   * We pass DoubleSuppliers to ArcadeDriveCommand using Java lambda expressions.
   * A lambda like:   () -> m_driverController.getLeftY()
   * means:           "a function that takes no arguments and returns getLeftY()"
   *
   * This is equivalent to writing an anonymous class:
   *   new DoubleSupplier() {
   *     public double getAsDouble() { return m_driverController.getLeftY(); }
   *   }
   *
   * Lambdas are just a shorter way to write that. The command stores the lambda and
   * calls it fresh every 20ms in execute(), so it always gets the current joystick position.
   */
  private void configureDefaultCommands() {
    m_drivetrain.setDefaultCommand(
        new ArcadeDriveCommand(
            m_drivetrain,
            () -> m_driverController.getLeftY(),   // speed: left stick Y axis
            () -> m_driverController.getRightX()   // turn:  right stick X axis
        )
    );
  }

  /**
   * Binds commands to controller buttons.
   *
   * CommandXboxController provides a trigger object for each button. You can attach
   * commands to those triggers using methods like:
   *   .onTrue(command)      → run command once when button is first pressed
   *   .whileTrue(command)   → run command continuously while button is held down
   *   .onFalse(command)     → run command once when button is released
   *   .toggleOnTrue(command)→ start command on first press, stop it on second press
   *
   * Examples (commented out — nothing extra needed for basic arcade drive):
   *   m_driverController.a().onTrue(new SomeOtherCommand(m_drivetrain));
   *   m_driverController.rightBumper().whileTrue(new SlowModeCommand(m_drivetrain));
   */
  private void configureButtonBindings() {
    // No extra button bindings needed for basic arcade drive.
    // The default command handles all driving through the joystick axes.
  }

  /**
   * Returns the command to run during autonomous mode.
   *
   * Robot.java calls this in autonomousInit() and schedules whatever command is returned.
   * Returning null means no autonomous command will run (the robot sits still in auto).
   *
   * In a more complete robot program, you would return a selected auto routine here,
   * typically chosen via a SendableChooser dropdown on the SmartDashboard.
   */
  public Command getAutonomousCommand() {
    return null;
  }
}

