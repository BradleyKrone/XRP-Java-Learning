# Commands

## What is a command?

A **command** is an action the robot performs.

Examples of commands are:

- "drive forward for 2 seconds"
- "turn left"
- "raise the arm"

If a [subsystem](../subsystems) is an **appliance** (like a toaster), then a
command is the **instruction** you give it ("toast this bread"). The command
uses the subsystem to actually get the job done.

## The 4 parts of a command

Every command has up to four simple parts. You do not have to use all of them.

1. **start** — `initialize()` runs **once** when the command begins.
2. **keep going** — `execute()` runs **over and over** (about 50 times a second)
   while the command is running.
3. **when to stop** — `isFinished()` answers a yes/no question: "are we done
   yet?" When it answers **yes**, the command stops.
4. **clean up** — `end()` runs **once** when the command finishes or is
   cancelled. A good place to stop the motors.

## How do I add a command?

Each command is its **own `.java` file** in this folder.

A new command usually looks like this (you will learn the details in class):

```java
package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Drivetrain;

public class DriveForward extends Command {
    private final Drivetrain m_drivetrain;

    public DriveForward(Drivetrain drivetrain) {
        m_drivetrain = drivetrain;
        addRequirements(drivetrain); // "I need to use this subsystem"
    }

    @Override
    public void initialize() {}     // runs once at the start

    @Override
    public void execute() {}        // runs over and over

    @Override
    public boolean isFinished() {   // are we done yet?
        return false;
    }

    @Override
    public void end(boolean interrupted) {} // clean up
}
```

> Tip: A command describes **what the robot DOES**. The part of the robot it
> controls lives in the [subsystems](../subsystems) folder.
