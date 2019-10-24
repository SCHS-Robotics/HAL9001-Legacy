package org.firstinspires.ftc.teamcode.season.programs;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.season.robot.SumthinBot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.BaseAutonomous;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.exceptions.GuiNotPresentException;
import org.firstinspires.ftc.teamcode.util.math.Vector;
//make a new autonomous called "OurAutonomous
@Autonomous (name = "OurAutonomous")
//make it extend base autnomous
public class OurAutonomous extends BaseAutonomous {
//let it use gyroscope
    public boolean usesGyro = true;
    //create a turn method for autonomous
    public void turnTo(double angle, double tolerance) throws InterruptedException {
        if (!usesGyro) {
            throw new GuiNotPresentException("turnTo must use a gyroscope");
        }

    }
//create a new robot called DESTROYER2 that extends SumthinBot
    SumthinBot DESTROYER2;
    @Override
    protected Robot buildRobot () {
        DESTROYER2 = new SumthinBot(this);
        return DESTROYER2;
    }
//actual autonomous code
    @Override
    public void main () throws InterruptedException {
//drives foward for 3 seconds
        DESTROYER2.mechanumDrive.driveTime(new Vector(0, 0.3), 3000);
//does a 180
        DESTROYER2.asd.turnTime(5000,-0.3);
//drives forward for 3 seconds
        DESTROYER2.mechanumDrive.driveTime(new Vector(0,.3), 3000);

        }
    }


    /*
A = 1: B = 2: C = 3: D = 4
If A + C = D then D = D - 2
If 2 * D + 4 * A &lt; 10 then A = A + 4
If A + D &gt; 3 * B + C then B = C – A
If B &lt; C then C = B
Print A + B + C + D

#include <iostream>
using namespace std;

struct robot {
	bool isSpecial;
	void init_robot() {}
	// movement wheels
	void moveForward() {}
	void moveBackward() {}
	void moveLeft() {}
	void moveRight() {}
	void turnLeft() {}
	void turnRight() {}
	// parking
	void parkAtResourceSite() {}
	void parkAtBuildingSite() {}
	// computer vision
	void checkBlock() {}
	void checkSkyStone() {}
	void checkBase() {}
	void checkLoc() {}
	void checkBlocDis() {}
	// actions
	void takeBlock() {}
	void dropBlock() {}
};

int main() {
	robot r;
	r.init_robot();
	bool active = true;
	r.partAtResourceSite();
	while (active) {
		r.checkLoc();
		for (int i=0; i<360; i+= 30) {
			if (r.checkBlock() && r.isSpecial) {
				while (r.checkBlockDis() > 1) {
					r.moveForward();
				}
				r.takeBlock();
				goBackTouildingSiteAndPlaceBlock();
			}
			turnLeft(30);
		}

		Get both special blocks individually (left/right side of bridge), park (left/right side of bridge) [start on block side] (33 pts)
		Park left/right side of bridge[start on building side] (5 pts)
		Park left/right side of bridge[start on resource side] (5 pts)
		Move foundation, park left/right [start on building side] (15 pts)
		Get 1 block {left block} (right/left side of bridge],  move foundation, park (left/right side of bridge) [start on block side] (29 pts)
		Get 1 block {right block} (right/left side of bridge), move foundation, park (left/right side of bridge) [start on block side] (29 pts)


	}
            return 0;
            }
     */