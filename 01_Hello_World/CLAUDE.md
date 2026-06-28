# CLAUDE.md — XRP Hello World

> **AI Assistant Sync Note:** This project is developed using both **Claude** and **GitHub Copilot**.
> `CLAUDE.md` is read by Claude; `.github/copilot-instructions.md` is read by GitHub Copilot.
> **When you update one file, update the other to keep them in sync.**

## About This Exercise

**Exercise 01 — Hello World** is a two-part first project. First, it verifies that the student's development environment is fully working — the project should build and simulate successfully out of the box. Second, it introduces the most basic programming concept: printing a message. The student adds a `System.out.println()` statement to confirm the robot program is running and to get comfortable making a change, rebuilding, and seeing the result.

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
