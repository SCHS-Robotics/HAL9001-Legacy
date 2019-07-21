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

@TeleOp(name = "Color Calibrator", group = "KILL ME IF THIS DOES NOT WORK")
public class ColorCalibrator extends BaseOpModeIterative {
    private ColorCalibBot robot;
    private ColorspaceCalib calibSystem;

    @Override
    protected Robot buildRobot() {
        robot = new ColorCalibBot(this);
        calibSystem = (ColorspaceCalib) robot.subSystems.get("Calib");
        calibSystem.startVision();
        return robot;
    }
}
