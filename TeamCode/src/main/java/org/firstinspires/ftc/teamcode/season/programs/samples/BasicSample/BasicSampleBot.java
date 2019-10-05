package org.firstinspires.ftc.teamcode.season.programs.samples.BasicSample;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.MechanumDrive;

public class BasicSampleBot extends Robot {

    public MechanumDrive drive;

    /**
     * Constructor for robot.
     *
     * @param opMode - The opmode the robot is currently running.
     */
    public BasicSampleBot(OpMode opMode) {
        super(opMode);
        //sets the drive subSystem to tank drive. This one uses default params
        //drive = new TankDrive(this, new TankDrive.Params("MotorConfigLeft", "MotorConfigRight"));
        drive = new MechanumDrive(this,new MechanumDrive.Params("forwardLeftMotor","forwardRightMotor","backLeftMotor","backRightMotor"));
        
         /*
        This is an example of how to setup TankDrive without using default params. Use .set(setting to set) to change a setting from default. Otherwise it will stay default.
        Remember to import button if uncomment this (try alt+enter)

        drive = new TankDrive(this,
                new TankDrive.Params("MotorConfigLeft", "MotorConfigRight")
                        .setConstantSpeedModifier(.5)
                        .setDriveStick(new Button(1,Button.DoubleInputs.right_stick_y))
        );
         */

        putSubSystem("Tank", drive);
    }
}