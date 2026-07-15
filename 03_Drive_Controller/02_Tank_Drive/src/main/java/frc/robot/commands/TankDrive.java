package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Drivetrain;
import java.util.function.DoubleSupplier;

/**
 * TankDrive reads the driver's joystick inputs and passes them to the
 * Drivetrain every robot loop (50 times per second).
 *
 * <p>A command is one action the robot can perform. Commands have four lifecycle
 * methods: initialize(), execute(), isFinished(), and end().
 * The scheduler calls execute() over and over until isFinished() returns true.
 */
public class TankDrive extends Command {

  private final Drivetrain m_drivetrain;
  private final DoubleSupplier m_leftSpeed;
  private final DoubleSupplier m_rightSpeed;

  /**
   * Creates a new TankDrive command.
   *
   * @param drivetrain  The drivetrain subsystem to drive.
   * @param leftSpeed   Supplies the left joystick Y-axis value each loop.
   * @param rightSpeed  Supplies the right joystick Y-axis value each loop.
   */
  public TankDrive(Drivetrain drivetrain, DoubleSupplier leftSpeed, DoubleSupplier rightSpeed) {
    m_drivetrain = drivetrain;
    m_leftSpeed = leftSpeed;
    m_rightSpeed = rightSpeed;
    // This tells the scheduler: "this command uses the drivetrain."
    // If another command also needs it, this one will be interrupted.
    addRequirements(drivetrain);
  }

  // execute() runs 50 times per second while this command is active.
  @Override
  public void execute() {
    m_drivetrain.tankDrive(m_leftSpeed.getAsDouble(), m_rightSpeed.getAsDouble());
  }

  // Returning false means "keep running until interrupted."
  // That's what we want for a drive command — run the whole match.
  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public void end(boolean interrupted) {
    m_drivetrain.stop();
  }
}
