package org.firstinspires.ftc.teamcode.season.programs.calibration_programs;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.CameraCalibBot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;

@StandAlone
@TeleOp(name = "Camera Calibrator", group = "Calibration")
public class CameraCalibrator extends BaseTeleop {

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
