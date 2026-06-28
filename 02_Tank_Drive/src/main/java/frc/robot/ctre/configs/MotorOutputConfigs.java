package frc.robot.ctre.configs;

import frc.robot.ctre.signals.InvertedValue;
import frc.robot.ctre.signals.NeutralModeValue;

/**
 * The "motor output" settings of a TalonFX: which direction is positive, and what to do when
 * stopped.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.configs.MotorOutputConfigs}. You almost
 * never create this directly — you reach it through a {@link TalonFXConfiguration}:
 *
 * <pre>
 *   var config = new TalonFXConfiguration();
 *   config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
 *   config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
 *   motor.getConfigurator().apply(config);
 * </pre>
 *
 * <p>The public fields are capitalized to match the CTRE style.
 */
public class MotorOutputConfigs {
  /** Which spin direction counts as positive. Defaults to {@code CounterClockwise_Positive}. */
  public InvertedValue Inverted = InvertedValue.CounterClockwise_Positive;

  /** What the motor does when stopped (coast or brake). Defaults to {@code Coast}. */
  public NeutralModeValue NeutralMode = NeutralModeValue.Coast;

  /**
   * Sets the invert direction and returns this object, so settings can be chained.
   *
   * @param value the direction that should count as positive
   * @return this configs object
   */
  public MotorOutputConfigs withInverted(InvertedValue value) {
    Inverted = value;
    return this;
  }

  /**
   * Sets the neutral mode and returns this object, so settings can be chained.
   *
   * @param value coast or brake
   * @return this configs object
   */
  public MotorOutputConfigs withNeutralMode(NeutralModeValue value) {
    NeutralMode = value;
    return this;
  }
}
