/*
 * Filename: ColorCalibrator.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.season.programs.calibration_programs;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.ColorCalibBot;
import org.firstinspires.ftc.teamcode.system.source.BaseOpModeIterative;
import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.util.calib.ColorspaceCalib;


/**
 * A simple teleop program for calibrating colorspaces for computer vision algorithms.
 */
@TeleOp(name = "Color Calibrator", group = "KILL ME IF THIS DOES NOT WORK")
public class ColorCalibrator extends BaseOpModeIterative {

    //The robot being used.
    private ColorCalibBot robot;
    //The color calibration subsystem.
    private ColorspaceCalib calibSystem;

    @Override
    protected Robot buildRobot() {
        robot = new ColorCalibBot(this);
        calibSystem = (ColorspaceCalib) robot.subSystems.get("Calib");
        calibSystem.startVision();
        return robot;
    }
}
