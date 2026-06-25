// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.xrp.XRPMotor;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * IntakeSubsystem represents the intake mechanism on the robot.
 *
 * In FRC command-based programming, a SUBSYSTEM is a class that:
 *   1. Owns the hardware (motors, sensors, solenoids, etc.)
 *   2. Exposes simple action methods (runIntake, stopIntake)
 *   3. Is registered with the WPILib scheduler (via SubsystemBase)
 *
 * The subsystem NEVER decides WHEN to run — that's the job of Commands.
 * The subsystem only knows HOW to do things.
 *
 * Think of a subsystem like an employee who knows their job skills,
 * and commands like a manager telling them when to use those skills.
 */
public class IntakeSubsystem extends SubsystemBase {

    // -----------------------------------------------------------------------
    // HARDWARE DECLARATIONS
    // We declare motors as 'private final' so nothing outside this class
    // can directly touch them — all control goes through our public methods.
    // -----------------------------------------------------------------------

    /**
     * Motor 1: XRP motor on channel 1.
     * This motor will spin at 100% power (full speed).
     *
     * XRPMotor channel numbers map to physical ports on the XRP board:
     *   Channel 0 = Left drive motor
     *   Channel 1 = Right drive motor (or accessory 1)
     *   Channel 2 = Accessory motor 2
     *   Channel 3 = Accessory motor 3
     */
    private final XRPMotor m_motorOne = new XRPMotor(1);

    /**
     * Motor 2: XRP motor on channel 2.
     * This motor will spin at 50% power (half speed).
     */
    private final XRPMotor m_motorTwo = new XRPMotor(2);

    /**
     * Constructor — runs once when the subsystem is created.
     * Use this to configure motors (inversions, current limits, etc.).
     * For basic XRP motors, there's nothing to configure at startup.
     */
    public IntakeSubsystem() {
        // No configuration needed for simple XRP motors.
        // On a real FRC robot with SPARK MAXes, you'd configure things here like:
        //   m_motorOne.setInverted(true);
        //   m_motorOne.setSmartCurrentLimit(40);
    }

    // -----------------------------------------------------------------------
    // PUBLIC ACTION METHODS
    // These are the "skills" the subsystem exposes. Commands will call these.
    // -----------------------------------------------------------------------

    /**
     * Starts both intake motors spinning.
     *
     * Motor speeds use values from -1.0 to 1.0:
     *   1.0  = 100% forward (full speed)
     *   0.5  = 50% forward (half speed)
     *   0.0  = stopped
     *  -1.0  = 100% reverse (full speed backward)
     */
    public void runIntake() {
        m_motorOne.set(1.0);   // Motor 1: 100% forward
        m_motorTwo.set(0.5);   // Motor 2: 50% forward
    }

    /**
     * Stops both intake motors immediately.
     * Setting speed to 0.0 cuts power — the motors coast to a stop.
     */
    public void stopIntake() {
        m_motorOne.set(0.0);
        m_motorTwo.set(0.0);
    }

    // -----------------------------------------------------------------------
    // PERIODIC
    // This method is called automatically every 20ms by the WPILib scheduler.
    // Use it to update dashboards, read sensors, or log data.
    // -----------------------------------------------------------------------

    @Override
    public void periodic() {
        // Currently empty — motors are controlled directly by commands.
        //
        // In a more advanced setup, you might add things like:
        //   SmartDashboard.putNumber("Motor 1 Speed", m_motorOne.get());
        //   SmartDashboard.putNumber("Motor 2 Speed", m_motorTwo.get());
    }
}
