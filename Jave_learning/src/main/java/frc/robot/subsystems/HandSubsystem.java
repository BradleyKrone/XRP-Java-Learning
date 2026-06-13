// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Every Java file must start with a "package" statement.
// It tells Java which folder this file lives in.
// "frc.robot.subsystems" maps to the folder src/main/java/frc/robot/subsystems/
package frc.robot.subsystems;

// "import" statements bring in code written by other people (libraries) so we can use it.
// Without these, Java would not know what XRPMotor or SubsystemBase mean.
import edu.wpi.first.wpilibj.xrp.XRPMotor;          // Controls a physical motor on the XRP robot
import edu.wpi.first.wpilibj2.command.SubsystemBase; // Base class that makes this a WPILib subsystem
import frc.robot.Constants.HandConstants;             // Our own constants defined in Constants.java

/**
 * HandSubsystem controls the hand (gripper) motor on the robot.
 *
 * What is a Subsystem?
 *   In WPILib's Command-Based framework, a "subsystem" represents one physical part of the robot
 *   (like the drive train, an arm, or a gripper). Subsystems own the hardware objects (motors,
 *   sensors) for that part of the robot.
 *
 *   The key rule: only ONE command can use a subsystem at a time. WPILib enforces this
 *   automatically once you extend SubsystemBase and declare the subsystem as a "requirement"
 *   inside commands. This prevents two commands from fighting over the same motor.
 *
 * "extends SubsystemBase" means HandSubsystem IS a SubsystemBase.
 *   Java inheritance lets our class inherit all the behavior of SubsystemBase while we add
 *   our own hand-specific methods on top of it.
 */
public class HandSubsystem extends SubsystemBase {

  // ── Hardware ────────────────────────────────────────────────────────────────

  // "private" means only code inside THIS class can touch this variable directly.
  // That is good design — outsiders should call our methods (open/stop) instead of
  // reaching in and changing the motor themselves.
  //
  // "final" means m_handMotor will always point to the same XRPMotor object;
  // we cannot accidentally replace it with a different motor later.
  private final XRPMotor m_handMotor;

  // ── Constructor ─────────────────────────────────────────────────────────────

  /**
   * A constructor is a special method that runs ONCE when you create a new object.
   * Here we set up the motor so it is ready to use.
   *
   * The "m_" prefix on variable names is a common Java/WPILib convention meaning
   * "member variable" — it helps you tell apart local variables from class-level ones.
   */
  public HandSubsystem() {
    // XRPMotor(channel) creates a motor object connected to the given channel on the XRP board.
    // We read the channel number from our constants file so it is easy to change later.
    m_handMotor = new XRPMotor(HandConstants.HAND_MOTOR_CHANNEL);
  }

  // ── Public Methods ──────────────────────────────────────────────────────────
  // These are the "API" of this subsystem — the actions other parts of the code can request.

  /**
   * Runs the hand motor at the preset grab speed defined in HandConstants.
   *
   * Calling motor.set(speed) tells the motor controller to spin the motor at the given
   * speed. The value must be between -1.0 and 1.0.
   */
  public void grab() {
    // HAND_MOTOR_SPEED is 0.5 → 50% power forward
    m_handMotor.set(HandConstants.HAND_MOTOR_SPEED);
  }

  /**
   * Stops the hand motor by setting its speed to 0.
   * The motor will coast (spin freely) until it slows down on its own.
   */
  public void stop() {
    // 0.0 means no power; the motor will coast to a stop
    m_handMotor.set(HandConstants.HAND_MOTOR_STOP);
  }

  /**
   * Sets the hand motor to any speed you pass in.
   * This is a more flexible version — useful if you later want variable speed (e.g., from a trigger).
   *
   * @param speed A value from -1.0 (full reverse) to 1.0 (full forward). 0.0 = stopped.
   */
  public void setSpeed(double speed) {
    m_handMotor.set(speed);
  }

  // ── Periodic ────────────────────────────────────────────────────────────────

  /**
   * periodic() is called automatically by the WPILib scheduler roughly every 20 milliseconds
   * (about 50 times per second) while the robot is running.
   *
   * Right now we do not need to do anything on each loop tick for the hand, so this is empty.
   * Later you could add sensor reads or safety checks here.
   */
  @Override
  public void periodic() {
    // Nothing needed here yet.
    // The @Override annotation tells Java (and other programmers) that we are intentionally
    // replacing a method that already exists in SubsystemBase.
  }
}
