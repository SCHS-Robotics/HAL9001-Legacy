package org.firstinspires.ftc.teamcode.season.programs.samples.BasicSample;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;

//@StandAlone is not required but stops it from auto using configs from autonomous
@StandAlone
//@Disabled should be removed when using
@Disabled
@TeleOp(name = "Tank Sample")
//extends BaseTeleop means it is a TeleOp program
public class BasicSampleTeleop extends BaseTeleop {

    private BasicSampleBot robot;

    //return the robot that will be used
    @Override
    protected Robot buildRobot() {
        robot = new BasicSampleBot(this);
        return robot;
    }

    //Not necessary to have this (you can delete it), basically if you want to do something special on start you would put it here
    @Override
    protected void onStart() {
        super.onStart();
    }

    //Not necessary to have this (you can delete it), basically if you want to do something special in a loop after pressing start you would put it here
    @Override
    protected void onUpdate() {
        super.onUpdate();
    }

    //Not necessary to have this (you can delete it), basically if you want to do something special on stop you would put it here
    @Override
    protected void onStop() {
        super.onStop();
    }

    //Not necessary to have this (you can delete it), basically if you want to do something special in a loop on init you would put it here
    @Override
    public void init_loop() {
        super.init_loop();
    }

    //Not necessary to have this (you can delete it), basically if you want to do something special on stop you would put it here
    @Override
    public void stop() {
        super.stop();
    }
}