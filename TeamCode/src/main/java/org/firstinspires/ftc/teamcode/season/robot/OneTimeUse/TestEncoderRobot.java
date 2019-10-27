package org.firstinspires.ftc.teamcode.season.robot.OneTimeUse;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.RobotSubsystems.EncoderSubsystem;
import org.firstinspires.ftc.teamcode.util.misc.Button;

public class TestEncoderRobot extends Robot {
    public EncoderSubsystem EncodeSub;
    public TestEncoderRobot(OpMode opMode) {
        super(opMode);
        startGui(new Button(1, Button.BooleanInputs.noButton));
        EncodeSub = new EncoderSubsystem(this, "1");
        putSubSystem("TestEncoderRobot", EncodeSub);
    }
}