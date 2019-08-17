package org.firstinspires.ftc.teamcode.season.programs.testing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.MenuBot;
import org.firstinspires.ftc.teamcode.system.source.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.Robot;

@TeleOp(name = "MenuOp", group = "banana")
public class MenuOp extends BaseTeleop {
    private MenuBot menuBot;

    @Override
    protected Robot buildRobot() {
        menuBot = new MenuBot(this);
        return menuBot;
    }
}
