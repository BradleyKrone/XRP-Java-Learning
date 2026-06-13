// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Drivetrain;

/**
 * ArcadeDriveCommand controls the Drivetrain subsystem during teleop using arcade-style inputs.
 *
 * --- What is a Command? ---
 * In FRC's command-based framework, "Commands" represent actions the robot takes.
 * They define WHAT the robot does, while Subsystems define HOW the robot does it.
 *
 * Every command has a lifecycle with four phases:
 *   1. initialize() — called once when the command starts
 *   2. execute()    — called repeatedly (every 20ms) while the command runs
 *   3. isFinished() — checked every loop; return true to stop the command naturally
 *   4. end()        — called once when the command stops (naturally or interrupted)
 *
 * --- Why use DoubleSupplier instead of a plain double? ---
 * If we passed a plain double (e.g., 0.5) to the constructor, it would be fixed forever.
 * But joystick values CHANGE over time — we need to read a fresh value every loop iteration.
 *
 * A DoubleSupplier is a Java functional interface that means "something that gives you a double."
 * You call .getAsDouble() on it to get the current value. By storing a DoubleSupplier instead of
 * a double, the command can re-read the joystick position every time execute() runs.
 *
 * In RobotContainer, we pass a lambda like:  () -> controller.getLeftY()
 * That lambda IS the DoubleSupplier — calling getAsDouble() on it runs controller.getLeftY()
 * and returns the current axis value at that moment.
 */
public class ArcadeDriveCommand extends Command {

    // The drivetrain subsystem this command controls.
    // Stored as a field so execute() and end() can call methods on it.
    private final Drivetrain m_drivetrain;

    // Suppliers that provide the speed and turn values each loop iteration.
    // These are set by the constructor and read by execute().
    // m_speedSupplier returns the left joystick Y axis value when called.
    // m_turnSupplier  returns the right joystick X axis value when called.
    private final DoubleSupplier m_speedSupplier;
    private final DoubleSupplier m_turnSupplier;

    /**
     * Creates a new ArcadeDriveCommand.
     *
     * @param drivetrain    The Drivetrain subsystem to control. Passed in so this command
     *                      doesn't have to know how the Drivetrain was created.
     * @param speedSupplier Provides the forward/backward speed input each loop. Typically
     *                      wired to the left joystick Y axis: () -> controller.getLeftY()
     * @param turnSupplier  Provides the turning input each loop. Typically wired to the
     *                      right joystick X axis: () -> controller.getRightX()
     */
    public ArcadeDriveCommand(Drivetrain drivetrain, DoubleSupplier speedSupplier, DoubleSupplier turnSupplier) {
        m_drivetrain    = drivetrain;
        m_speedSupplier = speedSupplier;
        m_turnSupplier  = turnSupplier;

        // addRequirements() is CRITICAL in command-based programming.
        //
        // It declares that this command "requires" (owns) the Drivetrain subsystem.
        // The CommandScheduler uses this information to enforce a rule:
        //   Only ONE command can require a given subsystem at a time.
        //
        // If two commands both require the Drivetrain and one starts while the other is running,
        // the scheduler automatically interrupts the first command and starts the second.
        // This prevents two commands from fighting over the motors simultaneously.
        //
        // As the default command, ArcadeDriveCommand runs whenever nothing else requires
        // the Drivetrain. When an autonomous command starts and requires the Drivetrain,
        // ArcadeDriveCommand is interrupted automatically. When auto finishes, the scheduler
        // restarts ArcadeDriveCommand as the default command.
        addRequirements(m_drivetrain);
    }

    /**
     * initialize() is called once when this command is first scheduled to run.
     *
     * Use this for any one-time setup that should happen at the START of the command.
     * For a continuous drive command, there's nothing special to set up — we just start
     * reading the joystick in execute() right away.
     */
    @Override
    public void initialize() {
        // Nothing to initialize for a continuous teleop drive command.
    }

    /**
     * execute() is called repeatedly every 20ms while this command is running.
     *
     * This is the heart of the command — the code here runs 50 times per second during teleop.
     * Each call:
     *   1. Reads the current joystick axis values via the DoubleSuppliers
     *   2. Applies the Y-axis inversion fix (explained below)
     *   3. Passes the corrected values to the drivetrain
     */
    @Override
    public void execute() {
        // Call getAsDouble() on each supplier to get the CURRENT joystick axis reading.
        // This re-reads the axis value fresh every loop — critical for responsive driving.
        double rawSpeed = m_speedSupplier.getAsDouble();
        double rawTurn  = m_turnSupplier.getAsDouble();

        // --- Joystick Y-Axis Inversion ---
        // WPILib's joystick Y axes are inverted by convention (matching hardware convention):
        //   Pushing the stick FORWARD (away from you) returns a NEGATIVE value (e.g., -0.8)
        //   Pulling the stick BACKWARD (toward you) returns a POSITIVE value (e.g., +0.8)
        //
        // This is counter-intuitive! We want:
        //   Push forward → positive speed → robot goes forward
        //   Pull back    → negative speed → robot goes backward
        //
        // The fix is simple: negate the raw Y value before using it.
        // After negation: forward push → positive, backward pull → negative. Much more natural.
        //
        // The X axis does NOT need negation:
        //   Pushing right → positive turn → robot turns right. That's already correct.
        double speed = -rawSpeed;   // negated: forward = positive
        double turn  =  rawTurn;    // unchanged: right = positive

        // Send the processed speed and turn values to the drivetrain.
        // The Drivetrain.arcadeDrive() method handles the deadband, wheel mixing, and clamping.
        m_drivetrain.arcadeDrive(speed, turn);
    }

    /**
     * isFinished() is checked after every execute() call to decide if the command should stop.
     *
     * Returning false means "keep running forever — don't stop on your own."
     *
     * This is the correct behavior for a default teleop drive command. We always want the
     * driver to have control. The only things that should stop this command are:
     *   - Teleop mode ending (robot disabled or auto starts)
     *   - Another command requiring the Drivetrain (e.g., an auto drive command)
     *
     * Both of those are handled externally by the scheduler, so we just return false here.
     */
    @Override
    public boolean isFinished() {
        // Never finish on our own — run until interrupted or the mode ends.
        return false;
    }

    /**
     * end() is called exactly once when this command stops, for any reason.
     *
     * @param interrupted true  → the command was cut short by an external interruption
     *                           (another command took over, or the mode ended)
     *                    false → the command stopped because isFinished() returned true
     *                           (won't happen here since we always return false)
     *
     * Safety rule: Always stop the motors when a drive command ends.
     * If we don't, the last motor speed stays in effect — the robot keeps moving after
     * the command stops, which can cause crashes or injuries. Always call stop() in end().
     */
    @Override
    public void end(boolean interrupted) {
        // Stop the drivetrain motors as a safety measure whenever this command ends.
        m_drivetrain.stop();
    }
}
