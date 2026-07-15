/**
 * ServoSubsystem is a SUBSYSTEM — it owns and controls the servo motor on the robot.
 *
 * <p>A subsystem wraps the physical hardware (here, one XRP servo) and exposes simple
 * methods for commands to call. Only one command may use this subsystem at a time;
 * the CommandScheduler enforces that rule automatically.
 */

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.xrp.XRPServo;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class ServoSubsystem extends SubsystemBase {

  // XRPServo controls one physical servo on the XRP robot board.
  // m_ prefix is the FRC convention for member variables.
  private final XRPServo m_servo;

  /**
   * Constructor — runs once when the ServoSubsystem object is created (at robot startup).
   *
   * <p>Accepting the port as a parameter lets RobotContainer create one ServoSubsystem
   * for Servo 1 (port 4) and another for Servo 2 (port 5) without needing a second class.
   *
   * @param port  The hardware channel the servo is connected to (4 = Servo 1, 5 = Servo 2).
   */
  public ServoSubsystem(int port) {
    m_servo = new XRPServo(port);
  }

  /**
   * Moves the servo to a specific angle.
   *
   * <p>The XRP servo accepts angles from 0 to 180 degrees.
   *   - 0°   = fully counterclockwise
   *   - 90°  = centered / neutral
   *   - 180° = fully clockwise
   *
   * @param angle  Target position in degrees. Should be between 0.0 and 180.0.
   */
  public void setAngle(double angle) {
    m_servo.setAngle(angle);
  }

  /**
   * Returns the servo to the center (neutral) position.
   * Called when the wiggle command ends so the servo does not stay at an odd angle.
   */
  public void center() {
    // 90° is the midpoint of the 0–180 degree range — the natural resting position.
    m_servo.setAngle(90.0);
  }

  /**
   * periodic() is called automatically by the CommandScheduler every 20 milliseconds.
   * Nothing needs to update each loop for a simple servo subsystem.
   */
  @Override
  public void periodic() {
    // Nothing needed here yet.
  }
}
