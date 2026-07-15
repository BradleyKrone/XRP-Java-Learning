package frc.robot.subsystems;

import edu.wpi.first.wpilibj.xrp.XRPMotor;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Drivetrain controls the left and right drive motors.
 *
 * <p>A subsystem represents one physical part of the robot. Only one command
 * can use this subsystem at a time.
 */
public class Drivetrain extends SubsystemBase {

  // These two objects represent the physical motors on the robot.
  // Channel 0 = left motor, Channel 1 = right motor.
  private final XRPMotor m_leftMotor = new XRPMotor(0);
  private final XRPMotor m_rightMotor = new XRPMotor(1);

  public Drivetrain() {
    // The right motor is mounted as a mirror image of the left.
    // Without this, both motors spin the same direction, and the robot spins in a circle
    // instead of going straight. Inverting the right motor fixes that.
    m_rightMotor.setInverted(true);
  }

  /**
   * Drives the robot using tank-style controls.
   *
   * @param leftSpeed  Speed for the left motor  (-1.0 to 1.0)
   * @param rightSpeed Speed for the right motor (-1.0 to 1.0)
   */
  public void tankDrive(double leftSpeed, double rightSpeed) {
    // The speed is capped at 0.75 so the robot isn't going full speed all the time.
    // In Tutorial 5 you will move this magic number into Constants.java.
    m_leftMotor.set(leftSpeed * 0.75);
    m_rightMotor.set(rightSpeed * 0.75);
  }

  /** Stops both motors. */
  public void stop() {
    m_leftMotor.set(0);
    m_rightMotor.set(0);
  }
}
