/*
 * Filename: CameraCalibrator.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/24/19
 */

package org.firstinspires.ftc.teamcode.season.programs.calibration_programs;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.CameraCalibBot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;

/**
 * A simple program for calibrating the phone's camera.
 */
@StandAlone
@TeleOp(name = "Camera Calibrator", group = "Calibration")
public class CameraCalibrator extends BaseTeleop {

    //The robot the program is using.
    private CameraCalibBot robot;

    @Override
    protected Robot buildRobot() {
        robot = new CameraCalibBot(this);
        return robot;
    }

    @Override
    protected void onStart() {
        robot.calib.startVision();
    }

    @Override
    protected void onStop() {
        robot.calib.stopVision();
    }
}
