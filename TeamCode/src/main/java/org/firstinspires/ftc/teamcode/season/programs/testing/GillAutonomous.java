package org.firstinspires.ftc.teamcode.season.programs.testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.season.robot.BillBot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseAutonomous;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.exceptions.GuiNotPresentException;
import org.firstinspires.ftc.teamcode.util.math.Vector;

@Autonomous(name = "GillAutonomous")
public class GillAutonomous extends BaseAutonomous {

    BillBot billBot;
    public boolean usingGyro = true;

    @Override
    protected Robot buildRobot() {
        BillBot bb = new BillBot(this);
        //billBot = new BillBot(this);
        return billBot;
    }

    @Override
    public void main() throws InterruptedException {
        Vector v = new Vector(0, 1);
        Vector c = new Vector(0, -1);
        //billBot.mD.drive(v, 1000);
        billBot.mD.wait(2000);
        billBot.mD.turnTo(Math.PI, 0.1);
        billBot.mD.wait(2000);
        //billBot.mD.drive(v, 1000);
        while(opModeIsActive()){
            // pass
        }
    }
}