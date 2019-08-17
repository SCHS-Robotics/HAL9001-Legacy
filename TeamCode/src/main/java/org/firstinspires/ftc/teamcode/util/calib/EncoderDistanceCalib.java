/*
 * Filename: EncoderDistanceCalib.java
 * Author: Cole Savage and Dylan Zueck
 * Team Name: Level Up, Crow Force
 * Date: TODO
 */

package org.firstinspires.ftc.teamcode.util.calib;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.system.source.Menu;
import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.system.source.SubSystem;

public class EncoderDistanceCalib extends SubSystem {

    private Menu calibMenu;
    private DriveTrain driveTrain;

    private DcMotor leftBack, leftFront, rightBack, rightFront;

    public enum DriveTrain {
        TANK_DRIVE, QUAD_WHEEL_DRIVE, MECHANUM_DRIVE, OMNIWHEEL_DRIVE
    }

    public EncoderDistanceCalib(Robot robot, Menu calibMenu, DriveTrain driveTrain) {
        super(robot);

        this.calibMenu = calibMenu;
        this.driveTrain = driveTrain;
    }

    @Override
    public void init() {

    }

    @Override
    public void init_loop() {

    }

    @Override
    public void handle() {

    }

    @Override
    public void stop() {

    }
}
