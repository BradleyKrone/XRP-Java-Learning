// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class is where we store numbers that are used throughout the robot code.
 *
 * Why use constants?
 *   Instead of scattering "magic numbers" like 0.5 or 2 everywhere in your code,
 *   you define them once here with a clear name. If you ever need to change a value,
 *   you only have to change it in ONE place instead of hunting through every file.
 *
 * "public static final" means:
 *   - public  → any file in the project can access it
 *   - static  → it belongs to the class itself, not to any object instance
 *   - final   → the value can NEVER be changed after it is set (it is a true constant)
 *
 * Inner classes (like OperatorConstants below) let us group related constants together
 * so the code stays organized as the project grows.
 */
public final class Constants {

  /**
   * OperatorConstants holds everything related to the human driver's controller.
   */
  public static final class OperatorConstants {

    // The USB port number the Xbox controller is plugged into on the Driver Station computer.
    // Port 0 is the first (and usually only) controller.
    public static final int XBOX_CONTROLLER_PORT = 0;
  }

  /**
   * HandConstants holds everything related to the hand (gripper) mechanism.
   */
  public static final class HandConstants {

    // The XRP robot has four motor output channels numbered 0-3.
    //   Channel 0 → left drive motor  (built-in)
    //   Channel 1 → right drive motor (built-in)
    //   Channel 2 → extra motor port  ← we use this for the hand
    //   Channel 3 → extra motor port
    public static final int HAND_MOTOR_CHANNEL = 2;

    // How fast the hand motor spins when the A button is held.
    // Motor speeds in WPILib go from -1.0 (full reverse) to 1.0 (full forward).
    // 0.5 means 50% power in the forward direction.
    public static final double HAND_MOTOR_SPEED = 0.5;

    // The speed used to stop the motor completely.
    // Setting a motor to 0.0 cuts all power so it coasts to a stop.
    public static final double HAND_MOTOR_STOP = 0.0;
  }
}
