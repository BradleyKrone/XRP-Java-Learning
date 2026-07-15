/**
 * RobotContainer is the "wiring hub" of the robot program.
 * This is where you connect everything together:
 *   - create your subsystems (the parts of the robot - see the subsystems folder)
 *   - create your controllers (Xbox controller, joystick, etc.)
 *   - say which button runs which command (see the commands folder)
 */

/**
 * RobotContainer is the "wiring hub" of the robot program.
 * This is where you connect everything together:
 *   - create your subsystems (the parts of the robot - see the subsystems folder)
 *   - create your controllers (Xbox controller, joystick, etc.)
 *   - say which button runs which command (see the commands folder)
 */

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;


public class RobotContainer {

  /**
   * Constructor — runs ONCE when the robot program first starts.
   * Think of it as the "setup" step.
   */
  public RobotContainer() {
    configureButtonBindings();
  }

  /**
   * This is where you map controller buttons to commands.
   * Each line inside here says: "when the driver presses THIS button, run THAT command."
   */
  private void configureButtonBindings() {}

  /**
   * Returns the command the robot should run during the Autonomous period.
   * Autonomous = the first 15 seconds of a match where NO driver is in control.
   */
  public Command getAutonomousCommand() {
    return null;
  }
}
