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
        ReallyReallyBot.gyro();
        ReallyReallyBot.asd.drive(new Vector(0, 1), 500);
        ReallyReallyBot.asd.wait(3000);
        ReallyReallyBot.asd.turnTo(Math.PI/2, 0.1);
        ReallyReallyBot.asd.wait(3000);
        ReallyReallyBot.asd.turnTo(Math.PI/2, 0.1);
        ReallyReallyBot.asd.wait(3000);
        ReallyReallyBot.asd.drive(new Vector(0, 1), 500);
    }
}
