/**
 * TurnDegrees is a COMMAND that rotates the robot a fixed number of degrees in place
 * using the onboard gyroscope for feedback.
 *
 * <p>Instead of spinning for a fixed amount of time (which varies with battery voltage),
 * this command watches the gyro heading and stops as soon as the robot has turned the
 * requested angle.
 *
 * <p>How it works:
 *   1. initialize() snapshots the current gyro heading as the starting point.
 *      Adding the requested degrees to that gives the target heading we want to reach.
 *   2. execute() spins the robot in the correct direction at a constant turn speed.
 *   3. isFinished() checks whether the gyro heading has reached (or passed) the target.
 *   4. end() stops the motors.
 *
 * <p>Pass a POSITIVE value to turn clockwise (right), a NEGATIVE value to turn
 * counterclockwise (left).
 */

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.AutoConstants;
import frc.robot.subsystems.Drivetrain;


public class TurnDegrees extends Command {

  // The drivetrain subsystem this command controls (motors + gyro).
  private final Drivetrain m_drivetrain;

  // How many degrees to turn. Positive = clockwise (right), negative = counterclockwise (left).
  private final double m_degrees;

  // The gyro heading at the moment this command started (captured in initialize()).
  private double m_startHeading;

  // +1.0 for a right turn, -1.0 for a left turn (set in initialize()).
  private double m_turnDir;

  /**
   * Constructor — called once when the command object is created.
   *
   * @param drivetrain  The drivetrain subsystem to control.
   * @param degrees     Degrees to turn. Positive = clockwise, negative = counterclockwise.
   *                    For a 180-degree spin use 180 (see AutoConstants.AUTO_TURN_DEGREES).
   */
  public TurnDegrees(Drivetrain drivetrain, double degrees) {
    m_drivetrain = drivetrain;
    m_degrees    = degrees;

    addRequirements(drivetrain);
  }

  /**
   * initialize() runs ONCE when the command first starts.
   *
   * <p>We snapshot the CURRENT heading here (not in the constructor) because the robot
   * may have already turned by the time this command actually starts running inside a
   * sequential group. Adding the desired rotation to the live heading gives us the
   * exact angle we need to reach.
   */
  @Override
  public void initialize() {
    // Snapshot the heading NOW so isFinished() can measure how far we've turned from this point.
    m_startHeading = m_drivetrain.getHeading();

    // Math.signum gives +1.0 for positive degrees (right turn) and -1.0 for negative (left turn).
    m_turnDir = Math.signum(m_degrees);
  }

  /**
   * execute() runs EVERY 20ms while the command is active.
   * We spin in place — no forward component — at the configured autonomous turn speed.
   * m_turnDir ensures the spin is in the correct direction.
   */
  @Override
  public void execute() {
    m_drivetrain.arcadeDrive(0, AutoConstants.AUTO_TURN_SPEED * m_turnDir);
  }

  /**
   * isFinished() tells the scheduler when the turn is complete.
   *
   * <p>We measure the ABSOLUTE angle turned from the start, regardless of which direction
   * the gyro heading accumulates. This avoids the bug where the heading sign convention
   * is opposite to what the turn direction expects, causing the command to spin forever.
   *
   * @return true when the robot has rotated at least the requested number of degrees.
   */
  @Override
  public boolean isFinished() {
    double angleTurned = Math.abs(m_drivetrain.getHeading() - m_startHeading);
    return angleTurned >= Math.abs(m_degrees);
  }

  /**
   * end() runs ONCE when the command stops — whether it finished or was interrupted.
   * Always stop the motors here so the robot does not keep spinning.
   *
   * @param interrupted true if cancelled by another command (e.g. X button released mid-turn).
   */
  @Override
  public void end(boolean interrupted) {
    m_drivetrain.stop();
  }
}
