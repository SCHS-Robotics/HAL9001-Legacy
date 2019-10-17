package org.firstinspires.ftc.teamcode.season.programs;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.season.robot.SumthinBot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseAutonomous;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.exceptions.GuiNotPresentException;
import org.firstinspires.ftc.teamcode.util.math.Vector;

@Autonomous (name = "OurAutonomous")
public class OurAutonomous extends BaseAutonomous {

    public boolean usesGyro = true;
    public void turnTo(double angle, double tolerance) throws InterruptedException {
        if (!usesGyro) {
            throw new GuiNotPresentException("turnTo must use a gyroscope");
        }

    }

    SumthinBot DESTROYER2;
    @Override
    protected Robot buildRobot () {
        DESTROYER2 = new SumthinBot(this);
        return DESTROYER2;
    }

    @Override
    public void main () throws InterruptedException {

        DESTROYER2.mechanumDrive.drive(new Vector(0, 0.3), 3000);
        DESTROYER2.asd.turnTime(5000,-0.3);
        DESTROYER2.mechanumDrive.drive(new Vector(0, 0.3), 3000);

        }
    }
