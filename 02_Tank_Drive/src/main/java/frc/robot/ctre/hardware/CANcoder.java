package frc.robot.ctre.hardware;

import static edu.wpi.first.units.Units.RotationsPerSecond;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.Encoder;
import frc.robot.ctre.StatusSignal;
import frc.robot.ctre.configs.CANcoderConfiguration;
import frc.robot.ctre.configs.CANcoderConfigurator;
import frc.robot.ctre.signals.SensorDirectionValue;

/**
 * A CTRE-style rotation sensor that secretly reads an XRP wheel encoder.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.hardware.CANcoder}. You create and use
 * it exactly like the real thing:
 *
 * <pre>
 *   CANcoder encoder = new CANcoder(0);       // ID 0 = the XRP left wheel encoder
 *   double rotations = encoder.getPosition().getValueAsDouble();
 * </pre>
 *
 * <p>The XRP has two wheel encoders: ID 0 is the left one (DIO 4,5) and ID 1 is the right one
 * (DIO 6,7). These are the same physical encoders a {@code TalonFX} uses for its position reading,
 * so a {@code TalonFX} and a {@code CANcoder} pointed at the same wheel will agree.
 *
 * <p><b>When you move to the real robot:</b> change the import from
 * {@code frc.robot.ctre.hardware.CANcoder} to {@code com.ctre.phoenix6.hardware.CANcoder}.
 */
public class CANcoder {
  /** The underlying shared XRP wheel encoder. */
  private final Encoder m_encoder;

  /** Lets {@code getConfigurator().apply(...)} change this sensor's settings. */
  private final CANcoderConfigurator m_configurator;

  /** Added to the raw reading so {@link #setPosition(double)} can move the zero point. */
  private double m_positionOffset;

  /**
   * Creates a rotation sensor for the given device ID.
   *
   * @param deviceId the CTRE-style device ID (0 = left wheel, 1 = right wheel)
   */
  public CANcoder(int deviceId) {
    int[] channels = XrpDeviceMap.cancoderChannels(deviceId);
    m_encoder = XrpDeviceMap.getEncoder(channels[0], channels[1]);
    m_configurator = new CANcoderConfigurator(this::applyConfiguration);
  }

  /**
   * Creates a rotation sensor for the given device ID. The {@code canbus} name is accepted to match
   * the real robot, but it is ignored on the XRP.
   *
   * @param deviceId the CTRE-style device ID
   * @param canbus the CAN bus name (ignored on the XRP)
   */
  public CANcoder(int deviceId, String canbus) {
    this(deviceId);
  }

  /**
   * The total rotation measured, in rotations (can be more than 1, and can be negative).
   *
   * @return a live signal carrying the position in rotations
   */
  public StatusSignal<Angle> getPosition() {
    return new StatusSignal<>(
        "Position", () -> m_encoder.getDistance() + m_positionOffset, Units.Rotations::of);
  }

  /**
   * The position within a single turn, in rotations from 0.0 up to (but not including) 1.0.
   *
   * <p>A real CANcoder knows its absolute angle even after a reboot; the XRP encoder cannot, so this
   * is simply the total rotation wrapped into one turn.
   *
   * @return a live signal carrying the within-one-turn position in rotations
   */
  public StatusSignal<Angle> getAbsolutePosition() {
    return new StatusSignal<>(
        "AbsolutePosition",
        () -> {
          double rotations = m_encoder.getDistance() + m_positionOffset;
          return rotations - Math.floor(rotations); // wrap into [0.0, 1.0)
        },
        Units.Rotations::of);
  }

  /**
   * The rotation speed, in rotations per second.
   *
   * @return a live signal carrying the velocity in rotations per second
   */
  public StatusSignal<AngularVelocity> getVelocity() {
    return new StatusSignal<>("Velocity", m_encoder::getRate, RotationsPerSecond::of);
  }

  /**
   * Tells the sensor that its current position should be treated as the given value (in rotations).
   *
   * @param rotations the value the current position should now report
   */
  public void setPosition(double rotations) {
    m_positionOffset = rotations - m_encoder.getDistance();
  }

  /**
   * Returns the configurator used to apply settings to this sensor.
   *
   * @return this sensor's configurator
   */
  public CANcoderConfigurator getConfigurator() {
    return m_configurator;
  }

  /** Applies a {@link CANcoderConfiguration} to the underlying XRP encoder. */
  private void applyConfiguration(CANcoderConfiguration config) {
    m_encoder.setReverseDirection(
        config.MagnetSensor.SensorDirection == SensorDirectionValue.Clockwise_Positive);
  }
}
