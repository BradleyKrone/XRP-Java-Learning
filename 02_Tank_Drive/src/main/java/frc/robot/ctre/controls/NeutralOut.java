package frc.robot.ctre.controls;

/**
 * Stop the motor.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.controls.NeutralOut}. It tells the
 * motor to stop driving. What "stop" feels like depends on the motor's neutral mode (coast or
 * brake) — see {@code frc.robot.ctre.signals.NeutralModeValue}.
 *
 * <pre>
 *   motor.setControl(new NeutralOut());   // stop the motor
 * </pre>
 */
public class NeutralOut implements ControlRequest {
  /** Creates a request to stop the motor. */
  public NeutralOut() {}
}
