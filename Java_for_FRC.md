# Java for FRC: A Guide for MATLAB/Simulink Programmers

This guide is written for someone who already understands programming logic (loops, conditionals, functions) from MATLAB/Simulink and wants to get productive in Java for FRC robotics. It skips the "what is a variable" basics and focuses on what is *different* about Java.

---

## Table of Contents

1. [Java vs MATLAB — The Big Picture](#1-java-vs-matlab--the-big-picture)
2. [Setting Up Your Environment](#2-setting-up-your-environment)
3. [Java Syntax Crash Course](#3-java-syntax-crash-course)
4. [Data Types and Variables](#4-data-types-and-variables)
5. [Operators and Math](#5-operators-and-math)
6. [Control Flow](#6-control-flow)
7. [Methods (Functions in Java)](#7-methods-functions-in-java)
8. [Object-Oriented Programming](#8-object-oriented-programming)
9. [Arrays and Collections](#9-arrays-and-collections)
10. [Packages and Imports](#10-packages-and-imports)
11. [How an FRC Robot Project is Structured](#11-how-an-frc-robot-project-is-structured)
12. [WPILib — The FRC Standard Library](#12-wpilib--the-frc-standard-library)
13. [Command-Based Programming](#13-command-based-programming)
14. [Arcade Drive — A Complete Walkthrough](#14-arcade-drive--a-complete-walkthrough)
15. [Controllers and Driver Input](#15-controllers-and-driver-input)
16. [Motors and Motor Controllers](#16-motors-and-motor-controllers)
17. [Sensors](#17-sensors)
18. [Autonomous Mode](#18-autonomous-mode)
19. [SmartDashboard and Shuffleboard](#19-smartdashboard-and-shuffleboard)
20. [Common FRC Patterns and Tips](#20-common-frc-patterns-and-tips)

---

## 1. Java vs MATLAB — The Big Picture

| Concept | MATLAB | Java |
|---|---|---|
| Execution | Interpreted, line by line | Compiled to bytecode, then run by JVM |
| Typing | Dynamic (variables can change type) | Static (types are locked at declaration) |
| Indexing | Starts at 1 | Starts at 0 |
| Code organization | Scripts and functions in `.m` files | Classes in `.java` files |
| Everything is a... | Matrix | Object |
| Semicolons | Suppress output | End every statement |
| Comments | `%` | `//` for single line, `/* */` for block |
| Main entry point | Script runs top to bottom | `public static void main(String[] args)` |
| Loops | `for i = 1:10` | `for (int i = 0; i < 10; i++)` |

### The most important mental shift

In MATLAB, you write scripts that execute top-to-bottom and call functions. In Java (and especially in FRC), you write **classes** — blueprints for objects that hold both data and behavior. Your FRC robot itself is an object (`Robot`), your drivetrain is an object (`DriveSubsystem`), and actions like "drive forward" are objects (`DriveForwardCommand`).

This is called **Object-Oriented Programming (OOP)** and is the core concept in Java.

---

## 2. Setting Up Your Environment

For FRC, everything is bundled in the **WPILib installer**. You do NOT install a separate JDK.

1. Download the WPILib installer from [docs.wpilib.org](https://docs.wpilib.org)
2. Run it — it installs:
   - A bundled JDK (Java Development Kit)
   - VS Code with the WPILib extension
   - Gradle (the build tool)
   - All FRC libraries
3. Use **"WPILib VS Code"** (not your system VS Code) to open robot projects

**To build and deploy your code:**
- Press `Ctrl+Shift+P` → `WPILib: Build Robot Code` to compile
- Press `Ctrl+Shift+P` → `WPILib: Deploy Robot Code` to send it to the robot
- Or use the WPILib icon in the top-right of VS Code

---

## 3. Java Syntax Crash Course

```java
// This is a single-line comment

/*
   This is a
   multi-line comment
*/

// Every statement ends with a semicolon
int x = 5;

// Code blocks are wrapped in curly braces { }
if (x > 3) {
    System.out.println("x is greater than 3");
}

// Printing to the console (like disp() in MATLAB)
System.out.println("Hello, robot!");  // prints with newline
System.out.print("No newline");       // prints without newline
```

---

## 4. Data Types and Variables

In MATLAB, everything is a `double` by default. In Java, you must declare the **type** of every variable.

### Primitive Types

```java
// Integer (whole number, no decimal) — most common in FRC for ports/IDs
int motorPort = 1;
int encoderCount = 0;

// Long — integer but bigger range (rarely needed in FRC)
long bigNumber = 10000000000L;

// Double — decimal number (most common for speeds, distances)
double driveSpeed = 0.75;
double distanceMeters = 1.5;

// Float — decimal but less precise (avoid in FRC, use double instead)
float f = 3.14f;

// Boolean — true or false only
boolean isFinished = false;
boolean limitSwitchTripped = true;

// Char — a single character
char letter = 'A';
```

### String (not a primitive, but acts like one)

```java
// String — text. Note the capital S.
String subsystemName = "Drivetrain";
String message = "Speed: " + driveSpeed;  // + concatenates strings

// In MATLAB you'd use [ ] or strcat(). In Java, use +
System.out.println("Motor port: " + motorPort);
```

### Variable Declaration Rules

```java
// Format: type variableName = value;
double speed = 0.5;

// You can declare without assigning
int port;
port = 4;  // assign later

// Variables declared with final cannot be changed (like a constant)
final double MAX_SPEED = 1.0;  // convention: UPPER_SNAKE_CASE for constants
```

---

## 5. Operators and Math

```java
// Arithmetic — same as MATLAB except ^ is NOT power
int a = 10, b = 3;

a + b   // 13
a - b   // 7
a * b   // 30
a / b   // 3  <-- INTEGER division! Drops the decimal (3.33 becomes 3)
a % b   // 1  <-- modulo (remainder)

// IMPORTANT: If you want decimal division, use doubles
double result = (double) a / b;  // 3.333...  (casting to double)
double result2 = 10.0 / 3;      // also works

// Power — use Math.pow(), NOT ^
double squared = Math.pow(2, 8);  // 256.0

// Math library (like MATLAB's built-in math functions)
Math.abs(-5)        // absolute value → 5
Math.sqrt(16.0)     // square root → 4.0
Math.max(3, 7)      // maximum → 7
Math.min(3, 7)      // minimum → 3
Math.PI             // 3.14159...
Math.sin(angle)     // angle in RADIANS (same as MATLAB)

// Shorthand assignment operators
speed += 0.1;   // same as: speed = speed + 0.1
speed -= 0.1;   // same as: speed = speed - 0.1
speed *= 2.0;   // same as: speed = speed * 2.0
count++;        // increment by 1 (same as count = count + 1)
count--;        // decrement by 1

// Comparison operators (return boolean)
a == b   // equal to
a != b   // not equal to
a > b    // greater than
a >= b   // greater than or equal to
a < b    // less than
a <= b   // less than or equal to

// Logical operators
true && false   // AND → false
true || false   // OR  → true
!true           // NOT → false
```

---

## 6. Control Flow

### If / Else If / Else

```java
// MATLAB:
// if x > 0
//     disp('positive')
// elseif x < 0
//     disp('negative')
// else
//     disp('zero')
// end

// Java:
if (x > 0) {
    System.out.println("positive");
} else if (x < 0) {
    System.out.println("negative");
} else {
    System.out.println("zero");
}
// Note: condition must be in parentheses, blocks in curly braces, no "end"
```

### For Loop

```java
// MATLAB: for i = 1:10
// Java:   for (int i = 0; i < 10; i++)
//         Note: starts at 0, uses < not <=

for (int i = 0; i < 10; i++) {
    System.out.println("i = " + i);  // prints 0 through 9
}

// Equivalent to MATLAB's for i = 1:10 (if you want 1 through 10):
for (int i = 1; i <= 10; i++) {
    System.out.println("i = " + i);
}

// Enhanced for loop — iterates over a collection (like MATLAB's for x = array)
int[] ports = {1, 2, 3, 4};
for (int port : ports) {
    System.out.println("Port: " + port);
}
```

### While Loop

```java
// MATLAB: while condition ... end
// Java:   while (condition) { ... }

int count = 0;
while (count < 5) {
    System.out.println("Count: " + count);
    count++;
}

// do-while runs at least once before checking condition
do {
    count++;
} while (count < 10);
```

### Switch Statement

```java
// Like a series of if/else if checks on a single value
int mode = 2;

switch (mode) {
    case 1:
        System.out.println("Mode 1: Tank Drive");
        break;  // IMPORTANT: break stops execution from falling through
    case 2:
        System.out.println("Mode 2: Arcade Drive");
        break;
    case 3:
        System.out.println("Mode 3: Curvature Drive");
        break;
    default:
        System.out.println("Unknown mode");
        break;
}
```

---

## 7. Methods (Functions in Java)

In MATLAB, functions are defined in `.m` files. In Java, functions (called **methods**) live inside classes.

```java
// MATLAB function:
// function result = addNumbers(a, b)
//     result = a + b;
// end

// Java method inside a class:
public double addNumbers(double a, double b) {
    return a + b;
}

// Format:
// [access modifier] [return type] methodName(type param1, type param2) { ... }
```

### Access Modifiers

```java
public double myMethod() { ... }   // anyone can call this
private double myMethod() { ... }  // only THIS class can call this
protected double myMethod() { ... } // this class and subclasses can call it
```

### Return Types

```java
// Must declare what type the method returns
public int getPort() { return 1; }          // returns an int
public double getSpeed() { return 0.75; }   // returns a double
public boolean isFinished() { return true; } // returns boolean
public void doSomething() { ... }           // void = returns nothing
```

### Static vs Instance Methods

```java
// Static methods belong to the class itself, not an object
// Call them as: ClassName.methodName()
public static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
}
// Usage: double clamped = MathHelper.clamp(1.5, -1.0, 1.0);

// Instance methods belong to an object
// Must create an object first, then call: object.methodName()
public double calculateDistance() {
    return encoderTicks * METERS_PER_TICK;
}
// Usage: double dist = myDrivetrain.calculateDistance();
```

---

## 8. Object-Oriented Programming

This is the biggest shift from MATLAB. Almost everything in Java is a class.

### What is a Class?

A **class** is a blueprint. An **object** (also called an **instance**) is built from that blueprint.

```java
// MATLAB doesn't have classes in the same way.
// Think of a class like a Simulink block definition,
// and objects as instances of that block placed on a diagram.

// Define a class (blueprint)
public class Motor {
    // Fields — the data the object holds (like struct fields in MATLAB)
    private int port;
    private double speed;
    private boolean isReversed;

    // Constructor — called when you create a new object with "new"
    public Motor(int port, boolean isReversed) {
        this.port = port;           // "this" refers to THIS object
        this.isReversed = isReversed;
        this.speed = 0.0;
    }

    // Methods — the behaviors the object can perform
    public void setSpeed(double speed) {
        if (isReversed) {
            this.speed = -speed;
        } else {
            this.speed = speed;
        }
    }

    public double getSpeed() {
        return speed;
    }
}
```

```java
// Creating objects from the class (instantiation)
Motor leftMotor = new Motor(1, false);   // port 1, not reversed
Motor rightMotor = new Motor(2, true);   // port 2, reversed

// Calling methods on objects
leftMotor.setSpeed(0.8);
rightMotor.setSpeed(0.8);

System.out.println(leftMotor.getSpeed());   // 0.8
System.out.println(rightMotor.getSpeed());  // -0.8
```

### Inheritance

A class can extend another class to inherit its fields and methods.

```java
// Parent class
public class Animal {
    public void breathe() {
        System.out.println("Breathing...");
    }
}

// Child class — inherits breathe() from Animal, adds its own behavior
public class Dog extends Animal {
    public void bark() {
        System.out.println("Woof!");
    }
}

Dog d = new Dog();
d.breathe();  // inherited from Animal
d.bark();     // defined in Dog
```

In FRC, this is everywhere. For example:
- Your `Robot` class **extends** `TimedRobot` (which provides the loop structure)
- Your subsystem classes **extend** `SubsystemBase`
- Your command classes **extend** `Command` or use `Commands.run()`

### Interfaces

An interface is a contract — it defines *what* methods a class must have, but not *how* they work.

```java
public interface Driveable {
    void setLeftSpeed(double speed);
    void setRightSpeed(double speed);
    void stop();
}

// A class "implements" an interface and must provide all methods
public class TankDrive implements Driveable {
    public void setLeftSpeed(double speed) { /* ... */ }
    public void setRightSpeed(double speed) { /* ... */ }
    public void stop() { setLeftSpeed(0); setRightSpeed(0); }
}
```

### @Override Annotation

When you override a method from a parent class, mark it with `@Override`. This tells the compiler to verify you're actually overriding something.

```java
public class Robot extends TimedRobot {
    @Override
    public void robotInit() {
        // Your initialization code here
    }

    @Override
    public void teleopPeriodic() {
        // Called every 20ms during teleop
    }
}
```

---

## 9. Arrays and Collections

### Arrays (Fixed Size)

```java
// MATLAB: arr = [1, 2, 3, 4, 5];
// Java:
int[] arr = {1, 2, 3, 4, 5};

// Access by index — starts at 0 in Java!
System.out.println(arr[0]);  // 1  (first element)
System.out.println(arr[4]);  // 5  (last element)

// Length property
System.out.println(arr.length);  // 5

// Declare with a size, fill in later
double[] speeds = new double[4];
speeds[0] = 0.5;
speeds[1] = -0.5;
speeds[2] = 0.75;
speeds[3] = -0.75;
```

### ArrayList (Dynamic Size — like a MATLAB cell array)

```java
import java.util.ArrayList;

ArrayList<Double> speedList = new ArrayList<>();
speedList.add(0.5);
speedList.add(0.75);
speedList.add(-0.5);

System.out.println(speedList.get(0));   // 0.5
System.out.println(speedList.size());   // 3

speedList.remove(1);  // removes element at index 1
```

### HashMap (Key-Value pairs — like a MATLAB struct)

```java
import java.util.HashMap;

HashMap<String, Integer> motorPorts = new HashMap<>();
motorPorts.put("leftFront", 1);
motorPorts.put("rightFront", 2);
motorPorts.put("leftBack", 3);
motorPorts.put("rightBack", 4);

int port = motorPorts.get("leftFront");  // 1
```

---

## 10. Packages and Imports

Packages are like folders that organize your code. When you need to use a class from another package, you **import** it.

```java
// At the top of every Java file, declare its package
package frc.robot;

// Import classes you want to use from other packages
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

// Wildcard import — imports everything from a package (use sparingly)
import edu.wpi.first.wpilibj.*;
```

In FRC, all WPILib classes start with `edu.wpi.first`. VS Code will suggest imports automatically — press `Ctrl+.` or click the lightbulb when you see a red underline.

---

## 11. How an FRC Robot Project is Structured

Your project in this repo is already set up. Here's what each file does:

```
src/main/java/frc/robot/
├── Main.java           — Entry point, never touch this
├── Robot.java          — Top-level robot lifecycle (init/periodic methods)
├── RobotContainer.java — Wires together subsystems, commands, and buttons
└── Constants.java      — All your magic numbers go here
```

When you add subsystems and commands, the structure grows to:

```
src/main/java/frc/robot/
├── Main.java
├── Robot.java
├── RobotContainer.java
├── Constants.java
├── subsystems/
│   ├── DriveSubsystem.java
│   ├── ArmSubsystem.java
│   └── IntakeSubsystem.java
└── commands/
    ├── ArcadeDriveCommand.java
    ├── ArmUpCommand.java
    └── AutoDriveForwardCommand.java
```

### Robot.java — The Lifecycle

`TimedRobot` (which `Robot` extends) calls these methods automatically:

| Method | When it's called |
|---|---|
| `robotInit()` / constructor | Once at startup |
| `robotPeriodic()` | Every 20ms, always |
| `autonomousInit()` | Once when auto starts |
| `autonomousPeriodic()` | Every 20ms during auto |
| `teleopInit()` | Once when teleop starts |
| `teleopPeriodic()` | Every 20ms during teleop |
| `disabledInit()` | Once when robot is disabled |
| `disabledPeriodic()` | Every 20ms while disabled |
| `testInit()` | Once when test mode starts |
| `testPeriodic()` | Every 20ms during test mode |

**The 20ms loop is critical.** Unlike MATLAB where your script runs once, FRC robot code runs in a loop forever. Every 20ms the robot wakes up and runs your periodic methods.

---

## 12. WPILib — The FRC Standard Library

WPILib is FIRST's official Java library. It provides everything you need to control a robot.

### Key WPILib packages

| Package | What's in it |
|---|---|
| `edu.wpi.first.wpilibj` | Core robot classes (TimedRobot, etc.) |
| `edu.wpi.first.wpilibj2.command` | Command-based framework |
| `edu.wpi.first.wpilibj.drive` | Drive helpers (DifferentialDrive, etc.) |
| `edu.wpi.first.wpilibj.smartdashboard` | SmartDashboard/Shuffleboard |
| `edu.wpi.first.math` | Kinematics, geometry, filters, controllers |

### XRP-Specific Classes (your robot)

The XRP is a small educational robot. It uses:

```java
import edu.wpi.first.wpilibj.xrp.XRPMotor;
import edu.wpi.first.wpilibj.xrp.XRPGyro;
import edu.wpi.first.wpilibj.xrp.XRPOnBoardIO;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
```

---

## 13. Command-Based Programming

FRC uses a design pattern called **command-based programming**. Instead of putting all your logic in `teleopPeriodic()`, you split your robot into:

- **Subsystems** — Hardware groupings (drivetrain, arm, intake)
- **Commands** — Actions the robot performs (drive forward, raise arm, intake ball)
- **Triggers/Buttons** — What causes a command to run

Think of it like this MATLAB/Simulink analogy:
- Subsystem = a Simulink block (encapsulates hardware)
- Command = a function that tells blocks what to do
- Trigger = an input signal that activates a function

### Why command-based?

It handles **concurrency** for you. Multiple things can happen simultaneously (drivetrain moving while arm is raising) without you writing threading code. The `CommandScheduler` manages it all.

### Subsystem

```java
package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.xrp.XRPMotor;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveSubsystem extends SubsystemBase {

    // Hardware fields
    private final XRPMotor m_leftMotor;
    private final XRPMotor m_rightMotor;
    private final DifferentialDrive m_drive;

    public DriveSubsystem() {
        m_leftMotor = new XRPMotor(0);    // left motor on channel 0
        m_rightMotor = new XRPMotor(1);   // right motor on channel 1
        m_rightMotor.setInverted(true);   // right side needs to be flipped

        m_drive = new DifferentialDrive(m_leftMotor::set, m_rightMotor::set);
    }

    // Public methods that commands can call
    public void arcadeDrive(double speed, double rotation) {
        m_drive.arcadeDrive(speed, rotation);
    }

    public void stop() {
        m_drive.stopMotor();
    }

    @Override
    public void periodic() {
        // Called every 20ms — put telemetry/logging here
    }
}
```

### Command

```java
package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DriveSubsystem;
import java.util.function.DoubleSupplier;

public class ArcadeDriveCommand extends Command {

    private final DriveSubsystem m_drive;
    private final DoubleSupplier m_speedSupplier;
    private final DoubleSupplier m_rotationSupplier;

    public ArcadeDriveCommand(
            DriveSubsystem drive,
            DoubleSupplier speed,
            DoubleSupplier rotation) {
        m_drive = drive;
        m_speedSupplier = speed;
        m_rotationSupplier = rotation;
        addRequirements(drive);  // tells scheduler this command uses the drive subsystem
    }

    @Override
    public void execute() {
        // Called every 20ms while this command is running
        m_drive.arcadeDrive(m_speedSupplier.getAsDouble(), m_rotationSupplier.getAsDouble());
    }

    @Override
    public boolean isFinished() {
        return false;  // runs forever (teleop drive command never finishes on its own)
    }

    @Override
    public void end(boolean interrupted) {
        m_drive.stop();
    }
}
```

### Wiring it together in RobotContainer

```java
public class RobotContainer {

    private final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
    private final XboxController m_controller = new XboxController(0);

    public RobotContainer() {
        configureButtonBindings();

        // Set the default command for the drivetrain
        // This runs whenever no other command is using the drivetrain
        m_driveSubsystem.setDefaultCommand(
            new ArcadeDriveCommand(
                m_driveSubsystem,
                () -> -m_controller.getLeftY(),    // forward/back from left stick
                () -> m_controller.getRightX()     // rotation from right stick
            )
        );
    }

    private void configureButtonBindings() {
        // Example: press A button to run a command
        new JoystickButton(m_controller, XboxController.Button.kA.value)
            .onTrue(new SomeOtherCommand(m_driveSubsystem));
    }
}
```

---

## 14. Arcade Drive — A Complete Walkthrough

Arcade drive is the most common drive mode for beginners. One stick controls forward/backward, another controls turning.

```
Left Stick Y-axis  → forward/backward speed
Right Stick X-axis → rotation (turning)
```

### How DifferentialDrive.arcadeDrive() works internally

```
leftSpeed  = forwardSpeed + rotation
rightSpeed = forwardSpeed - rotation
```

If you push straight forward (speed=1, rotation=0): both wheels at 1.
If you turn right (speed=0, rotation=0.5): left=0.5, right=-0.5 (spin in place).
If you drive and turn: a blend of both.

### Lambda Expressions (the `() ->` syntax)

You'll see this everywhere in FRC command-based code:

```java
() -> -m_controller.getLeftY()
```

This is a **lambda** — a short anonymous function. It means "a function that takes no arguments and returns `-m_controller.getLeftY()`".

In MATLAB, the equivalent is a function handle: `@() -controller.getLeftY()`

The `DoubleSupplier` type (used in the command constructor) is an interface that expects exactly this: a function with no arguments that returns a `double`. Lambdas make it easy to pass behavior without creating a named method.

```java
// These are equivalent:

// Option 1: Lambda (preferred in FRC)
DoubleSupplier mySupplier = () -> joystick.getY();

// Option 2: Method reference
DoubleSupplier mySupplier = joystick::getY;

// Option 3: Anonymous class (verbose, older style)
DoubleSupplier mySupplier = new DoubleSupplier() {
    @Override
    public double getAsDouble() {
        return joystick.getY();
    }
};
```

---

## 15. Controllers and Driver Input

### XboxController

```java
import edu.wpi.first.wpilibj.XboxController;

XboxController controller = new XboxController(0);  // port 0 (first USB controller)

// Joystick axes — return -1.0 to 1.0
controller.getLeftY()    // left stick vertical (negative = up on most controllers)
controller.getLeftX()    // left stick horizontal
controller.getRightY()   // right stick vertical
controller.getRightX()   // right stick horizontal
controller.getLeftTriggerAxis()   // left trigger  0.0 to 1.0
controller.getRightTriggerAxis()  // right trigger 0.0 to 1.0

// Buttons — return true/false
controller.getAButton()
controller.getBButton()
controller.getXButton()
controller.getYButton()
controller.getLeftBumper()
controller.getRightBumper()
controller.getStartButton()
controller.getBackButton()
```

### Binding buttons to commands (in RobotContainer)

```java
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

// Using Trigger (modern approach)
new Trigger(controller::getAButton)
    .onTrue(new MyCommand());        // run once when pressed
    
new Trigger(controller::getBButton)
    .whileTrue(new MyCommand());     // run while held, cancel when released

new Trigger(controller::getXButton)
    .onFalse(new MyCommand());       // run when button is released
```

### Joystick Deadband

Controller joysticks don't return exactly 0 when centered — there's noise. Apply a deadband to ignore small values:

```java
import edu.wpi.first.math.MathUtil;

double rawInput = controller.getLeftY();
double deadbanded = MathUtil.applyDeadband(rawInput, 0.1);  // ignore values within ±0.1
```

---

## 16. Motors and Motor Controllers

### XRP Motors (your robot)

```java
import edu.wpi.first.wpilibj.xrp.XRPMotor;

XRPMotor leftMotor = new XRPMotor(0);   // channel 0
XRPMotor rightMotor = new XRPMotor(1);  // channel 1

leftMotor.set(0.5);      // 50% forward
rightMotor.set(-0.5);    // 50% backward
leftMotor.set(0);        // stop

leftMotor.setInverted(true);  // flip the direction
```

### Real FRC Motors (for reference when you move to a full robot)

```java
// REV Spark MAX (brushless motor controller)
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

CANSparkMax motor = new CANSparkMax(1, MotorType.kBrushless);
motor.set(0.5);

// CTRE Talon SRX (brushed motor controller)
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

WPI_TalonSRX motor = new WPI_TalonSRX(1);
motor.set(ControlMode.PercentOutput, 0.5);
```

### DifferentialDrive

`DifferentialDrive` takes two motor outputs and handles the arcade/tank drive math:

```java
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

DifferentialDrive drive = new DifferentialDrive(leftMotor::set, rightMotor::set);

drive.arcadeDrive(speed, rotation);  // arcade drive
drive.tankDrive(leftSpeed, rightSpeed);  // tank drive
drive.curvatureDrive(speed, rotation, isQuickTurn);  // curvature drive
drive.stopMotor();  // stop both motors
```

---

## 17. Sensors

### XRP Built-in Sensors

```java
import edu.wpi.first.wpilibj.xrp.XRPGyro;
import edu.wpi.first.wpilibj.Encoder;

// Gyro — measures rotation
XRPGyro gyro = new XRPGyro();
gyro.reset();
double angle = gyro.getAngleZ();  // degrees rotated around Z axis (yaw)

// Encoder — measures wheel rotation
Encoder leftEncoder = new Encoder(4, 5);  // channels 4 and 5 on DIO
leftEncoder.reset();
int ticks = leftEncoder.get();        // raw encoder ticks
double distance = leftEncoder.getDistance();  // scaled distance
leftEncoder.setDistancePerPulse(0.01);  // meters per encoder tick
```

### Digital Inputs (limit switches)

```java
import edu.wpi.first.wpilibj.DigitalInput;

DigitalInput limitSwitch = new DigitalInput(0);  // DIO channel 0
boolean isTripped = !limitSwitch.get();  // often inverted (false = tripped)
```

### Analog Inputs (potentiometers, ultrasonic sensors)

```java
import edu.wpi.first.wpilibj.AnalogInput;

AnalogInput sensor = new AnalogInput(0);  // analog channel 0
double voltage = sensor.getVoltage();     // 0-5V
double value = sensor.getValue();          // 0-4095 (12-bit)
```

---

## 18. Autonomous Mode

During auto, commands are scheduled to run sequentially or in parallel.

### Simple Sequential Auto

```java
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public Command getAutonomousCommand() {
    return new SequentialCommandGroup(
        new DriveForwardCommand(m_driveSubsystem, 1.0),  // drive 1 meter
        new WaitCommand(0.5),                             // wait 0.5 seconds
        new TurnCommand(m_driveSubsystem, 90)             // turn 90 degrees
    );
}
```

### Parallel Commands

```java
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

// Run two commands at the same time
new ParallelCommandGroup(
    new DriveForwardCommand(m_driveSubsystem, 1.0),
    new RaiseArmCommand(m_armSubsystem)
)
```

### Using the Commands factory (modern style)

```java
import edu.wpi.first.wpilibj2.command.Commands;

// Drive for 2 seconds then stop
Commands.run(() -> m_drive.arcadeDrive(0.5, 0), m_driveSubsystem)
    .withTimeout(2.0)
    .andThen(() -> m_drive.stop());
```

### Auto Chooser (letting drivers pick auto mode)

```java
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

SendableChooser<Command> m_chooser = new SendableChooser<>();

// In constructor:
m_chooser.setDefaultOption("Drive Forward", new DriveForwardCommand(m_driveSubsystem, 1.0));
m_chooser.addOption("Do Nothing", new WaitCommand(15));
m_chooser.addOption("Drive + Turn", new SequentialCommandGroup(...));
SmartDashboard.putData("Auto Mode", m_chooser);

// In getAutonomousCommand():
public Command getAutonomousCommand() {
    return m_chooser.getSelected();
}
```

---

## 19. SmartDashboard and Shuffleboard

Like MATLAB's `disp()` but it goes to a live dashboard on your driver station laptop.

```java
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// Put values (call from periodic() methods)
SmartDashboard.putNumber("Left Speed", leftMotor.get());
SmartDashboard.putNumber("Gyro Angle", gyro.getAngleZ());
SmartDashboard.putBoolean("Limit Switch", limitSwitch.get());
SmartDashboard.putString("Auto Mode", "Drive Forward");

// Read values back (useful for tuning constants from dashboard)
double kP = SmartDashboard.getNumber("kP", 0.1);  // key, default value
```

### Shuffleboard (more advanced dashboard)

```java
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");
tab.addNumber("Left Speed", () -> leftMotor.get());
tab.addNumber("Right Speed", () -> rightMotor.get());
tab.addBoolean("Motors OK", () -> true);
```

---

## 20. Common FRC Patterns and Tips

### Constants File

Put all magic numbers in `Constants.java` so they're easy to find and change:

```java
public final class Constants {

    // Prevent instantiation
    private Constants() {}

    public static final class DriveConstants {
        public static final int LEFT_MOTOR_PORT = 0;
        public static final int RIGHT_MOTOR_PORT = 1;
        public static final double MAX_SPEED = 0.8;
        public static final double DEADBAND = 0.1;
        public static final double METERS_PER_ENCODER_TICK = 0.00195;
    }

    public static final class OperatorConstants {
        public static final int DRIVER_CONTROLLER_PORT = 0;
    }
}
```

Usage:
```java
import static frc.robot.Constants.DriveConstants.*;

XRPMotor motor = new XRPMotor(LEFT_MOTOR_PORT);
```

### The `m_` Prefix Convention

FRC teams commonly prefix member variables (fields of a class) with `m_`:

```java
private final XRPMotor m_leftMotor;    // m_ = member variable
private final XRPGyro m_gyro;
private double m_currentSpeed;
```

This makes it easy to distinguish class fields from local variables and method parameters.

### Null Checks

Java will throw a `NullPointerException` if you call a method on a null object. Always initialize objects in the constructor or field declaration:

```java
// BAD — command might be null
if (m_autonomousCommand != null) {
    m_autonomousCommand.cancel();
}

// Good — check before calling (you already see this pattern in Robot.java)
```

### Common Mistakes from MATLAB

| MATLAB habit | Java equivalent | Notes |
|---|---|---|
| `array(3)` | `array[2]` | Java is 0-indexed |
| `%` comment | `//` comment | `%` is modulo in Java |
| `end` to close blocks | `}` | Java uses curly braces |
| `function` keyword | Method inside a class | No standalone functions |
| `a^2` | `Math.pow(a, 2)` or `a*a` | `^` is XOR in Java |
| `disp(x)` | `System.out.println(x)` | |
| `;` suppresses output | `;` ends a statement | Required, not optional |
| `true/false` (lowercase) | `true/false` (lowercase) | Same! |
| `&&` short-circuit AND | `&&` | Same! |
| `length(arr)` | `arr.length` | Property, not function |

### Reading Error Messages

Java error messages look scary but follow a pattern:

```
Exception in thread "main" java.lang.NullPointerException
    at frc.robot.subsystems.DriveSubsystem.arcadeDrive(DriveSubsystem.java:42)
    at frc.robot.commands.ArcadeDriveCommand.execute(ArcadeDriveCommand.java:28)
```

Read it bottom-up: the error happened in `ArcadeDriveCommand.execute()` on line 28, which called `DriveSubsystem.arcadeDrive()` on line 42, where a `NullPointerException` occurred. Go to line 42 in DriveSubsystem and look for something that wasn't initialized.

### Useful Keyboard Shortcuts in VS Code

| Shortcut | Action |
|---|---|
| `Ctrl+.` | Quick fix / auto-import |
| `F12` | Go to definition |
| `Alt+F12` | Peek definition |
| `Shift+F12` | Find all references |
| `F2` | Rename symbol |
| `Ctrl+Space` | Trigger autocomplete |
| `Ctrl+Shift+P` | Command palette (for WPILib commands) |

---

## Where to Go Next

1. **WPILib Docs**: [docs.wpilib.org](https://docs.wpilib.org) — the authoritative reference for everything FRC Java
2. **Zero to Robot**: The official FRC getting started tutorial on docs.wpilib.org
3. **Java Tutorial**: [docs.oracle.com/javase/tutorial](https://docs.oracle.com/javase/tutorial) for deeper Java knowledge
4. **Chief Delphi**: [chiefdelphi.com](https://www.chiefdelphi.com) — the FRC community forum
5. **Your project**: Start by implementing arcade drive in your XRP project using this guide

---

*Document generated for the XRP-Java-Learning project. Last updated: 2026-06-13.*
