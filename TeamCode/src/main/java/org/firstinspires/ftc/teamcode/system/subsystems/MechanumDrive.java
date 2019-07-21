/*
 * Filename: MechanumDrive.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: TODO
 */

package org.firstinspires.ftc.teamcode.system.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.system.source.SubSystem;

public class MechanumDrive extends SubSystem {

    private DcMotor topRight, topLeft, botRight, botLeft;

    public DriveType type;

    /**
     * Specifies the type of drive the user will use.
     */
    public enum DriveType {
        STANDARD, FIELD_CENTRIC
    }

    /**
     * Initializes the Mechanum drive to Standard driving.
     *
     * @param robot - The robot whose drive we will initialize.
     */
    public MechanumDrive(Robot robot) {
        super(robot);
        this.type = DriveType.STANDARD;
    }

    /**
     * Initializes the Mechanum drive to a user-specified driving mode.
     *
     * @param robot - The robot whose drive we will initialize.
     * @param driveType - Type of drive to initialize this robot with
     */
    public MechanumDrive(Robot robot, DriveType driveType) {
        super(robot);
        this.type = driveType;
    }

    /**
     * Gets all four driving motors from the hardwareMap.
     */
    public void getMotors () {
        topRight = robot.hardwareMap.dcMotor.get("topRight");
        topLeft = robot.hardwareMap.dcMotor.get("topLeft");
        botRight = robot.hardwareMap.dcMotor.get("botRight");
        botLeft = robot.hardwareMap.dcMotor.get("botLeft");
    }

    /**
     * Resets all encoders affiliated with the drive train.
     */
    public void resetAllEncoders() {
        topRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        topLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        botRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        botLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Runs all motors in Encoder mode.
     */
    public void runAllWEncoders() {
        topRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        topLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        botRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        botLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void init() {
        getMotors();

        topLeft.setDirection(DcMotor.Direction.REVERSE);
        botLeft.setDirection(DcMotor.Direction.REVERSE);

        resetAllEncoders();

        runAllWEncoders();

    }

    @Override
    public void handle() {

    }

    @Override
    public void stop() {

    }

}
