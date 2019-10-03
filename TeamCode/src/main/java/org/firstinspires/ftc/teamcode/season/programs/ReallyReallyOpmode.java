package org.firstinspires.ftc.teamcode.season.programs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.ReallyReallyBot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;

@TeleOp(name = "Really Teleop")
public class ReallyReallyOpmode extends BaseTeleop {
    public ReallyReallyOpmode() {

    }
    ReallyReallyBot asd;
    @Override
    protected Robot buildRobot() {
         asd = new ReallyReallyBot(this);
         return asd;
    }
}