package org.firstinspires.ftc.teamcode.system.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;

public class GillOpMode extends SubSystem {
    DcMotor fl, fr, bl, br;
    String sfl, sfr, sbl, sbr;

    public GillOpMode(Robot robot1, String sfl, String sfr, String sbl, String sbr){
        super(robot1);
        this.sfl = sfl;
        this.sfr = sfr;
        this.sbl = sbl;
        this.sbr = sbr;
        fl = robot1.hardwareMap.dcMotor.get(sfl);
        fr = robot1.hardwareMap.dcMotor.get(sfr);
        bl = robot1.hardwareMap.dcMotor.get(sbl);
        br = robot1.hardwareMap.dcMotor.get(sbr);
    }

    @Override
    public void init() throws InterruptedException {
        fl.setDirection(DcMotor.Direction.FORWARD);
        fr.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.REVERSE);

    }

    @Override
    public void init_loop() throws InterruptedException {

    }

    @Override
    public void start() throws InterruptedException {
    }

    @Override
    public void handle() throws InterruptedException {
        fl.setPower(robot.gamepad1.left_stick_y);
        fr.setPower(-robot.gamepad1.left_stick_y);
        bl.setPower(robot.gamepad1.left_stick_y);
        br.setPower(-robot.gamepad1.left_stick_y);
        if(robot.gamepad1.left_stick_x > 0 ) {
            fl.setPower(robot.gamepad1.left_stick_x);
            fr.setPower(-robot.gamepad1.left_stick_x);
            bl.setPower(-robot.gamepad1.left_stick_x);
            br.setPower(robot.gamepad1.left_stick_x);
        }
        if(robot.gamepad1.left_stick_x < 0 ) {
            fl.setPower(-robot.gamepad1.left_stick_x);
            fr.setPower(robot.gamepad1.left_stick_x);
            bl.setPower(robot.gamepad1.left_stick_x);
            br.setPower(-robot.gamepad1.left_stick_x);
        }

    }

    @Override
    public void stop() throws InterruptedException {

    }
}