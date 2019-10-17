package org.firstinspires.ftc.teamcode.system.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;


public class GillSubSystem extends SubSystem {
    DcMotor fl, fr, bl, br;
    String sfl, sfr, sbl, sbr;

    public GillSubSystem(Robot robot1, String sfl, String sfr, String sbl, String sbr){
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
        //init_loop();
    }

    @Override
    public void init_loop() throws InterruptedException {
        //handle();
    }

    @Override
    public void start() throws InterruptedException {
        //init();
    }

    @Override
    public void handle() throws InterruptedException {
        if (robot.gamepad1.left_stick_x < 0) {  // left
            fl.setPower(-robot.gamepad1.left_stick_x);
            fr.setPower(robot.gamepad1.left_stick_x);
            bl.setPower(robot.gamepad1.left_stick_x);
            br.setPower(-robot.gamepad1.left_stick_x);
        }
        else if (robot.gamepad1.left_stick_x > 0) {  // right
            fl.setPower(-robot.gamepad1.left_stick_x);
            fr.setPower(robot.gamepad1.left_stick_x);
            bl.setPower(robot.gamepad1.left_stick_x);
            br.setPower(-robot.gamepad1.left_stick_x);
        }
        else if (robot.gamepad1.left_stick_x == 0){  // stay
            fl.setPower(0);
            fr.setPower(0);
            bl.setPower(0);
            br.setPower(0);
        }
        if(robot.gamepad1.left_stick_y > 0 ) {  // forward
            fl.setPower(robot.gamepad1.left_stick_y);
            fr.setPower(robot.gamepad1.left_stick_y);
            bl.setPower(robot.gamepad1.left_stick_y);
            br.setPower(robot.gamepad1.left_stick_y);
        }
        else if (robot.gamepad1.left_stick_y < 0) {  // backward
            fl.setPower(robot.gamepad1.left_stick_y);
            fr.setPower(robot.gamepad1.left_stick_y);
            bl.setPower(robot.gamepad1.left_stick_y);
            br.setPower(robot.gamepad1.left_stick_y);
        }
        else if (robot.gamepad1.left_stick_y == 0){  // stay
            fl.setPower(0);
            fr.setPower(0);
            bl.setPower(0);
            br.setPower(0);
        }

        if(robot.gamepad1.right_stick_x > 0 ) {  // turn
            fl.setPower(-robot.gamepad1.right_stick_x);
            fr.setPower(robot.gamepad1.right_stick_x);
            bl.setPower(-robot.gamepad1.right_stick_x);
            br.setPower(robot.gamepad1.right_stick_x);
        }//
        else if (robot.gamepad1.right_stick_x < 0) {  // turn
            fl.setPower(-robot.gamepad1.right_stick_x);
            fr.setPower(robot.gamepad1.right_stick_x);
            bl.setPower(-robot.gamepad1.right_stick_x);
            br.setPower(robot.gamepad1.right_stick_x);
        }
    }

    @Override
    public void stop() throws InterruptedException {//7
    }

    public void rotate(double milli, double rad) {
        for (int ctr = 0; ctr < milli; ++ctr) {
            fl.setPower(-rad / Math.PI);
            fr.setPower(rad / Math.PI);
            bl.setPower(-rad / Math.PI);
            fr.setPower(rad / Math.PI);
        }
    }
}