package frc.robot.ctre.signals;

/**
 * What a motor should do when it is told to stop (output = 0).
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.signals.NeutralModeValue} so the code
 * you write on the XRP looks exactly like the code you will write on a real Kraken X60 motor.
 *
 * <ul>
 *   <li>{@link #Coast} — the motor spins freely and coasts to a stop.
 *   <li>{@link #Brake} — the motor resists motion and stops quickly.
 * </ul>
 *
 * <p><b>XRP note:</b> the small XRP motors have no brake circuit, so {@code Brake} behaves the same
 * as {@code Coast} on the XRP. The setting is still remembered so your code is identical to the
 * real robot — on a real Kraken, {@code Brake} really does brake.
 */
public enum NeutralModeValue {
  /** Let the motor coast freely to a stop. */
  Coast,
  /** Actively brake the motor to a quick stop (no-op on XRP hardware). */
  Brake
}
