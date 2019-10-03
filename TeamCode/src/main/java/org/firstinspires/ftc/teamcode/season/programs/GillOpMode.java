package org.firstinspires.ftc.teamcode.season.programs;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.BillBot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
@TeleOp(name = "GillOpMode")
public class GillOpMode extends BaseTeleop {
    BillBot r;
    @Override
    protected Robot buildRobot() {
        r = new BillBot(this);
        return r;
    }
}