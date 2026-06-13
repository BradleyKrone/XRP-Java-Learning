// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.xrp.XRPMotor;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

/**
 * The Drivetrain subsystem controls the two drive motors on the XRP robot.
 *
 * --- What is a Subsystem? ---
 * In FRC's command-based framework, a "subsystem" represents a self-contained physical
 * part of the robot — like the drivetrain, an arm, or an intake. Each subsystem:
 *   1. Owns its hardware (motors, sensors) — only this class talks to those devices directly.
 *   2. Exposes public methods for commands to call (e.g., arcadeDrive, stop).
 *   3. Registers with the CommandScheduler so the scheduler can enforce that only ONE
 *      command uses this subsystem at a time.
 *
 * By extending SubsystemBase, this class is automatically registered with the scheduler
 * and gains a periodic() method that runs every 20ms.
 *
 * --- Why separate Commands from Subsystems? ---
 * Keeping the hardware logic here (in the subsystem) and the decision logic in Commands
 * makes the code easier to test, reuse, and understand. The subsystem doesn't care WHO
 * is calling arcadeDrive() — it just knows HOW to drive. A teleop command calls it with
 * joystick values; an auto command might call it with calculated values.
 */
public class Drivetrain extends SubsystemBase {

    // XRPMotor is the WPILib class for a motor on the XRP robot board.
    // 'private final' means:
    //   - private → nothing outside Drivetrain.java can access these directly
    //   - final   → the reference is set once in the constructor and never reassigned
    private final XRPMotor m_leftMotor;
    private final XRPMotor m_rightMotor;

    // --- Naming Convention ---
    // In FRC, member variables (fields) are typically prefixed with 'm_' to distinguish them
    // from local variables inside methods. This is a widely-used WPILib convention.
    // You'll see this pattern throughout FRC code: m_drivetrain, m_leftMotor, m_controller, etc.

    /**
     * Constructs a new Drivetrain subsystem.
     *
     * This constructor is called once at robot startup when RobotContainer is created.
     * It initializes both drive motors and configures the right motor's inversion.
     */
    public Drivetrain() {
        // Create the left motor. The argument (kLeftMotorID = 0) tells WPILib which physical
        // port on the XRP board this motor is connected to.
        m_leftMotor = new XRPMotor(DriveConstants.kLeftMotorID);

        // Create the right motor. (kRightMotorID = 1)
        m_rightMotor = new XRPMotor(DriveConstants.kRightMotorID);

        // Invert the right motor.
        //
        // Imagine looking at the robot from above. The left motor's positive direction spins
        // its wheel forward. But the right motor is on the OPPOSITE side — if you gave it the
        // same positive signal, its wheel would spin backward (because the motor faces the
        // other direction physically).
        //
        // setInverted(true) tells WPILib to flip the sign internally before sending the signal
        // to the motor hardware. Now positive = forward for BOTH sides.
        m_rightMotor.setInverted(DriveConstants.kRightMotorInverted);
    }

    /**
     * Drives the robot using arcade-style controls.
     *
     * --- What is Arcade Drive? ---
     * Arcade drive uses two inputs to control the robot:
     *   1. Speed  (forward / backward) — like the gas pedal and reverse
     *   2. Turn   (left / right)       — like the steering wheel
     *
     * This is different from "tank drive" where you control each side independently.
     * Most video game driving is arcade-style, so it feels natural to many drivers.
     *
     * --- The Math ---
     * To blend speed and turn into separate left/right motor outputs:
     *
     *   leftSpeed  = speed + turn
     *   rightSpeed = speed - turn
     *
     * Let's trace a few examples to build intuition:
     *
     *   Straight forward  (speed=1.0, turn=0.0): left=1.0,  right=1.0   ← equal, go straight
     *   Straight back     (speed=-1.0, turn=0.0): left=-1.0, right=-1.0  ← equal backward
     *   Turn right only   (speed=0.0, turn=1.0): left=1.0,  right=-1.0  ← spin right in place
     *   Turn left only    (speed=0.0, turn=-1.0): left=-1.0, right=1.0   ← spin left in place
     *   Forward + right   (speed=0.5, turn=0.3): left=0.8,  right=0.2   ← curve right
     *
     * Notice that when turning right, the LEFT wheel speeds up (to push the robot rightward)
     * and the RIGHT wheel slows down (or reverses), which is exactly what makes the robot turn.
     *
     * @param speed Forward/backward speed. Positive = forward, negative = backward. Range: [-1, 1]
     * @param turn  Turning rate. Positive = right, negative = left. Range: [-1, 1]
     */
    public void arcadeDrive(double speed, double turn) {

        // Step 1: Apply deadband to both inputs.
        // This filters out tiny joystick drift values near zero so the robot stays still
        // when the driver isn't touching the controller.
        speed = applyDeadband(speed, DriveConstants.kDeadband);
        turn  = applyDeadband(turn,  DriveConstants.kDeadband);

        // Step 2: Calculate individual wheel speeds using arcade drive math.
        // Left wheel gets speed PLUS turn (turning right means left wheel goes faster).
        // Right wheel gets speed MINUS turn (turning right means right wheel goes slower).
        double leftSpeed  = speed + turn;
        double rightSpeed = speed - turn;

        // Step 3: Clamp both speeds to the valid motor range [-1.0, 1.0].
        //
        // Why is clamping necessary? If speed=1.0 and turn=1.0, then leftSpeed=2.0,
        // which is outside what the motor controller accepts. Without clamping, you'd get
        // unpredictable behavior. With clamping, the robot drives at full speed while turning
        // as aggressively as possible given the speed constraint.
        leftSpeed  = clamp(leftSpeed,  -1.0, 1.0);
        rightSpeed = clamp(rightSpeed, -1.0, 1.0);

        // Step 4: Send the final speeds to the motors.
        // XRPMotor.set() accepts values from -1.0 (full reverse) to 1.0 (full forward).
        m_leftMotor.set(leftSpeed);
        m_rightMotor.set(rightSpeed);
    }

    /**
     * Stops both drive motors by setting their output to zero.
     *
     * This is called as a safety measure whenever a drive command ends.
     * Without explicitly stopping, the motors would keep running at their last set speed!
     */
    public void stop() {
        m_leftMotor.set(0.0);
        m_rightMotor.set(0.0);
    }

    /**
     * Applies a deadband to a joystick input value.
     *
     * A deadband is a "dead zone" around zero where we treat the input as exactly zero.
     * Real joysticks have mechanical imperfections — when you let go of the stick it should
     * return to exactly 0.0, but in practice it might read 0.02 or -0.05 due to wear or
     * manufacturing variation.
     *
     * Without a deadband:
     *   - Robot creeps slowly when driver isn't touching the controller
     *   - Motors make buzzing sounds from tiny oscillating commands
     *
     * With a deadband of 0.1:
     *   - Any value in the range (-0.1, 0.1) is treated as 0.0
     *   - The driver must move the stick at least 10% before the robot responds
     *
     * @param value    The raw joystick value, typically in the range [-1.0, 1.0]
     * @param deadband The size of the dead zone (must be positive, e.g. 0.1)
     * @return 0.0 if |value| is within the deadband, otherwise the original value unchanged
     */
    private double applyDeadband(double value, double deadband) {
        // Math.abs() returns the absolute value (strips the negative sign if present).
        // If the joystick is within deadband distance of zero in EITHER direction, return 0.
        if (Math.abs(value) < deadband) {
            return 0.0;
        }
        // Outside the deadband — return the original value unchanged.
        return value;
    }

    /**
     * Clamps a value to a specified [min, max] range.
     *
     * "Clamping" means: if the value exceeds the maximum, cap it at the maximum.
     * If it falls below the minimum, raise it to the minimum. Otherwise, leave it alone.
     *
     * Examples:
     *   clamp(1.5, -1.0, 1.0)  → 1.0   (too high, capped at max)
     *   clamp(-2.0, -1.0, 1.0) → -1.0  (too low, raised to min)
     *   clamp(0.7, -1.0, 1.0)  → 0.7   (in range, unchanged)
     *
     * We use Math.max and Math.min to implement this cleanly:
     *   Math.min(value, max) ensures we never exceed the max.
     *   Math.max(min, ...)   ensures we never go below the min.
     *
     * @param value The input value to clamp
     * @param min   The minimum allowed output value
     * @param max   The maximum allowed output value
     * @return The clamped value, guaranteed to be in [min, max]
     */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * periodic() is called automatically by the CommandScheduler every 20 milliseconds
     * (50 times per second) regardless of what mode the robot is in.
     *
     * The standard FRC robot loop runs at 50 Hz (20ms per cycle). This method is the
     * per-subsystem equivalent of the main loop — use it to:
     *   - Read sensor values and store them in fields
     *   - Publish telemetry to SmartDashboard for debugging
     *   - Run any ongoing calculations that don't belong in a command
     *
     * Currently empty — the drivetrain doesn't need any per-loop updates beyond what
     * the command does. Uncomment the SmartDashboard lines below to add motor speed logging.
     */
    @Override
    public void periodic() {
        // Example: publish motor speeds to the SmartDashboard (enable in Driver Station)
        // SmartDashboard.putNumber("Left Motor Speed",  m_leftMotor.get());
        // SmartDashboard.putNumber("Right Motor Speed", m_rightMotor.get());
    }
}
