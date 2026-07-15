/**
 * RangeSensor is a SUBSYSTEM — it owns and reads the ultrasonic rangefinder on the robot.
 *
 * <p>The XRP's rangefinder outputs an analog voltage between 0V and 5V:
 *   - 0V = ~20 mm away (very close)
 *   - 5V = ~4000 mm away (far)
 *
 * <p>We convert that voltage to millimeters with a simple linear formula, then expose
 * an isBlocked() method so commands can easily check whether something is in the way.
 *
 * <p>Note: unlike motor or servo subsystems, nothing "requires" this subsystem
 * exclusively — sensors can be read by any command at the same time.
 */

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.RangeConstants;


public class RangeSensor extends SubsystemBase {

  // AnalogInput reads the voltage (0–5V) from the ultrasonic sensor's output pin.
  // The port number is the analog channel on the XRP board (channel 2 = rangefinder).
  private final AnalogInput m_sensor;

  /**
   * Constructor — runs once at robot startup to create and configure the sensor.
   */
  public RangeSensor() {
    m_sensor = new AnalogInput(RangeConstants.RANGE_SENSOR_PORT);
  }

  /**
   * Reads the sensor voltage and converts it to a distance in millimeters.
   *
   * <p>The conversion is a straight-line (linear) mapping between the two known endpoints:
   *   - At 0V the object is 20mm away.
   *   - At 5V the object is 4000mm away.
   *
   * <p>Formula: distance = 20 + (voltage / 5.0) × (4000 − 20)
   *
   * @return  Distance to the nearest object in millimeters.
   */
  public double getDistanceMM() {
    double voltage = m_sensor.getVoltage();
    // Linear interpolation from voltage (0–5V) to distance (20–4000mm).
    return 20.0 + (voltage / 5.0) * (4000.0 - 20.0);
  }

  /**
   * Returns true when something is close enough to count as "blocking" the robot.
   * The threshold distance is defined in Constants so it is easy to tune.
   *
   * @return  true if an obstacle is within BLOCKED_DISTANCE_MM millimeters.
   */
  public boolean isBlocked() {
    return getDistanceMM() < RangeConstants.BLOCKED_DISTANCE_MM;
  }

  /**
   * periodic() is called every 20 ms by the CommandScheduler.
   * Nothing needs to happen each loop for a read-only sensor.
   */
  @Override
  public void periodic() {}
}
