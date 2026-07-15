/**
 * WiggleBothServos is a COMMAND — it wiggles Servo 1 and Servo 2 at the same time.
 *
 * <p>This command is triggered automatically by the range sensor: when the robot
 * detects an obstacle ahead, RobotContainer starts this command to wiggle both servos
 * as a signal that the path is blocked.
 *
 * <p>It uses the same timer-and-modulo timing technique as WiggleServo, but it
 * controls two servo subsystems simultaneously by declaring a requirement on both.
 * The CommandScheduler enforces that no other command can use either servo while
 * this command is running.
 */

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ServoConstants;
import frc.robot.subsystems.ServoSubsystem;


public class WiggleBothServos extends Command {

  // The two servo subsystems this command controls.
  private final ServoSubsystem m_servo1;
  private final ServoSubsystem m_servo2;

  // Timer tracks elapsed time so we can alternate positions without blocking the loop.
  private final Timer m_timer = new Timer();

  /**
   * Constructor — called once in RobotContainer when the Trigger is created.
   *
   * @param servo1  The first servo subsystem (port 4).
   * @param servo2  The second servo subsystem (port 5).
   */
  public WiggleBothServos(ServoSubsystem servo1, ServoSubsystem servo2) {
    m_servo1 = servo1;
    m_servo2 = servo2;

    // Declaring requirements on BOTH servos means this command exclusively controls
    // them. If the driver is wiggling a servo with B or Y, this command will
    // interrupt it — the obstacle signal takes priority.
    addRequirements(servo1, servo2);
  }

  /**
   * initialize() — called once when the obstacle is first detected.
   * Restart the timer so the wiggle cycle always begins cleanly.
   */
  @Override
  public void initialize() {
    m_timer.restart();
  }

  /**
   * execute() — called every 20ms while the obstacle is still detected.
   * Both servos receive the same angle at the same time so they move in sync.
   */
  @Override
  public void execute() {
    double elapsed = m_timer.get();

    // Modulo divides elapsed time into repeating cycles.
    // positionInCycle tells us where we are within the current 500ms cycle.
    double positionInCycle = elapsed % ServoConstants.SERVO_WIGGLE_CYCLE_SECONDS;

    if (positionInCycle < ServoConstants.SERVO_WIGGLE_HALF_CYCLE_SECONDS) {
      // First half of the cycle: move both servos to the left angle.
      m_servo1.setAngle(ServoConstants.SERVO_LEFT_ANGLE);
      m_servo2.setAngle(ServoConstants.SERVO_LEFT_ANGLE);
    } else {
      // Second half of the cycle: move both servos to the right angle.
      m_servo1.setAngle(ServoConstants.SERVO_RIGHT_ANGLE);
      m_servo2.setAngle(ServoConstants.SERVO_RIGHT_ANGLE);
    }
  }

  /**
   * isFinished() — return false so the command keeps running as long as the trigger
   * (range sensor) says the path is blocked. The Trigger.whileTrue() binding in
   * RobotContainer will cancel this command the moment isBlocked() returns false.
   */
  @Override
  public boolean isFinished() {
    return false;
  }

  /**
   * end() — called once when the obstacle clears (or another command interrupts).
   * Center both servos so they return to a neutral resting position.
   *
   * @param interrupted  true if cancelled by another command, false if finished normally.
   */
  @Override
  public void end(boolean interrupted) {
    m_servo1.center();
    m_servo2.center();
  }
}
