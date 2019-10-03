package org.firstinspires.ftc.teamcode.system.subsystems;

import org.firstinspires.ftc.teamcode.season.robot.EveryoneIsCool;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseAutonomous;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.math.Vector;

public class OpBob extends BaseAutonomous {
    EveryoneIsCool equalityBob;
    @Override
    protected Robot buildRobot() {
        equalityBob = new EveryoneIsCool(this);
        return equalityBob;
    }

    @Override
    public void main() throws InterruptedException {
        equalityBob.sampleMechanum.drive(new Vector(0,1),2000);

    }
}
