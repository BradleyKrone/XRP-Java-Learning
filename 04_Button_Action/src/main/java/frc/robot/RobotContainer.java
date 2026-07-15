/**
 * RobotContainer is the "wiring hub" of the robot program.
 * The default tank drive is already wired. Your job is to add one button action.
 */

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.TankDrive;
import frc.robot.subsystems.Drivetrain;


public class RobotContainer {

  private final CommandXboxController m_controller =
      new CommandXboxController(Constants.ControllerConstants.DRIVER_CONTROLLER_PORT);

  private final Drivetrain m_drivetrain = new Drivetrain();

  public RobotContainer() {
    configureButtonBindings();
  }

  private void configureButtonBindings() {
    // Default command: tank drive with the joysticks (already done).
    m_drivetrain.setDefaultCommand(
        new TankDrive(
            m_drivetrain,
            () -> m_controller.getLeftY(),
            () -> m_controller.getRightY()));

    // =========================================================================
    // TODO (Tutorial 4 — Add a Button Action)
    //
    // While the X button is held, spin in place.
    // When the button is released, the default command takes over again.
    //
    //   m_controller.x().whileTrue(
    //       new RunCommand(
    //           () -> m_drivetrain.tankDrive(0.5, -0.5),
    //           m_drivetrain));
    // =========================================================================

    // =========================================================================
  }

  public Command getAutonomousCommand() {
    return null;
  }
}
