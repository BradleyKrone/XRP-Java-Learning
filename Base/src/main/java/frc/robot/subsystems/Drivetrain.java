/**
 * Drivetrain is a SUBSYSTEM — it owns and controls the physical motors on the robot.
 *
 * <p>A subsystem represents one physical part of the robot (in this case, the wheels and motors).
 * It is responsible for:
 *   1. Creating and configuring the hardware objects (motors and sensors).
 *   2. Exposing simple methods that COMMANDS can call to make things happen.
 *
 * Only one command is allowed to use this subsystem at a time. If a second command tries to use
 * it, the first command is automatically cancelled. This prevents two commands from fighting over
 * the same motors.
 *
 * <p>This subsystem also owns the built-in XRP gyroscope and maintains a software heading angle
 * by integrating (accumulating) the yaw rate every 20 ms. Commands can lock a target heading
 * and retrieve the current heading to implement straight-line correction.
 */

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.xrp.XRPGyro;
import edu.wpi.first.wpilibj.xrp.XRPMotor;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;


public class Drivetrain extends SubsystemBase {

  // m_ prefix is the FRC convention for member variables (fields that belong to this object).
  // XRPMotor controls one physical motor on the XRP robot board.
  private final XRPMotor m_leftMotor;
  private final XRPMotor m_rightMotor;

  // The XRP has a built-in LSM6DSOX inertial measurement unit (IMU).
  // XRPGyro gives us access to the 3-axis gyroscope inside it.
  // No channel number is needed — there is only one IMU on the XRP board.
  private final XRPGyro m_gyro;

  // Quadrature encoders measure how far each wheel has turned.
  // Each full wheel revolution produces 585 counts.
  // DIO channels: left = 4 & 5, right = 6 & 7 (A and B quadrature signals).
  private final Encoder m_leftEncoder  = new Encoder(4, 5);
  private final Encoder m_rightEncoder = new Encoder(6, 7);

  // Meters traveled per encoder pulse.
  // Wheel circumference = π × diameter = π × 0.060 m = 0.18850 m
  // Distance per pulse  = 0.18850 m / 585 pulses = 0.000322 m/pulse
  private static final double METERS_PER_PULSE = Math.PI * 0.060 / 585.0;

  // Our software-integrated heading angle, in degrees.
  //
  // WHY integrate ourselves instead of calling m_gyro.getAngleZ()?
  // m_gyro.getAngleZ() also gives an integrated angle, but doing the integration
  // ourselves makes the math visible — you can see exactly how a rate sensor becomes
  // a heading angle. That is the learning goal here.
  //
  // Integration formula: heading += yawRate × dt
  //   yawRate = degrees/second from getYawRate() (bias-corrected)
  //   dt      = 0.020 seconds (the 20 ms WPILib scheduler loop period)
  //
  // Positive = turned right (clockwise when viewed from above).
  // Negative = turned left (counterclockwise).
  private double m_heading = 0.0;

  // The heading angle we want to hold while driving straight.
  // Set by lockCurrentHeading(); read by ArcadeDrive to compute PID correction.
  private double m_lockedHeading = 0.0;

  // Loop period in seconds. The WPILib scheduler calls periodic() every 20 ms.
  private static final double LOOP_PERIOD_SECONDS = 0.020;

  // ---- IMU Bias Calibration ------------------------------------------------
  //
  // PROBLEM: All gyroscopes have "drift" — even when the robot is perfectly still,
  // the sensor reports a small non-zero yaw rate (e.g. 0.08 deg/s). Over time,
  // integrating this tiny error shifts the heading by several degrees even though
  // the robot never moved. This is called "bias" or "zero-rate offset."
  //
  // FIX: At enable time, hold the robot still for 3 seconds and measure the average
  // raw yaw rate. That average IS the bias. Subtracting it from every future reading
  // makes the corrected rate ≈ 0 when sitting still, and accurate when turning.

  // How long (seconds) to collect samples during calibration.
  private static final double CALIBRATION_DURATION_SECONDS = 3.0;

  // Tracks elapsed time during the calibration window.
  private final Timer m_calibrationTimer = new Timer();

  // True while we are actively collecting calibration samples.
  private boolean m_calibrating = false;

  // True once calibration has finished and m_yawBias is valid.
  private boolean m_calibrated = false;

  // Running sum of raw yaw rate readings collected during calibration.
  private double m_yawRateAccumulator = 0.0;

  // Number of samples collected so far (used to compute the average).
  private int m_calibrationSampleCount = 0;

  // The computed average raw yaw rate while the robot was still.
  // Subtracted from every future getRateZ() call to zero out the drift.
  private double m_yawBias = 0.0;

  // Print throttle: we only print once per second (every 50 loops × 20 ms = 1000 ms).
  private int m_printCounter = 0;

  /**
   * Constructor — runs once when the Drivetrain object is created (at robot startup).
   * This is where we create and configure our hardware.
   */
  public Drivetrain() {
    // Create the motor objects using the port numbers defined in Constants.
    // Using constants instead of "magic numbers" means if the wiring ever changes,
    // we only update Constants.java — not every file that uses motors.
    m_leftMotor  = new XRPMotor(DriveConstants.LEFT_MOTOR_PORT);
    m_rightMotor = new XRPMotor(DriveConstants.RIGHT_MOTOR_PORT);

    // The right motor is physically wired in reverse on the XRP board.
    // Without inversion, calling setSpeed(0.5) on both motors would spin them
    // in opposite directions and the robot would spin in place instead of driving straight.
    m_rightMotor.setInverted(DriveConstants.RIGHT_MOTOR_INVERTED);

    // Create the gyro object. It starts up ready to use — no calibration call needed.
    m_gyro = new XRPGyro();

    // Tell each encoder how far one pulse represents (converts raw counts → meters).
    m_leftEncoder.setDistancePerPulse(METERS_PER_PULSE);
    m_rightEncoder.setDistancePerPulse(METERS_PER_PULSE);

    // The right wheel spins in the opposite physical direction for the same forward motion
    // (same reason the right MOTOR is inverted). Reversing the encoder keeps both sides
    // positive when the robot moves forward.
    m_rightEncoder.setReverseDirection(true);
  }

  /**
   * Returns the average distance traveled by the left and right wheels, in meters.
   *
   * <p>Positive = moved forward, negative = moved backward.
   * Call resetEncoders() before a measured move to start fresh from zero.
   *
   * @return Average wheel distance in meters.
   */
  public double getAverageDistance() {
    return (m_leftEncoder.getDistance() + m_rightEncoder.getDistance()) / 2.0;
  }

  /**
   * Resets both wheel encoders to zero.
   * Call this at the start of each measured move so getAverageDistance() starts from 0.
   */
  public void resetEncoders() {
    m_leftEncoder.reset();
    m_rightEncoder.reset();
  }

  /**
   * Starts the 3-second IMU bias calibration sequence.
   *
   * <p>Call this from Robot.teleopInit() so it runs every time the robot is enabled.
   * The robot MUST be held completely still for 3 seconds while this runs.
   *
   * <p>What happens internally:
   *   1. All state is reset (heading → 0, locked heading → 0, old bias → 0).
   *   2. periodic() begins collecting raw yaw rate samples.
   *   3. After 3 seconds, it computes average = bias and stops collecting.
   *   4. Every future getYawRate() call subtracts that bias from the raw sensor reading.
   */
  public void startCalibration() {
    m_calibrating              = true;
    m_calibrated               = false;
    m_yawRateAccumulator       = 0.0;
    m_calibrationSampleCount   = 0;
    m_yawBias                  = 0.0;
    m_heading                  = 0.0;
    m_lockedHeading            = 0.0;
    m_calibrationTimer.restart();
    System.out.println("[Gyro] Calibration started — hold the robot still for 3 seconds.");
  }

  /**
   * Drives the robot using arcade-drive style inputs.
   *
   * <p>In arcade drive:
   *   - One input controls forward/backward speed (from the left joystick Y axis).
   *   - One input controls left/right turning (from the right joystick X axis).
   *
   * <p>The math combines these two inputs to figure out how fast each individual
   * motor should spin:
   *   - To go straight forward:  both motors get the same positive speed.
   *   - To turn right:           left motor speeds up, right motor slows down.
   *   - To turn left:            right motor speeds up, left motor slows down.
   *
   * @param forward  Speed in the forward/backward direction. Range: -1.0 (full back) to +1.0 (full forward).
   * @param turn     Turning rate. Range: -1.0 (full left) to +1.0 (full right).
   */
  public void arcadeDrive(double forward, double turn) {
    // Scale both inputs by the max speed cap from Constants.
    // This lets you change the top speed in one place without touching this math.
    double scaledForward = forward * DriveConstants.MAX_DRIVE_SPEED;
    double scaledTurn    = turn    * DriveConstants.MAX_DRIVE_SPEED;

    // Arcade drive mixing formula:
    //   Left side  = forward + turn  (turning right means the left side goes faster)
    //   Right side = forward - turn  (turning right means the right side goes slower)
    m_leftMotor.set(scaledForward + scaledTurn);
    m_rightMotor.set(scaledForward - scaledTurn);
  }

  /**
   * Stops both motors immediately by setting their speed to zero.
   * This is called automatically by the command framework when a command ends
   * or is interrupted (e.g. the driver releases the joystick or another command takes over).
   */
  public void stop() {
    m_leftMotor.set(0);
    m_rightMotor.set(0);
  }

  /**
   * Returns the robot's current heading angle in degrees.
   *
   * <p>This value is computed by integrating the gyro yaw rate in periodic().
   * It is a running total — it does NOT reset to zero between drives unless you add
   * a resetHeading() call.
   *
   * <p>Positive = turned right (clockwise), negative = turned left (counterclockwise).
   *
   * @return Heading in degrees.
   */
  public double getHeading() {
    return m_heading;
  }

  /**
   * Returns the bias-corrected yaw rate in degrees per second.
   *
   * <p>This is the instantaneous spin speed with drift removed:
   *   correctedRate = rawRate − bias
   *
   * <p>Before calibration completes, bias = 0.0, so the raw rate is returned as-is.
   * After calibration, the reading should be ≈ 0.0 deg/s when the robot is still.
   * Positive = spinning right, negative = spinning left.
   * Integrating this rate over time produces the heading angle stored in m_heading.
   *
   * @return Bias-corrected yaw rate in degrees per second.
   */
  public double getYawRate() {
    return m_gyro.getRateZ() - m_yawBias;
  }

  /**
   * Saves the current heading as the target heading for straight-line correction.
   *
   * <p>ArcadeDrive calls this after the driver has released the turn stick for 100 ms.
   * From that point on, the PID in ArcadeDrive will try to steer back to this angle
   * whenever the driver presses forward.
   */
  public void lockCurrentHeading() {
    m_lockedHeading = m_heading;
  }

  /**
   * Returns the locked target heading in degrees.
   *
   * <p>The ArcadeDrive PID uses this as the setpoint — the angle the robot should
   * be at when driving straight.
   *
   * @return Locked (target) heading in degrees.
   */
  public double getLockedHeading() {
    return m_lockedHeading;
  }

  /**
   * periodic() is called automatically by the CommandScheduler every 20 milliseconds,
   * even when no command is running.
   *
   * <p>Two things happen here every loop:
   *
   * <p>1. CALIBRATION (first 3 seconds after startCalibration() is called):
   *    Raw yaw rate samples are accumulated. When time runs out, the average is saved
   *    as m_yawBias and calibration stops. Heading integration is paused during this
   *    window so the bias error does not corrupt the starting heading.
   *
   * <p>2. HEADING INTEGRATION (after calibration):
   *    heading += correctedYawRate × 0.020
   *    This converts the instantaneous spin rate into a total angle turned since enable.
   *
   * <p>3. CONSOLE PRINT (once per second):
   *    Prints the current calibrated yaw rate so you can verify the sensor is working.
   */
  @Override
  public void periodic() {

    // -----------------------------------------------------------------------
    // CALIBRATION
    // -----------------------------------------------------------------------
    if (m_calibrating) {
      // Collect the raw (un-corrected) rate — we are computing what the bias IS,
      // so we must NOT subtract anything yet.
      m_yawRateAccumulator += m_gyro.getRateZ();
      m_calibrationSampleCount++;

      if (m_calibrationTimer.hasElapsed(CALIBRATION_DURATION_SECONDS)) {
        // Average of all samples = the systematic zero-rate error of this gyro.
        m_yawBias    = m_yawRateAccumulator / m_calibrationSampleCount;
        m_calibrating = false;
        m_calibrated  = true;
        System.out.printf(
            "[Gyro] Calibration complete. Bias = %.4f deg/s  (%d samples)%n",
            m_yawBias, m_calibrationSampleCount);
      }

      // Skip heading integration while calibrating — we don't have a valid bias yet,
      // so integrating now would bake drift into the starting heading.
      return;
    }

    // -----------------------------------------------------------------------
    // HEADING INTEGRATION
    // getYawRate() returns the bias-corrected rate, so this should drift very little.
    // -----------------------------------------------------------------------
    m_heading += getYawRate() * LOOP_PERIOD_SECONDS;

    // -----------------------------------------------------------------------
    // CONSOLE PRINT — once per second so the output stays readable
    // -----------------------------------------------------------------------
    m_printCounter++;
    if (m_printCounter >= 50) {
      m_printCounter = 0;
      System.out.printf(
          "[Gyro] yawRate = %+7.3f deg/s   heading = %+8.3f deg%n",
          getYawRate(), m_heading);
    }
  }
}
