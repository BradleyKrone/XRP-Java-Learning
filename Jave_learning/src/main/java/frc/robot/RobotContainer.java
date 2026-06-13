// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// ── Imports ─────────────────────────────────────────────────────────────────
// Each import gives us access to a class from an external library.
// Think of it like adding a tool to your toolbox before you use it.

import edu.wpi.first.wpilibj2.command.Command;
// Command is the base type for all robot commands (actions the robot performs).

import edu.wpi.first.wpilibj2.command.StartEndCommand;
// StartEndCommand runs one piece of code when it starts and a DIFFERENT piece when it ends.
// Perfect for "hold button → do thing / release button → stop thing".

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
// CommandXboxController wraps a standard Xbox controller and exposes each button
// as a "Trigger" object we can attach commands to.

import frc.robot.Constants.OperatorConstants;
// Pulls in the USB port number for our controller from Constants.java.

import frc.robot.subsystems.HandSubsystem;
// The hand subsystem we just created.

/**
 * RobotContainer is the "glue" class of a Command-Based robot project.
 *
 * Its two main jobs are:
 *   1. Create all subsystems (HandSubsystem, DriveSubsystem, etc.) and store them here.
 *   2. Wire up controller buttons to commands in configureButtonBindings().
 *
 * Robot.java creates exactly one RobotContainer when the robot boots up, which in turn
 * creates everything else the robot needs.
 */
public class RobotContainer {

  // ── Subsystems ───────────────────────────────────────────────────────────────
  // We create our subsystem objects here so they live for the entire match.
  // "private final" keeps them hidden from other classes and prevents re-assignment.

  // HandSubsystem manages the hand motor.
  // "new HandSubsystem()" calls the constructor in HandSubsystem.java and builds the object.
  private final HandSubsystem m_handSubsystem = new HandSubsystem();

  // ── Controllers ──────────────────────────────────────────────────────────────
  // CommandXboxController wraps the Xbox controller plugged into USB port 0 on the
  // Driver Station. The port number comes from our constants so it is easy to change.

  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.XBOX_CONTROLLER_PORT);

  // ── Constructor ──────────────────────────────────────────────────────────────

  /**
   * The RobotContainer constructor runs once when the robot program starts.
   * We call configureButtonBindings() here so all buttons are set up right away.
   */
  public RobotContainer() {
    configureButtonBindings();
  }

  // ── Button Bindings ──────────────────────────────────────────────────────────

  /**
   * configureButtonBindings() connects physical controller buttons to Commands.
   *
   * How bindings work:
   *   m_driverController.a()  → returns a Trigger object that is "active" when A is held
   *   .whileTrue(command)     → runs the command for as long as the trigger is active
   *
   * The Trigger class has several useful methods:
   *   .onTrue(cmd)    → runs cmd once when button is first pressed
   *   .onFalse(cmd)   → runs cmd once when button is released
   *   .whileTrue(cmd) → starts cmd on press, interrupts/ends it on release
   *   .toggleOnTrue(cmd) → starts cmd on first press, stops it on second press
   */
  private void configureButtonBindings() {

    // ── A Button → Hand Motor ─────────────────────────────────────────────────
    //
    // Goal: hold A → hand motor runs at 0.5; release A → hand motor stops at 0.0
    //
    // We use StartEndCommand because it is designed exactly for this pattern:
    //   • First  lambda (→ m_handSubsystem.grab()) runs when the command STARTS
    //     (i.e., the moment the A button is pressed)
    //   • Second lambda (→ m_handSubsystem.stop()) runs when the command ENDS
    //     (i.e., the moment the A button is released)
    //
    // What is a lambda?
    //   () -> someCode()  is a short way to write a small, anonymous function in Java.
    //   The () means "this function takes no arguments".
    //   The -> separates the argument list from the body.
    //   So () -> m_handSubsystem.grab() means "a function that calls grab() and returns nothing".
    //
    // Why pass m_handSubsystem as the third argument?
    //   StartEndCommand needs to know which subsystem(s) it uses so WPILib can enforce the
    //   "only one command per subsystem at a time" rule. This is called declaring a "requirement".

    m_driverController.a().whileTrue(
        new StartEndCommand(
            () -> m_handSubsystem.grab(), // Called when A is PRESSED  → motor on at 0.5
            () -> m_handSubsystem.stop(), // Called when A is RELEASED → motor off at 0.0
            m_handSubsystem               // Declares this command uses HandSubsystem
        )
    );
  }

  // ── Autonomous ───────────────────────────────────────────────────────────────

  /**
   * getAutonomousCommand() is called by Robot.java at the start of the autonomous period.
   * It should return the Command you want to run during autonomous.
   *
   * For now we return null, meaning the robot does nothing in autonomous.
   * Later you could return a command that drives a path or performs a sequence of actions.
   *
   * @return The command to run in autonomous, or null for no action.
   */
  public Command getAutonomousCommand() {
    return null;
  }
}
