package org.firstinspires.ftc.teamcode.season.programs.testing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.Cygnus;
import org.firstinspires.ftc.teamcode.system.source.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.Robot;

@TeleOp(name = "TestOp", group = "banana")
public class TestOp extends BaseTeleop {
    private Cygnus cygnus;

    @Override
    protected Robot buildRobot() {
        cygnus = new Cygnus(this);
        return cygnus;
    }
}
