package org.firstinspires.ftc.teamcode.season.programs;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

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
        Vector Yeet = new Vector(0, .1);
        DESTROYER2.mechanumDrive.drive(Yeet, 1000);
        DESTROYER2.mechanumDrive.wait(30000);
        DESTROYER2.mechanumDrive.turnTo(Math.PI,.1);
        DESTROYER2.mechanumDrive.wait(30000);
        DESTROYER2.mechanumDrive.drive(Yeet, 1000);
        }
    }
