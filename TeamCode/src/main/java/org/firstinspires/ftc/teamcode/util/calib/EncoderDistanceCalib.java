/*
 * Filename: EncoderDistanceCalib.java
 * Author: Cole Savage and Dylan Zueck
 * Team Name: Level Up, Crow Force
 * Date: 9/1/19
 */

package org.firstinspires.ftc.teamcode.util.calib;

import org.firstinspires.ftc.teamcode.system.menus.DisplayMenu;
import org.firstinspires.ftc.teamcode.system.menus.EncoderDistanceCalibMenu;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.system.subsystems.MechanumDrive;
import org.firstinspires.ftc.teamcode.system.subsystems.OmniWheelDrive;
import org.firstinspires.ftc.teamcode.system.subsystems.QuadWheelDrive;
import org.firstinspires.ftc.teamcode.system.subsystems.TankDrive;
import org.firstinspires.ftc.teamcode.util.exceptions.GuiNotPresentException;
import org.firstinspires.ftc.teamcode.util.exceptions.NotAnAlchemistException;
import org.firstinspires.ftc.teamcode.util.math.Units;
import org.firstinspires.ftc.teamcode.util.math.Vector;
import org.firstinspires.ftc.teamcode.util.misc.BaseParam;
import org.firstinspires.ftc.teamcode.util.misc.Button;

import java.util.LinkedHashMap;

import static java.lang.Thread.sleep;

/**
 * Finds the number of encoder ticks per meter that can be used in the encoder.
 * 
 * BeforeHAL: Wow im so bad at coding. I only use one constructor in my classes.
 * AfterHAL: Wow im so good at coding. I only used one constructor in this class.
 */
public class EncoderDistanceCalib extends SubSystem {

    //The drivetrain subsystem being used.
    private SubSystem driveSubSystem;
    //How far the user measured the robot to move.
    private double distance;
    //The unit of distance being entered.
    private Units unit;
    //Map of motor names to encoder positions at the start and end of the program.
    private LinkedHashMap<String, Integer> startEncoderPos, endingEncoderPos;
    //The entry speed mode button.
    private Button switchSpeedButton;

    /**
     * An enum representing the DriveTrain being used.
     */
    public enum DriveTrain {
        TANK_DRIVE, QUAD_WHEEL_DRIVE, MECHANUM_DRIVE, OMNIWHEEL_DRIVE
    }
    private DriveTrain driveTrain;

    /**
     * An enum representing the state of the calibration program.
     */
    private enum State{
        RUNNING, DISPLAYING, DONE
    }
    private State state = State.RUNNING;

    /**
     * Constructor for EncoderDistanceCalib.
     *
     * @param robot - The robot using this subsystem.
     * @param driveTrain - The drivetrain being used.
     * @param unit - The unit of distance to enter.
     * @param params - The drivetrain params to use to create the drivetrain.
     * @param switchSpeedButton - The speed mode button for distance entry.
     */
    public EncoderDistanceCalib(Robot robot, DriveTrain driveTrain, Units unit, BaseParam params, Button switchSpeedButton) {
        super(robot);

        this.unit = unit;
        this.switchSpeedButton = switchSpeedButton;

        startEncoderPos = new LinkedHashMap<>();
        endingEncoderPos = new LinkedHashMap<>();

        if(!robot.usesGUI()){
            throw new GuiNotPresentException("ColorspaceCalib requires a GUI to correctly run");
        }

        this.driveTrain = driveTrain;

        switch (driveTrain) {
            case TANK_DRIVE:
                if (!(params instanceof TankDrive.Params)) {
                    throw new NotAnAlchemistException("Given param must be a param from passed DriveTrain");
                }
                driveSubSystem = new TankDrive(robot, (TankDrive.Params) params);
                break;
            case MECHANUM_DRIVE:
                if (!(params instanceof MechanumDrive.Params)) {
                    throw new NotAnAlchemistException("Given param must be a param from passed DriveTrain");
                }
                driveSubSystem = new MechanumDrive(robot, (MechanumDrive.Params) params);
                break;
            case OMNIWHEEL_DRIVE:
            if(!(params instanceof OmniWheelDrive.Params)){
                throw new NotAnAlchemistException("Given param must be a param from passed DriveTrain");
            }
            driveSubSystem = new OmniWheelDrive(robot, (OmniWheelDrive.Params) params);
                break;
            case QUAD_WHEEL_DRIVE:
                if (!(params instanceof QuadWheelDrive.Params)) {
                    throw new NotAnAlchemistException("Given param must be a param from passed DriveTrain");
                }
                driveSubSystem = new QuadWheelDrive(robot, (QuadWheelDrive.Params) params);
                break;
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        switch (driveTrain){
            case TANK_DRIVE:
                startEncoderPos.put("Left", ((TankDrive) driveSubSystem).getLeftMotorEncoderPos());
                startEncoderPos.put("Right", ((TankDrive) driveSubSystem).getRightMotorEncoderPos());
                break;
            case MECHANUM_DRIVE:
                startEncoderPos.put("BotLeft", ((MechanumDrive) driveSubSystem).getBotLeftEncoderPos());
                startEncoderPos.put("BotRight", ((MechanumDrive) driveSubSystem).getBotRightEncoderPos());
                startEncoderPos.put("TopLeft", ((MechanumDrive) driveSubSystem).getTopLeftEncoderPos());
                startEncoderPos.put("TopRight", ((MechanumDrive) driveSubSystem).getTopRightEncoderPos());
                break;
            case OMNIWHEEL_DRIVE:
                startEncoderPos.put("BotLeft", ((OmniWheelDrive) driveSubSystem).getBotLeftEncoderPos());
                startEncoderPos.put("BotRight", ((OmniWheelDrive) driveSubSystem).getBotRightEncoderPos());
                startEncoderPos.put("TopLeft", ((OmniWheelDrive) driveSubSystem).getTopLeftEncoderPos());
                startEncoderPos.put("TopRight", ((OmniWheelDrive) driveSubSystem).getTopRightEncoderPos());
                break;
            case QUAD_WHEEL_DRIVE:
                startEncoderPos.put("BotLeft", ((QuadWheelDrive) driveSubSystem).getBotLeftMotorEncoderPos());
                startEncoderPos.put("BotRight", ((QuadWheelDrive) driveSubSystem).getBotRightMotorEncoderPos());
                startEncoderPos.put("TopLeft", ((QuadWheelDrive) driveSubSystem).getTopLeftMotorEncoderPos());
                startEncoderPos.put("TopRight", ((QuadWheelDrive) driveSubSystem).getTopRightMotorEncoderPos());
                break;
        }
    }

    @Override
    public void handle() throws InterruptedException {
        if(state == State.RUNNING) {
            switch (driveTrain) {
                case TANK_DRIVE:
                    usingTankDrive();
                    break;
                case MECHANUM_DRIVE:
                    usingMechanumDrive();
                    break;
                case OMNIWHEEL_DRIVE:
                    usingOmniWheelDrive();
                    break;
                case QUAD_WHEEL_DRIVE:
                    usingQuadWheelDrive();
                    break;
            }
        }
        else if(state == State.DISPLAYING) {
            DisplayMenu displayMenu1 = new DisplayMenu(robot.gui);
            robot.gui.addMenu("DisplayMenu1", displayMenu1);
            for (String key: startEncoderPos.keySet()) {
                displayMenu1.addData(key, (endingEncoderPos.get(key) - startEncoderPos.get(key))/distance);
            }
        }
    }

    @Override
    public void stop() {
    }

    /**
     * Drive forward for 2 seconds using tank drive.
     *
     * @throws InterruptedException - Throws this exception when the program is interrupted unexpectedly.
     */
    private void usingTankDrive() throws InterruptedException {
        ((TankDrive) driveSubSystem).driveTime(2000, 1);
        sleep(100);
        endingEncoderPos.put("Left", ((TankDrive) driveSubSystem).getLeftMotorEncoderPos());
        endingEncoderPos.put("Right", ((TankDrive) driveSubSystem).getRightMotorEncoderPos());
        robot.gui.addMenu("Getting Menu", new EncoderDistanceCalibMenu(robot.gui, unit, switchSpeedButton, this));
        robot.gui.setActiveMenu("Getting menu");
        state = State.DISPLAYING;
    }

    /**
     * Drive forward for 2 seconds using mechanum drive.
     *
     * @throws InterruptedException - Throws this exception when the program is interrupted unexpectedly.
     */
    private void usingMechanumDrive() throws InterruptedException {
        ((MechanumDrive) driveSubSystem).driveTime(new Vector(0,1), 2000);
        sleep(100);
        endingEncoderPos.put("BotLeft", ((MechanumDrive) driveSubSystem).getBotLeftEncoderPos());
        endingEncoderPos.put("BotRight", ((MechanumDrive) driveSubSystem).getBotRightEncoderPos());
        endingEncoderPos.put("TopLeft", ((MechanumDrive) driveSubSystem).getTopLeftEncoderPos());
        endingEncoderPos.put("TopRight", ((MechanumDrive) driveSubSystem).getTopRightEncoderPos());
        robot.gui.addMenu("Getting Menu", new EncoderDistanceCalibMenu(robot.gui, unit, switchSpeedButton, this));
        robot.gui.setActiveMenu("Getting menu");
        state = State.DISPLAYING;
    }

    /**
     * Drive forward for 2 seconds using omniwheel drive.
     *
     * @throws InterruptedException - Throws this exception when the program is interrupted unexpectedly.
     */
    private void usingOmniWheelDrive() throws InterruptedException{
        ((OmniWheelDrive) driveSubSystem).drive(new Vector(0,1), 2000);
        sleep(100);
        endingEncoderPos.put("BotLeft", ((MechanumDrive) driveSubSystem).getBotLeftEncoderPos());
        endingEncoderPos.put("BotRight", ((MechanumDrive) driveSubSystem).getBotRightEncoderPos());
        endingEncoderPos.put("TopLeft", ((MechanumDrive) driveSubSystem).getTopLeftEncoderPos());
        endingEncoderPos.put("TopRight", ((MechanumDrive) driveSubSystem).getTopRightEncoderPos());
        robot.gui.addMenu("Getting Menu", new EncoderDistanceCalibMenu(robot.gui, unit, switchSpeedButton, this));
        robot.gui.setActiveMenu("Getting menu");
        state = State.DISPLAYING;
    }

    /**
     * Drive forward for 2 seconds using quad wheel drive.
     *
     * @throws InterruptedException - Throws this exception when the program is interrupted unexpectedly.
     */
    private void usingQuadWheelDrive() throws InterruptedException {
        ((QuadWheelDrive) driveSubSystem).driveTime(2000, 1);
        sleep(100);
        endingEncoderPos.put("BotLeft", ((QuadWheelDrive) driveSubSystem).getBotLeftMotorEncoderPos());
        endingEncoderPos.put("BotRight", ((QuadWheelDrive) driveSubSystem).getBotRightMotorEncoderPos());
        endingEncoderPos.put("TopLeft", ((QuadWheelDrive) driveSubSystem).getTopLeftMotorEncoderPos());
        endingEncoderPos.put("TopRight", ((QuadWheelDrive) driveSubSystem).getTopRightMotorEncoderPos());
        robot.gui.addMenu("Getting Menu", new EncoderDistanceCalibMenu(robot.gui, unit, switchSpeedButton, this));
        robot.gui.setActiveMenu("Getting menu");
        state = State.DISPLAYING;
    }

    /**
     * Closes menu and gets the distance from the user.
     *
     * @param distance - The distance entered by the user.
     */
    public void numberSelected(double distance){
        this.distance = distance;
        state = State.DISPLAYING;
        robot.gui.removeMenu("Getting Menu");
    }

}
