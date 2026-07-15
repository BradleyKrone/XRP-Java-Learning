package frc.robot.ctre.configs;

/**
 * The full set of settings for a CANcoder.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.configs.CANcoderConfiguration}. This
 * teaching wrapper includes the group you need first: {@link #MagnetSensor} (sensor direction).
 *
 * <pre>
 *   var config = new CANcoderConfiguration();
 *   config.MagnetSensor.SensorDirection = SensorDirectionValue.Clockwise_Positive;
 *   encoder.getConfigurator().apply(config);
 * </pre>
 */
public class CANcoderConfiguration {
  /** Direction settings for the sensor. */
  public MagnetSensorConfigs MagnetSensor = new MagnetSensorConfigs();
}
