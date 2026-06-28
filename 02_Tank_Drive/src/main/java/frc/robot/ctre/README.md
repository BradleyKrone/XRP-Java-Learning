# `frc.robot.ctre` — A CTRE Phoenix 6 Look-Alike for the XRP

## Why this exists

On the **XRP** robot you normally use WPILib classes: `XRPMotor`, `Encoder`, `XRPGyro`.
On the **competition robot** you use **CTRE Phoenix 6** hardware instead:

| Competition part | Phoenix 6 class |
|---|---|
| Kraken X60 motor | `TalonFX` |
| CANcoder encoder | `CANcoder` |
| Pigeon 2 IMU | `Pigeon2` |

That means students learn one API on the XRP and then have to relearn a different one
the moment the season starts. This package removes that cliff. It provides classes that
**look and behave like Phoenix 6** but actually drive the XRP hardware underneath. You
write competition-style code now, on the cheap robot.

## How to use it

Import from `frc.robot.ctre.*` and write Phoenix 6 code:

```java
import frc.robot.ctre.hardware.TalonFX;
import frc.robot.ctre.controls.DutyCycleOut;
import frc.robot.ctre.configs.TalonFXConfiguration;
import frc.robot.ctre.signals.InvertedValue;

TalonFX leftMotor  = new TalonFX(0);   // ID 0 = XRP left motor
TalonFX rightMotor = new TalonFX(1);   // ID 1 = XRP right motor

// Invert the right motor the Phoenix 6 way (instead of XRPMotor.setInverted):
var rightConfig = new TalonFXConfiguration();
rightConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
rightMotor.getConfigurator().apply(rightConfig);

// Drive:
leftMotor.set(0.5);                            // simple percent power
rightMotor.setControl(new DutyCycleOut(0.5));  // or the control-request style

// Read sensors:
double leftRotations = leftMotor.getPosition().getValueAsDouble();
```

## The transition to the real robot (this is the whole point)

When the season starts and you move to real Kraken/CANcoder/Pigeon hardware:

1. **Install the CTRE Phoenix 6 vendordep** (in VS Code: *WPILib: Manage Vendor
   Libraries → Install new libraries (online)*, paste CTRE's Phoenix 6 URL).
2. **Find-and-replace `frc.robot.ctre` → `com.ctre.phoenix6`** in your `import` lines.

That's it. Every class name, method name, public field, and enum value in this package
was chosen to match Phoenix 6 exactly, so the body of your robot code does not change.

| This wrapper | Real Phoenix 6 |
|---|---|
| `frc.robot.ctre.hardware.TalonFX` / `CANcoder` / `Pigeon2` | `com.ctre.phoenix6.hardware.*` |
| `frc.robot.ctre.controls.DutyCycleOut` / `VoltageOut` / `NeutralOut` | `com.ctre.phoenix6.controls.*` |
| `frc.robot.ctre.configs.TalonFXConfiguration` (etc.) | `com.ctre.phoenix6.configs.*` |
| `frc.robot.ctre.signals.NeutralModeValue` (etc.) | `com.ctre.phoenix6.signals.*` |
| `frc.robot.ctre.StatusSignal` / `StatusCode` | `com.ctre.phoenix6.*` |

## How device IDs map to XRP hardware

A real Phoenix 6 device is found by a numeric ID you set in Phoenix Tuner. The XRP has no
CAN bus, so this wrapper translates IDs into fixed XRP channels (see `XrpDeviceMap`):

```
TalonFX  ID 0  -> XRP motor 0  + left  wheel encoder (DIO 4,5)
TalonFX  ID 1  -> XRP motor 1  + right wheel encoder (DIO 6,7)
TalonFX  ID 2  -> XRP motor 2  (no encoder)
TalonFX  ID 3  -> XRP motor 3  (no encoder)

CANcoder ID 0  -> left  wheel encoder (DIO 4,5)
CANcoder ID 1  -> right wheel encoder (DIO 6,7)

Pigeon2  any   -> the single built-in XRP gyro
```

Using an ID outside these ranges throws a clear error explaining the valid choices.

## Honest differences from real hardware

The XRP is simpler than competition hardware, so a few things are approximations. They
are intentional and documented so nothing is a surprise:

- **Brake mode does nothing.** XRP motors have no brake circuit, so
  `NeutralModeValue.Brake` is remembered but the motor still coasts. On a real Kraken it
  truly brakes.
- **Voltage control is approximate.** `VoltageOut` / `getMotorVoltage()` convert between
  volts and percent power using the live battery voltage, because XRP motors only
  understand percent. A real Kraken does true voltage control.
- **Absolute position is not truly absolute.** A real CANcoder remembers its angle across
  reboots; the XRP encoder cannot, so `getAbsolutePosition()` is just the total rotation
  wrapped into one turn.
- **Spare motors have no encoder.** IDs 2 and 3 are bare motor ports, so their
  `getPosition()` / `getVelocity()` always read 0.

## Example: a Phoenix 6-style Drivetrain

> This is a reference snippet, **not** a finished file — building the `Drivetrain` is your
> exercise. It shows how the wrapper is meant to be used.

```java
package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;
import frc.robot.ctre.hardware.TalonFX;
import frc.robot.ctre.configs.TalonFXConfiguration;
import frc.robot.ctre.signals.InvertedValue;

public class Drivetrain extends SubsystemBase {
  private final TalonFX m_left  = new TalonFX(DriveConstants.LEFT_DRIVE_TALON_ID);
  private final TalonFX m_right = new TalonFX(DriveConstants.RIGHT_DRIVE_TALON_ID);

  public Drivetrain() {
    var rightConfig = new TalonFXConfiguration();
    rightConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive; // right side is reversed
    m_right.getConfigurator().apply(rightConfig);
  }

  public void tankDrive(double leftPercent, double rightPercent) {
    m_left.set(leftPercent);
    m_right.set(rightPercent);
  }
}
```
