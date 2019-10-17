package org.firstinspires.ftc.teamcode.system.subsystems;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;


import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.util.exceptions.DumpsterFireException;

import static java.lang.Thread.sleep;


public class OurCode extends SubSystem {

    public enum turnType {
        CLOCKWISE, COUNTER_CLOCKWISE, TURN_AROUND
    }

    DcMotor FR;
    DcMotor FL;
    DcMotor BL;
    DcMotor BR;

    public void stopMovement(){
        FL.setPower(0);
        BL.setPower(0);
        FR.setPower(0);
        BR.setPower(0);
    }

    public void turnTime(double timeMs, double power) throws InterruptedException{

        if(timeMs < 0) {
            throw new DumpsterFireException("HAL is cool, but can't travel back in time. Time must be positive. No jk Dylan is just dumb. Alex is the best and LITERALLY A GOD AT MINESWEEPER");
        }

        double startTime = System.currentTimeMillis();
        turn(power);
        while(System.currentTimeMillis() - startTime <= timeMs){sleep(1);}
        stopMovement();
    }

    public OurCode(Robot r) {
        super(r);

        FL = robot.hardwareMap.dcMotor.get("topLeft");
        FR = robot.hardwareMap.dcMotor.get("topRight");
        BL = robot.hardwareMap.dcMotor.get("bottomLeft");
        BR = robot.hardwareMap.dcMotor.get("bottomRight");

        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);


    }


    public void turn(double speed){
        FL.setPower(speed);
        BL.setPower(speed);
        FR.setPower(-speed);
        FR.setPower(-speed);
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
