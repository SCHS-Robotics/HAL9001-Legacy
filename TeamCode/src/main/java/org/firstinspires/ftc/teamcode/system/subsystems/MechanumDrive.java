/*
 * Filename: MechanumDrive.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: 9/1/19
 */

package org.firstinspires.ftc.teamcode.system.subsystems;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.util.annotations.AutonomousConfig;
import org.firstinspires.ftc.teamcode.util.annotations.TeleopConfig;
import org.firstinspires.ftc.teamcode.util.control.PIDController;
import org.firstinspires.ftc.teamcode.util.exceptions.DumpsterFireException;
import org.firstinspires.ftc.teamcode.util.exceptions.GuiNotPresentException;
import org.firstinspires.ftc.teamcode.util.exceptions.InvalidMoveCommandException;
import org.firstinspires.ftc.teamcode.util.exceptions.NotAnAlchemistException;
import org.firstinspires.ftc.teamcode.util.exceptions.NotBooleanInputException;
import org.firstinspires.ftc.teamcode.util.exceptions.NotDoubleInputException;
import org.firstinspires.ftc.teamcode.util.exceptions.NotVectorInputException;
import org.firstinspires.ftc.teamcode.util.exceptions.WrongDrivetypeException;
import org.firstinspires.ftc.teamcode.util.functional_interfaces.BiFunction;
import org.firstinspires.ftc.teamcode.util.math.EncoderToDistanceProcessor;
import org.firstinspires.ftc.teamcode.util.math.Units;
import org.firstinspires.ftc.teamcode.util.math.Vector;
import org.firstinspires.ftc.teamcode.util.misc.BaseParam;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;
import org.firstinspires.ftc.teamcode.util.misc.Toggle;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.Math.PI;
import static java.lang.Thread.sleep;

/**
 * A built in mechanum drive class with 7 drive modes.
 */
public class MechanumDrive extends SubSystem {

    //Names of all the controls.
    private static final String DRIVESTICK = "drivestick", LEFT_DRIVESTICK = "drivestick_left", RIGHT_DRIVESTICK = "drivestick_right", TURNSTICK = "turnstick", TURN_LEFT = "turn_left", TURN_RIGHT = "turn_right", TTA_STICK = "tta_stick", SPEED_MODE = "speed_mode_toggle";
    //Motors used to control the robot.
    private DcMotorEx topRight, topLeft, botRight, botLeft;
    //Gyroscope used to get the robot's current angle.
    private BNO055IMU imu;
    //The motor config. [0] is top left, [1] is top right, [2] is bottom left, [3] is bottom right.
    private String[] config;
    //Power bias to add or subtract for turning left and right.
    private double turnRightPower, turnLeftPower;
    //The customizable gamepad used to control the robot.
    private CustomizableGamepad inputs;
    //PID controllers for turning to specific angles and driving in a straight line, respectively.
    private PIDController turnPID, stabilityPID;
    //A boolean designating whether or not the drive will use the gyroscope.
    private boolean usesGyro;
    //The number of encoder ticks per meter traveled.
    private double encoderPerMeter;
    //Which IMU the robot should use when configuring the rev hub internal gyroscope.
    private int imuNumber;
    //Speed mode multipliers for use in speed toggle and speed reduction/amplification.
    private double constantSpeedMultiplier, speedModeMultiplier, slowModeMultiplier;
    //A toggle that turns speed mode on and off.
    private Toggle speedModeToggle;
    //A boolean specifying whether the drive system is using specific values for the configurable settings.
    private static boolean useSpecific = false;

    //Specifies the type of drive the user will use.
    public enum DriveType {
        STANDARD, FIELD_CENTRIC, MATTHEW, ARCADE, STANDARD_TTA, FIELD_CENTRIC_TTA, ARCADE_TTA
    }
    private DriveType driveType;

    /**
     * A constructor for the mechanum drive that takes parameters as input.
     *
     * @param robot - The robot the drive is currently being used on.
     * @param params - The parameters for the drive.
     */
    public MechanumDrive(Robot robot, Params params) {
        super(robot);

        this.driveType = params.driveType;

        this.encoderPerMeter = params.encoderPerMeter;

        this.constantSpeedMultiplier = params.constantSpeedMultiplier;
        slowModeMultiplier = params.speedModeMultiplier;
        speedModeMultiplier = 1;

        usesGyro = params.useGyro;

        //Gyro should only be used if the robot is in field centric mode, one of the turn to angle modes, or explicitly uses the gyroscope.
        if(params.useGyro) {
            imu = robot.hardwareMap.get(BNO055IMU.class,params.imuNumber == 1 ? "imu" : "imu 1");
        }
        imuNumber = params.imuNumber;

        turnPID = params.turnPID;
        stabilityPID = params.stabilityPID;

        this.config = params.config.clone();

        topLeft = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[0]);
        topRight = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[1]);
        botLeft = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[2]);
        botRight = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[3]);

        resetAllEncoders();

        if(params.changeVelocityPID) {
            topLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp,params.vki,params.vkd,params.vkf));
            topRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp,params.vki,params.vkd,params.vkf));
            botLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp,params.vki,params.vkd,params.vkf));
            botRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp,params.vki,params.vkd,params.vkf));
        }

        topLeft.setDirection(DcMotor.Direction.REVERSE);
        botLeft.setDirection(DcMotor.Direction.REVERSE);

        //Add buttons to controller.
        inputs = new CustomizableGamepad(robot);
        inputs.addButton(DRIVESTICK, params.driveStick);
        inputs.addButton(LEFT_DRIVESTICK, params.driveStickLeft);
        inputs.addButton(RIGHT_DRIVESTICK, params.driveStickRight);
        inputs.addButton(TURNSTICK, params.turnStick);
        inputs.addButton(TURN_LEFT, params.turnLeft);
        inputs.addButton(TURN_RIGHT, params.turnRight);
        inputs.addButton(TTA_STICK, params.ttaStick);
        inputs.addButton(SPEED_MODE, params.speedMode);

        speedModeToggle = new Toggle(Toggle.ToggleTypes.flipToggle, false);

        turnLeftPower = params.turnLeftPower;
        turnRightPower = params.turnRightPower;
    }

    /**
     * A constructor for the mechanum drive that takes parameters as input and uses config.
     *
     * @param robot - The robot the drive is currently being used on.
     * @param params - The parameters for the drive.
     * @param usingSpecific - Whether or not specific parameters were used instead of the configuration increment system.
     */
    public MechanumDrive(Robot robot, SpecificParams params, boolean usingSpecific) {
        super(robot);
        usesConfig = true;
        useSpecific = usingSpecific;

        turnLeftPower = params.turnLeftPower;
        turnRightPower = params.turnRightPower;

        this.encoderPerMeter = params.encodersPerMeter;

        this.constantSpeedMultiplier = params.constantSpeedMultipler;
        slowModeMultiplier = params.slowModeMultiplier;
        speedModeMultiplier = 1;


        this.config = params.config.clone();

        topLeft = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[0]);
        topRight = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[1]);
        botLeft = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[2]);
        botRight = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[3]);

        resetAllEncoders();

        if(params.changeVelocityPID) {
            topLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp,params.vki,params.vkd,params.vkf));
            topRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp,params.vki,params.vkd,params.vkf));
            botLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp,params.vki,params.vkd,params.vkf));
            botRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp,params.vki,params.vkd,params.vkf));
        }

        stabilityPID = params.stabilityPID;
        turnPID = params.turnPID;
    }

    @Override
    public void init() throws InterruptedException{
        if((driveType == DriveType.FIELD_CENTRIC || driveType == DriveType.STANDARD_TTA || driveType == DriveType.FIELD_CENTRIC_TTA || driveType == DriveType.ARCADE_TTA || usesGyro) && !usesConfig) {
            imu.initialize(new BNO055IMU.Parameters());
            while(!imu.isGyroCalibrated()){sleep(1);}
        }
    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() throws InterruptedException{
        if(usesConfig && robot.isTeleop()) {
            setUsingConfigs();
        }
        else if(usesConfig && robot.isAutonomous()) {
            setUsingConfigsAutonomous();
        }
    }

    @Override
    public void handle() {
        speedModeToggle.updateToggle(inputs.getBooleanInput(SPEED_MODE));
        if(speedModeToggle.getCurrentState()) {
            speedModeMultiplier = slowModeMultiplier;
        }
        else {
            speedModeMultiplier = 1;
        }

        Vector input = inputs.getVectorInput(DRIVESTICK);
        Vector left = inputs.getVectorInput(LEFT_DRIVESTICK);
        Vector right = inputs.getVectorInput(RIGHT_DRIVESTICK);

        input.scalarMultiply(constantSpeedMultiplier*speedModeMultiplier);
        left.scalarMultiply(constantSpeedMultiplier*speedModeMultiplier);
        right.scalarMultiply(constantSpeedMultiplier*speedModeMultiplier);

        Vector tta = inputs.getVectorInput(TTA_STICK);

        double turnPower = inputs.getDoubleInput(TURNSTICK)*constantSpeedMultiplier*speedModeMultiplier;
        boolean turnLeft = inputs.getBooleanInput(TURN_LEFT);
        boolean turnRight = inputs.getBooleanInput(TURN_RIGHT);

        double correction, turnCorrection;

        switch (driveType) {

            //Standard vector drive. 1 control for driving, one for turning.
            case STANDARD:
                input.rotate(-(PI / 4));

                correction = usesGyro ? stabilityPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle) : 0;

                if((turnPower != 0 || turnLeft || turnRight) && usesGyro) {
                    stabilityPID.setSetpoint(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle);
                    correction = 0;
                }

                if(!turnLeft && !turnRight) {
                    topLeft.setPower(Range.clip(input.x + turnPower - correction,-1,1));
                    topRight.setPower(Range.clip(input.y - turnPower + correction,-1,1));
                    botLeft.setPower(Range.clip(input.y + turnPower - correction,-1,1));
                    botRight.setPower(Range.clip(input.x - turnPower + correction,-1,1));
                }
                else if(turnLeft) {
                    topLeft.setPower(Range.clip(input.x - turnLeftPower,-1,1));
                    topRight.setPower(Range.clip(input.y + turnLeftPower,-1,1));
                    botLeft.setPower(Range.clip(input.y - turnLeftPower,-1,1));
                    botRight.setPower(Range.clip(input.x + turnLeftPower,-1,1));
                }
                else {
                    topLeft.setPower(Range.clip(input.x + turnRightPower,-1,1));
                    topRight.setPower(Range.clip(input.y - turnRightPower,-1,1));
                    botLeft.setPower(Range.clip(input.y + turnRightPower,-1,1));
                    botRight.setPower(Range.clip(input.x - turnRightPower,-1,1));
                }

                break;

            //Standard drive, but the turn control is a joystick that tells the robot what angle to turn to.
            case STANDARD_TTA:
                input.rotate(-(PI / 4));

                correction = usesGyro ? stabilityPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle) : 0;

                if(!tta.isZeroVector() && usesGyro) {
                    turnPID.setSetpoint(tta.theta);
                }

                turnCorrection = turnPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle);

                if((!tta.isZeroVector() || turnLeft || turnRight) && usesGyro) {
                    stabilityPID.setSetpoint(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle);
                    correction = 0;
                    turnCorrection = 0;
                }

                if(!turnLeft && !turnRight) {
                    topLeft.setPower(Range.clip(input.x - turnCorrection - correction,-1,1));
                    topRight.setPower(Range.clip(input.y + turnCorrection + correction,-1,1));
                    botLeft.setPower(Range.clip(input.y - turnCorrection - correction,-1,1));
                    botRight.setPower(Range.clip(input.x + turnCorrection + correction,-1,1));
                }
                else if(turnLeft) {
                    topLeft.setPower(Range.clip(input.x - turnLeftPower,-1,1));
                    topRight.setPower(Range.clip(input.y + turnLeftPower,-1,1));
                    botLeft.setPower(Range.clip(input.y - turnLeftPower,-1,1));
                    botRight.setPower(Range.clip(input.x + turnLeftPower,-1,1));
                }
                else {
                    topLeft.setPower(Range.clip(input.x + turnRightPower,-1,1));
                    topRight.setPower(Range.clip(input.y - turnRightPower,-1,1));
                    botLeft.setPower(Range.clip(input.y + turnRightPower,-1,1));
                    botRight.setPower(Range.clip(input.x - turnRightPower,-1,1));
                }

                break;

            //Standard vector drive where the front of the robot is fixed.
            case FIELD_CENTRIC:
                input.rotate(-((PI / 4) + imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle));

                correction = usesGyro ? stabilityPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle) : 0;

                if((turnPower != 0 || turnLeft || turnRight) && usesGyro) {
                    stabilityPID.setSetpoint(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle);
                    correction = 0;
                }

                if(!turnLeft && !turnRight) {
                    topLeft.setPower(Range.clip(input.x + turnPower - correction,-1,1));
                    topRight.setPower(Range.clip(input.y - turnPower + correction,-1,1));
                    botLeft.setPower(Range.clip(input.y + turnPower - correction,-1,1));
                    botRight.setPower(Range.clip(input.x - turnPower + correction,-1,1));
                }
                else if(turnLeft) {
                    topLeft.setPower(Range.clip(input.x - turnLeftPower,-1,1));
                    topRight.setPower(Range.clip(input.y + turnLeftPower,-1,1));
                    botLeft.setPower(Range.clip(input.y - turnLeftPower,-1,1));
                    botRight.setPower(Range.clip(input.x + turnLeftPower,-1,1));
                }
                else {
                    topLeft.setPower(Range.clip(input.x + turnRightPower,-1,1));
                    topRight.setPower(Range.clip(input.y - turnRightPower,-1,1));
                    botLeft.setPower(Range.clip(input.y + turnRightPower,-1,1));
                    botRight.setPower(Range.clip(input.x - turnRightPower,-1,1));
                }
                break;

            //Standard vector drive where the front of the robot is fixed and the turn control is a joystick that gives the robot an angle to turn to.
            case FIELD_CENTRIC_TTA:
                input.rotate(-((PI / 4) + imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle));

                correction = usesGyro ? stabilityPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle) : 0;

                if(!tta.isZeroVector() && usesGyro) {
                    turnPID.setSetpoint(tta.theta);
                }

                turnCorrection = turnPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle);

                if((!tta.isZeroVector() || turnLeft || turnRight) && usesGyro) {
                    stabilityPID.setSetpoint(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle);
                    correction = 0;
                    turnCorrection = 0;
                }

                if(!turnLeft && !turnRight) {
                    topLeft.setPower(Range.clip(input.x - turnCorrection - correction,-1,1));
                    topRight.setPower(Range.clip(input.y + turnCorrection + correction,-1,1));
                    botLeft.setPower(Range.clip(input.y - turnCorrection - correction,-1,1));
                    botRight.setPower(Range.clip(input.x + turnCorrection + correction,-1,1));
                }
                else if(turnLeft) {
                    topLeft.setPower(Range.clip(input.x - turnLeftPower,-1,1));
                    topRight.setPower(Range.clip(input.y + turnLeftPower,-1,1));
                    botLeft.setPower(Range.clip(input.y - turnLeftPower,-1,1));
                    botRight.setPower(Range.clip(input.x + turnLeftPower,-1,1));
                }
                else {
                    topLeft.setPower(Range.clip(input.x + turnRightPower,-1,1));
                    topRight.setPower(Range.clip(input.y - turnRightPower,-1,1));
                    botLeft.setPower(Range.clip(input.y + turnRightPower,-1,1));
                    botRight.setPower(Range.clip(input.x - turnRightPower,-1,1));
                }
                break;

            //Arcade drive.
            case ARCADE:
                correction = usesGyro ? stabilityPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle) : 0;

                if((turnPower != 0 || turnLeft || turnRight) && usesGyro) {
                    stabilityPID.setSetpoint(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle);
                    correction = 0;
                }

                if(!turnLeft && !turnRight) {
                    if (input.isZeroVector()) {
                        topLeft.setPower(Range.clip(turnPower,-1,1));
                        topRight.setPower(Range.clip(-turnPower,-1,1));
                        botLeft.setPower(Range.clip(turnPower,-1,1));
                        botRight.setPower(Range.clip(-turnPower,-1,1));
                    } else if (input.theta < PI / 4 || input.theta > (7 * PI) / 4) { //right side of the square
                        topLeft.setPower(Range.clip(input.r + turnPower - correction,-1,1));
                        topRight.setPower(Range.clip(-input.r - turnPower + correction,-1,1));
                        botLeft.setPower(Range.clip(-input.r + turnPower - correction,-1,1));
                        botRight.setPower(Range.clip(input.r - turnPower + correction,-1,1));
                    } else if (input.theta > PI / 4 && input.theta < (3 * PI) / 4) { //top side of the square
                        topLeft.setPower(Range.clip(-input.r + turnPower - correction,-1,1));
                        topRight.setPower(Range.clip(input.r - turnPower + correction,-1,1));
                        botLeft.setPower(Range.clip(input.r + turnPower - correction,-1,1));
                        botRight.setPower(Range.clip(-input.r - turnPower + correction,-1,1));
                    } else if (input.theta > (3 * PI) / 4 && input.theta < (5 * PI) / 4) { //left side of the square
                        topLeft.setPower(Range.clip(input.r + turnPower - correction,-1,1));
                        topRight.setPower(Range.clip(input.r - turnPower + correction,-1,1));
                        botLeft.setPower(Range.clip(input.r + turnPower - correction,-1,1));
                        botRight.setPower(Range.clip(input.r - turnPower + correction,-1,1));
                    } else if (input.theta > (5 * PI) / 4 && input.theta < (7 * PI) / 4) { //Bottom side of the square
                        topLeft.setPower(Range.clip(-input.r + turnPower - correction,-1,1));
                        topRight.setPower(Range.clip(-input.r - turnPower + correction,-1,1));
                        botLeft.setPower(Range.clip(-input.r + turnPower - correction,-1,1));
                        botRight.setPower(Range.clip(-input.r - turnPower + correction,-1,1));
                    }
                }
                else if(turnLeft) {
                    if (input.isZeroVector()) {
                        topLeft.setPower(Range.clip(-turnLeftPower,-1,1));
                        topRight.setPower(Range.clip(turnLeftPower,-1,1));
                        botLeft.setPower(Range.clip(-turnLeftPower,-1,1));
                        botRight.setPower(Range.clip(turnLeftPower,-1,1));
                    } else if (input.theta < PI / 4 || input.theta > (7 * PI) / 4) { //right side of the square
                        topLeft.setPower(Range.clip(input.r - turnLeftPower,-1,1));
                        topRight.setPower(Range.clip(-input.r + turnLeftPower,-1,1));
                        botLeft.setPower(Range.clip(-input.r - turnLeftPower,-1,1));
                        botRight.setPower(Range.clip(input.r + turnLeftPower,-1,1));
                    } else if (input.theta > PI / 4 && input.theta < (3 * PI) / 4) { //top side of the square
                        topLeft.setPower(Range.clip(-input.r - turnLeftPower,-1,1));
                        topRight.setPower(Range.clip(input.r + turnLeftPower,-1,1));
                        botLeft.setPower(Range.clip(input.r - turnLeftPower,-1,1));
                        botRight.setPower(Range.clip(-input.r + turnLeftPower,-1,1));
                    } else if (input.theta > (3 * PI) / 4 && input.theta < (5 * PI) / 4) { //left side of the square
                        topLeft.setPower(Range.clip(input.r - turnLeftPower,-1,1));
                        topRight.setPower(Range.clip(input.r + turnLeftPower,-1,1));
                        botLeft.setPower(Range.clip(input.r - turnLeftPower,-1,1));
                        botRight.setPower(Range.clip(input.r + turnLeftPower,-1,1));
                    } else if (input.theta > (5 * PI) / 4 && input.theta < (7 * PI) / 4) { //Bottom side of the square
                        topLeft.setPower(Range.clip(-input.r - turnLeftPower,-1,1));
                        topRight.setPower(Range.clip(-input.r + turnLeftPower,-1,1));
                        botLeft.setPower(Range.clip(-input.r - turnLeftPower,-1,1));
                        botRight.setPower(Range.clip(-input.r + turnLeftPower,-1,1));
                    }
                }
                else {
                    if (input.isZeroVector()) {
                        topLeft.setPower(Range.clip(turnRightPower,-1,1));
                        topRight.setPower(Range.clip(-turnRightPower,-1,1));
                        botLeft.setPower(Range.clip(turnRightPower,-1,1));
                        botRight.setPower(Range.clip(-turnRightPower,-1,1));
                    } else if (input.theta < PI / 4 || input.theta > (7 * PI) / 4) { //right side of the square
                        topLeft.setPower(Range.clip(input.r + turnRightPower,-1,1));
                        topRight.setPower(Range.clip(-input.r - turnRightPower,-1,1));
                        botLeft.setPower(Range.clip(-input.r + turnRightPower,-1,1));
                        botRight.setPower(Range.clip(input.r - turnRightPower,-1,1));
                    } else if (input.theta > PI / 4 && input.theta < (3 * PI) / 4) { //top side of the square
                        topLeft.setPower(Range.clip(-input.r + turnRightPower,-1,1));
                        topRight.setPower(Range.clip(input.r - turnRightPower,-1,1));
                        botLeft.setPower(Range.clip(input.r + turnRightPower,-1,1));
                        botRight.setPower(Range.clip(-input.r - turnRightPower,-1,1));
                    } else if (input.theta > (3 * PI) / 4 && input.theta < (5 * PI) / 4) { //left side of the square
                        topLeft.setPower(Range.clip(input.r + turnRightPower,-1,1));
                        topRight.setPower(Range.clip(input.r - turnRightPower,-1,1));
                        botLeft.setPower(Range.clip(input.r + turnRightPower,-1,1));
                        botRight.setPower(Range.clip(input.r - turnRightPower,-1,1));
                    } else if (input.theta > (5 * PI) / 4 && input.theta < (7 * PI) / 4) { //Bottom side of the square
                        topLeft.setPower(Range.clip(-input.r + turnRightPower,-1,1));
                        topRight.setPower(Range.clip(-input.r - turnRightPower,-1,1));
                        botLeft.setPower(Range.clip(-input.r + turnRightPower,-1,1));
                        botRight.setPower(Range.clip(-input.r - turnRightPower,-1,1));
                    }
                }

                break;

            //Arcade drive with turn to angle functionality.
            case ARCADE_TTA:
                correction = usesGyro ? stabilityPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle) : 0;

                if((turnPower != 0 || turnLeft || turnRight) && usesGyro) {
                    stabilityPID.setSetpoint(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle);
                    correction = 0;
                }

                if(!tta.isZeroVector() && usesGyro) {
                    turnPID.setSetpoint(tta.theta);
                }

                turnCorrection = turnPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle);

                if((!tta.isZeroVector() || turnLeft || turnRight) && usesGyro) {
                    stabilityPID.setSetpoint(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle);
                    correction = 0;
                    turnCorrection = 0;
                }

                if(!turnLeft && !turnRight) {
                    if (input.isZeroVector()) {
                        topLeft.setPower(Range.clip(-turnCorrection,-1,1));
                        topRight.setPower(Range.clip(turnCorrection,-1,1));
                        botLeft.setPower(Range.clip(-turnCorrection,-1,1));
                        botRight.setPower(Range.clip(turnCorrection,-1,1));
                    } else if (input.theta < PI / 4 || input.theta > (7 * PI) / 4) { //right side of the square
                        topLeft.setPower(Range.clip(input.r - turnCorrection - correction,-1,1));
                        topRight.setPower(Range.clip(-input.r + turnCorrection + correction,-1,1));
                        botLeft.setPower(Range.clip(-input.r - turnCorrection - correction,-1,1));
                        botRight.setPower(Range.clip(input.r + turnCorrection + correction,-1,1));
                    } else if (input.theta > PI / 4 && input.theta < (3 * PI) / 4) { //top side of the square
                        topLeft.setPower(Range.clip(-input.r - turnCorrection - correction,-1,1));
                        topRight.setPower(Range.clip(input.r + turnCorrection + correction,-1,1));
                        botLeft.setPower(Range.clip(input.r - turnCorrection - correction,-1,1));
                        botRight.setPower(Range.clip(-input.r + turnCorrection + correction,-1,1));
                    } else if (input.theta > (3 * PI) / 4 && input.theta < (5 * PI) / 4) { //left side of the square
                        topLeft.setPower(Range.clip(input.r - turnCorrection - correction,-1,1));
                        topRight.setPower(Range.clip(input.r + turnCorrection + correction,-1,1));
                        botLeft.setPower(Range.clip(input.r - turnCorrection - correction,-1,1));
                        botRight.setPower(Range.clip(input.r + turnCorrection + correction,-1,1));
                    } else if (input.theta > (5 * PI) / 4 && input.theta < (7 * PI) / 4) { //Bottom side of the square
                        topLeft.setPower(Range.clip(-input.r - turnCorrection - correction,-1,1));
                        topRight.setPower(Range.clip(-input.r + turnCorrection + correction,-1,1));
                        botLeft.setPower(Range.clip(-input.r - turnCorrection - correction,-1,1));
                        botRight.setPower(Range.clip(-input.r + turnCorrection + correction,-1,1));
                    }
                }
                else if(turnLeft) {
                    if (input.isZeroVector()) {
                        topLeft.setPower(Range.clip(-turnLeftPower,-1,1));
                        topRight.setPower(Range.clip(turnLeftPower,-1,1));
                        botLeft.setPower(Range.clip(-turnLeftPower,-1,1));
                        botRight.setPower(Range.clip(turnLeftPower,-1,1));
                    } else if (input.theta < PI / 4 || input.theta > (7 * PI) / 4) { //right side of the square
                        topLeft.setPower(Range.clip(input.r - turnLeftPower,-1,1));
                        topRight.setPower(Range.clip(-input.r + turnLeftPower,-1,1));
                        botLeft.setPower(Range.clip(-input.r - turnLeftPower,-1,1));
                        botRight.setPower(Range.clip(input.r + turnLeftPower,-1,1));
                    } else if (input.theta > PI / 4 && input.theta < (3 * PI) / 4) { //top side of the square
                        topLeft.setPower(Range.clip(-input.r - turnLeftPower,-1,1));
                        topRight.setPower(Range.clip(input.r + turnLeftPower,-1,1));
                        botLeft.setPower(Range.clip(input.r - turnLeftPower,-1,1));
                        botRight.setPower(Range.clip(-input.r + turnLeftPower,-1,1));
                    } else if (input.theta > (3 * PI) / 4 && input.theta < (5 * PI) / 4) { //left side of the square
                        topLeft.setPower(Range.clip(input.r - turnLeftPower,-1,1));
                        topRight.setPower(Range.clip(input.r + turnLeftPower,-1,1));
                        botLeft.setPower(Range.clip(input.r - turnLeftPower,-1,1));
                        botRight.setPower(Range.clip(input.r + turnLeftPower,-1,1));
                    } else if (input.theta > (5 * PI) / 4 && input.theta < (7 * PI) / 4) { //Bottom side of the square
                        topLeft.setPower(Range.clip(-input.r - turnLeftPower,-1,1));
                        topRight.setPower(Range.clip(-input.r + turnLeftPower,-1,1));
                        botLeft.setPower(Range.clip(-input.r - turnLeftPower,-1,1));
                        botRight.setPower(Range.clip(-input.r + turnLeftPower,-1,1));
                    }
                }
                else {
                    if (input.isZeroVector()) {
                        topLeft.setPower(Range.clip(turnRightPower,-1,1));
                        topRight.setPower(Range.clip(-turnRightPower,-1,1));
                        botLeft.setPower(Range.clip(turnRightPower,-1,1));
                        botRight.setPower(Range.clip(-turnRightPower,-1,1));
                    } else if (input.theta < PI / 4 || input.theta > (7 * PI) / 4) { //right side of the square
                        topLeft.setPower(Range.clip(input.r + turnRightPower,-1,1));
                        topRight.setPower(Range.clip(-input.r - turnRightPower,-1,1));
                        botLeft.setPower(Range.clip(-input.r + turnRightPower,-1,1));
                        botRight.setPower(Range.clip(input.r - turnRightPower,-1,1));
                    } else if (input.theta > PI / 4 && input.theta < (3 * PI) / 4) { //top side of the square
                        topLeft.setPower(Range.clip(-input.r + turnRightPower,-1,1));
                        topRight.setPower(Range.clip(input.r - turnRightPower,-1,1));
                        botLeft.setPower(Range.clip(input.r + turnRightPower,-1,1));
                        botRight.setPower(Range.clip(-input.r - turnRightPower,-1,1));
                    } else if (input.theta > (3 * PI) / 4 && input.theta < (5 * PI) / 4) { //left side of the square
                        topLeft.setPower(Range.clip(input.r + turnRightPower,-1,1));
                        topRight.setPower(Range.clip(input.r - turnRightPower,-1,1));
                        botLeft.setPower(Range.clip(input.r + turnRightPower,-1,1));
                        botRight.setPower(Range.clip(input.r - turnRightPower,-1,1));
                    } else if (input.theta > (5 * PI) / 4 && input.theta < (7 * PI) / 4) { //Bottom side of the square
                        topLeft.setPower(Range.clip(-input.r + turnRightPower,-1,1));
                        topRight.setPower(Range.clip(-input.r - turnRightPower,-1,1));
                        botLeft.setPower(Range.clip(-input.r + turnRightPower,-1,1));
                        botRight.setPower(Range.clip(-input.r - turnRightPower,-1,1));
                    }
                }

                break;

            //Special driving mode requested by Matthew. Two joysticks, one controlling each side of the robot. Stability PID and turn to angle PID do not matter here.
            case MATTHEW:
                left.rotate(-(PI / 4));
                right.rotate(-(PI / 4));

                if(!turnLeft &&  !turnRight) {
                    topLeft.setPower(left.x);
                    botLeft.setPower(left.y);

                    topRight.setPower(right.y);
                    botRight.setPower(right.x);
                }
                else if (turnLeft) {
                    topLeft.setPower(left.x - turnLeftPower);
                    botLeft.setPower(left.y - turnLeftPower);

                    topRight.setPower(right.y + turnLeftPower);
                    botRight.setPower(right.x + turnLeftPower);
                }
                else {
                    topLeft.setPower(left.x - turnRightPower);
                    botLeft.setPower(left.y - turnRightPower);

                    topRight.setPower(right.y + turnRightPower);
                    botRight.setPower(right.x + turnRightPower);
                }
                break;
        }
    }

    @Override
    public void stop() {

    }

    /**
     * Resets all encoders affiliated with the drive train.
     */
    public void resetAllEncoders() {
        topLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        topRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        botLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        botRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        topLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        topRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        botLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        botRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Turns to a specified angle within a specified tolerance.
     *
     * @param angle - The angle to turn to.
     * @param tolerance - The tolerance that the angle must be within.
     *
     * @throws InterruptedException - Throws this exception if the program is unexpectedly interrupted.
     */
    public void turnTo(double angle, double tolerance) throws InterruptedException{
        if(!usesGyro) {
            throw new GuiNotPresentException("turnTo must use a gyroscope");
        }
        turnPID.setSetpoint(angle);
        while(Math.abs(angle-imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle) < tolerance) {
            double correction = turnPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle);
            topLeft.setPower(-correction);
            topRight.setPower(correction);
            botLeft.setPower(-correction);
            botRight.setPower(correction);
            sleep(1);
        }
        topLeft.setPower(0);
        topRight.setPower(0);
        botLeft.setPower(0);
        botRight.setPower(0);
    }

    /**
     * Makes the robot move and/or turn. This is only used if the drive is being controlled matthew-style.
     *
     * @param leftVector - The left input vector.
     * @param rightVector - The right input vector.
     */
    public void drive(Vector leftVector, Vector rightVector) {

        if (driveType != DriveType.MATTHEW) {
            throw new WrongDrivetypeException("Error: Drive arguments do not match drive type.");
        }

        leftVector.scalarMultiply(constantSpeedMultiplier);
        rightVector.scalarMultiply(constantSpeedMultiplier);

        leftVector.rotate(-(PI / 4));
        rightVector.rotate(-(PI / 4));

        leftVector.scalarMultiply(constantSpeedMultiplier);
        rightVector.scalarMultiply(constantSpeedMultiplier);

        topLeft.setPower(leftVector.x);
        botLeft.setPower(leftVector.y);

        topRight.setPower(rightVector.y);
        botRight.setPower(rightVector.x);
    }

    /**
     * Makes the robot move. Use this for any non-matthew drive mode.
     *
     * @param v - The direction vector indicating how the robot should move.
     */
    public void drive(Vector v){
        if (driveType == DriveType.MATTHEW) {
            throw new WrongDrivetypeException("Error: Drive arguments do not match drive type.");
        }

        v.scalarMultiply(constantSpeedMultiplier);

        switch (driveType) {
            case STANDARD_TTA:
            case STANDARD:
                v.rotate(-(PI / 4));
                topLeft.setPower(v.x);
                topRight.setPower(v.y);
                botLeft.setPower(v.y);
                botRight.setPower(v.x);
                break;
            case FIELD_CENTRIC_TTA:
            case FIELD_CENTRIC:
                if(!usesGyro) {
                    throw new WrongDrivetypeException("Field Centric Drive Must uses the IMU but the IMU was never set up");
                }
                v.rotate(-(PI / 4) + imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle);
                topLeft.setPower(v.x);
                topRight.setPower(v.y);
                botLeft.setPower(v.y);
                botRight.setPower(v.x);
                break;
            case ARCADE_TTA:
            case ARCADE:
                if (v.isZeroVector()) {
                    topLeft.setPower(0);
                    topRight.setPower(0);
                    botLeft.setPower(0);
                    botRight.setPower(0);
                }
                else if (v.theta < PI / 4 || v.theta > (7 * PI) / 4) { //right side of the square
                    topLeft.setPower(v.r);
                    topRight.setPower(-v.r);
                    botLeft.setPower(-v.r);
                    botRight.setPower(v.r);
                } else if (v.theta > PI / 4 && v.theta < (3 * PI) / 4) { //top side of the square
                    topLeft.setPower(-v.r);
                    topRight.setPower(v.r);
                    botLeft.setPower(v.r);
                    botRight.setPower(-v.r);
                } else if (v.theta > (3 * PI) / 4 && v.theta < (5 * PI) / 4) { //left side of the square
                    topLeft.setPower(v.r);
                    topRight.setPower(v.r);
                    botLeft.setPower(v.r);
                    botRight.setPower(v.r);
                } else if (v.theta > (5 * PI) / 4 && v.theta < (7 * PI) / 4) { //Bottom side of the square
                    topLeft.setPower(-v.r);
                    topRight.setPower(-v.r);
                    botLeft.setPower(-v.r);
                    botRight.setPower(-v.r);
                }
                break;
        }
    }

    /**
     * Makes the robot move. Use this for any non-matthew drive mode.
     *
     * @param v - The direction and power that the robot should move at.
     * @param stabilityControl - Whether or not to use the drive's stability control system.
     */
    public void drive(Vector v, boolean stabilityControl){
        if (driveType == DriveType.MATTHEW) {
            throw new WrongDrivetypeException("Error: Drive arguments do not match drive type.");
        }

        v.scalarMultiply(constantSpeedMultiplier);

        double correction;

        if(stabilityControl && usesGyro) {
            correction = stabilityPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle);
        }
        else {
            correction = 0;
        }

        switch (driveType) {
            case STANDARD_TTA:
            case STANDARD:
                v.rotate(-(PI / 4));
                topLeft.setPower(v.x - correction);
                topRight.setPower(v.y + correction);
                botLeft.setPower(v.y - correction);
                botRight.setPower(v.x + correction);
                break;
            case FIELD_CENTRIC_TTA:
            case FIELD_CENTRIC:
                if(!usesGyro) {
                    throw new WrongDrivetypeException("Field Centric Drive Must uses the IMU but the IMU was never set up");
                }
                v.rotate(-(PI / 4) + imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle);
                topLeft.setPower(v.x - correction);
                topRight.setPower(v.y + correction);
                botLeft.setPower(v.y - correction);
                botRight.setPower(v.x + correction);
                break;
            case ARCADE_TTA:
            case ARCADE:
                if (v.isZeroVector()) {
                    topLeft.setPower(0);
                    topRight.setPower(0);
                    botLeft.setPower(0);
                    botRight.setPower(0);
                }
                else if (v.theta < PI / 4 || v.theta > (7 * PI) / 4) { //right side of the square
                    topLeft.setPower(v.r - correction);
                    topRight.setPower(-v.r + correction);
                    botLeft.setPower(-v.r - correction);
                    botRight.setPower(v.r + correction);
                } else if (v.theta > PI / 4 && v.theta < (3 * PI) / 4) { //top side of the square
                    topLeft.setPower(-v.r - correction);
                    topRight.setPower(v.r + correction);
                    botLeft.setPower(v.r - correction);
                    botRight.setPower(-v.r + correction);
                } else if (v.theta > (3 * PI) / 4 && v.theta < (5 * PI) / 4) { //left side of the square
                    topLeft.setPower(v.r - correction);
                    topRight.setPower(v.r + correction);
                    botLeft.setPower(v.r - correction);
                    botRight.setPower(v.r + correction);
                } else if (v.theta > (5 * PI) / 4 && v.theta < (7 * PI) / 4) { //Bottom side of the square
                    topLeft.setPower(-v.r - correction);
                    topRight.setPower(-v.r + correction);
                    botLeft.setPower(-v.r - correction);
                    botRight.setPower(-v.r + correction);
                }
                break;
        }
    }

    public void drive(Vector v, double distance, Units unit) throws InterruptedException{
        if (driveType == DriveType.MATTHEW) {
            throw new WrongDrivetypeException("Error: Drive arguments do not match drive type.");
        }

        v.scalarMultiply(constantSpeedMultiplier);

        if(v.isZeroVector() && distance != 0) {
            throw new InvalidMoveCommandException("You can't move anywhere if you aren't trying to move ;)");
        }

        if(distance < 0) {
            throw new DumpsterFireException("Where you're going, you don't need roads! (distance must be positive)");
        }

        if((driveType == DriveType.ARCADE || driveType == DriveType.ARCADE_TTA) && (v.theta == PI/4 || v.theta == (3*PI)/4 || v.theta == (5*PI)/4 || v.theta == (7*PI)/4)) {
            throw new InvalidMoveCommandException("Error: You input an invalid velocity vector for arcade drive.");
        }

        Vector displacement = new Vector(distance,v.theta,Vector.CoordinateType.POLAR);
        EncoderToDistanceProcessor encProcessor = new EncoderToDistanceProcessor(encoderPerMeter);

        resetAllEncoders();

        double thresh1;
        double thresh2;

        switch (driveType) {
            case STANDARD_TTA:
            case STANDARD:
                v.rotate(-(PI / 4));
                displacement.rotate(-(PI / 4));

                thresh1 = encProcessor.getEncoderAmount(Math.abs(displacement.x),unit);
                thresh2 = encProcessor.getEncoderAmount(Math.abs(displacement.y),unit);

                while(Math.abs(topLeft.getCurrentPosition()) < thresh1 && Math.abs(topRight.getCurrentPosition()) < thresh2 && Math.abs(botLeft.getCurrentPosition()) < thresh2 && Math.abs(botRight.getCurrentPosition()) < thresh1) {
                    topLeft.setPower(v.x);
                    topRight.setPower(v.y);
                    botLeft.setPower(v.y);
                    botRight.setPower(v.x);
                    sleep(1);
                }
                break;
            case FIELD_CENTRIC_TTA:
            case FIELD_CENTRIC:
                if(!usesGyro) {
                    throw new WrongDrivetypeException("Field Centric Drive Must uses the IMU but the IMU was never set up");
                }

                v.rotate(-(PI / 4) + imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle);
                displacement.rotate(-(PI / 4) + imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle);

                thresh1 = encProcessor.getEncoderAmount(Math.abs(displacement.x),unit);
                thresh2 = encProcessor.getEncoderAmount(Math.abs(displacement.y),unit);

                while(Math.abs(topLeft.getCurrentPosition()) < thresh1 && Math.abs(topRight.getCurrentPosition()) < thresh2 && Math.abs(botLeft.getCurrentPosition()) < thresh2 && Math.abs(botRight.getCurrentPosition()) < thresh1) {
                    topLeft.setPower(v.x);
                    topRight.setPower(v.y);
                    botLeft.setPower(v.y);
                    botRight.setPower(v.x);
                    sleep(1);
                }
                break;
            case ARCADE_TTA:
            case ARCADE:

                double thresh = encoderPerMeter*Math.sqrt(2)/2;

                while(Math.abs(topLeft.getCurrentPosition()) < thresh && Math.abs(topRight.getCurrentPosition()) < thresh && Math.abs(botLeft.getCurrentPosition()) < thresh && Math.abs(botRight.getCurrentPosition()) < thresh) {
                    if (v.theta < PI / 4 || v.theta > (7 * PI) / 4) { //right side of the square
                        topLeft.setPower(v.r);
                        topRight.setPower(-v.r);
                        botLeft.setPower(-v.r);
                        botRight.setPower(v.r);
                    } else if (v.theta > PI / 4 && v.theta < (3 * PI) / 4) { //top side of the square
                        topLeft.setPower(-v.r);
                        topRight.setPower(v.r);
                        botLeft.setPower(v.r);
                        botRight.setPower(-v.r);
                    } else if (v.theta > (3 * PI) / 4 && v.theta < (5 * PI) / 4) { //left side of the square
                        topLeft.setPower(v.r);
                        topRight.setPower(v.r);
                        botLeft.setPower(v.r);
                        botRight.setPower(v.r);
                    } else if (v.theta > (5 * PI) / 4 && v.theta < (7 * PI) / 4) { //Bottom side of the square
                        topLeft.setPower(-v.r);
                        topRight.setPower(-v.r);
                        botLeft.setPower(-v.r);
                        botRight.setPower(-v.r);
                    }
                    sleep(1);
                }
                break;
        }
    }

    /**
     * Makes the robot move a certain distance. Use this for any non-matthew drive mode.
     *
     * @param v - The direction and power that the robot should move at.
     * @param distance - The distance the robot should travel.
     * @param unit - The unit of distance the robot should travel.
     * @param stabilityControl - Whether the robot should use stability control.
     *
     * @throws InterruptedException - Throws this exception if the program is unexpectedly interrupted.
     */
    public void drive(Vector v, double distance, Units unit, boolean stabilityControl) throws InterruptedException{
        if (driveType == DriveType.MATTHEW) {
            throw new WrongDrivetypeException("Error: Drive arguments do not match drive type.");
        }

        v.scalarMultiply(constantSpeedMultiplier);

        if(v.isZeroVector() && distance != 0) {
            throw new InvalidMoveCommandException("You can't move anywhere if you aren't trying to move ;)");
        }

        if(distance < 0) {
            throw new DumpsterFireException("Where you're going, you don't need roads! (distance must be positive)");
        }

        if((driveType == DriveType.ARCADE || driveType == DriveType.ARCADE_TTA) && (v.theta == PI/4 || v.theta == (3*PI)/4 || v.theta == (5*PI)/4 || v.theta == (7*PI)/4)) {
            throw new InvalidMoveCommandException("Error: You input an invalid velocity vector for arcade drive.");
        }

        Vector displacement = new Vector(distance,v.theta,Vector.CoordinateType.POLAR);
        EncoderToDistanceProcessor encProcessor = new EncoderToDistanceProcessor(encoderPerMeter);

        double correction;

        if(stabilityControl && usesGyro) {
            correction = stabilityPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle);
        }
        else {
            correction = 0;
        }

        resetAllEncoders();

        double thresh1;
        double thresh2;

        switch (driveType) {
            case STANDARD_TTA:
            case STANDARD:
                v.rotate(-(PI / 4));
                displacement.rotate(-(PI / 4));

                thresh1 = encProcessor.getEncoderAmount(Math.abs(displacement.x),unit);
                thresh2 = encProcessor.getEncoderAmount(Math.abs(displacement.y),unit);

                while(Math.abs(topLeft.getCurrentPosition()) < thresh1 && Math.abs(topRight.getCurrentPosition()) < thresh2 && Math.abs(botLeft.getCurrentPosition()) < thresh2 && Math.abs(botRight.getCurrentPosition()) < thresh1) {
                    topLeft.setPower(v.x - correction);
                    topRight.setPower(v.y + correction);
                    botLeft.setPower(v.y - correction);
                    botRight.setPower(v.x + correction);
                    sleep(1);
                }
                break;
            case FIELD_CENTRIC_TTA:
            case FIELD_CENTRIC:
                if(!usesGyro) {
                    throw new WrongDrivetypeException("Field Centric Drive Must uses the IMU but the IMU was never set up");
                }

                v.rotate(-(PI / 4) + imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle);
                displacement.rotate(-(PI / 4) + imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle);

                thresh1 = encProcessor.getEncoderAmount(Math.abs(displacement.x),unit);
                thresh2 = encProcessor.getEncoderAmount(Math.abs(displacement.y),unit);

                while(Math.abs(topLeft.getCurrentPosition()) < thresh1 && Math.abs(topRight.getCurrentPosition()) < thresh2 && Math.abs(botLeft.getCurrentPosition()) < thresh2 && Math.abs(botRight.getCurrentPosition()) < thresh1) {
                    topLeft.setPower(v.x - correction);
                    topRight.setPower(v.y + correction);
                    botLeft.setPower(v.y - correction);
                    botRight.setPower(v.x + correction);
                    sleep(1);
                }
                break;
            case ARCADE_TTA:
            case ARCADE:

                double thresh = encoderPerMeter*Math.sqrt(2)/2;

                while(Math.abs(topLeft.getCurrentPosition()) < thresh && Math.abs(topRight.getCurrentPosition()) < thresh && Math.abs(botLeft.getCurrentPosition()) < thresh && Math.abs(botRight.getCurrentPosition()) < thresh) {
                    if (v.theta < PI / 4 || v.theta > (7 * PI) / 4) { //right side of the square
                        topLeft.setPower(v.r - correction);
                        topRight.setPower(-v.r + correction);
                        botLeft.setPower(-v.r - correction);
                        botRight.setPower(v.r + correction);
                    } else if (v.theta > PI / 4 && v.theta < (3 * PI) / 4) { //top side of the square
                        topLeft.setPower(-v.r - correction);
                        topRight.setPower(v.r + correction);
                        botLeft.setPower(v.r - correction);
                        botRight.setPower(-v.r + correction);
                    } else if (v.theta > (3 * PI) / 4 && v.theta < (5 * PI) / 4) { //left side of the square
                        topLeft.setPower(v.r - correction);
                        topRight.setPower(v.r + correction);
                        botLeft.setPower(v.r - correction);
                        botRight.setPower(v.r + correction);
                    } else if (v.theta > (5 * PI) / 4 && v.theta < (7 * PI) / 4) { //Bottom side of the square
                        topLeft.setPower(-v.r - correction);
                        topRight.setPower(-v.r + correction);
                        botLeft.setPower(-v.r - correction);
                        botRight.setPower(-v.r + correction);
                    }
                    sleep(1);
                }
                break;
        }
    }

    /**
     * Makes the robot move for a certain amount of time. Use this for any non-matthew drive mode.
     *
     * @param v - Makes the robot move a certain distance. Use this for any non-matthew drive mode.
     * @param timeMs - The amount of time in ms the robot should move.
     * @param stabilityControl - Whether the robot should use stability control.
     *
     * @throws InterruptedException - Throws this exception if the program is unexpectedly interrupted.
     */
    public void drive(Vector v, double timeMs, boolean stabilityControl) throws InterruptedException{
        if (driveType == DriveType.MATTHEW) {
            throw new WrongDrivetypeException("Error: Drive arguments do not match drive type.");
        }

        if((driveType == DriveType.ARCADE || driveType == DriveType.ARCADE_TTA) && (v.theta == PI/4 || v.theta == (3*PI)/4 || v.theta == (5*PI)/4 || v.theta == (7*PI)/4)) {
            throw new InvalidMoveCommandException("Error: You input an invalid velocity vector for arcade drive.");
        }

        v.scalarMultiply(constantSpeedMultiplier);

        long startTime = System.currentTimeMillis();

        double correction;

        if(stabilityControl && usesGyro) {
            correction = stabilityPID.getCorrection(imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle);
        }
        else {
            correction = 0;
        }

        resetAllEncoders();

        switch (driveType) {
            case STANDARD_TTA:
            case STANDARD:
                v.rotate(-(PI / 4));

                while(System.currentTimeMillis() - startTime < timeMs) {
                    topLeft.setPower(v.x - correction);
                    topRight.setPower(v.y + correction);
                    botLeft.setPower(v.y - correction);
                    botRight.setPower(v.x + correction);
                    sleep(1);
                }
                break;
            case FIELD_CENTRIC_TTA:
            case FIELD_CENTRIC:
                if(!usesGyro) {
                    throw new WrongDrivetypeException("Field Centric Drive Must uses the IMU but the IMU was never set up");
                }

                v.rotate(-(PI / 4) + imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle);

                while(System.currentTimeMillis() - startTime < timeMs) {
                    topLeft.setPower(v.x - correction);
                    topRight.setPower(v.y + correction);
                    botLeft.setPower(v.y - correction);
                    botRight.setPower(v.x + correction);
                    sleep(1);
                }
                break;
            case ARCADE_TTA:
            case ARCADE:

                while(System.currentTimeMillis() - startTime < timeMs) {
                    if (v.theta < PI / 4 || v.theta > (7 * PI) / 4) { //right side of the square
                        topLeft.setPower(v.r - correction);
                        topRight.setPower(-v.r + correction);
                        botLeft.setPower(-v.r - correction);
                        botRight.setPower(v.r + correction);
                    } else if (v.theta > PI / 4 && v.theta < (3 * PI) / 4) { //top side of the square
                        topLeft.setPower(-v.r - correction);
                        topRight.setPower(v.r + correction);
                        botLeft.setPower(v.r - correction);
                        botRight.setPower(-v.r + correction);
                    } else if (v.theta > (3 * PI) / 4 && v.theta < (5 * PI) / 4) { //left side of the square
                        topLeft.setPower(v.r - correction);
                        topRight.setPower(v.r + correction);
                        botLeft.setPower(v.r - correction);
                        botRight.setPower(v.r + correction);
                    } else if (v.theta > (5 * PI) / 4 && v.theta < (7 * PI) / 4) { //Bottom side of the square
                        topLeft.setPower(-v.r - correction);
                        topRight.setPower(-v.r + correction);
                        botLeft.setPower(-v.r - correction);
                        botRight.setPower(-v.r + correction);
                    }
                    sleep(1);
                }
                break;
        }
    }

    /**
     * Makes the robot move for a certain amount of time. Use this for any non-matthew drive mode.
     *
     * @param v - The direction and power that the robot should move at.
     * @param timeMs - The time in ms that the robot should move for.
     *
     * @throws InterruptedException - Throws this exception if the program is unexpectedly interrupted.
     */
    public void drive(Vector v, double timeMs) throws InterruptedException{
        if (driveType == DriveType.MATTHEW) {
            throw new WrongDrivetypeException("Error: Drive arguments do not match drive type.");
        }

        if((driveType == DriveType.ARCADE || driveType == DriveType.ARCADE_TTA) && (v.theta == PI/4 || v.theta == (3*PI)/4 || v.theta == (5*PI)/4 || v.theta == (7*PI)/4)) {
            throw new InvalidMoveCommandException("Error: You input an invalid velocity vector for arcade drive.");
        }

        v.scalarMultiply(constantSpeedMultiplier);

        long startTime = System.currentTimeMillis();

        resetAllEncoders();

        switch (driveType) {
            case STANDARD_TTA:
            case STANDARD:
                v.rotate(-(PI / 4));

                while(System.currentTimeMillis() - startTime < timeMs) {
                    topLeft.setPower(v.x);
                    topRight.setPower(v.y);
                    botLeft.setPower(v.y);
                    botRight.setPower(v.x);
                    sleep(1);
                }
                break;
            case FIELD_CENTRIC_TTA:
            case FIELD_CENTRIC:
                if(!usesGyro) {
                    throw new WrongDrivetypeException("Field Centric Drive Must uses the IMU but the IMU was never set up");
                }

                v.rotate(-(PI / 4) + imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,AngleUnit.RADIANS).firstAngle);

                while(System.currentTimeMillis() - startTime < timeMs) {
                    topLeft.setPower(v.x);
                    topRight.setPower(v.y);
                    botLeft.setPower(v.y);
                    botRight.setPower(v.x);
                    sleep(1);
                }
                break;
            case ARCADE_TTA:
            case ARCADE:
                while(System.currentTimeMillis() - startTime < timeMs) {
                    if (v.theta < PI / 4 || v.theta > (7 * PI) / 4) { //right side of the square
                        topLeft.setPower(v.r);
                        topRight.setPower(-v.r);
                        botLeft.setPower(-v.r);
                        botRight.setPower(v.r);
                    } else if (v.theta > PI / 4 && v.theta < (3 * PI) / 4) { //top side of the square
                        topLeft.setPower(-v.r);
                        topRight.setPower(v.r);
                        botLeft.setPower(v.r);
                        botRight.setPower(-v.r);
                    } else if (v.theta > (3 * PI) / 4 && v.theta < (5 * PI) / 4) { //left side of the square
                        topLeft.setPower(v.r);
                        topRight.setPower(v.r);
                        botLeft.setPower(v.r);
                        botRight.setPower(v.r);
                    } else if (v.theta > (5 * PI) / 4 && v.theta < (7 * PI) / 4) { //Bottom side of the square
                        topLeft.setPower(-v.r);
                        topRight.setPower(-v.r);
                        botLeft.setPower(-v.r);
                        botRight.setPower(-v.r);
                    }
                    sleep(1);
                }
                break;
        }
    }

    /**
     * Gets the current encoder position of the top left motor.
     *
     * @return The current encoder position of the top left motor.
     */
    public int getTopLeftEncoderPos() {
        return topLeft.getCurrentPosition();
    }

    /**
     * Gets the current encoder position of the top right motor.
     *
     * @return The current encoder position of the top right motor.
     */
    public int getTopRightEncoderPos() {
        return topRight.getCurrentPosition();
    }

    /**
     * Gets the current encoder position of the bottom left motor.
     *
     * @return The current encoder position of the bottom left motor.
     */
    public int getBotLeftEncoderPos() {
        return botLeft.getCurrentPosition();
    }

    /**
     * Gets the current encoder position of the bottom right motor.
     *
     * @return The current encoder position of the bottom right motor.
     */
    public int getBotRightEncoderPos() {
        return botRight.getCurrentPosition();
    }

    /**
     * Gets an array of length 4 representing the current encoder position of all 4 motors. [0] is top left, [1] is top right, [2] is bottom left, [3] is bottom right
     *
     * @return An array of length 4 representing the current encoder position of all 4 motors.
     */
    public int[] getEncoderPos() {
        return new int[] {topLeft.getCurrentPosition(), topRight.getCurrentPosition(), botLeft.getCurrentPosition(), botRight.getCurrentPosition()};
    }

    /**
     * Updates the mechanum drive's mode.
     *
     * @param driveType - The driving mode that the drivetrain will be set to.
     *
     * @throws InterruptedException - Throws this exception if the program is interrupted unexpectedly.
     */
    public void setDriveMode(DriveType driveType) throws InterruptedException {
        boolean useGyro = driveType == DriveType.STANDARD_TTA || driveType == DriveType.FIELD_CENTRIC || driveType == DriveType.FIELD_CENTRIC_TTA || driveType == DriveType.ARCADE_TTA;
        if(!usesGyro && useGyro) {
            imu = robot.hardwareMap.get(BNO055IMU.class,imuNumber == 1 ? "imu" : "imu 1");
            imu.initialize(new BNO055IMU.Parameters());
            while(!imu.isGyroCalibrated()){sleep(1);}
        }
        usesGyro = useGyro;
        this.driveType = driveType;
    }
    public void setUseGyro(boolean useGyro) throws InterruptedException {
        if(!usesGyro && useGyro) {
            imu = robot.hardwareMap.get(BNO055IMU.class,imuNumber == 1 ? "imu" : "imu 1");
            imu.initialize(new BNO055IMU.Parameters());
            while(!imu.isGyroCalibrated()){sleep(1);}
        }
        usesGyro = useGyro;
    }

    /**
     * Gets the motor config.
     *
     * @return - The motor config.
     */
    public String[] getConfig() {
        return config;
    }

    /**
     * Pulls teleop config settings from global robot config.
     *
     * @throws InterruptedException - Throws this exception is the program is unexpectedly interrupted.
     */
    private void setUsingConfigs() throws InterruptedException{
        inputs = robot.pullControls(this.getClass().getSimpleName());
        Map<String, Object> settingsData = robot.pullNonGamepad(this.getClass().getSimpleName());

        imuNumber = (int) settingsData.get("ImuNumber");

        setDriveMode((DriveType) settingsData.get("DriveType"));
        setUseGyro((boolean) settingsData.get("UseGyro"));

        if(!useSpecific) {
            turnLeftPower = (int) settingsData.get("turnLeftPower");
            turnRightPower = (int) settingsData.get("turnRightPower");
            constantSpeedMultiplier = (double) settingsData.get("ConstantSpeedMultiplier");
            slowModeMultiplier = (double) settingsData.get("SlowModeMultiplier");
        }
    }

    /**
     * Pulls teleop config settings from global robot config.
     *
     * @throws InterruptedException - Throws this exception is the program is unexpectedly interrupted.
     */
    private void setUsingConfigsAutonomous() throws InterruptedException{
        Map<String, Object> settingsData = robot.pullNonGamepad(this.getClass().getSimpleName());

        imuNumber = (int) settingsData.get("ImuNumber");

        setDriveMode((DriveType) settingsData.get("DriveType"));
        setUseGyro((boolean) settingsData.get("UseGyro"));

        if(!useSpecific) {
            constantSpeedMultiplier = (double) settingsData.get("ConstantSpeedMultiplier");
        }
    }

    /**
     * Teleop configuration settings.
     *
     * @return The teleop configuration.
     */
    @TeleopConfig
    public static ConfigParam[] teleopConfig() {
        if(useSpecific) {
            return new ConfigParam[]{
                    new ConfigParam("DriveType", new LinkedHashMap<String, Object>() {{
                        put(DriveType.STANDARD.name(), DriveType.STANDARD);
                        put(DriveType.STANDARD_TTA.name(), DriveType.STANDARD_TTA);
                        put(DriveType.FIELD_CENTRIC.name(), DriveType.FIELD_CENTRIC);
                        put(DriveType.FIELD_CENTRIC_TTA.name(), DriveType.FIELD_CENTRIC_TTA);
                        put(DriveType.ARCADE.name(), DriveType.ARCADE);
                        put(DriveType.ARCADE_TTA.name(), DriveType.ARCADE_TTA);
                        put(DriveType.MATTHEW.name(), DriveType.MATTHEW);
                    }}, DriveType.STANDARD.name()),
                    new ConfigParam(DRIVESTICK, Button.VectorInputs.right_stick),
                    new ConfigParam(LEFT_DRIVESTICK, Button.VectorInputs.noButton),
                    new ConfigParam(RIGHT_DRIVESTICK, Button.VectorInputs.noButton),
                    new ConfigParam(TURNSTICK, Button.DoubleInputs.left_stick_x),
                    new ConfigParam(TURN_LEFT, Button.BooleanInputs.noButton),
                    new ConfigParam(TURN_RIGHT, Button.BooleanInputs.noButton),
                    new ConfigParam(TTA_STICK, Button.VectorInputs.noButton),
                    new ConfigParam(SPEED_MODE, Button.BooleanInputs.noButton),
                    new ConfigParam("UseGyro", ConfigParam.booleanMap, false),
                    new ConfigParam("ImuNumber", ConfigParam.numberMap(1, 2, 1), "1"),
            };
        }
        else {
            return new ConfigParam[]{
                    new ConfigParam("DriveType", new LinkedHashMap<String, Object>() {{
                        put(DriveType.STANDARD.name(), DriveType.STANDARD);
                        put(DriveType.STANDARD_TTA.name(), DriveType.STANDARD_TTA);
                        put(DriveType.FIELD_CENTRIC.name(), DriveType.FIELD_CENTRIC);
                        put(DriveType.FIELD_CENTRIC_TTA.name(), DriveType.FIELD_CENTRIC_TTA);
                        put(DriveType.ARCADE.name(), DriveType.ARCADE);
                        put(DriveType.ARCADE_TTA.name(), DriveType.ARCADE_TTA);
                        put(DriveType.MATTHEW.name(), DriveType.MATTHEW);
                    }}, DriveType.STANDARD.name()),
                    new ConfigParam(DRIVESTICK, Button.VectorInputs.right_stick),
                    new ConfigParam(LEFT_DRIVESTICK, Button.VectorInputs.noButton),
                    new ConfigParam(RIGHT_DRIVESTICK, Button.VectorInputs.noButton),
                    new ConfigParam(TURNSTICK, Button.DoubleInputs.left_stick_x),
                    new ConfigParam(TURN_LEFT, Button.BooleanInputs.noButton),
                    new ConfigParam(TURN_RIGHT, Button.BooleanInputs.noButton),
                    new ConfigParam(TTA_STICK, Button.VectorInputs.noButton),
                    new ConfigParam(SPEED_MODE, Button.BooleanInputs.noButton),
                    new ConfigParam("turnLeftPower", ConfigParam.numberMap(0, 1, 0.05), "0.3"),
                    new ConfigParam("turnRightPower", ConfigParam.numberMap(0, 1, 0.05), "0.3"),
                    new ConfigParam("UseGyro", ConfigParam.booleanMap, false),
                    new ConfigParam("ImuNumber", ConfigParam.numberMap(1, 2, 1), "1"),
                    new ConfigParam("ConstantSpeedMultiplier", ConfigParam.numberMap(0, 10, 0.05), "1"),
                    new ConfigParam("SlowModeMultiplier", ConfigParam.numberMap(0, 10, 0.05), "1")
            };
        }
    }

    /**
     * Autonomous configuration settings.
     *
     * @return The teleop configuration.
     */
    @AutonomousConfig
    public static ConfigParam[] autonomousConfig() {
        if(useSpecific) {
            return new ConfigParam[]{
                    new ConfigParam("DriveType", new LinkedHashMap<String, Object>() {{
                        put(DriveType.STANDARD.name(), DriveType.STANDARD);
                        put(DriveType.STANDARD_TTA.name(), DriveType.STANDARD_TTA);
                        put(DriveType.FIELD_CENTRIC.name(), DriveType.FIELD_CENTRIC);
                        put(DriveType.FIELD_CENTRIC_TTA.name(), DriveType.FIELD_CENTRIC_TTA);
                        put(DriveType.ARCADE.name(), DriveType.ARCADE);
                        put(DriveType.ARCADE_TTA.name(), DriveType.ARCADE_TTA);
                        put(DriveType.MATTHEW.name(), DriveType.MATTHEW);
                    }}, DriveType.STANDARD.name()),
                    new ConfigParam("UseGyro", ConfigParam.booleanMap, false),
                    new ConfigParam("ImuNumber", ConfigParam.numberMap(1, 2, 1), "1"),
            };
        }
        else {
            return new ConfigParam[]{
                    new ConfigParam("DriveType", new LinkedHashMap<String, Object>() {{
                        put(DriveType.STANDARD.name(), DriveType.STANDARD);
                        put(DriveType.STANDARD_TTA.name(), DriveType.STANDARD_TTA);
                        put(DriveType.FIELD_CENTRIC.name(), DriveType.FIELD_CENTRIC);
                        put(DriveType.FIELD_CENTRIC_TTA.name(), DriveType.FIELD_CENTRIC_TTA);
                        put(DriveType.ARCADE.name(), DriveType.ARCADE);
                        put(DriveType.ARCADE_TTA.name(), DriveType.ARCADE_TTA);
                        put(DriveType.MATTHEW.name(), DriveType.MATTHEW);
                    }}, DriveType.STANDARD.name()),
                    new ConfigParam("UseGyro", ConfigParam.booleanMap, false),
                    new ConfigParam("ImuNumber", ConfigParam.numberMap(1, 2, 1), "1"),
                    new ConfigParam("ConstantSpeedMultiplier", ConfigParam.numberMap(0, 1, 0.05), 1)
            };
        }
    }

    /**
     * A parameter class for passing all desired options into mechanum drive.
     */
    public static final class Params implements BaseParam {
        //The driveType of the mechanum drive.
        private DriveType driveType;
        //The buttons used in the various drive modes.
        private Button driveStick, driveStickLeft, driveStickRight, turnStick, turnLeft, turnRight, ttaStick, speedMode;
        //The motor config.
        private String[] config;
        //The imu number being used for the gyroscope.
        private int imuNumber;
        //The turning power bias for left and right turns.
        private double turnLeftPower, turnRightPower;
        //The PID controllers for turning to angles and stability, respectively.
        private PIDController turnPID, stabilityPID;
        //A boolean specifying whether or not to use the gyroscope.
        private boolean useGyro;
        //A boolean specifying whether to change the motor velocity PID.
        private boolean changeVelocityPID;
        //The new velocity PID coefficients.
        private double vkp, vki, vkd, vkf;
        //The number of encoder ticks per meter traveled.
        private double encoderPerMeter;
        //The constant used to scale the drive's speed and to change the robot's speed in speed mode.
        private double constantSpeedMultiplier, speedModeMultiplier;

        /**
         * A constructor for the parameters object. Sets default parameter values.
         *
         * @param topLeft - The top left motor config name.
         * @param topRight - The top right motor config name.
         * @param botLeft - The bottom left motor config name.
         * @param botRight - The bottom right motor config name.
         */
        public Params(String topLeft, String topRight, String botLeft, String botRight) {

            config = new String[4];
            config[0] = topLeft;
            config[1] = topRight;
            config[2] = botLeft;
            config[3] = botRight;

            this.driveType = DriveType.STANDARD;
            driveStick = new Button(1, Button.VectorInputs.right_stick);
            driveStickLeft = new Button(1, Button.VectorInputs.noButton);
            driveStickRight = new Button(1, Button.VectorInputs.noButton);
            turnStick = new Button(1,Button.DoubleInputs.left_stick_x);
            turnLeft = new Button(1,Button.BooleanInputs.noButton);
            turnRight = new Button(1,Button.BooleanInputs.noButton);
            ttaStick = new Button(1,Button.VectorInputs.noButton);
            speedMode = new Button(1,Button.BooleanInputs.noButton);
            imuNumber = 1;
            turnLeftPower = 0;
            turnRightPower = 0;
            useGyro = false;
            changeVelocityPID = false;
            turnPID = new PIDController(0,0,0);
            stabilityPID = new PIDController(0,0,0);
            encoderPerMeter = 1440;
            constantSpeedMultiplier = 1;
        }

        /**
         * Sets the drive type to be used.
         *
         * @param driveType - The driveType that will be used.
         * @return - This instance of Params.
         */
        public Params setDriveType(DriveType driveType) {
            this.driveType = driveType;
            useGyro = driveType == DriveType.STANDARD_TTA || driveType == DriveType.FIELD_CENTRIC || driveType == DriveType.FIELD_CENTRIC_TTA || driveType == DriveType.ARCADE_TTA;
            return this;
        }

        /**
         * Sets the driving input button. Must be a vector input.
         *
         * @param driveStick - The vector input used to control the robot.
         * @return - This instance of Params.
         *
         * @throws NotVectorInputException - Throws this if the input button is not a vector input.
         */
        public Params setDriveStick(Button driveStick) {
            if(!driveStick.isVector) {
                throw new NotVectorInputException("DriveStick must be a vector input");
            }
            this.driveStick = driveStick;
            return this;
        }

        /**
         * Sets the driving input button for the left side of the robot. Must be a vector input.
         *
         * @param driveStickLeft - The vector input used to control the left side of the robot.
         * @return - This instance of Params.
         *
         * @throws NotVectorInputException - Throws this if input button is not a vector input.
         */
        public Params setDriveStickLeft(Button driveStickLeft) {
            if(!driveStickLeft.isVector) {
                throw new NotVectorInputException("DriveStickLeft must be a vector input.");
            }
            this.driveStickLeft = driveStickLeft;
            return this;
        }

        /**
         * Sets the driving input button for the right side of the robot. Must be a vector input.
         *
         * @param driveStickRight - The vector input used to control the right side of the robot.
         * @return - This instance of Params.
         *
         * @throws NotDoubleInputException - Throws this if input button is not a vector input.
         */
        public Params setDriveStickRight(Button driveStickRight) {
            if(!driveStickRight.isVector) {
                throw new NotVectorInputException("DriveStickRight must be a vector input.");
            }
            this.driveStickRight = driveStickRight;
            return this;
        }

        /**
         * Sets the turning input button. Must be a double input.
         *
         * @param turnStick - The double input used to control the robot's turning speed.
         * @return - This instance of Params.
         *
         * @throws NotDoubleInputException - Throws this if the input button is not a double input.
         */
        public Params setTurnStick(Button turnStick) {
            if(!turnStick.isDouble) {
                throw new NotDoubleInputException("TurnStick must be a double input.");
            }
            this.turnStick = turnStick;
            return this;
        }

        /**
         * Sets the turn to angle input button. Must be a vector input.
         *
         * @param ttaStick - The vector input used to control what angle the robot turns to.
         * @return - This instance of Params.
         *
         * @throws NotVectorInputException - Throws this is the input button is not a vector input.
         */
        public Params setTTAStick(Button ttaStick) {
            if(!driveStickRight.isVector) {
                throw new NotVectorInputException("TTA Stick must be a vector input.");
            }
            this.ttaStick = ttaStick;
            return this;
        }

        /**
         * Sets left turn button input. Must be a boolean input.
         *
         * @param turnLeft - The button used to make the robot turn left.
         * @param turnSpeed - The speed at which the robot should turn left when the button is pressed.
         * @return - This instance of Params.
         *
         * @throws NotBooleanInputException - Throws this if the input button is not a boolean input.
         */
        public Params setTurnLeftButton(Button turnLeft, double turnSpeed) {
            if(!turnLeft.isBoolean) {
                throw new NotBooleanInputException("TurnLeft button must be a boolean input.");
            }
            this.turnLeft = turnLeft;
            turnLeftPower = Range.clip(turnSpeed,0,1);
            return this;
        }

        /**
         * Sets right turn button input. Must be a boolean input.
         *
         * @param turnRight - The button used to make the robot turn right.
         * @param turnSpeed - The speec at which the robot should turn right when the button is pressed.
         * @return - This instance of Params.
         *
         * @throws NotBooleanInputException - Throws this if the input button is not a boolean input.
         */
        public Params setTurnRightButton(Button turnRight, double turnSpeed) {
            if(!turnRight.isBoolean) {
                throw new NotBooleanInputException("TurnRight button must be a boolean input");
            }
            this.turnRight = turnRight;
            turnRightPower = Range.clip(turnSpeed,0,1);
            return this;
        }

        /**
         * Sets the button used to activate/deactivate speed mode.
         *
         * @param speedMode - The speed mode button.
         * @return This instance of Params.
         */
        public Params setSpeedModeButton(Button speedMode) {
            if(!speedMode.isBoolean) {
                throw new NotBooleanInputException("SpeedMode button must be a boolean input");
            }
            this.speedMode = speedMode;
            return this;
        }

        /**
         * Sets the number imu for the drive system to use.
         *
         * @param imuNumber - The imu's number. Must be either 1 or 2.
         * @return - This instance of Params.
         *
         * @throws NotAnAlchemistException - Throws this if the imu number is not 1 or 2. Can't make something out of nothing.
         */
        public Params setImuNumber(int imuNumber) {
            if(imuNumber != 1 && imuNumber != 2) {
                throw new NotAnAlchemistException("IMU number must be either 1 or 2");
            }
            this.imuNumber = imuNumber;
            return this;
        }

        /**
         * Sets the coefficients for the turnPID controller.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @return - This instance of Params.
         */
        public Params setTurnPIDCoeffs(double kp, double ki, double kd) {
            useGyro = true;
            turnPID = new PIDController(kp, ki, kd, (Double target, Double current) -> {
                BiFunction<Double, Double, Double> mod = (Double x, Double m) -> (x % m + m) % m;

                //cw - ccw +
                double cw = -mod.apply(mod.apply(target,2*PI) - mod.apply(current,2*PI), 2*PI);
                double ccw = mod.apply(mod.apply(current,2*PI) - mod.apply(target,2*PI), 2*PI);

                return Math.abs(ccw) < Math.abs(cw) ? ccw : cw;
            });
            return this;
        }

        /**
         * Sets the coefficients for the turnPID controller.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @param useDegrees - A boolean specifying if the units are in degrees.
         * @return - This instance of Params.
         */
        public Params setTurnPIDCoeffs(double kp, double ki, double kd, boolean useDegrees) {
            useGyro = true;
            turnPID = new PIDController(kp, ki, kd, (Double target, Double current) -> {
                BiFunction<Double, Double, Double> mod = (Double x, Double m) -> (x % m + m) % m;

                double m = useDegrees ? 360 : 2*PI;

                //cw - ccw +
                double cw = -mod.apply(mod.apply(target,m) - mod.apply(current,m), m);
                double ccw = mod.apply(mod.apply(current,m) - mod.apply(target,m), m);

                return Math.abs(ccw) < Math.abs(cw) ? ccw : cw;
            });
            return this;
        }

        /**
         * Sets the PID controller used for turning to specific angles.
         *
         * @param turnPID - The PID to use for turning to specific angles.
         * @return This instance of Params.
         */
        public Params setTurnPID(PIDController turnPID) {
            useGyro = true;
            this.turnPID = turnPID;
            return this;
        }

        /**
         * Sets the coefficients for the stability PID controller.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @return - This instance of Params.
         */
        public Params setStabilityPIDCoeffs(double kp, double ki, double kd) {
            useGyro = true;
            stabilityPID = new PIDController(kp, ki, kd, (Double target, Double current) -> {
                BiFunction<Double, Double, Double> mod = (Double x, Double m) -> (x % m + m) % m;

                //cw - ccw +
                double cw = -mod.apply(mod.apply(target,2*PI) - mod.apply(current,2*PI), 2*PI);
                double ccw = mod.apply(mod.apply(current,2*PI) - mod.apply(target,2*PI), 2*PI);

                return Math.abs(ccw) < Math.abs(cw) ? ccw : cw;
            });
            return this;
        }

        /**
         * Sets the coefficients for the stabilityPID controller.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @param useDegrees - A boolean specifying if the units are in degrees.
         * @return - This instance of Params.
         */
        public Params setStabilityPIDCoeffs(double kp, double ki, double kd, boolean useDegrees) {
            useGyro = true;
            stabilityPID = new PIDController(kp, ki, kd, (Double target, Double current) -> {
                BiFunction<Double, Double, Double> mod = (Double x, Double m) -> (x % m + m) % m;

                double m = useDegrees ? 360 : 2*PI;

                //cw - ccw +
                double cw = -mod.apply(mod.apply(target,m) - mod.apply(current,m), m);
                double ccw = mod.apply(mod.apply(current,m) - mod.apply(target,m), m);

                return Math.abs(ccw) < Math.abs(cw) ? ccw : cw;
            });
            return this;
        }

        /**
         * Sets the PID controller used for stability control.
         *
         * @param stabilityPID - The PID to use for stability control.
         * @return This instance of Params.
         */
        public Params setStabilityPID(PIDController stabilityPID) {
            useGyro = true;
            this.stabilityPID = stabilityPID;
            return this;
        }

        /**
         * Sets the velocity PID coefficients.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @param kf - Feedforward gain.
         * @return - This instance of Params.
         */
        public Params setVelocityPID(double kp, double ki, double kd, double kf) {
            changeVelocityPID = true;
            vkp = kp;
            vki = ki;
            vkd = kd;
            vkf = kf;
            return this;
        }

        /**
         * Sets the number of encoder ticks per meter distance traveled.
         *
         * @param encoderPerMeter - The number of encoder ticks per meter distance traveled.
         * @return - This instance of Params.
         */
        public Params setEncoderPerMeter(double encoderPerMeter) {
            this.encoderPerMeter = encoderPerMeter;
            return this;
        }

        /**
         * Sets a constant speed multiplier to scale the calculated linear velocities by.
         *
         * @param constantSpeedModifier - The value to multiply the speed by.
         * @return - This instance of Params.
         */
        public Params setConstantSpeedModifier(double constantSpeedModifier) {
            this.constantSpeedMultiplier = constantSpeedModifier;
            return this;
        }

        /**
         * Sets the multiplier that will be applied when speed mode is entered. If the multiplier is < 1 then the robot will slow down, otherwise it will speed up.
         *
         * @param speedModeMultiplier - The multiplier that will be applied when speed mode is entered.
         * @return This instance of Params.
         */
        public Params setSpeedModeMultiplier(double speedModeMultiplier) {
            this.speedModeMultiplier = speedModeMultiplier;
            return this;
        }
    }

    /**
     * A class used for entering necessary params while in config mode or for using more specific versions of params than can be set with the config.
     */
    public static final class SpecificParams {

        //An array of length 4 containing the motor config. [0] = topLeft, [1] = topRight, [2] = bottomLeft, [3] = bottomRight.
        private String[] config;
        //Constants used in various parts of mechanum drive.
        private double encodersPerMeter, turnLeftPower, turnRightPower, constantSpeedMultipler, slowModeMultiplier;
        //Velocity PID coefficients.
        private double vkp, vki, vkd, vkf;
        //A boolean specifying if the velocity PID was changed.
        private boolean changeVelocityPID;
        //Two PID controllers used to stabilize linear motion and to turn to specific angles.
        private PIDController stabilityPID, turnPID;

        /**
         * A constructor for SpecificParams.
         *
         * @param topLeft - The top left motor config name.
         * @param topRight - The top right motor config name.
         * @param botLeft - The bottom left motor config name.
         * @param botRight - The bottom right motor config name
         */
        public SpecificParams(String topLeft, String topRight, String botLeft, String botRight) {
            config = new String[4];
            config[0] = topLeft;
            config[1] = topRight;
            config[2] = botLeft;
            config[3] = botRight;

            vkp = 0;
            vki = 0;
            vkd = 0;
            vkf = 0;
            changeVelocityPID = false;

            turnPID = new PIDController(0,0,0);
            stabilityPID = new PIDController(0,0,0);
        }

        /**
         * Sets the number of encoders ticks per meter distance.
         *
         * @param encodersPerMeter - The number of encoders ticks per meter distance
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setEncodersPerMeter(double encodersPerMeter) {
            this.encodersPerMeter = encodersPerMeter;
            return this;
        }

        /**
         * Set the turn left power.
         *
         * @param turnLeftPower - The turn left power.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setTurnLeftPower(double turnLeftPower) {
            this.turnLeftPower = turnLeftPower;
            return this;

        }

        /**
         * Set the turn right power.
         *
         * @param turnRightPower - The turn right power.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setTurnRightPower(double turnRightPower) {
            this.turnRightPower = turnRightPower;
            return this;
        }

        /**
         * Set the constant speed multiplier.
         *
         * @param constantSpeedMultipler - The constant speed multiplier. Always multiplied by velocity.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setConstantSpeedMultipler(double constantSpeedMultipler) {
            this.constantSpeedMultipler = constantSpeedMultipler;
            return this;
        }

        /**
         * Set the slow mode multiplier.
         *
         * @param slowModeMultiplier - The slow mode multiplier. Applied to velocity when in slow/speed mode.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setSlowModeMultiplier(double slowModeMultiplier) {
            this.slowModeMultiplier = slowModeMultiplier;
            return this;
        }

        /**
         *
         * @param kp - Proportional gain for velocity PID.
         * @param ki - Integral gain for velocity PID.
         * @param kd - Derivative gain for velocity PID.
         * @param kf - Feedforward gain for velocity PID.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setVelocityPID(double kp, double ki, double kd, double kf) {
            changeVelocityPID = true;
            vkp = kp;
            vki = ki;
            vkd = kd;
            vkf = kf;
            return this;
        }

        /**
         *
         * @param kp - Proportional gain for velocity PID.
         * @param ki - Integral gain for velocity PID.
         * @param kd - Derivative gain for velocity PID.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setTurnPIDCoeffs(double kp, double ki, double kd) {
            turnPID = new PIDController(kp, ki, kd, (Double target, Double current) -> {
                BiFunction<Double, Double, Double> mod = (Double x, Double m) -> (x % m + m) % m;

                //cw - ccw +
                double cw = -mod.apply(mod.apply(target,2*PI) - mod.apply(current,2*PI), 2*PI);
                double ccw = mod.apply(mod.apply(current,2*PI) - mod.apply(target,2*PI), 2*PI);

                return Math.abs(ccw) < Math.abs(cw) ? ccw : cw;
            });
            return this;
        }

        /**
         * Sets the coefficients for the turnPID controller.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @param useDegrees - A boolean specifying if the units are in degrees.
         * @return - This instance of Params.
         */
        public SpecificParams setTurnPIDCoeffs(double kp, double ki, double kd, boolean useDegrees) {
            turnPID = new PIDController(kp, ki, kd, (Double target, Double current) -> {
                BiFunction<Double, Double, Double> mod = (Double x, Double m) -> (x % m + m) % m;

                double m = useDegrees ? 360 : 2*PI;

                //cw - ccw +
                double cw = -mod.apply(mod.apply(target,m) - mod.apply(current,m), m);
                double ccw = mod.apply(mod.apply(current,m) - mod.apply(target,m), m);

                return Math.abs(ccw) < Math.abs(cw) ? ccw : cw;
            });
            return this;
        }

        /**
         * Sets the PID controller to use for turning to specific angles.
         *
         * @param turnPID - The PID controller that will be used for turning.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setTurnPID(PIDController turnPID) {
            this.turnPID = turnPID;
            return this;
        }

        /**
         * Sets the coefficients for the stabilityPID controller.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setStabilityPIDCoeffs(double kp, double ki, double kd) {
            stabilityPID = new PIDController(kp, ki, kd, (Double target, Double current) -> {
                BiFunction<Double, Double, Double> mod = (Double x, Double m) -> (x % m + m) % m;

                //cw - ccw +
                double cw = -mod.apply(mod.apply(target,2*PI) - mod.apply(current,2*PI), 2*PI);
                double ccw = mod.apply(mod.apply(current,2*PI) - mod.apply(target,2*PI), 2*PI);

                return Math.abs(ccw) < Math.abs(cw) ? ccw : cw;
            });
            return this;
        }

        /**
         * Sets the coefficients for the stability PID controller.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @param useDegrees - A boolean specifying if the units are in degrees.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setStabilityPIDCoeffs(double kp, double ki, double kd, boolean useDegrees) {
            stabilityPID = new PIDController(kp, ki, kd, (Double target, Double current) -> {
                BiFunction<Double, Double, Double> mod = (Double x, Double m) -> (x % m + m) % m;

                double m = useDegrees ? 360 : 2*PI;

                //cw - ccw +
                double cw = -mod.apply(mod.apply(target,m) - mod.apply(current,m), m);
                double ccw = mod.apply(mod.apply(current,m) - mod.apply(target,m), m);

                return Math.abs(ccw) < Math.abs(cw) ? ccw : cw;
            });
            return this;
        }

        /**
         * Sets the PID controller that will be used for stability control.
         *
         * @param stabilityPID - The PID controller that will be used for stability control.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setStabilityPID(PIDController stabilityPID) {
            this.stabilityPID = stabilityPID;
            return this;
        }
    }
}