# How the Intake Code Works
### A guide for new coders and Java beginners

---

## The Big Idea: Separating "What" from "When"

Before we look at any code, here's the most important idea in this entire project:

> **Subsystems answer "WHAT can this do?"**
> **Commands answer "WHEN should it do it?"**

That's it. Everything else follows from this one rule.

Let's build up to it with an analogy.

---

## An Analogy: The Restaurant Kitchen

Imagine a restaurant kitchen.

- The **chef** knows *how* to cook every dish. They know how to flip burgers, fry fries, make salads. They don't care who ordered — they just cook when asked.
- The **waiter** knows *when* to ask the chef to cook. When a customer orders a burger, the waiter walks to the kitchen and says "make a burger." When the customer is done, the waiter says "stop making burgers."
- The **manager** decides *which waiter goes to which table* — i.e., who controls what.

In our robot code:

| Restaurant  | Robot Code         |
|-------------|--------------------|
| Chef        | Subsystem          |
| Waiter      | Command            |
| Manager     | RobotContainer     |

The chef (subsystem) doesn't know when to cook. The waiter (command) doesn't know how to cook. They each do their one job, and they work together.

---

## Why Split Them Up At All?

You might be wondering: *why not just put everything in one file?*

Great question. Here's the problem with doing it all in one place:

```
// The "just put it all together" approach — this gets messy fast
if (aButtonIsPressed) {
    motor1.set(1.0);
    motor2.set(0.5);
} else {
    motor1.set(0.0);
    motor2.set(0.0);
}

// But wait... what about autonomous? I need to copy-paste this:
if (autoIsRunning && timer < 3.0) {
    motor1.set(1.0);   // Copied again...
    motor2.set(0.5);   // And again...
}
```

Every time you need the intake to run, you copy-paste the same motor code. Then when you want to change the intake speed, you have to find every copy and change each one. You'll miss one. Things break.

With subsystems and commands, you write the motor code **once** in the subsystem, and every command just calls `runIntake()`. Change it in one place, it's changed everywhere.

---

## File 1: The Subsystem (`IntakeSubsystem.java`)

### What is it?

The subsystem is the "hardware owner." It's the only file in the entire project that is allowed to touch the intake motors directly.

```java
public class IntakeSubsystem extends SubsystemBase {

    private final XRPMotor m_motorOne = new XRPMotor(1);  // 100% speed motor
    private final XRPMotor m_motorTwo = new XRPMotor(2);  // 50% speed motor

    public void runIntake() {
        m_motorOne.set(1.0);   // 100% forward
        m_motorTwo.set(0.5);   // 50% forward
    }

    public void stopIntake() {
        m_motorOne.set(0.0);
        m_motorTwo.set(0.0);
    }
}
```

### Breaking it down line by line

**`extends SubsystemBase`**

This is called *inheritance*. It means `IntakeSubsystem` is a *type* of `SubsystemBase`. By extending it, our class gets automatically registered with the WPILib robot scheduler — the system that runs all robot code every 20 milliseconds. Without this, the scheduler wouldn't know our subsystem exists.

**`private final XRPMotor m_motorOne = new XRPMotor(1);`**

- `private` — only this file can access this variable. No other file can reach in and control the motor directly. Everything goes through our methods.
- `final` — once the motor is created, this variable will never point to a different motor. It's locked in.
- `XRPMotor` — the type of motor controller we're using (specific to the XRP simulation robot).
- `new XRPMotor(1)` — creates a new motor object connected to channel 1 on the XRP board.
- `m_motorOne` — the name of the variable. The `m_` prefix is an FRC convention meaning "member variable" (a variable that belongs to the class, not just a method).

**`public void runIntake()`**

- `public` — any other class can call this method.
- `void` — this method doesn't return any value back to the caller.
- `runIntake()` — the name. It clearly describes what it does.

**`m_motorOne.set(1.0)`**

The `.set()` method tells the motor what speed to run at. Values go from `-1.0` (full reverse) to `1.0` (full forward), with `0.0` being stopped. Think of it like a percentage: `0.5` = 50%.

### What the subsystem does NOT do

Notice that the subsystem never says *"when the A button is pressed, run."* It has no idea a button even exists. That's intentional. The subsystem is oblivious to the outside world — it just offers skills and waits to be called.

---

## File 2: The Command (`RunIntakeCommand.java`)

### What is it?

The command is the "behavior." It says: *at this moment in time, I want the intake to run.* It doesn't know or care why — it just acts.

```java
public class RunIntakeCommand extends Command {

    private final IntakeSubsystem m_intake;

    public RunIntakeCommand(IntakeSubsystem intake) {
        m_intake = intake;
        addRequirements(m_intake);
    }

    @Override
    public void initialize() {
        m_intake.runIntake();
    }

    @Override
    public void execute() {
        // Nothing needed here
    }

    @Override
    public void end(boolean interrupted) {
        m_intake.stopIntake();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
```

### The lifecycle of a command

Every WPILib command has four stages. Think of them like a play in four acts:

```
┌─────────────────────────────────────────────────────────┐
│                  COMMAND LIFECYCLE                      │
│                                                         │
│  1. initialize()  → Called ONCE when command starts     │
│         ↓                                               │
│  2. execute()     → Called every 20ms while running     │
│         ↓                                               │
│  3. isFinished()  → Checked every 20ms                  │
│     (if true → go to end; if false → back to execute)  │
│         ↓                                               │
│  4. end()         → Called ONCE when command finishes   │
└─────────────────────────────────────────────────────────┘
```

**`initialize()`** — The "start" moment. We call `runIntake()` here because we only need to set the motor speed once. Motors don't forget their speed — they keep spinning until told otherwise.

**`execute()`** — Runs every 20ms in a loop. We leave this empty here, but it's useful when you need to continuously update something (like reading a joystick to adjust speed dynamically).

**`end(boolean interrupted)`** — The "cleanup" moment. This is called no matter *why* the command stopped — button released, timeout expired, another command took over. Always stop the motors here. If you forget, the motors keep spinning after the command ends, which is dangerous.

**`isFinished()`** — We return `false` here. This tells the scheduler: *"I have no natural stopping point — I'll run until you cancel me."* Something external must stop it:
- In **teleop**: the button being released
- In **auto**: a timeout running out

### The most important line: `addRequirements(m_intake)`

This is the lock system.

```java
addRequirements(m_intake);
```

This line tells the scheduler: *"This command owns the intake. Nobody else can use it while I'm running."*

Without it, imagine this scenario:
- Command A is spinning the intake in
- Command B tries to spin the intake out at the same time
- Both commands send conflicting signals to the same motors
- Bad things happen

With `addRequirements`, if Command B tries to run while Command A is active, the scheduler automatically cancels Command A first, then starts Command B. Only one command controls the intake at any time.

### `@Override` — What does that mean?

The `@Override` annotation (the `@` symbol things) means: *"I am replacing a method that was defined in my parent class."*

`Command` (our parent class) already has `initialize()`, `execute()`, `end()`, and `isFinished()` methods — but they do nothing by default. We're *overriding* them with our own behavior. `@Override` tells Java to double-check that we spelled the method name correctly, which catches typos.

---

## File 3: RobotContainer (The Wiring)

### What is it?

RobotContainer is where everything connects together. It creates the subsystems and tells commands which buttons trigger them.

```java
public class RobotContainer {

    private final IntakeSubsystem m_intake = new IntakeSubsystem();
    private final CommandXboxController m_controller = new CommandXboxController(0);

    public RobotContainer() {
        configureButtonBindings();
    }

    private void configureButtonBindings() {
        m_controller.a().whileTrue(new RunIntakeCommand(m_intake));
    }

    public Command getAutonomousCommand() {
        return new RunIntakeCommand(m_intake).withTimeout(3.0);
    }
}
```

### `whileTrue()` — The key to hold-to-run

```java
m_controller.a().whileTrue(new RunIntakeCommand(m_intake));
```

Read this out loud: *"While the A button is true (held down), run RunIntakeCommand."*

- Button **pressed** → `RunIntakeCommand` is scheduled → `initialize()` fires → motors spin
- Button **released** → `RunIntakeCommand` is cancelled → `end()` fires → motors stop

`whileTrue()` is different from `onTrue()`. `onTrue()` fires once and never looks back. `whileTrue()` watches the button continuously and cancels the command the moment you let go.

### The same command in autonomous

```java
public Command getAutonomousCommand() {
    return new RunIntakeCommand(m_intake).withTimeout(3.0);
}
```

Notice we use the **exact same** `RunIntakeCommand`. We just add `.withTimeout(3.0)` to it.

`.withTimeout(3.0)` is a *decorator* — it wraps around our command and adds a timer. After 3 seconds, the timeout fires, which cancels our command, which calls `end()`, which stops the motors.

This is why `isFinished()` returns `false`. The command itself doesn't know when to stop. In teleop, the button controls it. In auto, the timeout controls it. The command just runs until told otherwise.

---

## The Full Picture

Here's how all three files work together when the A button is pressed:

```
DRIVER PRESSES A BUTTON
        │
        ▼
CommandXboxController detects button press
        │
        ▼
whileTrue() schedules RunIntakeCommand
        │
        ▼
RunIntakeCommand.initialize() is called
        │
        ▼
m_intake.runIntake() is called
        │
        ▼
m_motorOne.set(1.0) ── Motor 1 spins at 100%
m_motorTwo.set(0.5) ── Motor 2 spins at 50%

        [Driver holds A — execute() loops every 20ms, does nothing]

DRIVER RELEASES A BUTTON
        │
        ▼
whileTrue() detects button release, cancels command
        │
        ▼
RunIntakeCommand.end(true) is called
        │
        ▼
m_intake.stopIntake() is called
        │
        ▼
m_motorOne.set(0.0) ── Motor 1 stops
m_motorTwo.set(0.0) ── Motor 2 stops
```

---

## Why This Pattern Matters

This command-subsystem pattern is used by nearly every FRC team in the world because it solves real problems:

| Problem | How This Pattern Solves It |
|---|---|
| Same code needed in teleop and auto | One command class, used in both places |
| Two things fighting over a motor | `addRequirements()` prevents it |
| Want to change motor speed later | Change it once in the subsystem, everywhere updates |
| Robot grows to 10+ subsystems | Each subsystem stays in its own file, easy to find |
| New teammate needs to understand intake | Read one file: IntakeSubsystem.java |

The pattern feels like more files than necessary at first. But as robots get more complex — with drive, arm, shooter, climber, intake, LEDs all running at once — having this separation is what keeps the code manageable.

---

## Glossary of New Terms

| Term | What it means |
|---|---|
| `class` | A blueprint that defines variables and methods |
| `extends` | Inherit from another class (get its features) |
| `private` | Only this file can access this variable/method |
| `public` | Any other file can access this variable/method |
| `final` | This variable can never be reassigned |
| `void` | This method returns no value |
| `new` | Creates a new instance (object) from a class blueprint |
| `@Override` | Replacing a method from the parent class |
| `subsystem` | A class that owns and controls a piece of hardware |
| `command` | A class that describes a specific robot behavior |
| `scheduler` | The WPILib system that runs commands every 20ms |
| `addRequirements` | Locks a subsystem so only one command uses it at a time |
| `whileTrue()` | Run a command while a button is held; stop when released |
| `.withTimeout(N)` | Automatically cancel a command after N seconds |
