/*
 * Filename: ColorCalibrator.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.season.programs.calibration_programs;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.ColorCalibBot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;


/**
 * A simple teleop program for calibrating colorspaces for computer vision algorithms.
 */
@StandAlone
@TeleOp(name = "Color Calibrator", group = "Calibration")
public class ColorCalibrator extends BaseTeleop {

    //The robot being used.
    private ColorCalibBot robot;

    @Override
    protected Robot buildRobot() {
        robot = new ColorCalibBot(this);
        return robot;
    }

    @Override
    protected void onStart() {
        super.onStart();
        robot.calibSystem.startVision();

    }

    @Override
    protected void onStop() {
        super.onStop();
        robot.calibSystem.stopVision();
    }
}
