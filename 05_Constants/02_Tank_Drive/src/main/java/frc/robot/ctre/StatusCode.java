package frc.robot.ctre;

/**
 * The result of an action on a CTRE device (for example, applying a configuration).
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.StatusCode}. The real enum has dozens
 * of error codes for things that can go wrong on a CAN bus. The XRP does not use a CAN bus, so our
 * wrapper only ever returns {@link #OK} — but methods still return a {@code StatusCode} so your
 * code reads the same as it will on the competition robot.
 */
public enum StatusCode {
  /** The action completed successfully. */
  OK;

  /**
   * Whether the action succeeded.
   *
   * @return true on the XRP (actions always succeed)
   */
  public boolean isOK() {
    return this == OK;
  }
}
