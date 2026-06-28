package frc.robot.ctre.hardware;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.xrp.XRPGyro;
import frc.robot.ctre.StatusSignal;

/**
 * A CTRE-style IMU (gyro) that secretly reads the XRP's built-in gyro.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.hardware.Pigeon2}. You create and use it
 * exactly like the real thing:
 *
 * <pre>
 *   Pigeon2 imu = new Pigeon2(0);
 *   double heading = imu.getYaw().getValueAsDouble();   // degrees
 *   imu.setYaw(0);                                       // call this heading "zero"
 * </pre>
 *
 * <p>The XRP has exactly one built-in gyro, so the device ID is accepted (to match the real robot)
 * but otherwise ignored.
 *
 * <p>"Yaw" is the heading (spinning left/right, the angle you care about most for driving). "Pitch"
 * is tipping forward/back and "roll" is tipping side to side.
 *
 * <p><b>When you move to the real robot:</b> change the import from
 * {@code frc.robot.ctre.hardware.Pigeon2} to {@code com.ctre.phoenix6.hardware.Pigeon2}.
 */
public class Pigeon2 {
  /** The underlying XRP gyro. */
  private final XRPGyro m_gyro;

  /** Added to the raw yaw so {@link #setYaw(double)} can move the zero heading. */
  private double m_yawOffset;

  /**
   * Creates an IMU. The device ID is accepted to match the real robot but ignored (the XRP has only
   * one gyro).
   *
   * @param deviceId the CTRE-style device ID (ignored on the XRP)
   */
  public Pigeon2(int deviceId) {
    m_gyro = new XRPGyro();
  }

  /**
   * Creates an IMU. The device ID and {@code canbus} name are accepted to match the real robot but
   * ignored on the XRP.
   *
   * @param deviceId the CTRE-style device ID (ignored)
   * @param canbus the CAN bus name (ignored)
   */
  public Pigeon2(int deviceId, String canbus) {
    this(deviceId);
  }

  /**
   * The heading (yaw), in degrees. Increases as the robot turns counter-clockwise.
   *
   * @return a live signal carrying the yaw in degrees
   */
  public StatusSignal<Angle> getYaw() {
    return new StatusSignal<>("Yaw", () -> m_gyro.getAngleZ() + m_yawOffset, Degrees::of);
  }

  /**
   * The pitch (tipping forward/back), in degrees.
   *
   * @return a live signal carrying the pitch in degrees
   */
  public StatusSignal<Angle> getPitch() {
    return new StatusSignal<>("Pitch", m_gyro::getAngleY, Degrees::of);
  }

  /**
   * The roll (tipping side to side), in degrees.
   *
   * @return a live signal carrying the roll in degrees
   */
  public StatusSignal<Angle> getRoll() {
    return new StatusSignal<>("Roll", m_gyro::getAngleX, Degrees::of);
  }

  /**
   * How fast the heading is changing, in degrees per second.
   *
   * @return a live signal carrying the turn rate in degrees per second
   */
  public StatusSignal<AngularVelocity> getAngularVelocityZWorld() {
    return new StatusSignal<>("AngularVelocityZWorld", m_gyro::getRateZ, DegreesPerSecond::of);
  }

  /**
   * The heading as a {@link Rotation2d}, convenient for odometry and drive math.
   *
   * @return the current heading
   */
  public Rotation2d getRotation2d() {
    return Rotation2d.fromDegrees(m_gyro.getAngleZ() + m_yawOffset);
  }

  /**
   * Sets the current heading to a specific value (in degrees) without physically turning the robot.
   *
   * @param yawDegrees the heading the robot should now report
   */
  public void setYaw(double yawDegrees) {
    m_yawOffset = yawDegrees - m_gyro.getAngleZ();
  }

  /** Resets the heading so that the current direction becomes zero degrees. */
  public void reset() {
    m_gyro.reset();
    m_yawOffset = 0;
  }
}
