package org.firstinspires.ftc.teamcode.season.programs.testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.season.robot.ReallyReallyBot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseAutonomous;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.math.Vector;

@Autonomous(name = "Really Autonomous")
public class CoAutonomous1 extends BaseAutonomous {

    ReallyReallyBot bot;
    @Override
    protected Robot buildRobot() {
        bot = new ReallyReallyBot(this);
        return bot;
    }

    @Override
    public void main() throws InterruptedException {
        bot.mDrive.drive(new Vector(0, 0.5), 2000);
        bot.dsa.waitt(7000);
        bot.dsa.turnBot(0.3, 700);
        bot.dsa.waitt(7000);
        bot.mDrive.drive(new Vector(0, 0.5), 2000);
    }
}
