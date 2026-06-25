// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;

/**
 * RunIntakeCommand tells the intake subsystem to spin its motors.
 *
 * In FRC command-based programming, a COMMAND is a class that:
 *   1. Borrows a subsystem (via addRequirements)
 *   2. Describes WHEN to do something (initialize, execute, end)
 *   3. Knows when it is done (isFinished)
 *
 * This command is designed to work in TWO places:
 *
 *   TELEOP: Bound to the A button with whileTrue() in RobotContainer.
 *           Button press  → initialize() → motors spin
 *           Button release → end()       → motors stop
 *
 *   AUTO:   Scheduled by getAutonomousCommand() with .withTimeout(N).
 *           Scheduler starts it → initialize() → motors spin
 *           N seconds pass      → end()        → motors stop
 *
 * The same command class handles both cases — this is code reuse in action!
 */
public class RunIntakeCommand extends Command {

    /**
     * A reference to the intake subsystem we will control.
     * We store it here so initialize(), execute(), and end() can all use it.
     */
    private final IntakeSubsystem m_intake;

    /**
     * Constructor — called once when a RunIntakeCommand object is created
     * (e.g., when the robot is turned on, or when auto starts).
     *
     * @param intake The IntakeSubsystem instance to control.
     *               This is passed in from RobotContainer so there is only
     *               ever ONE intake subsystem shared across the whole robot.
     */
    public RunIntakeCommand(IntakeSubsystem intake) {
        m_intake = intake;

        // addRequirements is critical!
        //
        // It tells the WPILib scheduler: "This command needs exclusive access
        // to the intake subsystem." If another command tries to use the intake
        // while this one is running, the scheduler will automatically cancel
        // the older command before starting the new one.
        //
        // This prevents dangerous situations like two commands fighting over
        // the same motor at the same time.
        addRequirements(m_intake);
    }

    /**
     * initialize() is called ONCE when the command is first scheduled.
     *
     * This is the right place to START the motors because we only want to
     * call runIntake() once — not every 20ms. Once the motors are set to a
     * speed, they hold that speed until we change it.
     *
     * In teleop: called when the A button is first pressed.
     * In auto:   called when the scheduler picks up this command.
     */
    @Override
    public void initialize() {
        m_intake.runIntake();
    }

    /**
     * execute() is called repeatedly every 20ms while the command is running.
     *
     * We leave this empty because we already set the motor speed in initialize().
     * Motors don't need to be "re-told" to keep spinning — they just do.
     *
     * You would use execute() for things that need to update continuously,
     * like reading a joystick value or checking a sensor and adjusting speed.
     */
    @Override
    public void execute() {
        // Nothing to do — motors hold their speed automatically.
    }

    /**
     * end() is called ONCE when the command finishes, for any reason:
     *   - The button was released (interrupted = true, via whileTrue)
     *   - .withTimeout() expired (interrupted = true)
     *   - isFinished() returned true (interrupted = false)
     *
     * ALWAYS clean up in end(). If we didn't stop the motors here, they
     * would keep spinning forever even after the button was released!
     *
     * @param interrupted true if stopped by an outside force, false if isFinished() triggered it
     */
    @Override
    public void end(boolean interrupted) {
        m_intake.stopIntake();
    }

    /**
     * isFinished() is checked every 20ms. If it returns true, the command
     * ends itself naturally and end(false) is called.
     *
     * We return FALSE here because this command should run indefinitely —
     * it has no natural stopping condition of its own. It relies on external
     * forces to stop it:
     *   - In teleop: whileTrue() cancels it when the button is released
     *   - In auto:   .withTimeout(N) cancels it after N seconds
     *
     * If you returned true here immediately, the motors would never spin.
     */
    @Override
    public boolean isFinished() {
        return false; // Run forever until externally cancelled
    }
}
