package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.MechanumDrive;
import org.firstinspires.ftc.teamcode.util.calib.CameraCalib;

public class CameraCalibBot extends Robot {

    public CameraCalib calib;

    public CameraCalibBot(OpMode opMode) {
        super(opMode);

        calib = new CameraCalib(this);
        putSubSystem("Camera Calib",calib);
    }
}
