package frc.robot.ctre.controls;

/**
 * A "control request" — a small object that describes HOW you want a motor to run.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.controls.ControlRequest}. Instead of
 * calling many different methods on the motor, Phoenix 6 has you build a request object and hand it
 * to the motor:
 *
 * <pre>
 *   motor.setControl(new DutyCycleOut(0.5));   // run at 50% power
 *   motor.setControl(new VoltageOut(6.0));     // run at 6 volts
 *   motor.setControl(new NeutralOut());        // stop
 * </pre>
 *
 * <p>This interface is just a common type so that {@code setControl(...)} can accept any kind of
 * request. The known kinds are {@link DutyCycleOut}, {@link VoltageOut}, and {@link NeutralOut}.
 */
public interface ControlRequest {}
