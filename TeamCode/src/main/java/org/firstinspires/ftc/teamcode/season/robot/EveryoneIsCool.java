package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.MechanumDrive;

public class EveryoneIsCool extends Robot {
    public MechanumDrive sampleMechanum;
    public EveryoneIsCool(OpMode opMode) {
        super(opMode);
        sampleMechanum = new MechanumDrive(this, new MechanumDrive.Params("frontLeft", "frontRight", "backleft", "backRight"));
        putSubSystem("Bob", sampleMechanum);
    }
}
