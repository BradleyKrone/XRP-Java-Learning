package frc.robot.ctre.signals;

/**
 * Which direction counts as "positive" for a CANcoder sensor.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.signals.SensorDirectionValue}. It lets
 * you flip the sign of an encoder's reading without rewiring anything, just like on the real robot.
 */
public enum SensorDirectionValue {
  /** Counter-clockwise rotation produces an increasing position (the default). */
  CounterClockwise_Positive,
  /** Clockwise rotation produces an increasing position (reversed). */
  Clockwise_Positive
}
