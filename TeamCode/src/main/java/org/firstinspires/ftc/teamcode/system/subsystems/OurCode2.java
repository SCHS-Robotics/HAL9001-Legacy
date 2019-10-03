package org.firstinspires.ftc.teamcode.system.subsystems;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;


public class OurCode2 extends SubSystem {

    private ElapsedTime runtime = new ElapsedTime();
    DcMotor FR;
    DcMotor FL;
    DcMotor BL;
    DcMotor BR;
    boolean done = false;
    int stage = 1;



    public OurCode2(Robot r) {
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
        runtime.reset();

    }


    @Override
    public void init_loop() throws InterruptedException {
    }

    @Override
    public void start() throws InterruptedException {

    }

    @Override
    public void handle() throws InterruptedException {
        if(!done){
            switch(stage){
                case 1:
                    if(runtime.milliseconds() < 5000){
                        FL.setPower(1);
                        BL.setPower(1);
                        FR.setPower(1);
                        BR.setPower(1);
                    }
                    else {
                        FL.setPower(0);
                        BL.setPower(0);
                        FR.setPower(0);
                        BR.setPower(0);
                        stage++;
                }
                    break;
                case 2:
                    if(runtime.milliseconds() < 10000){
                        FL.setPower(1);
                        BL.setPower(1);
                        FR.setPower(-1);
                        BR.setPower(-1);
                    }
                    else {
                        FL.setPower(0);
                        BL.setPower(0);
                        FR.setPower(0);
                        BR.setPower(0);
                        stage++;
                    }
                    break;
                case 3:
                    if(runtime.milliseconds() < 5000){
                        FL.setPower(1);
                        BL.setPower(1);
                        FR.setPower(1);
                        BR.setPower(1);
                    }
                    else {
                        FL.setPower(0);
                        BL.setPower(0);
                        FR.setPower(0);
                        BR.setPower(0);
                        stage++;
                    }
                    break;
            }
        }
    }

    @Override
    public void stop () throws InterruptedException {

    }
}
