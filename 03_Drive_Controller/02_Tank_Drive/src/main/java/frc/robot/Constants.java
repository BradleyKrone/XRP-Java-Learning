/**
 * Constants is one single place to keep important numbers for your robot.
 *
 * <p>Things like motor port numbers, driving speeds, or button numbers go here. Keeping them in one
 * place means that when a number needs to change, you only have to change it once, and it is easy to
 * find.
 *
 * Example of what you might add later:
 *   - public static final int LEFT_MOTOR_PORT = 0;
 *   - public static final int RIGHT_MOTOR_PORT = 1;
 *   - public static final double DRIVE_SPEED = 0.5;
 *
 * Note: Only put numbers and simple values here - do not put any robot actions in this file.
 */

package frc.robot;


public final class Constants {

  /**
   * Settings for the Drivetrain.
   *
   * <p>These device IDs are the CTRE-style IDs used by the {@code frc.robot.ctre} wrapper. On the
   * XRP they map to fixed hardware (ID 0 = left motor/encoder, ID 1 = right motor/encoder). On a
   * real robot these would be the IDs you assign each device in Phoenix Tuner.
   */
  public static final class DriveConstants {
    /** Device ID for the left drive motor (TalonFX). */
    public static final int LEFT_DRIVE_TALON_ID = 0;

    /** Device ID for the right drive motor (TalonFX). Remember: the right side runs inverted. */
    public static final int RIGHT_DRIVE_TALON_ID = 1;

    /** Device ID for the left wheel encoder (CANcoder), if you read it separately. */
    public static final int LEFT_CANCODER_ID = 0;

    /** Device ID for the right wheel encoder (CANcoder), if you read it separately. */
    public static final int RIGHT_CANCODER_ID = 1;

    /** Device ID for the IMU (Pigeon2). The XRP has one gyro, so the value does not matter. */
    public static final int PIGEON_ID = 0;

    /** How much to scale the joystick before sending it to the motors (1.0 = full speed). */
    public static final double DRIVE_SPEED_SCALE = 1.0;
  }

  /** Settings for the driver's controller. */
  public static final class ControllerConstants {
    /** USB port the driver's controller is plugged into (shown in the Driver Station). */
    public static final int DRIVER_CONTROLLER_PORT = 0;
  }
}
