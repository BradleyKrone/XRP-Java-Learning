/**
 * RobotContainer is the "wiring hub" of the robot program.
 * This is where subsystems, controllers, and commands are connected together.
 */

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.TankDrive;
import frc.robot.subsystems.Drivetrain;


public class RobotContainer {

  // =========================================================================
  // TODO (Tutorial 3 — Step 1): Create the controller and drivetrain objects.
  //
  //   private final CommandXboxController m_controller =
  //       new CommandXboxController(Constants.ControllerConstants.DRIVER_CONTROLLER_PORT);
  //
  //   private final Drivetrain m_drivetrain = new Drivetrain();
  // =========================================================================

  // =========================================================================

  public RobotContainer() {
    configureButtonBindings();
  }

  private void configureButtonBindings() {
    // =========================================================================
    // TODO (Tutorial 3 — Step 2): Set the drivetrain's default command.
    //
    //   m_drivetrain.setDefaultCommand(
    //       new TankDrive(
    //           m_drivetrain,
    //           () -> m_controller.getLeftY(),
    //           () -> m_controller.getRightY()));
    // =========================================================================

    // =========================================================================
  }

  public Command getAutonomousCommand() {
    return null;
  }
}
