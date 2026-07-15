/**
 * RobotContainer is the "wiring hub" of the robot program.
 * Everything is wired. Your only task this tutorial is in Drivetrain.java and Constants.java.
 */

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.TankDrive;
import frc.robot.subsystems.Drivetrain;


public class RobotContainer {

  // The Xbox controller plugged into USB port 0 on the driver station.
  private final CommandXboxController m_controller =
      new CommandXboxController(Constants.ControllerConstants.DRIVER_CONTROLLER_PORT);

  // The drivetrain subsystem — controls the left and right motors.
  private final Drivetrain m_drivetrain = new Drivetrain();

  public RobotContainer() {
    configureButtonBindings();
  }

  private void configureButtonBindings() {
    // The "default command" runs whenever no other command is using the drivetrain.
    // Here we set it to TankDrive, so the driver always has control.
    m_drivetrain.setDefaultCommand(
        new TankDrive(
            m_drivetrain,
            () -> m_controller.getLeftY(),   // left joystick Y → left motors
            () -> m_controller.getRightY())); // right joystick Y → right motors
  }

  public Command getAutonomousCommand() {
    return null;
  }
}
