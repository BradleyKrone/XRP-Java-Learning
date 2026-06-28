# CLAUDE.md — XRP Tank Drive

> **AI Assistant Sync Note:** This project is developed using both **Claude** and **GitHub Copilot**.
> `CLAUDE.md` is read by Claude; `.github/copilot-instructions.md` is read by GitHub Copilot.
> **When you update one file, update the other to keep them in sync.**

## About This Exercise

**Exercise 02 — Tank Drive** is the first project where the student gets the robot moving. Students learn how to program the left and right motors independently to create tank drive — the foundational movement system of the XRP robot. The student creates a `Drivetrain` subsystem with left and right `XRPMotor` instances (remember to invert the right motor), writes a drive command that reads the Xbox controller's left and right joystick Y-axes and passes those values directly to each motor, and wires everything together in `RobotContainer`. By the end of this exercise the robot can be driven around under driver control in simulation or on real hardware.

## Build Commands

```bash
# Windows
.\gradlew build           # compile the project
.\gradlew simulateJava    # run in WPILib simulation (opens sim GUI + virtual driver station)
.\gradlew clean           # clear build outputs

# macOS / Linux
./gradlew build
./gradlew simulateJava
./gradlew clean
```

Simulation connects to a physical XRP robot over Wi-Fi at `192.168.42.1` if one is present.

## Project Context

This is an **educational FRC robot project** targeting the **XRP (eXtensible Robotics Platform)** —
a small, affordable two-wheeled robot designed for learning FRC programming. It uses the same
WPILib command-based Java programming model as a full competition robot.

- **Robot type**: Tank drive (left and right wheels controlled independently)
- **WPILib version**: 2026.2.1 | **Java version**: 17
- **Purpose**: Learning scaffold for students. The skeleton is **intentionally incomplete** —
  empty subsystems/commands are exercises to fill in, not bugs.

## Architecture

```
Main.java → Robot.java → RobotContainer.java → subsystems/ + commands/
```

| File / Folder | Role |
|---|---|
| `Main.java` | Entry point. Never changed. |
| `Robot.java` | Control loop. Calls `CommandScheduler.run()` every 20 ms. Rarely changed. |
| `RobotContainer.java` | Wiring hub. Creates subsystems, controllers, maps buttons to commands. |
| `Constants.java` | All configuration values (ports, speeds, IDs). No logic allowed here. |
| `subsystems/` | One `.java` file per physical robot part. Extend `SubsystemBase`. |
| `commands/` | One `.java` file per robot action. Extend `Command`. |

## WPILib Command-Based Framework

- `CommandScheduler` is the engine — called once per 20 ms loop in `Robot.robotPeriodic()`.
- A **subsystem** owns the hardware (motors, sensors) for one robot part. Only one command
  may use a subsystem at a time.
- A **command** is an action. It has four lifecycle methods: `initialize()`, `execute()`,
  `isFinished()`, `end(boolean interrupted)`.
- Commands declare their subsystem dependencies with `addRequirements(subsystem)` in the
  constructor. If a new command needs an in-use subsystem, the old command is interrupted.
- Button bindings live in `RobotContainer.configureButtonBindings()` using trigger objects
  (e.g. `CommandXboxController`).

## Conventions

- Member variables: `m_` prefix (e.g. `m_drivetrain`, `m_controller`).
- All config values (port numbers, speeds, controller IDs): `Constants.java` only.
- Packages: `frc.robot` (top-level), `frc.robot.subsystems`, `frc.robot.commands`.
- Subsystems injected into commands via constructor parameters (dependency injection).

## Hardware Reference

**Controller:** `CommandXboxController` at port 0.

**Motors** (`XRPMotor` class):

| Channel | Component | Notes |
|---|---|---|
| 0 | Left Motor | |
| 1 | Right Motor | **Must be inverted** — positive output spins backward by default |
| 2 | Motor 3 (optional expansion) | |
| 3 | Motor 4 (optional expansion) | |

**Servos** (`XRPServo` class):

| Channel | Component |
|---|---|
| 4 | Servo 1 |
| 5 | Servo 2 |

**Encoders** (`Encoder` class, DIO channels — 585 counts/wheel revolution):

| DIO Channels | Component |
|---|---|
| 4, 5 | Left Encoder (A, B) |
| 6, 7 | Right Encoder (A, B) |

**Onboard I/O** (DIO channels):

| DIO Channel | Component | WPILib Class |
|---|---|---|
| 0 | USER Button | `DigitalInput` |
| 1 | Green LED | `DigitalOutput` |

**Analog Sensors** (`AnalogInput` class):

| Channel | Component |
|---|---|
| 0 | Left Reflectance Sensor (0V = white, 5V = black) |
| 1 | Right Reflectance Sensor (0V = white, 5V = black) |
| 2 | Ultrasonic Rangefinder (0V = 20mm, 5V = 4000mm) |

**IMU:** `XRPGyro` — built-in LSM6DSOX, 3-axis gyro + 3-axis accelerometer. No channel needed.

**Wheel specs:** 60mm diameter, 155mm trackwidth.

## CTRE-Style Hardware Wrapper (`frc.robot.ctre`)

The competition robot uses **CTRE Phoenix 6** hardware — **Kraken X60** motors (`TalonFX`),
**CANcoder** encoders, and a **Pigeon 2** IMU (`Pigeon2`) — not the raw XRP classes. To stop
students from having to relearn everything at the start of the season, this project ships a
**self-contained Phoenix 6 look-alike** in `src/main/java/frc/robot/ctre/`. These classes expose the
Phoenix 6 API but drive the XRP hardware underneath. **Prefer them over raw `XRPMotor`/`Encoder`/
`XRPGyro` in student-facing code.**

- Motors: `frc.robot.ctre.hardware.TalonFX` (wraps `XRPMotor` + the wheel encoder)
- Encoders: `frc.robot.ctre.hardware.CANcoder` (wraps the wheel `Encoder`)
- IMU: `frc.robot.ctre.hardware.Pigeon2` (wraps `XRPGyro`)
- Supporting types mirror Phoenix 6 exactly: `controls.{DutyCycleOut, VoltageOut, NeutralOut}`,
  `configs.{TalonFXConfiguration, CANcoderConfiguration, ...}`, `signals.{NeutralModeValue,
  InvertedValue, SensorDirectionValue}`, and `StatusSignal` / `StatusCode`.

**Right-motor inversion is done the CTRE way** — apply a `TalonFXConfiguration` with
`MotorOutput.Inverted = InvertedValue.Clockwise_Positive` via `motor.getConfigurator().apply(...)`,
**not** `XRPMotor.setInverted()`.

**Device ID → XRP mapping:** TalonFX/CANcoder ID `0` = left (motor 0 / DIO 4,5), ID `1` = right
(motor 1 / DIO 6,7); TalonFX IDs `2`/`3` = spare motors (no encoder); `Pigeon2` ID is ignored (one
gyro). Configure IDs in `Constants.DriveConstants`.

**Season transition:** install the CTRE Phoenix 6 vendordep and find/replace `frc.robot.ctre` →
`com.ctre.phoenix6` in imports — class/method/field/enum names all match, so nothing else changes.
The wrapper is intentionally honest about XRP limits (brake mode coasts, voltage is approximate,
spare motors have no encoder). Full details in `src/main/java/frc/robot/ctre/README.md`.
