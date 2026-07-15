package frc.robot.ctre.controls;

/**
 * Run the motor at a fraction of full power.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.controls.DutyCycleOut}. "Duty cycle"
 * just means "percent of full power", from {@code -1.0} (full reverse) through {@code 0.0} (stop)
 * to {@code +1.0} (full forward). This is the simplest way to drive a motor.
 *
 * <pre>
 *   motor.setControl(new DutyCycleOut(0.5));            // 50% forward
 *   motor.setControl(new DutyCycleOut(0).withOutput(-0.25)); // then change to 25% reverse
 * </pre>
 *
 * <p>Note the public field {@link #Output} is capitalized — that is the CTRE style, kept here so
 * your code matches the real robot.
 */
public class DutyCycleOut implements ControlRequest {
  /** The requested power, from -1.0 (full reverse) to +1.0 (full forward). */
  public double Output;

  /**
   * Creates a request to run at the given fraction of full power.
   *
   * @param output power from -1.0 to +1.0
   */
  public DutyCycleOut(double output) {
    Output = output;
  }

  /**
   * Sets the output and returns this request, so you can build and pass it in one line.
   *
   * @param output power from -1.0 to +1.0
   * @return this request
   */
  public DutyCycleOut withOutput(double output) {
    Output = output;
    return this;
  }
}
