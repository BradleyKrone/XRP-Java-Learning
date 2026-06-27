// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

// import xrp motor controller
import edu.wpi.first.wpilibj.xrp.XRPMotor;
// import the constants class
import frc.robot.Constants;




public class ShooterMotor extends SubsystemBase {

  // Define a new motor controller for the shooter motor
  private XRPMotor m_shooterMotor = new XRPMotor(Constants.ShooterConstants.SHOOTER_MOTOR_ID); // 2 is the CAN ID of the motor controller
  
  /** Creates a new ShooterMotor. */
  public ShooterMotor() {}

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  // Turns motor on at a constant speed
  public void ShooterOn() {
    m_shooterMotor.set(Constants.ShooterConstants.SHOOTER_MOTOR_SPEED);
  }

  // Turns motor off
  public void ShooterOff() {
    m_shooterMotor.set(0);
  }
}