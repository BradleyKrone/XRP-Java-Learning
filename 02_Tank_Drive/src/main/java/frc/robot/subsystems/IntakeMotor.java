// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
// import xrp motor controller
import edu.wpi.first.wpilibj.xrp.XRPMotor;
// import the constants class
import frc.robot.Constants;


public class IntakeMotor extends SubsystemBase {

  // Define a new motor controller for the intake motor
  private XRPMotor m_intakeMotor = new XRPMotor(Constants.IntakeConstants.INTAKE_MOTOR_ID); // 1 is the CAN ID of the motor controller

  /** Creates a new IntakeMotor. */
  // public IntakeMotor() {}

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  // Method to set the intake motor speed
  public void IntakeOn() {
    m_intakeMotor.set(Constants.IntakeConstants.INTAKE_MOTOR_SPEED);
  }

  // Method to stop the intake motor
  public void IntakeOff() {
    m_intakeMotor.set(0);
  } 

}
