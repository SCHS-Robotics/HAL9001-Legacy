package org.firstinspires.ftc.teamcode.season.programs.samples.AutonomousUsingConfig;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseAutonomous;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;

@StandAlone
@Disabled
public class AutonomousConfigSelectorOpModeSample extends BaseAutonomous {

    //Robot to be used in the program
    private AutonomousConfigSelectorBotSample robot;

    @Override
    protected Robot buildRobot() {
        //Creates the robot
        robot = new AutonomousConfigSelectorBotSample(this);
        return robot;
    }

    @Override
    public void main() throws InterruptedException {
        switch (robot.autonomous.autonomousMode) {
            case ("NameOfFirstAutonomous"):
                //do stuff here
                break;
            case ("NameOfSecondAutonomous"):
                //do other things here
                break;
            case ("NameOfThirdAutonomous"):
                //do even more othery things here
                break;
        }
    }
}
