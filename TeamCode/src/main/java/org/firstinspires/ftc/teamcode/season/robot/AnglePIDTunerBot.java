/*
 * Filename: AnglePIDTunerBot.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/11/19
 */

package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.calib.AnglePIDTunerSystem;
import org.firstinspires.ftc.teamcode.util.misc.Button;

/**
 * A robot object containing a subsystem used to tune turn-to-angle PID controllers.
 */
public class AnglePIDTunerBot extends Robot {

    /**
     * Constructor for AnglePIDTunerBot
     *
     * @param opMode - The opmode the robot is running.
     */
    public AnglePIDTunerBot(OpMode opMode) {
        super(opMode);
        startGui(new Button(1, Button.BooleanInputs.b));
        putSubSystem("PID Tuner",new AnglePIDTunerSystem(this,Math.PI/4));
    }
}
