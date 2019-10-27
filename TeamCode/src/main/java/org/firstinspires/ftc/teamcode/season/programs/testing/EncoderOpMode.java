package org.firstinspires.ftc.teamcode.season.programs.testing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.OneTimeUse.TestEncoderRobot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;


@StandAlone
@TeleOp(name = "Encoder Test")
public class EncoderOpMode extends BaseTeleop {
    Robot Encodbot;


    @Override
    protected Robot buildRobot() {
        Encodbot = new TestEncoderRobot(this);
        return Encodbot;
    }
}
