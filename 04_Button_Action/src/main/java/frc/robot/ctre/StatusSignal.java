package frc.robot.ctre;

import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;

/**
 * A live reading from a CTRE device — for example a motor's position, a CANcoder's velocity, or the
 * Pigeon's yaw angle.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.StatusSignal}. On the real robot a
 * signal is a value that the device keeps sending over the CAN bus. You almost always read it one
 * of two ways:
 *
 * <pre>
 *   double rotations = motor.getPosition().getValueAsDouble();   // a plain number
 *   Angle  angle     = motor.getPosition().getValue();           // a number WITH units
 * </pre>
 *
 * <p>On the XRP there is no CAN bus, so the signal simply reads the current value straight from the
 * XRP hardware every time you ask. The value is always up to date, so {@link #refresh()} does
 * nothing here (it exists only so your code matches the real robot).
 *
 * @param <T> the WPILib unit type of this signal, such as {@code Angle} or {@code AngularVelocity}
 */
public class StatusSignal<T> {
  /** Where the raw number comes from (read fresh from the XRP each call). */
  private final DoubleSupplier m_value;

  /** Wraps the raw number in its WPILib unit (for example, "5.0" becomes "5 Rotations"). */
  private final DoubleFunction<T> m_asUnit;

  /** A short name for this signal, e.g. "Position" — handy when printing for debugging. */
  private final String m_name;

  /**
   * Creates a signal. This is called by the wrapper's hardware classes (TalonFX, CANcoder,
   * Pigeon2); you do not create one yourself.
   *
   * @param name a short label for the signal
   * @param value reads the current raw value from the XRP hardware
   * @param asUnit turns that raw value into a WPILib unit object
   */
  public StatusSignal(String name, DoubleSupplier value, DoubleFunction<T> asUnit) {
    m_name = name;
    m_value = value;
    m_asUnit = asUnit;
  }

  /**
   * The current value as a plain {@code double}.
   *
   * <p>The number's meaning depends on the signal: rotations for a position, rotations per second
   * for a velocity, volts for a voltage, degrees for the Pigeon's yaw, and so on.
   *
   * @return the current value with no units attached
   */
  public double getValueAsDouble() {
    return m_value.getAsDouble();
  }

  /**
   * The current value as a WPILib unit object (for example an {@code Angle} or {@code Voltage}).
   *
   * <p>Use this when you want the type system to track units for you. Otherwise
   * {@link #getValueAsDouble()} is simpler.
   *
   * @return the current value with its units attached
   */
  public T getValue() {
    return m_asUnit.apply(m_value.getAsDouble());
  }

  /**
   * Refreshes the signal from the device.
   *
   * <p>On a real robot this grabs the newest value off the CAN bus. On the XRP the value is already
   * read fresh on every call, so this does nothing and simply returns the same signal so calls can
   * be chained, exactly like Phoenix 6.
   *
   * @return this signal
   */
  public StatusSignal<T> refresh() {
    return this;
  }

  /**
   * The short name of this signal (for example "Position").
   *
   * @return the signal name
   */
  public String getName() {
    return m_name;
  }

  @Override
  public String toString() {
    return m_name + ": " + getValueAsDouble();
  }
}
