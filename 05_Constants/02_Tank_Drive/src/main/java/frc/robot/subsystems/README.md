# Subsystems

## What is a subsystem?

A **subsystem** is one physical part of the robot.

On an XRP robot, examples of subsystems are:

- the **drivetrain** (the two wheels that move the robot around)
- an **arm** (if your robot has one)
- a **gripper** or claw

Think of a subsystem as **one appliance in a kitchen**. The toaster is one
subsystem. The blender is another. Each appliance does its own job, and you
control them one at a time.

## What does a subsystem do?

A subsystem **owns** the motors and sensors for its part of the robot. Because
the subsystem owns that hardware, only **one thing can use it at a time**. This
keeps the robot from trying to (for example) drive forward and backward at the
exact same moment.

A subsystem also has simple helper methods, like `drive(...)` or `stop()`, that
the rest of your program calls instead of talking to the motors directly.

## How do I add a subsystem?

Each subsystem is its **own `.java` file** in this folder.

A new subsystem usually looks like this (you will learn the details in class):

```java
package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Drivetrain extends SubsystemBase {
    // motors and sensors go here

    public Drivetrain() {
        // set things up
    }

    // helper methods like drive() and stop() go here
}
```

> Tip: A subsystem describes **what a part of the robot IS and what it can do**.
> The *actions* it performs live in the [commands](../commands) folder.
