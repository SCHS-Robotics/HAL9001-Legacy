package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.RobotSubsystems.FoundationGrabberSubsystem;
import org.firstinspires.ftc.teamcode.system.subsystems.RobotSubsystems.IntakeSubSystem;

public class MainRobot extends Robot {
    FoundationGrabberSubsystem grabber;
    IntakeSubSystem intake;
    public MainRobot(OpMode opMode) {
        super(opMode);
        grabber = new FoundationGrabberSubsystem(this, "1");
        // we need to make the "2" thing
        intake = new IntakeSubSystem(this,"1", "2");
        super.putSubSystem("FoundationGrabber", grabber);
        super.putSubSystem("IntakeSubSystem", intake);
    }

}
