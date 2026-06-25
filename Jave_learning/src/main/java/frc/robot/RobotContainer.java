// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.RunIntakeCommand;
import frc.robot.subsystems.IntakeSubsystem;

/**
 * RobotContainer is the "glue" class for the entire robot.
 *
 * Its two main jobs are:
 *   1. Create all subsystems (hardware) and commands (behaviors)
 *   2. Wire driver inputs (buttons/joysticks) to commands
 *
 * Nothing else in the robot should create subsystems — there is only ever
 * ONE instance of each subsystem, created here and shared everywhere.
 *
 * This pattern is used by most competitive FRC teams because it keeps all
 * control mappings in one place, making it easy to change button assignments
 * without digging through subsystem or command files.
 */
public class RobotContainer {

  // =========================================================================
  // SUBSYSTEMS
  // Declare and instantiate all subsystems here as private final fields.
  // 'final' means the reference can never be reassigned after creation.
  // =========================================================================

  /** The intake subsystem — controls the two intake motors. */
  private final IntakeSubsystem m_intake = new IntakeSubsystem();

  // =========================================================================
  // CONTROLLERS
  // CommandXboxController wraps a standard USB Xbox controller.
  // It provides named button triggers: .a(), .b(), .x(), .y(),
  // .leftBumper(), .rightBumper(), .leftTrigger(), .rightTrigger(), etc.
  //
  // Port 0 = the first gamepad plugged into the Driver Station PC.
  // =========================================================================

  /** The driver's Xbox controller, connected on USB port 0. */
  private final CommandXboxController m_controller = new CommandXboxController(0);

  /**
   * Constructor — runs once when the robot program starts.
   * We call configureButtonBindings() here to set up all our button mappings.
   */
  public RobotContainer() {
    configureButtonBindings();
  }

  /**
   * Wires driver buttons to commands (teleop control mappings).
   *
   * Binding types you'll use most often:
   *
   *   trigger.whileTrue(cmd)    — Run cmd while button held; cancel when released
   *   trigger.onTrue(cmd)       — Run cmd once when button is first pressed
   *   trigger.toggleOnTrue(cmd) — Toggle cmd on/off each time button is pressed
   *
   * We use whileTrue() for the intake because we want the motors to spin ONLY
   * while the driver is holding A, and stop the moment they let go.
   */
  private void configureButtonBindings() {

    // A BUTTON → Run intake while held, stop when released
    //
    // Step-by-step flow when the driver presses and releases A:
    //
    //   1. Driver presses A
    //   2. whileTrue() schedules RunIntakeCommand
    //   3. RunIntakeCommand.initialize() is called → m_intake.runIntake() → motors spin
    //   4. RunIntakeCommand.execute() loops every 20ms (does nothing here)
    //   5. Driver releases A
    //   6. whileTrue() cancels RunIntakeCommand
    //   7. RunIntakeCommand.end(true) is called → m_intake.stopIntake() → motors stop
    //
    m_controller.a().whileTrue(new RunIntakeCommand(m_intake));
  }

  /**
   * Returns the command to run during the 15-second autonomous period.
   *
   * We reuse the SAME RunIntakeCommand here — no new code needed!
   * The only addition is .withTimeout(3.0), which wraps the command in a
   * decorator that automatically cancels it after 3 seconds.
   *
   * This works because RunIntakeCommand.isFinished() returns false, meaning
   * it runs indefinitely until something external stops it. In teleop that's
   * the button release; in auto that's the timeout.
   *
   * Want to run multiple actions in auto? Chain them like this:
   *
   *   return new SequentialCommandGroup(
   *       new RunIntakeCommand(m_intake).withTimeout(3.0),
   *       // add more commands here, they run one after another
   *   );
   */
  public Command getAutonomousCommand() {
    // Run the intake for 3 seconds at the start of autonomous, then stop.
    return new RunIntakeCommand(m_intake).withTimeout(3.0);
  }
}
