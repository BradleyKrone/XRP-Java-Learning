package frc.robot.ctre.controls;

/**
 * Run the motor at a specific voltage.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.controls.VoltageOut}. Driving by
 * voltage is more consistent than driving by percent power, because it does not change as the
 * battery drains. A full battery is about 12 volts.
 *
 * <pre>
 *   motor.setControl(new VoltageOut(6.0));   // run at 6 volts (about half power on a full battery)
 * </pre>
 *
 * <p><b>XRP note:</b> the XRP motor only understands percent power, so the wrapper converts your
 * requested voltage into a percent using the current battery voltage. It is close, but not as exact
 * as the real Kraken's built-in voltage control.
 */
public class VoltageOut implements ControlRequest {
  /** The requested voltage, in volts. */
  public double Output;

  /**
   * Creates a request to run at the given voltage.
   *
   * @param outputVolts the voltage to apply, in volts
   */
  public VoltageOut(double outputVolts) {
    Output = outputVolts;
  }

  /**
   * Sets the voltage and returns this request, so you can build and pass it in one line.
   *
   * @param outputVolts the voltage to apply, in volts
   * @return this request
   */
  public VoltageOut withOutput(double outputVolts) {
    Output = outputVolts;
    return this;
  }
}
