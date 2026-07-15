package frc.robot.ctre.configs;

import frc.robot.ctre.StatusCode;
import java.util.function.Consumer;

/**
 * The object that actually applies a {@link TalonFXConfiguration} to a motor.
 *
 * <p>This mirrors CTRE Phoenix 6's {@code com.ctre.phoenix6.configs.TalonFXConfigurator}. You get
 * one from a motor with {@code motor.getConfigurator()} and then call {@code apply(...)}:
 *
 * <pre>
 *   motor.getConfigurator().apply(config);
 * </pre>
 *
 * <p>You never construct this yourself — the {@code TalonFX} creates it for you.
 */
public class TalonFXConfigurator {
  /** Does the real work of pushing the settings onto the XRP motor. Supplied by the TalonFX. */
  private final Consumer<TalonFXConfiguration> m_applier;

  /**
   * Creates a configurator. Called by {@code TalonFX}; you do not call this.
   *
   * @param applier code (provided by the owning TalonFX) that applies a configuration
   */
  public TalonFXConfigurator(Consumer<TalonFXConfiguration> applier) {
    m_applier = applier;
  }

  /**
   * Applies the given settings to the motor.
   *
   * @param config the settings to apply
   * @return {@link StatusCode#OK} (applying always succeeds on the XRP)
   */
  public StatusCode apply(TalonFXConfiguration config) {
    m_applier.accept(config);
    return StatusCode.OK;
  }
}
