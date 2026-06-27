// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
// import the subsystems
import frc.robot.subsystems.IntakeMotor;


/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class IntakeState extends Command {
  private final IntakeMotor m_intake;

  /**
   * Creates a new IntakeState.
   * 
   * This is the CONSTRUCTOR - it runs once when you create this command (e.g. "new IntakeState(m_intakeMotor)").
   * The 'intake' parameter lets you pass in the actual motor object from RobotContainer
   * so this command knows which motor to control.
   */
  public IntakeState(IntakeMotor intake) {
    // Save the motor object that was passed in so we can use it in other
    // methods like initialize(). Without this, initialize() wouldn't know
    // what m_intake is referring to.
    m_intake = intake;

    // Tell the command scheduler that this command "owns" the IntakeMotor subsystem.
    // This prevents two commands from trying to control the same motor at the same time.
    // If another command tries to use IntakeMotor while this one is running,
    // the scheduler will automatically stop one of them.
    addRequirements(m_intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // Turn on the intake motor when the command is initialized
    m_intake.IntakeOn();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // Turn off the intake motor when the command ends
    m_intake.IntakeOff();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
