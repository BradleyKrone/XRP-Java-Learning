package frc.robot.ctre.configs;

import frc.robot.ctre.StatusCode;
import java.util.function.Consumer;

/**
 * The object that actually applies a {@link CANcoderConfiguration} to a CANcoder.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.configs.CANcoderConfigurator}. You get
 * one with {@code encoder.getConfigurator()} and then call {@code apply(...)}. You never construct
 * this yourself — the {@code CANcoder} creates it for you.
 */
public class CANcoderConfigurator {
  /** Does the real work of pushing the settings onto the XRP encoder. Supplied by the CANcoder. */
  private final Consumer<CANcoderConfiguration> m_applier;

  /**
   * Creates a configurator. Called by {@code CANcoder}; you do not call this.
   *
   * @param applier code (provided by the owning CANcoder) that applies a configuration
   */
  public CANcoderConfigurator(Consumer<CANcoderConfiguration> applier) {
    m_applier = applier;
  }

  /**
   * Applies the given settings to the encoder.
   *
   * @param config the settings to apply
   * @return {@link StatusCode#OK} (applying always succeeds on the XRP)
   */
  public StatusCode apply(CANcoderConfiguration config) {
    m_applier.accept(config);
    return StatusCode.OK;
  }
}
