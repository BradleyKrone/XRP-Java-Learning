/**
 * RobotContainer is the "wiring hub" of the robot program.
 * This is where you connect everything together:
 *   - create your subsystems (the parts of the robot - see the subsystems folder)
 *   - create your controllers (Xbox controller, joystick, etc.)
 *   - say which button runs which command (see the commands folder)
 */

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import java.util.function.BooleanSupplier;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.Constants.ServoConstants;
import frc.robot.commands.ArcadeDrive;
import frc.robot.commands.DriveDistance;
import frc.robot.commands.TurnDegrees;
import frc.robot.commands.WiggleBothServos;
import frc.robot.commands.WiggleDrive;
import frc.robot.commands.WiggleServo;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.RangeSensor;
import frc.robot.subsystems.ServoSubsystem;


public class RobotContainer {

  // --- Subsystems ---
  // Create one instance of each subsystem. These objects own the physical hardware.
  // The m_ prefix is the FRC convention for member variables.
  private final Drivetrain     m_drivetrain  = new Drivetrain();
  private final ServoSubsystem m_servo1      = new ServoSubsystem(ServoConstants.SERVO_1_PORT);
  private final ServoSubsystem m_servo2      = new ServoSubsystem(ServoConstants.SERVO_2_PORT);
  private final RangeSensor    m_rangeSensor = new RangeSensor();

  // --- Controllers ---
  // CommandXboxController wraps a standard Xbox controller and adds WPILib trigger/button support.
  // The number is the USB port on the driver station (0 = first controller plugged in).
  private final CommandXboxController m_driverController =
      new CommandXboxController(OIConstants.DRIVER_CONTROLLER_PORT);

  /**
   * Constructor — runs ONCE when the robot program first starts.
   * We configure the default command here, then set up any button bindings.
   */
  public RobotContainer() {
    // Set ArcadeDrive as the DEFAULT command for the Drivetrain subsystem.
    // A default command runs automatically whenever no other command is using the subsystem.
    // Because isFinished() always returns false, this command runs the entire time
    // the robot is enabled — the driver is always in control of the wheels.
    m_drivetrain.setDefaultCommand(
        new ArcadeDrive(m_drivetrain, m_driverController, m_rangeSensor));

    configureButtonBindings();
  }

  /**
   * This is where you map controller buttons to commands.
   * Each line inside here says: "when the driver presses THIS button, run THAT command."
   */
  private void configureButtonBindings() {
    // whileTrue() starts WiggleDrive the moment A is pressed and cancels it the moment
    // A is released. When WiggleDrive is cancelled, its end() method stops the motors,
    // and ArcadeDrive (the default command) automatically resumes.
    m_driverController.a().whileTrue(new WiggleDrive(m_drivetrain, m_driverController));

    // Hold B to wiggle Servo 1 (port 4) back and forth between 45° and 135°.
    // When B is released, WiggleServo.end() returns the servo to the center (90°).
    m_driverController.b().whileTrue(new WiggleServo(m_servo1));

    // Hold Y to wiggle Servo 2 (port 5) the same way.
    m_driverController.y().whileTrue(new WiggleServo(m_servo2));

    // True when the driver touches any stick or button (X is excluded — it's the toggle).
    // Any axis past the deadband OR any button press will stop the auto routine.
    BooleanSupplier anyDriverInput = () ->
        Math.abs(m_driverController.getLeftY())         > OIConstants.JOYSTICK_DEADBAND ||
        Math.abs(m_driverController.getLeftX())         > OIConstants.JOYSTICK_DEADBAND ||
        Math.abs(m_driverController.getRightX())        > OIConstants.JOYSTICK_DEADBAND ||
        Math.abs(m_driverController.getRightY())        > OIConstants.JOYSTICK_DEADBAND ||
        m_driverController.getLeftTriggerAxis()         > OIConstants.JOYSTICK_DEADBAND ||
        m_driverController.getRightTriggerAxis()        > OIConstants.JOYSTICK_DEADBAND ||
        m_driverController.a().getAsBoolean()           ||
        m_driverController.b().getAsBoolean()           ||
        m_driverController.y().getAsBoolean()           ||
        m_driverController.leftBumper().getAsBoolean()  ||
        m_driverController.rightBumper().getAsBoolean() ||
        m_driverController.start().getAsBoolean()       ||
        m_driverController.back().getAsBoolean();

    // Press X once to START the repeating out-and-back auto routine:
    //   1. Drive forward 3 feet  (or until the range sensor sees something within 300mm)
    //   2. Spin 180 degrees in place
    //   3. Drive forward 3 feet  (or until the range sensor sees something within 300mm)
    //   4. Repeat from step 1
    //
    // The routine stops when ANY of these happen:
    //   - X is pressed a second time  (toggleOnTrue cancels it)
    //   - The driver touches any stick or button  (.until(anyDriverInput) ends it)
    //
    // Commands.sequence() chains the three steps back-to-back.
    // .repeatedly() restarts the sequence from step 1 each time it completes.
    // .until(anyDriverInput) wraps everything and stops the loop on any driver input.
    m_driverController.x().toggleOnTrue(
        Commands.sequence(
            new DriveDistance(m_drivetrain, AutoConstants.DRIVE_DISTANCE_METERS,
                              m_rangeSensor, AutoConstants.AUTO_RANGE_STOP_MM),
            new TurnDegrees(m_drivetrain,   AutoConstants.AUTO_TURN_DEGREES),
            new DriveDistance(m_drivetrain, AutoConstants.DRIVE_DISTANCE_METERS,
                              m_rangeSensor, AutoConstants.AUTO_RANGE_STOP_MM)
        ).repeatedly().until(anyDriverInput)
    );

    // Sensor trigger: automatically wiggle BOTH servos whenever the range sensor
    // detects an obstacle within BLOCKED_DISTANCE_MM millimeters.
    // Trigger wraps any boolean condition — here, the sensor's isBlocked() method.
    // whileTrue() starts WiggleBothServos the moment isBlocked() becomes true and
    // cancels it the moment the path clears (isBlocked() returns false again).
    new Trigger(m_rangeSensor::isBlocked)
        .whileTrue(new WiggleBothServos(m_servo1, m_servo2));

  }

  /**
   * Starts the drivetrain's 3-second IMU bias calibration sequence.
   *
   * <p>Called by Robot.teleopInit() every time the robot is enabled so the gyro
   * gets a fresh calibration each run. The robot must be held still for 3 seconds.
   */
  public void startCalibration() {
    m_drivetrain.startCalibration();
  }

  /**
   * Returns the command the robot should run during the Autonomous period.
   * Autonomous = the first 15 seconds of a match where NO driver is in control.
   */
  public Command getAutonomousCommand() {
    return null;
  }
}
