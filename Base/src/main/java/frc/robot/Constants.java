/**
 * Constants is one single place to keep important numbers for your robot.
 *
 * <p>Things like motor port numbers, driving speeds, or button numbers go here. Keeping them in one
 * place means that when a number needs to change, you only have to change it once, and it is easy
 * to find.
 *
 * Note: Only put numbers and simple values here - do not put any robot actions in this file.
 */

package frc.robot;


public final class Constants {

  /**
   * DriveConstants holds all the numbers related to the drivetrain (motors, speeds, etc.).
   * Using an inner class groups related constants together so it's clear what each value is for.
   */
  public static final class DriveConstants {

    // The XRP has its motors on these hardware channels.
    // Channel 0 = left side, Channel 1 = right side.
    public static final int LEFT_MOTOR_PORT  = 0;
    public static final int RIGHT_MOTOR_PORT = 1;

    // The right motor is physically wired so that a positive voltage makes it spin
    // BACKWARD (opposite of the left motor). We invert it here so that positive
    // output = forward on both sides.
    public static final boolean RIGHT_MOTOR_INVERTED = true;

    // Caps the maximum speed sent to the motors (0.0 = stopped, 1.0 = full speed).
    // Starting at 0.6 keeps things safe while you are learning.
    public static final double MAX_DRIVE_SPEED = 1;
  }

  /**
   * WiggleConstants holds the timing and speed values for the WiggleDrive command.
   * Adjust WIGGLE_TURN_SPEED to make the wiggle faster/slower, and the half-cycle
   * value to change how long each left/right turn lasts.
   */
  public static final class WiggleConstants {

    // How fast the robot turns during each wiggle phase (0.0 = stopped, 1.0 = full speed).
    public static final double WIGGLE_TURN_SPEED = 1;

    // Each full wiggle cycle = left turn + right turn. Total = 500ms (0.5 seconds).
    public static final double WIGGLE_CYCLE_SECONDS = 0.50;

    // How long each half of the cycle lasts (250ms = 0.25 seconds).
    public static final double WIGGLE_HALF_CYCLE_SECONDS = 0.25;
  }

  /**
   * ServoConstants holds the port number and angle positions for the servo wiggle action.
   * Changing LEFT_ANGLE and RIGHT_ANGLE controls how wide the servo swings side-to-side.
   */
  public static final class ServoConstants {

    // XRP Servo 1 is on hardware channel 4, Servo 2 is on channel 5.
    // Both use the same ServoSubsystem class — we just pass a different port number.
    public static final int SERVO_1_PORT = 4;
    public static final int SERVO_2_PORT = 5;

    // The two positions the servo alternates between during a wiggle (degrees, 0–180).
    public static final double SERVO_LEFT_ANGLE  = 45.0;
    public static final double SERVO_RIGHT_ANGLE = 135.0;

    // Each full wiggle cycle = left position + right position. Total = 500ms.
    public static final double SERVO_WIGGLE_CYCLE_SECONDS      = 0.50;

    // How long the servo holds each position (250ms = half the cycle).
    public static final double SERVO_WIGGLE_HALF_CYCLE_SECONDS = 0.25;
  }

  /**
   * RangeConstants holds the port and threshold for the ultrasonic rangefinder.
   * Adjust BLOCKED_DISTANCE_MM to change how close an obstacle must be before
   * the robot treats it as "blocked."
   */
  public static final class RangeConstants {

    // Analog channel 2 on the XRP board is wired to the ultrasonic rangefinder.
    public static final int RANGE_SENSOR_PORT = 2;

    // If the sensor reads a distance closer than this value (in millimeters),
    // the robot considers its path blocked.
    // NOTE: Currently set to 55mm (below the ~64mm baseline reading) so driving
    // is unblocked while we debug the sensor wiring. Raise this to ~300mm once
    // the sensor is confirmed working.
    public static final double BLOCKED_DISTANCE_MM = 150.0;
  }

  /**
   * AutoConstants holds the values used by the autonomous "out-and-back" routine triggered by the
   * X button. All distances and speeds live here so they are easy to tune in one place.
   */
  public static final class AutoConstants {

    // 3 feet converted to meters (1 foot = 0.3048 m).
    // This is how far the robot drives forward before spinning and returning.
    public static final double DRIVE_DISTANCE_METERS = 3 * 0.3048; // ≈ 0.914 m

    // Speed used for straight driving during the auto routine (0.0–1.0).
    public static final double AUTO_DRIVE_SPEED = 1.0;

    // Speed used for the 180-degree spin (0.0–1.0).
    public static final double AUTO_TURN_SPEED = 1.0;

    // How many degrees to turn in the middle of the out-and-back routine.
    public static final double AUTO_TURN_DEGREES = 180.0;

    // If the range sensor reads closer than this during a DriveDistance, the drive stops early
    // so the robot can turn around before hitting the obstacle.
    // Double the normal BLOCKED_DISTANCE_MM (150 mm × 2 = 300 mm ≈ 12 inches).
    public static final double AUTO_RANGE_STOP_MM = 300.0;
  }

  /**
   * OIConstants holds numbers related to the Operator Interface — the controllers and joysticks.
   * OI = "Operator Interface" (standard FRC term for controllers).
   */
  public static final class OIConstants {

    // USB port number the Xbox controller is plugged into on the driver station computer.
    public static final int DRIVER_CONTROLLER_PORT = 0;

    // Joystick inputs below this value are treated as zero.
    // This prevents tiny unintentional movements when the thumbstick is "at rest"
    // but not perfectly centered (a real-world imperfection called "joystick drift").
    public static final double JOYSTICK_DEADBAND = 0.1;
  }

  /**
   * HeadingConstants holds the tuning values for the gyro-based heading-hold ("straight-line
   * correction") feature in ArcadeDrive.
   *
   * <p>How it works:
   *   1. The drivetrain gyro integrates the yaw rate every 20 ms to produce a heading angle.
   *   2. When the driver releases the turn stick, a 100 ms debounce timer starts.
   *   3. After 100 ms of no steering input, the current heading is "locked in" as the target.
   *   4. While the driver is pressing forward AND the heading is locked, a PID controller
   *      calculates a small turn correction to keep the robot on that heading.
   *
   * <p>Tuning guide:
   *   - Start with just KP (set KI and KD to 0).
   *   - Increase KP until the robot corrects quickly but doesn't oscillate (wiggle side to side).
   *   - KI can fix a persistent drift that KP alone never fully corrects (use very small values).
   *   - KD damps overshoot — add it last if the robot corrects but then oscillates.
   */
  public static final class HeadingConstants {

    // How long (seconds) the turn stick must sit at zero before the heading is captured.
    // 100 ms prevents locking to an intermediate heading while the driver is still turning.
    public static final double HEADING_LOCK_DELAY_SECONDS = 0.10;

    // Proportional gain — the main knob for tuning.
    // Every degree of heading error is multiplied by KP to produce a turn correction.
    // Example: 5 degrees off × KP 0.025 = 0.125 turn output (about 12.5% steering correction).
    //
    // KP = 0.025 was selected from a data-driven sweep test (9 gains from 0.005 to 1.5).
    // The robot drove 4-foot legs forward and backward while being pushed by hand to simulate
    // real disturbances (e.g. hitting a bump or being nudged). Heading error was logged
    // for every 20 ms loop and analyzed for steady-state accuracy and visible oscillation.
    //
    // Why not lower (0.005 or 0.010)?
    //   Those gains had near-zero oscillation but were too weak to pull the robot back after
    //   a push. The heading error stayed elevated for the rest of the leg.
    //
    // Why not higher (0.050 or above)?
    //   KP 0.050 produced ~2.5 heading direction-reversals per second while driving straight
    //   — a visible left-right weave. Higher gains were worse (3-4+ reversals/sec).
    //
    // KP 0.025 had the best balance: ~1° steady-state heading error and only ~1 direction
    // reversal per second during straight driving, which is not visible to the eye.
    public static final double HEADING_KP = 0.025;

    // Integral gain — accumulates correction over time for stubborn persistent errors.
    // Leave at 0.0 until KP is tuned; then add very small values (e.g. 0.001) if needed.
    public static final double HEADING_KI = 0.0;

    // Derivative gain — reduces overshoot by braking the correction as the error shrinks.
    // Leave at 0.0 until KP is tuned; add only if the robot oscillates around straight.
    public static final double HEADING_KD = 0.0;

    // Hard cap on how much turn output the PID is allowed to add.
    // Prevents the gyro from "stealing" too much steering authority from the driver.
    // 0.3 = up to 30% of full turn speed can be used for heading correction.
    public static final double HEADING_MAX_CORRECTION = 1;

    // The forward input must be at least this large before heading hold turns on.
    // Prevents the PID from running (and wasting effort) when the robot is barely creeping.
    public static final double HEADING_MIN_FORWARD_SPEED = 0.1;
  }

}
