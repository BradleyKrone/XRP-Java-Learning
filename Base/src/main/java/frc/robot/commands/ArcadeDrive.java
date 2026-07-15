/**
 * ArcadeDrive is a COMMAND — it represents the action of "driving the robot with a controller."
 *
 * <p>A command is a single robot action. It has four lifecycle methods the scheduler calls:
 *   1. initialize()  — runs ONCE when the command first starts.
 *   2. execute()     — runs EVERY 20 ms loop while the command is active.
 *   3. isFinished()  — checked every loop; return true to stop the command.
 *   4. end()         — runs ONCE when the command stops (normally or interrupted).
 *
 * <p>This is a "default command" — it runs continuously as long as no other command
 * needs the Drivetrain. The driver holds the joystick and the robot follows.
 *
 * <p>This command also implements HEADING HOLD (yaw-rate rejection):
 *   - While the driver is steering, things work normally.
 *   - When the driver releases the turn stick, a 100 ms debounce timer starts.
 *   - After 100 ms of no steering input, the current heading is "locked in."
 *   - While the driver presses forward with no turn input, a PID controller
 *     computes a small turn correction to keep the robot driving on that heading.
 */

package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.HeadingConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.RangeSensor;


public class ArcadeDrive extends Command {

  // The subsystem and controller this command uses.
  // They are passed in from RobotContainer so the command doesn't create them itself
  // (this pattern is called "dependency injection" — the command receives what it needs).
  private final Drivetrain             m_drivetrain;
  private final CommandXboxController  m_controller;

  // Read-only sensor used to detect obstacles ahead.
  // We do NOT addRequirements for this — sensors can be read by any command simultaneously.
  private final RangeSensor            m_rangeSensor;

  // --- Heading Hold (straight-line correction) ---

  // Measures how long the turn stick has been at zero.
  // When this exceeds HEADING_LOCK_DELAY_SECONDS, we lock the heading.
  private final Timer m_steeringIdleTimer = new Timer();

  // True once the heading has been locked and we are actively correcting.
  // Resets to false the moment the driver touches the turn stick again.
  private boolean m_headingLocked = false;

  // PID controller that calculates how much turn correction is needed.
  //
  // PIDController.calculate(measurement, setpoint) returns:
  //   output = KP × (setpoint - measurement)
  //            + KI × (accumulated error over time)
  //            + KD × (rate of change of error)
  //
  // "measurement" = current heading from the gyro.
  // "setpoint"    = the locked heading we want to stay on.
  // "output"      = turn correction to add to the driver's turn input.
  private final PIDController m_headingPid = new PIDController(
      HeadingConstants.HEADING_KP,
      HeadingConstants.HEADING_KI,
      HeadingConstants.HEADING_KD
  );

  /**
   * Constructor — called once in RobotContainer to create this command.
   *
   * @param drivetrain   The Drivetrain subsystem this command will control.
   * @param controller   The Xbox controller the driver uses.
   * @param rangeSensor  The ultrasonic range sensor used to detect obstacles.
   */
  public ArcadeDrive(Drivetrain drivetrain, CommandXboxController controller, RangeSensor rangeSensor) {
    m_drivetrain  = drivetrain;
    m_controller  = controller;
    m_rangeSensor = rangeSensor;

    // Tell the scheduler that this command needs the Drivetrain.
    // This means: if another command also needs the Drivetrain, this one will be
    // interrupted (cancelled) so the other command can run.
    addRequirements(m_drivetrain);
  }

  /**
   * initialize() — called once when the command starts (or restarts after being interrupted).
   *
   * We reset the heading hold state here so the robot always starts fresh.
   * If the driver wasn't steering when the command (re)starts, the debounce timer
   * will expire after 100 ms and lock a heading automatically.
   */
  @Override
  public void initialize() {
    // Start the idle timer fresh. It will count up from zero; once it exceeds
    // HEADING_LOCK_DELAY_SECONDS the heading will be locked.
    m_steeringIdleTimer.restart();

    // Start unlocked — let the timer determine when to lock.
    m_headingLocked = false;

    // Clear any accumulated integral error from a previous run.
    m_headingPid.reset();
  }

  /**
   * execute() — called every 20 ms while this command is running.
   *
   * <p>Processing order each loop:
   *   1. Read and deadband the joystick axes.
   *   2. Update the heading-lock state machine.
   *   3. If heading is locked and driver is pressing forward, add PID correction to turn.
   *   4. Block forward if an obstacle is detected.
   *   5. Send final (forward, turn) to the drivetrain.
   */
  @Override
  public void execute() {

    // -----------------------------------------------------------------------
    // 1. READ JOYSTICK
    // -----------------------------------------------------------------------

    // Left joystick Y axis = forward/backward.
    // WPILib convention: Y axis is INVERTED — pushing the stick forward (up) gives a NEGATIVE value.
    // We negate it so that "push forward = positive number = move forward."
    double forward = -m_controller.getLeftY();

    // Right joystick X axis = turning left/right.
    // Positive = right, negative = left (no inversion needed here).
    double turn = m_controller.getRightX();

    // Apply deadband: treat tiny joystick values as exactly zero to prevent unwanted drift.
    forward = applyDeadband(forward, OIConstants.JOYSTICK_DEADBAND);
    turn    = applyDeadband(turn,    OIConstants.JOYSTICK_DEADBAND);

    // -----------------------------------------------------------------------
    // 2. HEADING-LOCK STATE MACHINE
    //
    //   State A — STEERING ACTIVE:  driver is touching the turn stick.
    //             → Reset the idle timer. Release any existing heading lock.
    //
    //   State B — IDLE (not yet locked):  turn stick at zero, timer counting up.
    //             → Once the timer exceeds 100 ms, capture current heading → State C.
    //
    //   State C — LOCKED:  heading has been saved; PID can now correct.
    //             → Stay locked until the driver steers again (→ State A).
    // -----------------------------------------------------------------------

    if (Math.abs(turn) > 0) {
      // --- State A: driver is actively steering ---
      // Restart the idle timer so its count resets every loop the stick is held.
      // Release the lock so the PID does NOT fight the driver's input.
      m_steeringIdleTimer.restart();
      m_headingLocked = false;

    } else if (!m_headingLocked
               && m_steeringIdleTimer.hasElapsed(HeadingConstants.HEADING_LOCK_DELAY_SECONDS)) {
      // --- Transition from State B → C ---
      // The turn stick has been at zero for more than 100 ms.
      // Capture the current heading as the target we want to hold.
      m_drivetrain.lockCurrentHeading();

      // Clear accumulated integral error so the PID starts fresh on this new target.
      m_headingPid.reset();

      m_headingLocked = true;
    }
    // If m_headingLocked is already true, we stay in State C (no action needed here).

    // -----------------------------------------------------------------------
    // 3. HEADING CORRECTION (only active in State C, while driving forward)
    //
    //   If the heading is locked AND the driver is pressing forward:
    //     - Ask the PID how much turn is needed to bring us back on heading.
    //     - Clamp it so it can't overwhelm the driver's intended direction.
    //     - Add it to the turn input (which is 0.0 at this point).
    //
    //   If the driver is not pressing forward (stopped or reversing), skip it —
    //   correcting heading while stopped or backing up doesn't make sense.
    // -----------------------------------------------------------------------

    if (m_headingLocked && Math.abs(forward) >= HeadingConstants.HEADING_MIN_FORWARD_SPEED) {
      // PIDController.calculate(measurement, setpoint):
      //   measurement = where we ARE (current heading)
      //   setpoint    = where we WANT TO BE (locked heading)
      //   returns     = kP × (setpoint − measurement)
      double correction = MathUtil.clamp(
          m_headingPid.calculate(
              m_drivetrain.getHeading(),
              m_drivetrain.getLockedHeading()),
          -HeadingConstants.HEADING_MAX_CORRECTION,
           HeadingConstants.HEADING_MAX_CORRECTION);

      // SUBTRACT (not add) because the XRP gyro follows NWU convention:
      // left turn = positive yaw rate. In arcade drive, positive turn = right.
      // Without the negation, drifting right would produce a positive correction
      // that turns right further — a runaway feedback loop.
      turn -= correction;
    }

    // -----------------------------------------------------------------------
    // 4. OBSTACLE BLOCKING
    // -----------------------------------------------------------------------

    // The range sensor detected something ahead — clamp forward to zero.
    // Reverse (negative forward) is still allowed so the driver can back away.
    // Turning is also still allowed — only straight-ahead thrust is blocked.
    if (m_rangeSensor.isBlocked() && forward > 0) {
      forward = 0;
    }

    // -----------------------------------------------------------------------
    // 5. SEND TO DRIVETRAIN
    // -----------------------------------------------------------------------

    m_drivetrain.arcadeDrive(forward, turn);

  }

  /**
   * isFinished() — return true to stop the command.
   * Returning false means "keep running forever" — correct for a joystick drive command.
   * The command will only stop if another command interrupts it.
   */
  @Override
  public boolean isFinished() {
    return false;
  }

  /**
   * end() — called once when the command finishes or is interrupted.
   * We always stop the motors here as a safety measure so the robot doesn't keep rolling.
   *
   * @param interrupted  true if the command was cancelled by another command; false if it
   *                     finished normally (which can't happen here since isFinished() never returns true).
   */
  @Override
  public void end(boolean interrupted) {
    m_drivetrain.stop();
  }

  /**
   * Helper: returns 0.0 if the value is within the deadband, otherwise returns the original value.
   * Math.abs() gives us the size of the number ignoring its sign (+/-).
   *
   * @param value     The raw joystick input.
   * @param deadband  The minimum size a value must be to pass through (from Constants).
   * @return          The filtered joystick value.
   */
  private double applyDeadband(double value, double deadband) {
    if (Math.abs(value) < deadband) {
      return 0.0;
    }
    return value;
  }
}
