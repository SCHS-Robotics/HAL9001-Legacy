/*
 * Filename: SubSystem.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package org.firstinspires.ftc.teamcode.system.source;

public abstract class SubSystem {
    protected Robot robot;
    public abstract void init() throws InterruptedException;

    public abstract void handle();

    public abstract void stop();

    public SubSystem(Robot robot){
        this.robot = robot;
    }
}
