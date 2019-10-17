package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.season.programs.OurAutonomous;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.MechanumDrive;
import org.firstinspires.ftc.teamcode.system.subsystems.OurCode;
import org.firstinspires.ftc.teamcode.system.subsystems.OurCode2;
import org.firstinspires.ftc.teamcode.system.subsystems.OurServo;
import org.firstinspires.ftc.teamcode.util.misc.Button;

public class SumthinBot extends Robot{

        /**
         * Ctor for robot
         *
         * @param opMode - The opmode the robot is currently running.
         */
public OurCode asd;


        public MechanumDrive mechanumDrive = new MechanumDrive (this, new MechanumDrive.Params("topLeft", "topRight", "bottomLeft", "bottomRight")
                .setDriveType(MechanumDrive.DriveType.STANDARD)
                .setDriveStick(new Button(1, Button.VectorInputs.left_stick))
                .setTurnStick(new Button(1, Button.DoubleInputs.right_stick_x))

        );



        public SumthinBot(OpMode opMode) {
            super(opMode);
            asd = new OurCode(this);
            super.putSubSystem("Driving", mechanumDrive);
            super.putSubSystem("Alex", asd);


        }

}