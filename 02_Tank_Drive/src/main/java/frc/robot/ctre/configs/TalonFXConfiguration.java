package frc.robot.ctre.configs;

/**
 * The full set of settings for a TalonFX motor.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.configs.TalonFXConfiguration}. On a
 * real robot this holds many groups of settings (current limits, PID gains, and more). This
 * teaching wrapper includes the group you need first: {@link #MotorOutput} (direction and neutral
 * mode). More groups can be added later in the same style.
 *
 * <p>Typical use:
 *
 * <pre>
 *   var config = new TalonFXConfiguration();
 *   config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
 *   motor.getConfigurator().apply(config);
 * </pre>
 */
public class TalonFXConfiguration {
  /** Direction and neutral-mode settings for the motor. */
  public MotorOutputConfigs MotorOutput = new MotorOutputConfigs();
}
