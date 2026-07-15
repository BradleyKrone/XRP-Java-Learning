/**
 * WiggleDrive is a COMMAND — it describes one specific robot action.
 *
 * <p>This command makes the robot wiggle back and forth in place by alternating between
 * turning left and turning right every 250 milliseconds. It is designed to run while
 * the A button is held down, and stops automatically when the button is released.
 *
 * <p>How the timing works:
 *   - We start a timer when the command begins (initialize).
 *   - Each loop, we divide the elapsed time into repeating 500ms "wiggle cycles".
 *   - The first 250ms of each cycle = turn left.
 *   - The last  250ms of each cycle = turn right.
 *   - This repeats until the button is released and the command is interrupted.
 */

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.OIConstants;
import frc.robot.Constants.WiggleConstants;
import frc.robot.subsystems.Drivetrain;


public class WiggleDrive extends Command {

  // The drivetrain subsystem this command controls.
  private final Drivetrain m_drivetrain;

  // The Xbox controller so we can read the left joystick for forward/backward movement.
  private final CommandXboxController m_controller;

  // WPILib Timer tracks elapsed time in seconds, letting us know which phase of the
  // wiggle cycle we are in without using Thread.sleep() (which would block the robot loop).
  private final Timer m_timer = new Timer();

  /**
   * Constructor — called once when the command object is created.
   *
   * @param drivetrain  The drivetrain subsystem to control.
   * @param controller  The Xbox controller to read forward speed from.
   */
  public WiggleDrive(Drivetrain drivetrain, CommandXboxController controller) {
    m_drivetrain = drivetrain;
    m_controller = controller;

    // addRequirements tells the CommandScheduler that this command "owns" the drivetrain.
    // If ArcadeDrive is currently running (it's the default command), this will interrupt it
    // the moment WiggleDrive starts. When WiggleDrive ends, ArcadeDrive will resume automatically.
    addRequirements(drivetrain);
  }

  /**
   * initialize() runs ONCE when the command first starts (A button pressed).
   * We restart the timer here so our phase tracking always starts fresh from zero.
   */
  @Override
  public void initialize() {
    // restart() resets the timer to zero AND starts it running immediately.
    m_timer.restart();
  }

  /**
   * execute() runs EVERY 20ms while the command is active (button still held).
   * This is where the wiggle logic lives.
   */
  @Override
  public void execute() {
    // Get how many seconds have passed since initialize().
    double elapsed = m_timer.get();

    // Read left joystick Y axis for forward/backward speed.
    // Y axis is inverted by WPILib convention — negate it so push-forward = positive.
    double forward = -m_controller.getLeftY();

    // Ignore tiny joystick values that are just drift noise (same deadband as ArcadeDrive).
    if (Math.abs(forward) < OIConstants.JOYSTICK_DEADBAND) {
      forward = 0.0;
    }

    // Use modulo (%) to find where we are within the current wiggle cycle.
    //   e.g. at 0.60s: 0.60 % 0.50 = 0.10 → first half → turn left
    //   e.g. at 0.85s: 0.85 % 0.50 = 0.35 → second half → turn right
    double positionInCycle = elapsed % WiggleConstants.WIGGLE_CYCLE_SECONDS;

    if (positionInCycle < WiggleConstants.WIGGLE_HALF_CYCLE_SECONDS) {
      // First 250ms of the cycle: turn left (negative turn value = left in arcade drive).
      m_drivetrain.arcadeDrive(forward, -WiggleConstants.WIGGLE_TURN_SPEED);
    } else {
      // Last 250ms of the cycle: turn right (positive turn value = right in arcade drive).
      m_drivetrain.arcadeDrive(forward, WiggleConstants.WIGGLE_TURN_SPEED);
    }
  }

  /**
   * isFinished() tells the scheduler when this command is done.
   * Returning false means "never stop on my own" — the command only ends when the
   * button is released (which cancels it externally via whileTrue binding).
   */
  @Override
  public boolean isFinished() {
    return false;
  }

  /**
   * end() runs ONCE when the command stops — whether it finished normally or was interrupted.
   * The interrupted parameter is true when another command cancelled us (including releasing
   * the button). We always stop the motors here so the robot does not keep driving.
   *
   * @param interrupted true if the command was cancelled, false if it finished normally.
   */
  @Override
  public void end(boolean interrupted) {
    m_drivetrain.stop();
  }
}
