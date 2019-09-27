
package org.firstinspires.ftc.teamcode.season.programs.testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.season.robot.PracticeRobot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseAutonomous;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;

@Autonomous(name = "Practice Sample")
public class PracticeAutonomous extends BaseAutonomous {
    PracticeRobot robot;
    @Override
    protected Robot buildRobot() {
        robot = new PracticeRobot(this);
        return robot;
    }

    @Override
    public void main() throws InterruptedException {
        robot.drive.NorthSouth(0.3);
        waitFor(1000);
        robot.drive.turn(0.5);
        waitFor(510);
    }
}
