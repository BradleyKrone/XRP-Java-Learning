/**
 * DriveDistance is a COMMAND that drives the robot a fixed distance using encoder feedback.
 *
 * <p>Instead of driving for a fixed amount of time (which is unreliable — speed can vary),
 * this command measures actual wheel rotation with the encoders and stops when the robot
 * has traveled the requested distance.
 *
 * <p>How it works:
 *   1. initialize() resets the encoders to zero so we measure ONLY the distance from this
 *      command's start, not total distance since the robot powered on.
 *   2. execute() drives at a constant speed in the correct direction every 20ms.
 *   3. isFinished() stops early if the range sensor detects an obstacle, OR when the
 *      target encoder distance is reached — whichever comes first.
 *   4. end() stops the motors so the robot does not keep rolling.
 *
 * <p>Pass a POSITIVE value to drive forward, a NEGATIVE value to drive backward.
 * To use without range sensing, call the two-argument constructor.
 */

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.AutoConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.RangeSensor;


public class DriveDistance extends Command {

  // The drivetrain subsystem this command controls (motors + encoders).
  private final Drivetrain m_drivetrain;

  // How far to travel in meters. Positive = forward, negative = backward.
  private final double m_meters;

  // Optional range sensor. When non-null, isFinished() also checks sensor distance.
  // null means "no range checking" — used when the sensor is not wired or not needed.
  private final RangeSensor m_rangeSensor;

  // Stop early if the range sensor reads closer than this many millimeters.
  // Only used when m_rangeSensor is not null.
  private final double m_stopDistanceMM;

  /**
   * Constructor WITHOUT range sensing — drives the full distance using encoders only.
   *
   * @param drivetrain  The drivetrain subsystem to control.
   * @param meters      Distance to travel in meters. Positive = forward, negative = backward.
   */
  public DriveDistance(Drivetrain drivetrain, double meters) {
    this(drivetrain, meters, null, Double.MAX_VALUE);
  }

  /**
   * Constructor WITH range sensing — stops early if an obstacle is detected.
   *
   * @param drivetrain      The drivetrain subsystem to control.
   * @param meters          Distance to travel in meters. Positive = forward, negative = backward.
   * @param rangeSensor     The range sensor to monitor for obstacles.
   * @param stopDistanceMM  Stop early if the sensor reads closer than this many millimeters.
   */
  public DriveDistance(Drivetrain drivetrain, double meters,
                       RangeSensor rangeSensor, double stopDistanceMM) {
    m_drivetrain    = drivetrain;
    m_meters        = meters;
    m_rangeSensor   = rangeSensor;
    m_stopDistanceMM = stopDistanceMM;

    // addRequirements tells the CommandScheduler that this command "owns" the drivetrain.
    // Any command currently using the drivetrain (e.g. ArcadeDrive) will be interrupted.
    // RangeSensor is NOT listed here — sensors can be read by multiple commands at once.
    addRequirements(drivetrain);
  }

  /**
   * initialize() runs ONCE when the command first starts.
   * We reset both encoders here so getAverageDistance() starts counting from 0,
   * measuring ONLY the distance traveled during this command.
   */
  @Override
  public void initialize() {
    m_drivetrain.resetEncoders();
  }

  /**
   * execute() runs EVERY 20ms while the command is active.
   * We drive at a constant autonomous speed in the correct direction.
   *
   * <p>Math.signum(m_meters) returns:
   *   +1.0 if m_meters is positive (drive forward)
   *   -1.0 if m_meters is negative (drive backward)
   * Multiplying by it gives the right direction without a separate if/else.
   */
  @Override
  public void execute() {
    m_drivetrain.arcadeDrive(AutoConstants.AUTO_DRIVE_SPEED * Math.signum(m_meters), 0);
  }

  /**
   * isFinished() tells the scheduler when this command is done.
   *
   * <p>Two conditions can end the drive — whichever triggers first:
   *   1. ENCODER: the robot has traveled the requested distance.
   *   2. RANGE SENSOR: an obstacle is closer than m_stopDistanceMM.
   *      When this happens the command ends early so the next command in the
   *      sequence (TurnDegrees) can immediately spin the robot around.
   *
   * @return true when either stopping condition is met.
   */
  @Override
  public boolean isFinished() {
    // Check range sensor first — obstacle detection takes priority over distance.
    if (m_rangeSensor != null && m_rangeSensor.getDistanceMM() < m_stopDistanceMM) {
      return true;
    }
    // Fall back to encoder distance.
    return Math.abs(m_drivetrain.getAverageDistance()) >= Math.abs(m_meters);
  }

  /**
   * end() runs ONCE when the command stops — whether it finished or was interrupted.
   * Always stop the motors here so the robot does not keep rolling.
   *
   * @param interrupted true if cancelled externally (e.g. driver touched a button).
   */
  @Override
  public void end(boolean interrupted) {
    m_drivetrain.stop();
  }
}
