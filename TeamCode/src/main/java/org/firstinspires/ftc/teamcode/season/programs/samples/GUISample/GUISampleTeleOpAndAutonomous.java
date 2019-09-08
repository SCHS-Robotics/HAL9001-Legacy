package org.firstinspires.ftc.teamcode.season.programs.samples.GUISample;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;


//@StandAlone is not required but stops it from auto using configs from autonomous
@StandAlone
//@Disabled should be removed when using
@Disabled
@TeleOp(name = "GUISampleTeleop")
//extends BaseTeleop means it is a TeleOp program
public class GUISampleTeleOpAndAutonomous extends BaseTeleop {

    private GUISampleBot robot;

    @Override
    protected Robot buildRobot() {
        robot = new GUISampleBot(this);
        return robot;
    }

    //There are other methods that you can use to do stuff in teleop, refer to TankSample to see them.

}
