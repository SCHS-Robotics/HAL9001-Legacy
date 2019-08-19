/*
 * Filename: ConfigDebugger.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/18/19
 */

package org.firstinspires.ftc.teamcode.season.programs.calibration_programs;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.ConfigDebugBot;
import org.firstinspires.ftc.teamcode.system.source.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.Robot;

/**
 * A simple program for debugging config files
 */
@TeleOp(name = "Config Debugger", group = "Debuggers")
public class ConfigDebugger extends BaseTeleop {

    //The robot being used.
    private ConfigDebugBot robot;

    @Override
    public Robot buildRobot() {
        robot = new ConfigDebugBot(this);
        return robot;
    }
}
