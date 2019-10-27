package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.MechanumDrive;
import org.firstinspires.ftc.teamcode.system.subsystems.RobotSubsystems.AutonomousSelectorSubsystemUsingConfig;
import org.firstinspires.ftc.teamcode.system.subsystems.RobotSubsystems.EncoderSubsystem;
import org.firstinspires.ftc.teamcode.system.subsystems.RobotSubsystems.FoundationGrabberSubsystem;
import org.firstinspires.ftc.teamcode.system.subsystems.RobotSubsystems.IntakeSubSystem;

public class MainRobot extends Robot {

    public MechanumDrive mDrive;
    public FoundationGrabberSubsystem grabber;
    public IntakeSubSystem blockIntake;
    public AutonomousSelectorSubsystemUsingConfig selector;

    public MainRobot(OpMode opMode) {
        super(opMode);

        grabber = new FoundationGrabberSubsystem(this, "1");
        mDrive = new MechanumDrive(this, new MechanumDrive.SpecificParams("topLeft", "topRight", "bottomLeft", "bottomRight"), false  );

        // we need to make the "2" thing
        blockIntake = new IntakeSubSystem(this,"1", "2");
        selector = new AutonomousSelectorSubsystemUsingConfig(this);

        super.putSubSystem("SubsystemSelector", selector);
        super.putSubSystem("MainRobot", mDrive);
        super.putSubSystem("FoundationGrabber", grabber);
        super.putSubSystem("IntakeSubSystem", blockIntake);
    }

}
