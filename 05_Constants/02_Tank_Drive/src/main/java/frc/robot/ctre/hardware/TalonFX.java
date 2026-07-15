package frc.robot.ctre.hardware;

import static edu.wpi.first.units.Units.RotationsPerSecond;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.xrp.XRPMotor;
import frc.robot.ctre.StatusSignal;
import frc.robot.ctre.configs.TalonFXConfiguration;
import frc.robot.ctre.configs.TalonFXConfigurator;
import frc.robot.ctre.controls.ControlRequest;
import frc.robot.ctre.controls.DutyCycleOut;
import frc.robot.ctre.controls.NeutralOut;
import frc.robot.ctre.signals.InvertedValue;

/**
 * A CTRE-style motor controller that secretly drives an XRP motor.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.hardware.TalonFX} — the controller built
 * into a Kraken X60. You create and use it exactly like the real thing:
 *
 * <pre>
 *   TalonFX motor = new TalonFX(0);          // ID 0 = the XRP left motor
 *   motor.set(0.5);                          // 50% power
 *   motor.setControl(new DutyCycleOut(0.5)); // the Phoenix 6 way to do the same thing
 *   double rotations = motor.getPosition().getValueAsDouble();
 * </pre>
 *
 * <p><b>How IDs map to XRP hardware</b> is described in {@code XrpDeviceMap}: ID 0 is the left motor
 * (with the left wheel encoder), ID 1 is the right motor (with the right wheel encoder), and IDs 2
 * and 3 are the spare motor ports (which have no encoder).
 *
 * <p><b>When you move to the real robot:</b> change the import from
 * {@code frc.robot.ctre.hardware.TalonFX} to {@code com.ctre.phoenix6.hardware.TalonFX} and the rest
 * of your code keeps working.
 */
public class TalonFX {
  /** The underlying XRP motor this class drives. */
  private final XRPMotor m_motor;

  /** The wheel encoder on this motor's gearbox, or {@code null} for the spare motors (2, 3). */
  private final Encoder m_encoder;

  /** Lets {@code getConfigurator().apply(...)} change this motor's settings. */
  private final TalonFXConfigurator m_configurator;

  /**
   * Added to the raw encoder reading so {@link #setPosition(double)} can "move" the zero point
   * without touching the shared encoder.
   */
  private double m_positionOffset;

  /**
   * Creates a motor controller for the given device ID.
   *
   * @param deviceId the CTRE-style device ID (0 = left, 1 = right, 2 and 3 = spare)
   */
  public TalonFX(int deviceId) {
    m_motor = new XRPMotor(XrpDeviceMap.motorChannel(deviceId));

    int[] encoderChannels = XrpDeviceMap.motorEncoderChannels(deviceId);
    m_encoder = (encoderChannels == null) ? null : XrpDeviceMap.getEncoder(encoderChannels[0], encoderChannels[1]);

    m_configurator = new TalonFXConfigurator(this::applyConfiguration);
  }

  /**
   * Creates a motor controller for the given device ID. The {@code canbus} name is accepted so your
   * code matches the real robot, but it is ignored on the XRP (which has no CAN bus).
   *
   * @param deviceId the CTRE-style device ID
   * @param canbus the CAN bus name (ignored on the XRP)
   */
  public TalonFX(int deviceId, String canbus) {
    this(deviceId);
  }

  /**
   * Runs the motor at a fraction of full power.
   *
   * @param dutyCycle power from -1.0 (full reverse) to +1.0 (full forward)
   */
  public void set(double dutyCycle) {
    m_motor.set(dutyCycle);
  }

  /**
   * Runs the motor according to a control request — the Phoenix 6 way to command a motor.
   *
   * <p>Supported requests: {@link DutyCycleOut} and {@link NeutralOut}.
   *
   * @param request the control request describing how to run the motor
   */
  public void setControl(ControlRequest request) {
    if (request instanceof DutyCycleOut dutyCycle) {
      m_motor.set(dutyCycle.Output);
    } else if (request instanceof NeutralOut) {
      m_motor.set(0.0);
    } else {
      throw new IllegalArgumentException(
          "This XRP TalonFX wrapper does not support control request: "
              + request.getClass().getSimpleName());
    }
  }

  /** Stops the motor. */
  public void stopMotor() {
    m_motor.set(0.0);
  }

  /**
   * Returns the configurator used to apply settings to this motor.
   *
   * @return this motor's configurator
   */
  public TalonFXConfigurator getConfigurator() {
    return m_configurator;
  }

  /**
   * The motor's position, in rotations of the wheel.
   *
   * <p>Spare motors (IDs 2 and 3) have no encoder, so their position always reads 0.
   *
   * @return a live signal carrying the position in rotations
   */
  public StatusSignal<Angle> getPosition() {
    return new StatusSignal<>(
        "Position",
        () -> (m_encoder == null) ? 0.0 : m_encoder.getDistance() + m_positionOffset,
        Units.Rotations::of);
  }

  /**
   * The motor's speed, in rotations per second.
   *
   * <p>Spare motors (IDs 2 and 3) have no encoder, so their velocity always reads 0.
   *
   * @return a live signal carrying the velocity in rotations per second
   */
  public StatusSignal<AngularVelocity> getVelocity() {
    return new StatusSignal<>(
        "Velocity",
        () -> (m_encoder == null) ? 0.0 : m_encoder.getRate(),
        RotationsPerSecond::of);
  }

  /**
   * Tells the motor that its current position should be treated as the given value (in rotations).
   * Useful for "zeroing" before a move.
   *
   * @param rotations the value the current position should now report
   */
  public void setPosition(double rotations) {
    if (m_encoder != null) {
      m_positionOffset = rotations - m_encoder.getDistance();
    }
  }

  /** Applies a {@link TalonFXConfiguration} to the underlying XRP motor. */
  private void applyConfiguration(TalonFXConfiguration config) {
    m_motor.setInverted(config.MotorOutput.Inverted == InvertedValue.Clockwise_Positive);
  }
}
