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
///////////////////////////////////// Driver Controller Constants ///////////////////////////////////
// Driver Controller constants
  public static final class DriverControllerConstants {
    // Driver controller port
    public static final int DRIVER_CONTROLLER_PORT = 0;
  }

/////////////////////////////////// Shooter Motor Constatnts ///////////////////////////////////
// Shooter constants
public static final class ShooterConstants {
  // Shooter motor id
  public static final int SHOOTER_MOTOR_ID = 2;

  // Shooter motor speed
  public static final double SHOOTER_MOTOR_SPEED = 0.75;
}


/////////////////////////////////// Intake Motor Constants ///////////////////////////////////
// Intake constants
public static final class IntakeConstants {
// Intake motor id
public static final int INTAKE_MOTOR_ID = 1;

// intake motor speed
public static final double INTAKE_MOTOR_SPEED = 0.5;
}
}