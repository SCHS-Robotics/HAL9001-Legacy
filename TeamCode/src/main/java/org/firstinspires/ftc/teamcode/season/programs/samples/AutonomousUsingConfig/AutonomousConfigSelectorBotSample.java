package org.firstinspires.ftc.teamcode.season.programs.samples.AutonomousUsingConfig;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.TankDrive;

/**
 * Sample robot to show how to select autonomous modes using config.
 */
public class AutonomousConfigSelectorBotSample extends Robot {

    //The AutonomousConfigSubsystem
    public AutonomousConfigSelectorSample autonomous;
    //The tankdrive subsystem
    public TankDrive tankDrive;

    /**
     * Ctor for robot.
     *
     * @param opMode - The opmode the robot is currently running.
     */
    public AutonomousConfigSelectorBotSample(OpMode opMode) {
        super(opMode);
        //Creates new autonomous sample configurator
        autonomous = new AutonomousConfigSelectorSample(this);
        //Creates tank drive subsystem
        tankDrive = new TankDrive(this, new TankDrive.Params("leftMotor", "rightMotor"));

        //Adds the subsystems.
        putSubSystem("autonomous", autonomous);
        putSubSystem("drive", tankDrive);
    }
}
