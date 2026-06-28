package frc.robot.ctre.hardware;

import edu.wpi.first.wpilibj.Encoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal "wiring diagram" that maps CTRE-style device IDs onto real XRP hardware.
 *
 * <p>On a real robot, every CTRE device (motor, encoder, IMU) has a numeric ID you assign in
 * Phoenix Tuner, and the robot finds it on the CAN bus by that ID. The XRP has no CAN bus — its
 * parts are wired to fixed channels. This class is the one place that translates "device ID" into
 * "which XRP channel", so the rest of the wrapper (and your robot code) can pretend it is talking to
 * CTRE devices by ID.
 *
 * <p><b>The wiring this wrapper assumes:</b>
 *
 * <pre>
 *   TalonFX (motor) ID 0  -> XRP motor channel 0  + left  encoder on DIO 4,5
 *   TalonFX (motor) ID 1  -> XRP motor channel 1  + right encoder on DIO 6,7
 *   TalonFX (motor) ID 2  -> XRP motor channel 2  (no encoder)
 *   TalonFX (motor) ID 3  -> XRP motor channel 3  (no encoder)
 *
 *   CANcoder         ID 0  -> left  encoder on DIO 4,5
 *   CANcoder         ID 1  -> right encoder on DIO 6,7
 *
 *   Pigeon2          any   -> the single built-in XRP gyro
 * </pre>
 *
 * <p>This class is package-private on purpose — it is plumbing that students do not need to touch.
 */
final class XrpDeviceMap {
  /** The XRP wheel encoders report 585 counts for one full turn of the wheel. */
  static final double COUNTS_PER_REVOLUTION = 585.0;

  /** DIO channels for the left wheel encoder (channels A, B). */
  static final int[] LEFT_ENCODER_DIO = {4, 5};

  /** DIO channels for the right wheel encoder (channels A, B). */
  static final int[] RIGHT_ENCODER_DIO = {6, 7};

  /**
   * One shared {@link Encoder} object per DIO channel pair.
   *
   * <p>The XRP has only two physical encoders, but both a {@code TalonFX} (which exposes an
   * integrated encoder, like a real Kraken) and a {@code CANcoder} may want to read the same one.
   * WPILib will not let two {@code Encoder} objects use the same DIO channels, so we hand out a
   * single shared object instead of creating a second one.
   */
  private static final Map<String, Encoder> s_encoders = new HashMap<>();

  private XrpDeviceMap() {}

  /**
   * Returns the XRP motor channel for a TalonFX device ID.
   *
   * @param deviceId the CTRE-style device ID (0-3)
   * @return the XRP motor channel (same number)
   * @throws IllegalArgumentException if the ID is not 0-3
   */
  static int motorChannel(int deviceId) {
    if (deviceId < 0 || deviceId > 3) {
      throw new IllegalArgumentException(
          "TalonFX device ID "
              + deviceId
              + " is not valid on the XRP. Use 0 (left), 1 (right), 2, or 3.");
    }
    return deviceId;
  }

  /**
   * Returns the encoder DIO channels that live on the same gearbox as a given drive motor, or
   * {@code null} if that motor has no encoder.
   *
   * @param deviceId the TalonFX device ID
   * @return a two-element array {A, B} of DIO channels, or {@code null} for motors 2 and 3
   */
  static int[] motorEncoderChannels(int deviceId) {
    switch (deviceId) {
      case 0:
        return LEFT_ENCODER_DIO;
      case 1:
        return RIGHT_ENCODER_DIO;
      default:
        return null; // motors 2 and 3 are bare PWM outputs with no encoder
    }
  }

  /**
   * Returns the encoder DIO channels for a CANcoder device ID.
   *
   * @param deviceId the CANcoder device ID (0 = left, 1 = right)
   * @return a two-element array {A, B} of DIO channels
   * @throws IllegalArgumentException if the ID is not 0 or 1
   */
  static int[] cancoderChannels(int deviceId) {
    switch (deviceId) {
      case 0:
        return LEFT_ENCODER_DIO;
      case 1:
        return RIGHT_ENCODER_DIO;
      default:
        throw new IllegalArgumentException(
            "CANcoder device ID "
                + deviceId
                + " is not valid on the XRP. Use 0 (left encoder) or 1 (right encoder).");
    }
  }

  /**
   * Returns the shared {@link Encoder} for a DIO channel pair, creating it the first time it is
   * asked for. The encoder is scaled so that {@link Encoder#getDistance()} reads in <b>wheel
   * rotations</b> and {@link Encoder#getRate()} reads in <b>rotations per second</b>.
   *
   * @param channelA the DIO channel for encoder signal A
   * @param channelB the DIO channel for encoder signal B
   * @return the shared encoder object
   */
  static synchronized Encoder getEncoder(int channelA, int channelB) {
    String key = channelA + "," + channelB;
    Encoder encoder = s_encoders.get(key);
    if (encoder == null) {
      encoder = new Encoder(channelA, channelB);
      // 1 / 585 turns the raw count into rotations, so getDistance() == rotations.
      encoder.setDistancePerPulse(1.0 / COUNTS_PER_REVOLUTION);
      s_encoders.put(key, encoder);
    }
    return encoder;
  }
}
