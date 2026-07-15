/**
 * WiggleServo is a COMMAND — it describes one specific robot action.
 *
 * <p>This command makes the servo wiggle back and forth by alternating between a left angle
 * and a right angle every 250 milliseconds. It is designed to run while the B button is
 * held down, and stops automatically when the button is released.
 *
 * <p>How the timing works:
 *   - We start a timer when the command begins (initialize).
 *   - Each loop, we divide the elapsed time into repeating 500ms "wiggle cycles".
 *   - The first 250ms of each cycle = move to SERVO_LEFT_ANGLE (e.g., 45°).
 *   - The last  250ms of each cycle = move to SERVO_RIGHT_ANGLE (e.g., 135°).
 *   - This repeats until the button is released and the command is interrupted.
 */

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ServoConstants;
import frc.robot.subsystems.ServoSubsystem;


public class WiggleServo extends Command {

  // The servo subsystem this command controls.
  private final ServoSubsystem m_servo;

  // WPILib Timer tracks elapsed time in seconds so we know which wiggle phase we are in,
  // without using Thread.sleep() (which would block the 20ms robot loop).
  private final Timer m_timer = new Timer();

  /**
   * Constructor — called once when the command object is created.
   *
   * @param servo  The servo subsystem to control.
   */
  public WiggleServo(ServoSubsystem servo) {
    m_servo = servo;

    // addRequirements tells the CommandScheduler that this command "owns" the servo subsystem.
    // If any other command is using the servo, it will be interrupted when WiggleServo starts.
    addRequirements(servo);
  }

  /**
   * initialize() runs ONCE when the command first starts (B button pressed).
   * We restart the timer here so phase tracking always starts fresh from zero.
   */
  @Override
  public void initialize() {
    // restart() resets the timer to zero AND starts it running immediately.
    m_timer.restart();
  }

  /**
   * execute() runs EVERY 20ms while the command is active (B button still held).
   * This is where the wiggle logic lives — it's the same modulo trick as WiggleDrive.
   */
  @Override
  public void execute() {
    // How many seconds have passed since initialize().
    double elapsed = m_timer.get();

    // Use modulo (%) to find where we are within the current wiggle cycle.
    //   e.g. at 0.60s: 0.60 % 0.50 = 0.10 → first half → left angle
    //   e.g. at 0.85s: 0.85 % 0.50 = 0.35 → second half → right angle
    double positionInCycle = elapsed % ServoConstants.SERVO_WIGGLE_CYCLE_SECONDS;

    if (positionInCycle < ServoConstants.SERVO_WIGGLE_HALF_CYCLE_SECONDS) {
      // First 250ms: move servo to the left angle.
      m_servo.setAngle(ServoConstants.SERVO_LEFT_ANGLE);
    } else {
      // Last 250ms: move servo to the right angle.
      m_servo.setAngle(ServoConstants.SERVO_RIGHT_ANGLE);
    }
  }

  /**
   * isFinished() tells the scheduler when this command is done on its own.
   * Returning false means "never stop by myself" — the command only ends when the
   * B button is released, which cancels it externally via the whileTrue() binding.
   */
  @Override
  public boolean isFinished() {
    return false;
  }

  /**
   * end() runs ONCE when the command stops — whether it finished or was interrupted.
   * We return the servo to the center (90°) so it does not stay tilted to one side.
   *
   * @param interrupted true if the command was cancelled, false if it finished normally.
   */
  @Override
  public void end(boolean interrupted) {
    m_servo.center();
  }
}
