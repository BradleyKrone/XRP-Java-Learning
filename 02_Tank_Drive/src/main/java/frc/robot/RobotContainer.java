/**
 * RobotContainer is the "wiring hub" of the robot program.
 * This is where you connect everything together:
 *   - create your subsystems (the parts of the robot - see the subsystems folder)
 *   - create your controllers (Xbox controller, joystick, etc.)
 *   - say which button runs which command (see the commands folder)
 */

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
// import CommandXboxController class
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
// import the subsystems
import frc.robot.subsystems.IntakeMotor;
import frc.robot.subsystems.ShooterMotor;

// import the commands
import frc.robot.commands.IntakeState;


public class RobotContainer {

  // define xbox controller
  private final CommandXboxController m_controller = new CommandXboxController(Constants.DriverControllerConstants.DRIVER_CONTROLLER_PORT);
  // define intake subsystem
  private final IntakeMotor m_intakeMotor = new IntakeMotor();

  /**
   * Constructor — runs ONCE when the robot program first starts.
   * Think of it as the "setup" step.
   * Put any one-time initialization here (creating subsystems, creating controllers, etc.)
   * and call configureButtonBindings() so buttons are ready to use.
   */
  public RobotContainer() {
    configureButtonBindings();

  }

  /**
   * This is where you map controller buttons to commands.
   * Each line inside here says: "when the driver presses THIS button, run THAT command."
   */
  private void configureButtonBindings() {

    // While A is held, run the intake motor. When released, it stops automatically.
    m_controller.a().whileTrue(new IntakeState(m_intakeMotor));
  }

  /**
   * Returns the command the robot should run during the Autonomous period.
   * Autonomous = the first 15 seconds of a match where NO driver is in control.
   */
  public Command getAutonomousCommand() {
    return null;
  }
}
