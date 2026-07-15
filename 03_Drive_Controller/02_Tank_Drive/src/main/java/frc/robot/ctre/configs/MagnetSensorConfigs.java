package frc.robot.ctre.configs;

import frc.robot.ctre.signals.SensorDirectionValue;

/**
 * The "magnet sensor" settings of a CANcoder — mainly which direction counts as positive.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.configs.MagnetSensorConfigs}. (A real
 * CANcoder senses a rotating magnet, which is where the name comes from.) You reach it through a
 * {@link CANcoderConfiguration}:
 *
 * <pre>
 *   var config = new CANcoderConfiguration();
 *   config.MagnetSensor.SensorDirection = SensorDirectionValue.Clockwise_Positive;
 *   encoder.getConfigurator().apply(config);
 * </pre>
 */
public class MagnetSensorConfigs {
  /** Which spin direction counts as positive. Defaults to {@code CounterClockwise_Positive}. */
  public SensorDirectionValue SensorDirection = SensorDirectionValue.CounterClockwise_Positive;

  /**
   * Sets the sensor direction and returns this object, so settings can be chained.
   *
   * @param value the direction that should count as positive
   * @return this configs object
   */
  public MagnetSensorConfigs withSensorDirection(SensorDirectionValue value) {
    SensorDirection = value;
    return this;
  }
}
