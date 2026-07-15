package frc.robot.ctre.signals;

/**
 * Which direction counts as "positive" for a motor.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.signals.InvertedValue}. On a real
 * robot you choose the direction so that a positive output drives the wheel the way you expect.
 *
 * <p>On the XRP the right drive motor is mounted backwards, so it must be set to
 * {@link #Clockwise_Positive} for the robot to drive straight when both motors get a positive
 * command — exactly like you would invert a motor on the competition robot.
 */
public enum InvertedValue {
  /** Positive output spins the motor counter-clockwise (the default, no inversion). */
  CounterClockwise_Positive,
  /** Positive output spins the motor clockwise (inverted). */
  Clockwise_Positive
}
