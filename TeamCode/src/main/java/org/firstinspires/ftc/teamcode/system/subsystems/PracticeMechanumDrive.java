package org.firstinspires.ftc.teamcode.system.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;

public class PracticeMechanumDrive extends SubSystem {
    private DcMotor fLeftMotor;
    private DcMotor fRightMotor;
    private DcMotor bLeftMotor;
    private DcMotor bRightMotor;

    public PracticeMechanumDrive(Robot robot) {
        super(robot);

    }

    @Override
    public void init() throws InterruptedException {
        fRightMotor = robot.hardwareMap.dcMotor.get("forwardRightMotor");
        fLeftMotor = robot.hardwareMap.dcMotor.get("forwardLeftMotor");
        bLeftMotor = robot.hardwareMap.dcMotor.get("backLeftMotor");
        bRightMotor= robot.hardwareMap.dcMotor.get("backRightMotor");

        fLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        bLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        robot.telemetry.addData("Status", "Intitialized");
        robot.telemetry.update();


    }

    public void NorthSouth(double speed) throws InterruptedException {
        fLeftMotor.setPower(speed);
        fRightMotor.setPower(speed);
        bLeftMotor.setPower(speed);
        bRightMotor.setPower(speed);
    }

    public void turn (double rotation) throws InterruptedException {
        fLeftMotor.setPower(rotation);
        fRightMotor.setPower(-rotation);
        bLeftMotor.setPower(rotation);
        bRightMotor.setPower(-rotation);

    }

    public void init_loop() throws InterruptedException {

    }

    @Override
    public void start() throws InterruptedException {

    }

    @Override
    public void handle() throws InterruptedException {

    }

    @Override
    public void stop() throws InterruptedException {

    }


}
