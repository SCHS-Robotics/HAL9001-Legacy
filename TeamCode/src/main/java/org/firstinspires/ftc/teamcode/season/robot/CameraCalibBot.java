/*
 * Filename: CameraCalibBot.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/24/19
 */

package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.calib.CameraCalib;

/**
 * A robot containing the color calibration subsystem.
 */
public class CameraCalibBot extends Robot {

    //The CameraCalib subsystem.
    public CameraCalib calib;

    /**
     * Constructor for CameraCalibBot.
     *
     * @param opMode - The opmode the robot is running.
     */
    public CameraCalibBot(OpMode opMode) {
        super(opMode);

        calib = new CameraCalib(this);
        putSubSystem("Camera Calib",calib);
    }
}
