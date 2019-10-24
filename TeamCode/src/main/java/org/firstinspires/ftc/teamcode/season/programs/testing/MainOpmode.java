package org.firstinspires.ftc.teamcode.season.programs.testing;
import org.firstinspires.ftc.teamcode.season.robot.MainRobot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;

@StandAlone
@TeleOp(name = "Main Program")
public class MainOpmode extends BaseTeleop {
    Robot Main;

    @Override
    protected Robot buildRobot() {
        Main = new MainRobot(this);
        return Main;
    }

}
