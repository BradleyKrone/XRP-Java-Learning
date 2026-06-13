// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * Constants are organized into nested inner classes by topic (OperatorConstants, DriveConstants,
 * etc.). This keeps related constants grouped together and lets you statically import just the
 * group you need, like: import static frc.robot.Constants.DriveConstants.*;
 *
 * ALL fields here are 'public static final':
 *   - public  → accessible from anywhere in the project
 *   - static  → belongs to the class itself, not to any instance (no 'new Constants()' needed)
 *   - final   → the value can never be changed after it is set (it's a true constant)
 */
public final class Constants {

    /**
     * OperatorConstants holds values related to the driver's controller hardware.
     * If you swap to a different controller port or controller type, change it here
     * and it updates everywhere automatically.
     */
    public static final class OperatorConstants {

        // The USB port number on the Driver Station laptop where the Xbox controller is plugged in.
        // Port 0 = first controller connected, Port 1 = second, etc.
        // Check the Driver Station app's USB tab if your controller isn't responding.
        public static final int kDriverControllerPort = 0;
    }

    /**
     * DriveConstants holds values related to the drivetrain's motors and behavior.
     */
    public static final class DriveConstants {

        // XRP motor device IDs (the physical port numbers on the XRP board).
        // Motor 0 is wired to the LEFT drive wheel.
        // Motor 1 is wired to the RIGHT drive wheel.
        public static final int kLeftMotorID  = 0;
        public static final int kRightMotorID = 1;

        // The right motor faces the opposite direction from the left motor because they
        // are mounted on opposite sides of the robot chassis (mirrored).
        // Without inversion, sending +1.0 to both motors would spin one wheel forward
        // and the other backward — causing the robot to spin in a circle instead of going straight.
        // Setting this to true inverts the right motor so that +1.0 drives BOTH wheels forward.
        public static final boolean kRightMotorInverted = true;

        // Deadband threshold for joystick inputs.
        // Physical joysticks are imperfect: even when you release the stick, the sensor might
        // read a tiny nonzero value like 0.03 or -0.07 due to wear and manufacturing tolerance.
        // Without a deadband, those tiny values would cause the robot to slowly creep or vibrate.
        // Any joystick reading with an absolute value BELOW this threshold is treated as exactly 0.
        // 0.1 means the stick must be moved at least 10% of the way before the robot responds.
        public static final double kDeadband = 0.1;
    }
}

