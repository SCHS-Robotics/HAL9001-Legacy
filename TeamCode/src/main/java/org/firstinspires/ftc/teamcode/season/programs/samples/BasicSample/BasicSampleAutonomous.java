package org.firstinspires.ftc.teamcode.season.programs.samples.BasicSample;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseAutonomous;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;
import org.firstinspires.ftc.teamcode.util.math.Vector;

import java.util.Map;

//@StandAlone is not required but stops it from auto using configs from autonomous
@StandAlone
//@Disabled should be removed when using
@Autonomous(name = "Tank Sample")
//extends BaseTeleop means it is an Autonomous program
public class BasicSampleAutonomous extends BaseAutonomous {

    private BasicSampleBot robot;

    @Override
    protected Robot buildRobot() {
        robot = new BasicSampleBot(this);
        return robot;
    }

    @Override
    public void main() throws InterruptedException {
        //runs the init(), init_loop(), and start functions behind the scenes. Runs this once you press start.
        //runs main on start
        //Stuff for init or init_loop belongs in subsystems

        //Map<String,Object> config = robot.pullNonGamepad(robot.drive);
        //int theMysticalParamOfMystery = (int) config.get("myIntAutoParam");

        robot.drive.driveTime(900,0.5);
    }
}
