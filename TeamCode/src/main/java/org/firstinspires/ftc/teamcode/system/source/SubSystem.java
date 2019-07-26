/*
 * Filename: SubSystem.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package org.firstinspires.ftc.teamcode.system.source;

import java.lang.InterruptedException;

/**
 * An abstract class representing a subsystem on the robot.
 */
public abstract class SubSystem {

    //The robot the subsystem belongs to.
    protected Robot robot;

    /**
     * An abstract method containing the code that the subsystem runs when being initialized.
     *
     * @throws InterruptedException - Throws this exception if the program is unexpectedly interrupted.
     */
    public abstract void init() throws InterruptedException;

    /**
     * An abstract method containing the code that the subsystem runs every loop in a teleop program.
     */
    public abstract void handle();

    /**
     * An abstract method containing the code that the subsystem runs when the program is stopped.
     */
    public abstract void stop();

    /**
     * Ctor for subsystem.
     *
     * @param robot - The robot the subsystem is contained within.
     */
    public SubSystem(Robot robot){
        this.robot = robot;
    }
}
