package org.firstinspires.ftc.teamcode.season.programs;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;


import org.firstinspires.ftc.teamcode.season.robot.MainRobot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseAutonomous;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.math.Vector;


@Autonomous (name = "Autonomous")
public class Autonomouss extends BaseAutonomous {
    private MainRobot robot;
    @Override
    protected Robot buildRobot() {
        robot = new MainRobot(this);
        return robot;
    }
    Vector straight = new Vector(0,1);
    Vector backwards = new Vector(0, -1);
    Vector left = new Vector(-1, 0);
    Vector right = new Vector(1, 0);
    @Override
    public void main() throws InterruptedException {
        switch(robot.selector.autonomous) {
            case("ParkOnBridge"):
                if(robot.selector.color == "Red" || robot.selector.color == "Blue") {
                    robot.mDrive.driveTime(straight, 2000); }
                break;

            case("MoveFoundationPark"):
                if(robot.selector.startPos == "Construction") {
                    if (robot.selector.color == "Red") {
                        robot.mDrive.driveTime(straight, 2000);
                        robot.grabber.toggleDown();
                        robot.mDrive.driveTime(backwards, 750);
                        robot.mDrive.turnTo(90, 1);
                        robot.grabber.toggleUp();
                        robot.mDrive.driveTime(backwards, 700);
                    }

                    if (robot.selector.color == "Blue") {
                        robot.mDrive.driveTime(straight, 2000);
                        robot.grabber.toggleDown();
                        robot.mDrive.driveTime(backwards, 750);
                        robot.mDrive.turnTo(-90, 1);
                        robot.grabber.toggleUp();
                        robot.mDrive.driveTime(backwards, 700);
                    }
                }
                break;

            case("MaxPoints"):
                int layout = 0;
                if(robot.selector.color == "Red") {
                    if (robot.selector.startPos == "Resourse") {
                        //runopencv have it output the layout(1,2, or 3)
                        if (layout == 1) {
                            //drive to leftmost block and intake
                            robot.mDrive.driveTime(right, 300);
                            robot.mDrive.driveTime(straight, 200);
                            robot.blockIntake.intake(1500);
                            //backup, drive towards the foundation
                            robot.mDrive.driveTime(backwards, 200);
                            robot.mDrive.driveTime(left, 2500);
                            //move forwards to the foundation, and place the block
                            robot.mDrive.driveTime(straight, 200);
                            robot.blockIntake.output(1500);
                            //grab the foundation, pull it back, and then turn to move it into building zone
                            robot.grabber.toggleDown();
                            robot.mDrive.driveTime(backwards, 250);
                            robot.mDrive.turnTo(-90, 1);
                            robot.grabber.toggleUp();
                            //move back towards special block 2 (position 4), turn into place, drive forward and grab the block
                            robot.mDrive.driveTime(backwards, 2000);
                            robot.mDrive.turnTo(90, 1);
                            robot.mDrive.driveTime(straight, 200);
                            robot.blockIntake.intake(1500);
                            //back up, turn to face the foundation and drive to it
                            robot.mDrive.driveTime(backwards, 500);
                            robot.mDrive.turnTo(-90, 1);
                            robot.mDrive.driveTime(straight, 2200);
                            //place block and park under the bridge
                            robot.blockIntake.output(1500);
                            robot.mDrive.driveTime(backwards, 1250);
                        }
                    }
                }
                if(robot.selector.color == "Blue") {
                    if (robot.selector.startPos == "Resourse") {
                        //runopencv have it output the layout(1,2, or 3)
                        if (layout == 1) {
                            //drive to leftmost block and intake
                            robot.mDrive.driveTime(right, 300);
                            robot.mDrive.driveTime(straight, 200);
                            robot.blockIntake.intake(1500);
                            //backup, drive towards the foundation
                            robot.mDrive.driveTime(backwards, 200);
                            robot.mDrive.driveTime(left, 2500);
                            //move forwards to the foundation, and place the block
                            robot.mDrive.driveTime(straight, 200);
                            robot.blockIntake.output(1500);
                            //grab the foundation, pull it back, and then turn to move it into building zone
                            robot.grabber.toggleDown();
                            robot.mDrive.driveTime(backwards, 250);
                            robot.mDrive.turnTo(-90, 1);
                            robot.grabber.toggleUp();
                            //move back towards special block 2 (position 4), turn into place, drive forward and grab the block
                            robot.mDrive.driveTime(backwards, 2000);
                            robot.mDrive.turnTo(90, 1);
                            robot.mDrive.driveTime(straight, 200);
                            robot.blockIntake.intake(1500);
                            //back up, turn to face the foundation and drive to it
                            robot.mDrive.driveTime(backwards, 500);
                            robot.mDrive.turnTo(-90, 1);
                            robot.mDrive.driveTime(straight, 2200);
                            //place block and park under the bridge
                            robot.blockIntake.output(1500);
                            robot.mDrive.driveTime(backwards, 1250);
                        }
                    }
                }
                break;
        }
    }
}

