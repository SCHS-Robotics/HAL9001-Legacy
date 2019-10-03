package org.firstinspires.ftc.teamcode.season.programs;

import android.provider.Settings;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.season.robot.SumthinBot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;

@TeleOp(name = "Our_Teleop")
public class OurOpmode extends BaseTeleop {

    Robot DESTROYER;

    @Override
    protected Robot buildRobot() {
        DESTROYER = new SumthinBot(this);
        return  DESTROYER;
    }
}
