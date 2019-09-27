package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.PracticeMechanumDrive;

public class PracticeRobot extends Robot {
    public PracticeMechanumDrive drive;

    public PracticeRobot(OpMode opMode) {
        super(opMode);
        drive = new PracticeMechanumDrive(this);

        putSubSystem("Mechanum", drive);

    }
}
