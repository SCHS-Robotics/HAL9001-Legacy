/*
 * Filename: ConfigDebugBot.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/18/19
 */

package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.debug.ConfigDebugMenu;
import org.firstinspires.ftc.teamcode.util.misc.Button;

/**
 * A robot object containing a menu used to debug config files
 */
public class ConfigDebugBot extends Robot {

    /**
     * Constructor for the config debug robot.
     *
     * @param opMode - The opmode the robot is running.
     */
    public ConfigDebugBot(OpMode opMode) {
        super(opMode);
        startGui(new Button(1, Button.BooleanInputs.noButton));
        gui.addMenu("Config Debug", new ConfigDebugMenu(gui));
    }
}
