package org.firstinspires.ftc.teamcode.season.programs.testing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.Cygnus;
import org.firstinspires.ftc.teamcode.season.robot.MenuBot;
import org.firstinspires.ftc.teamcode.system.source.BaseOpModeIterative;
import org.firstinspires.ftc.teamcode.system.source.Robot;

@TeleOp(name = "TestOp", group = "banana")
public class TestOp extends BaseOpModeIterative {
    private Cygnus cygnus;

    @Override
    protected Robot buildRobot() {
        cygnus = new Cygnus(this);
        return cygnus;
    }
}
