/*
 * Filename: AnglePIDTuner.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/19/19
 */

package org.firstinspires.ftc.teamcode.season.programs.calibration_programs;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.AnglePIDTunerBot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;

/**
 * A simple teleop program for tuning turn-to-angle PID controllers.
 */
@StandAlone
@TeleOp(name = "Angle PID Tuner", group = "Calibration")
public class AnglePIDTuner extends BaseTeleop {

    //The robot being used.
    private AnglePIDTunerBot robot;

    @Override
    public Robot buildRobot() {
        robot = new AnglePIDTunerBot(this);
        return robot;
    }
}
