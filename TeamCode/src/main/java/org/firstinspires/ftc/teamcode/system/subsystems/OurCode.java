package org.firstinspires.ftc.teamcode.system.subsystems;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;


public class OurCode extends SubSystem {


    DcMotor FR;
    DcMotor FL;
    DcMotor BL;
    DcMotor BR;

    public OurCode(Robot r) {
        super(r);

        FL = robot.hardwareMap.dcMotor.get("topLeft");
        FR = robot.hardwareMap.dcMotor.get("topRight");
        BL = robot.hardwareMap.dcMotor.get("bottomLeft");
        BR = robot.hardwareMap.dcMotor.get("bottomRight");

        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);


    }

    @Override
    public void init() throws InterruptedException {

    }


    @Override
    public void init_loop() throws InterruptedException {
    }

    @Override
    public void start() throws InterruptedException {

    }

    @Override
    public void handle() throws InterruptedException {



        if(robot.gamepad1.left_stick_y >= .01 || robot.gamepad1.left_stick_y <= -.01) {
            BR.setPower(robot.gamepad1.left_stick_y);
            BL.setPower(robot.gamepad1.left_stick_y);
            FL.setPower(robot.gamepad1.left_stick_y);
            FR.setPower(robot.gamepad1.left_stick_y);

            robot.telemetry.addLine("Left Stick Move Y");
        }

        if(robot.gamepad1.left_stick_x >= .01 || robot.gamepad1.left_stick_x <= -.01) {
            BR.setPower(-robot.gamepad1.left_stick_x);
            BL.setPower(robot.gamepad1.left_stick_x);
            FL.setPower(-robot.gamepad1.left_stick_x);
            FR.setPower(robot.gamepad1.left_stick_x);

            robot.telemetry.addLine("Left Stick Move X");
        }

        if(robot.gamepad1.left_stick_x < .1 && robot.gamepad1.left_stick_x > -.01 && robot.gamepad1.left_stick_y < .01 && robot.gamepad1.left_stick_y > -.01) {
            BR.setPower(0);
            BL.setPower(0);
            FL.setPower(0);
            FR.setPower(0);

            robot.telemetry.addLine("Right Stick Move X");
        }

        if(robot.gamepad1.right_stick_x < .1 && robot.gamepad1.right_stick_x > -.1) {
            BR.setPower(0);
            BL.setPower(0);
            FL.setPower(0);
            FR.setPower(0);

            robot.telemetry.addLine("Left Stick Move Y Stop");
        }

        if(robot.gamepad1.right_stick_x >= .1 || robot.gamepad1.right_stick_x <= -.1) {
            BR.setPower(robot.gamepad1.right_stick_x);
            BL.setPower(-robot.gamepad1.right_stick_x);
            FL.setPower(-robot.gamepad1.right_stick_x);
            FR.setPower(robot.gamepad1.right_stick_x);

            robot.telemetry.addLine("Left Stick Move Y");
        }

        robot.telemetry.addLine("Nothing Happened");

        robot.telemetry.update();

    }
    @Override
    public void stop() throws InterruptedException {

    }
}
