package org.firstinspires.ftc.teamcode.system.subsystems;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;

public class OurServo extends SubSystem {
    /**
     * Ctor for subsystem.
     *
     * @param robot - The robot the subsystem is contained within.
     */
    Servo servo;

    public OurServo(Robot robot) {
        super(robot);

        servo = robot.hardwareMap.servo.get("arm");
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

        if (robot.gamepad1.b == true) {
            servo.setPosition(1);
        } else if (robot.gamepad1.a == true) {
            servo.setPosition(0);
        }
    }
    @Override
    public void stop() throws InterruptedException {
    }
}
