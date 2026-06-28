# GitHub Copilot Instructions — XRP Base

> **AI Assistant Sync Note:** This project is developed using both **GitHub Copilot** and **Claude**.
> `.github/copilot-instructions.md` is read by GitHub Copilot; `CLAUDE.md` is read by Claude.
> **When you update one file, update the other to keep them in sync.**

## About This Project

**Base** is the reusable starting template for all exercises in this series. It contains the minimal project scaffold — `Main.java`, `Robot.java`, `RobotContainer.java`, and `Constants.java` — with no subsystems or commands. When starting a new exercise, copy this folder and build on top of it.

## What This Project Is

This is an **educational FRC robot project** for learning how to program a
**WPILib command-based robot** in Java. It targets the **XRP (eXtensible
Robotics Platform)** — a small, affordable robot designed for learning FRC
programming without needing a full-size competition robot.

The project is a **learning scaffold**. The skeleton code is intentionally
incomplete. Subsystems and commands are meant to be added by a student as part
of a guided lesson. Missing implementations are not bugs — they are exercises.

## The XRP Robot

- **XRP** is a small two-wheeled robot made by WPILib for education.
- It uses the same programming model as a full FRC robot (WPILib, command-based
  Java, Gradle, driver station) but runs on inexpensive hardware.
- It drives as a **tank drive**: the left and right wheels are controlled
  independently. Turning is done by driving the two sides at different speeds.
- The robot can be tested in **simulation** (no physical hardware needed) or
  connected to a real XRP device over Wi-Fi at `192.168.42.1`.
- WPILib version: **2026.2.1**. Java version: **17**.

## Architecture

```
Main.java
  └── Robot.java          (main control loop, extends TimedRobot, runs at 50 Hz)
        └── RobotContainer.java   (the "wiring hub" — connects subsystems, controllers, buttons)
              ├── subsystems/     (physical parts of the robot)
              └── commands/       (actions the robot can perform)
```

**`Main.java`** — Entry point. Starts the robot. Almost never changed.

**`Robot.java`** — Manages robot operating modes: Disabled, Autonomous (15 s,
robot drives itself), Teleop (135 s, driver controls), and Test. Calls
`CommandScheduler.getInstance().run()` every 20 ms — this is what drives all
commands and subsystems. Rarely changed by students.

**`RobotContainer.java`** — The one file where everything is wired together.
Students create subsystem instances here, create controller objects (Xbox
controller, joystick), and call `configureButtonBindings()` to map buttons to
commands. `getAutonomousCommand()` returns the command to run during autonomous.

**`Constants.java`** — A single class that holds every magic number used
anywhere in the robot program (motor port numbers, drive speeds, controller IDs,
etc.). Nothing else goes in this file. No robot actions.

**`subsystems/`** — Each subsystem is one physical part of the robot (e.g.
`Drivetrain.java`). A subsystem owns the motors and sensors for its part of the
robot. Only one command can use a subsystem at a time. Subsystems extend
`SubsystemBase` and have a `periodic()` method that runs at 50 Hz.

**`commands/`** — Each command is one action the robot can perform (e.g.
`ButtonDrive.java`). Commands use subsystems to do work. A command has four
lifecycle methods: `initialize()` (runs once at start), `execute()` (runs 50
times/second while active), `isFinished()` (returns true to stop),
`end(boolean interrupted)` (cleanup). Commands declare which subsystems they
need using `addRequirements(subsystem)`.

## WPILib Command-Based Framework

This project uses the WPILib **command-based** programming model. Key ideas:

- The `CommandScheduler` is the engine that runs everything. It is called once
  per 20 ms loop in `Robot.robotPeriodic()`.
- A **subsystem** describes a robot part and what it can do.
- A **command** describes an action. Commands are scheduled, run, and cancelled
  by the scheduler.
- Button bindings tell the scheduler "when this button is held, run this
  command." This is done via trigger objects (e.g. `CommandXboxController`).
- `addRequirements()` in a command prevents two commands from fighting over the
  same subsystem. If a new command needs a subsystem that is already in use, the
  old command is interrupted.

## Build and Run Commands

All commands use the Gradle wrapper (`gradlew.bat` on Windows, `./gradlew` on
macOS/Linux).

| Task | Command |
|------|---------|
| Build the project | `.\gradlew build` |
| Run in simulation | `.\gradlew simulateJava` |
| Clean build outputs | `.\gradlew clean` |

Simulation opens the WPILib simulation GUI and a virtual driver station. If a
physical XRP is connected to the same network, it appears at `192.168.42.1`.

## Conventions

- **Member variables** use the `m_` prefix (e.g. `m_drivetrain`, `m_controller`).
- **All configuration values** (port numbers, speeds, IDs) belong in
  `Constants.java`, not scattered across other files.
- **Package**: `frc.robot` for top-level classes, `frc.robot.subsystems` for
  subsystems, `frc.robot.commands` for commands.
- **Subsystems** extend `SubsystemBase` (from `edu.wpi.first.wpilibj2.command`).
- **Commands** extend `Command` (from `edu.wpi.first.wpilibj2.command`).
- Commands call `addRequirements(subsystem)` in their constructor to declare
  which subsystems they use.
- Subsystems are created once in `RobotContainer` and passed (injected) into
  commands via constructor parameters.

## Hardware Reference

**Controller:** `CommandXboxController` (from `edu.wpi.first.wpilibj2.command.button`) at port 0.

**Motors** — use the `XRPMotor` class (not `Spark` or other PWM classes):

| Channel | Component | Notes |
|---------|-----------|-------|
| 0 | Left Motor | |
| 1 | Right Motor | **Must be inverted** — positive output spins backward by default |
| 2 | Motor 3 (optional) | Available for expansion |
| 3 | Motor 4 (optional) | Available for expansion |

**Servos** — use the `XRPServo` class (not the standard `Servo` class):

| Channel | Component |
|---------|-----------|
| 4 | Servo 1 |
| 5 | Servo 2 |

**Encoders** — use the `Encoder` class with DIO channels.
The encoders count up when moving forward. 585 counts per wheel revolution.

| DIO Channels | Component |
|--------------|-----------|
| 4, 5 | Left Encoder (Channel A, B) |
| 6, 7 | Right Encoder (Channel A, B) |

**Onboard I/O** — DIO channels:

| DIO Channel | Component | WPILib Class |
|-------------|-----------|--------------|
| 0 | USER Button | `DigitalInput` |
| 1 | Green LED | `DigitalOutput` |

**Analog Sensors** — use `AnalogInput`:

| Channel | Component | Range |
|---------|-----------|-------|
| 0 | Left Reflectance Sensor | 0V = white, 5V = black |
| 1 | Right Reflectance Sensor | 0V = white, 5V = black |
| 2 | Ultrasonic Rangefinder | 0V = 20mm, 5V = 4000mm |

**IMU:** `XRPGyro` class — built-in LSM6DSOX with 3-axis gyro and 3-axis accelerometer.
No channel needed. Calibrates automatically at boot (green LED blinks for ~3–5 s).

**Wheel specs:** 60mm diameter, 155mm trackwidth.
