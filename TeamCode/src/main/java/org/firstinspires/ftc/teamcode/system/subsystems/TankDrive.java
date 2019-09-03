/*
 * Filename: TankDrive.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/17/19
 */

package org.firstinspires.ftc.teamcode.system.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.util.annotations.AutonomousConfig;
import org.firstinspires.ftc.teamcode.util.annotations.TeleopConfig;
import org.firstinspires.ftc.teamcode.util.control.PIDController;
import org.firstinspires.ftc.teamcode.util.exceptions.NotBooleanInputException;
import org.firstinspires.ftc.teamcode.util.exceptions.NotDoubleInputException;
import org.firstinspires.ftc.teamcode.util.misc.BaseParam;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;
import org.firstinspires.ftc.teamcode.util.misc.Toggle;

import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * A customizable tankdrive subsystem.
 */
public class TankDrive extends SubSystem{

    private static boolean useSpecific = false;

    //The drivetrain's two motors
    private DcMotor left, right;

    //A boolean determining if the robot should be allowed to turn and move simultaneously
    private boolean turnAndMove;

    //A toggle object that detects if a boolean input changes twice (like a square pulse)
    private Toggle speedToggle = new Toggle(Toggle.ToggleTypes.flipToggle, false);

    //Modifiers for speed and joystick operations
    private double deadzone, currentSpeedModeModifier, speedModeModifier, constantSpeedModifier;

    //Object that stores wanted buttons and is used to retrieve button inputs.
    private CustomizableGamepad inputs;

    //Button names CustomizableGamepad will use.
    private static final String SPEEDMODEBUTTON = "speedModeButton", DRIVESTICK = "driveStick", TURNSTICK = "turnStick";

    /**
     * This constructor should be used when using this program for TeleOp.
     *
     * @param robot - The robot we will be using.
     */
    public TankDrive(Robot robot, Params params){
        super(robot);

        inputs = new CustomizableGamepad(robot);

        setMotorConfiguration(params.leftMotor, params.rightMotor);
        setDeadzone(params.deadzone);
        setSpeedModeModifier(params.speedModeModifier);
        setConstantSpeedModifier(params.constantSpeedModifier);
        setTurnAndMove(params.turnAndMove);
        setDriveStick(params.buttonsToSet[0]);
        setTurnStick(params.buttonsToSet[1]);
        setSpeedMode(params.buttonsToSet[2]);
    }

    public TankDrive(Robot robot, NumberParams params){
        super(robot);

        setDeadzone(params.deadzone);
        setSpeedModeModifier(params.speedModeModifier);
        setConstantSpeedModifier(params.constantSpeedModifier);
        
        usesConfig = true;
    }

    public TankDrive(Robot robot, String LeftMotorConfig, String RightMotorConfig){
        super(robot);

        setMotorConfiguration(LeftMotorConfig, RightMotorConfig);

        usesConfig = true;
    }

    @Override
    public void init() throws InterruptedException
    {
        normalDirection();
        resetEncoders();
    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        if(usesConfig && robot.isTeleop()) {
            setUsingConfigs();
        }
        else if(usesConfig && robot.isAutonomous()) {
            setUsingConfigsAutonomous();
        }
    }

    @Override
    public void handle() {
        if(!inputs.checkNoButton(SPEEDMODEBUTTON)){
            speedToggle.updateToggle(inputs.getBooleanInput(SPEEDMODEBUTTON));
            if(speedToggle.getCurrentState()){
                currentSpeedModeModifier = speedModeModifier;
            }
            else {
                currentSpeedModeModifier = 1;
            }
        }
        //drives forward and turns at the same time
        if (turnAndMove) {
            if (Math.abs(inputs.getDoubleInput(DRIVESTICK)) > deadzone && Math.abs(inputs.getDoubleInput(TURNSTICK)) > deadzone) {
                turnAndMoveForwardRight(inputs.getDoubleInput(DRIVESTICK), inputs.getDoubleInput(TURNSTICK));
            } else if (Math.abs(inputs.getDoubleInput(DRIVESTICK)) > deadzone) {
                driveForward(inputs.getDoubleInput(DRIVESTICK));
            } else if (Math.abs(inputs.getDoubleInput(TURNSTICK)) > deadzone) {
                turnClockwise(inputs.getDoubleInput(TURNSTICK));
            }
        }
        //drives forward and turns but not at the same time
        else {
            if (inputs.getDoubleInput(TURNSTICK) > deadzone) {
                turnClockwise(inputs.getDoubleInput(TURNSTICK));
            } else if (inputs.getDoubleInput(DRIVESTICK) > deadzone) {
                driveForward(inputs.getDoubleInput(DRIVESTICK));
            } else {
                stopMovement();
            }
        }
    }

    @Override
    public void stop() {
        stopMovement();
    }

    /**
     * Reverses direction of the robot.
     */
    public void reverseDirection(){
        left.setDirection(DcMotor.Direction.FORWARD);
        right.setDirection(DcMotor.Direction.REVERSE);
    }

    /**
     * Sets the direction of the robot(default direction).
     */
    public void normalDirection(){
        left.setDirection(DcMotor.Direction.REVERSE);
        right.setDirection(DcMotor.Direction.FORWARD);
    }

    /**
     * Resents the encoders.
     */
    public void resetEncoders(){
        left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Makes the robot drive forward.
     *
     * @param speed - Speed to drive forward(-1)-(1).
     */
    public void driveForward(double speed){
        left.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
        right.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
    }

    /**
     * Makes the robot turn.
     *
     * @param speed - Speed to turn at(-1)-(1) (positive speed is turn right & negative speed is turn left).
     */
    public void turnClockwise(double speed){
        left.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
        right.setPower(-((speed * constantSpeedModifier) * currentSpeedModeModifier));
    }

    /**
     * Moves forward and turns at the same time.
     *
     * @param forwardComponent - double from (-1)-(1) of intensity of forward movement in the turn move
     * @param rightComponent - double from (-1)-(1) of intensity of turn movement in the turn move(positive for right negative for left).
     */
    public void turnAndMoveForwardRight(double forwardComponent, double rightComponent){
        left.setPower(((forwardComponent/2 + rightComponent/2) * constantSpeedModifier) * currentSpeedModeModifier);
        right.setPower(((forwardComponent/2 - rightComponent/2) * constantSpeedModifier) * currentSpeedModeModifier);
    }

    /**
     * Stops all movement.
     */
    public void stopMovement(){
        left.setPower(0);
        right.setPower(0);
    }

    /**
     * Sets the power of the left motor.
     *
     * @param speed - Power to set the motor to(-1)-(1).
     */
    public void setPowerLeft(double speed){
        left.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
    }

    /**
     * Sets the power of the right motor.
     *
     * @param speed - Power to set the motor to(-1)-(1).
     */
    public void setPowerRight(double speed){
        right.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
    }

    /**
     * Moves forward or backwards for a set time(runs on a different thread so other things can happen in parallel).
     *
     * @param timeMs - time to drive for in milliseconds.
     * @param power - power to drive at(-1)-(1).
     */
    public void driveForwardTime(double timeMs, double power) throws InterruptedException{
        double startTime = System.currentTimeMillis();
        driveForward(power);
        while(System.currentTimeMillis() - startTime <= timeMs) {sleep(1);}
        stopMovement();
    }

    /**
     * Turns for a set time(runs on a different thread so other things can happen in parallel) (positive power for right negative power for left).
     *
     * @param timeMs - time to turn for in milliseconds.
     * @param power - power to turn at (-1)-(1).
     */
    public void turnClockwiseTime(double timeMs, double power) throws InterruptedException{
        double startTime = System.currentTimeMillis();
        turnClockwise(power);
        while(System.currentTimeMillis() - startTime <= timeMs){sleep(1);}
        stopMovement();
    }

    /**
     * Drives and turns for a set time(runs on a different thread so other things can happen in parallel) (positive power for right or forward negative power for left or backwards).
     *
     * @param timeMs - time to turn and drive for in milliseconds.
     * @param forwardComponent - double from (-1)-(1) of intensity of forward movement in the turn move
     * @param rightComponent - double from (-1)-(1) of intensity of turn movement in the turn move(positive for right negative for left).
     */
    public void turnAndMoveRightTime(double timeMs, double forwardComponent, double rightComponent) throws InterruptedException{
        double startTime = System.currentTimeMillis();
        turnAndMoveForwardRight(forwardComponent, rightComponent);
        while(System.currentTimeMillis() - startTime <= timeMs){sleep(1);}
        stopMovement();
    }

    /**
     * Dives using encoders.
     *
     * @param encoderDistance - Encoder distance to travel.
     * @param power - Double from (-1)-(1) of intensity of the movement.
     */
    public void moveForwardEncoders(int encoderDistance, double power) throws InterruptedException{
        int startEncoderPos = left.getCurrentPosition();
        driveForward(power);
        while(Math.abs(left.getCurrentPosition() - startEncoderPos) <= encoderDistance) {sleep(1);}
        stopMovement();
    }

    /**
     * Turns using encoders.
     *
     * @param encoderDistance - Encoder distance to travel.
     * @param power - double from (-1)-(1) of intensity of turn in the turn move(positive for right negative for left).
     */
    public void turnClockwiseEncoders(int encoderDistance, double power) throws InterruptedException{
        int leftStartEncoderPos = left.getCurrentPosition();
        int rightStartEncoderPos = right.getCurrentPosition();
        if(power > 0) {
            turnClockwise(power);
            while (Math.abs(left.getCurrentPosition() - leftStartEncoderPos) <= encoderDistance) {sleep(1);}
            stopMovement();
        }
        if (power < 0){
            turnClockwise(power);
            while (Math.abs(right.getCurrentPosition() - rightStartEncoderPos) <= encoderDistance){sleep(1);}
            stopMovement();
        }
    }

    /**
     * Turn while driving using encoders.
     *
     * @param encoderDistance - Encoder distance to travel.
     * @param forwardComponent - double from (-1)-(1) of intensity of forward movement in the turn move
     * @param rightComponent - double from (-1)-(1) of intensity of turn movement in the turn move(positive for right negative for left).
     */
    public void turnAndMoveRightEncoders(int encoderDistance, double forwardComponent, double rightComponent) throws InterruptedException{
        int leftStartEncoderPos = left.getCurrentPosition();
        int rightStartEncoderPos = right.getCurrentPosition();
        turnAndMoveForwardRight(forwardComponent, rightComponent);
        while(Math.abs((left.getCurrentPosition() - leftStartEncoderPos)/2 + (right.getCurrentPosition() - rightStartEncoderPos)/2) <= encoderDistance) {sleep(1);}
        stopMovement();
    }


    /**TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
     * WIP
     */
    public void PIDDrive(double kp, double ki, double kd){
        PIDController pid = new PIDController(kp, ki, kd);
        //turnClockwise(pid.getCorrection());
    }

    public void PIDTurn(double kp, double ki, double kd, double targetAngle) {
        PIDController pid = new PIDController(kp, ki, kd);
        pid.setSetpoint(targetAngle);



        //turnClockwise(-pid.getCorrection());
    }

    /**
     * Configures the motors.
     *
     * @param leftConfigurationName - Left motor configuration name.
     * @param rightConfigurationName - Right motor configuration name.
     */
    public void setMotorConfiguration(String leftConfigurationName, String rightConfigurationName){
        left = robot.hardwareMap.dcMotor.get(leftConfigurationName);
        right = robot.hardwareMap.dcMotor.get(rightConfigurationName);
    }

    /**
     * Sets deadzone for double inputs.
     *
     * @param deadzone - Double value (-1)-(1) to set deadzone to.
     */
    public void setDeadzone(double deadzone){
        this.deadzone = deadzone;
    }

    /**
     * Sets speedModeModifier.
     *
     * @param speedModeModifier - Value to set speedModeModifier to.
     */
    public void setSpeedModeModifier(double speedModeModifier){ this.speedModeModifier = speedModeModifier; }

    /**
     * Sets constantSpeedModifier.
     *
     * @param constantSpeedModifier - Value to set constantSpeedModeModifier to.
     */
    public void setConstantSpeedModifier(double constantSpeedModifier){ this.constantSpeedModifier = constantSpeedModifier; }

    /**
     * Sets wether or not to be able to turn and move at the same time.
     *
     * @param turnAndMove - Should the robot turn and move at the same time true or false.
     */
    public void setTurnAndMove(boolean turnAndMove) { this.turnAndMove = turnAndMove; }

    /**
     * Sets the double input responsible for moving forward and backwards.
     *
     * @param button - Double input responsible for moving forward and backwards.
     *
     * @throws NotDoubleInputException - Throws an exception if button does not return double values.
     */
    public void setDriveStick(Button button){
        if(button.isDouble) {
            inputs.addButton(DRIVESTICK, button);
        }
        else {
            throw new NotDoubleInputException("driveStick was not set to a double input");
        }
    }

    /**
     * Sets the double input responsible for turning right and left.
     *
     * @param button - Double input responsible for turning right and left.
     *
     * @throws NotDoubleInputException - Throws an exception if button does not return double values.
     */
    public void setTurnStick(Button button){
        if(button.isDouble) {
            inputs.addButton(TURNSTICK, button);
        }
        else {
            throw new NotDoubleInputException("turnStick was not set to a double input");
        }
    }

    /**
     * Sets the boolean input responsible for toggling speedMode.
     *
     * @param button - The boolean input responsible for toggling speedMode.
     *
     * @throws NotBooleanInputException - Throws an exception if button does not return boolean values.
     */
    public void setSpeedMode(Button button){
        if(button.isBoolean) {
            inputs.addButton(SPEEDMODEBUTTON, button);
        }
        else {
            throw new NotBooleanInputException("speedModeButton was not set to a boolean input");
        }
    }

    private void setUsingConfigs() {
        inputs = robot.pullControls(this.getClass().getSimpleName());
        Map<String, Object> settingsData = robot.pullNonGamepad(this.getClass().getSimpleName());

        setTurnAndMove((Boolean) settingsData.get("Turn and Move"));
        if (!useSpecific) {
            setDeadzone((Double) settingsData.get("Deadzone"));
            setConstantSpeedModifier((Double) settingsData.get("ConstantSpeedModifier"));
            setSpeedModeModifier((Double) settingsData.get("SpeedModeModifier"));
        }
    }

    private void setUsingConfigsAutonomous(){
        inputs = robot.pullControls(this.getClass().getSimpleName());
        Map<String, Object> settingsData = robot.pullNonGamepad(this.getClass().getSimpleName());

        setConstantSpeedModifier((double) settingsData.get("ConstantSpeedModifier"));
    }

    public int getLeftMotorEncoderPos(){
        return left.getCurrentPosition();
    }

    public int getRightMotorEncoderPos(){
        return right.getCurrentPosition();
    }

    public int[] getMotorEncoderPoses(){
        return new int[]{left.getCurrentPosition(), right.getCurrentPosition()};
    }

    @TeleopConfig
    public static ConfigParam[] teleOpConfig() {

        if(useSpecific) {
            return new ConfigParam[]{
                    new ConfigParam(DRIVESTICK, Button.DoubleInputs.left_stick_y),
                    new ConfigParam(TURNSTICK, Button.DoubleInputs.right_stick_x),
                    new ConfigParam(SPEEDMODEBUTTON, Button.DoubleInputs.noButton),
                    new ConfigParam("Turn and Move", ConfigParam.booleanMap, "true")
            };
        }
        else {
            return new ConfigParam[]{
                    new ConfigParam(DRIVESTICK, Button.DoubleInputs.left_stick_y),
                    new ConfigParam(TURNSTICK, Button.DoubleInputs.right_stick_x),
                    new ConfigParam(SPEEDMODEBUTTON, Button.DoubleInputs.noButton),
                    new ConfigParam("Turn and Move", ConfigParam.booleanMap, "true"),
                    new ConfigParam("Deadzone", ConfigParam.numberMap(0, 1, .05), 0.0),
                    new ConfigParam("SpeedModeModifier", ConfigParam.numberMap(0,100, .05), 1.0),
                    new ConfigParam("ConstantSpeedModifier", ConfigParam.numberMap(0,100, .05), 1.0)
            };
        }
    }

    @AutonomousConfig
    public static ConfigParam[] autonomousConfig() {
        return new ConfigParam[]{
                new ConfigParam("ConstantSpeedModifier", ConfigParam.numberMap(0,100, .05), 1.0)
        };
    }

    public static final class Params implements BaseParam {

        //Motor config names to be used in TankDrive to set the motors
        private String leftMotor, rightMotor;

        //Array of buttons to set the buttons to for the TankDrive class [1] is driveStick, [2] is turnStick, and [3] is speedModeButton.
        private Button[] buttonsToSet = new Button[3];
        private boolean turnAndMove;
        private double deadzone = 0, speedModeModifier = 1, constantSpeedModifier = 1;

        public Params(String leftMotorConfig, String rightMotorConfig) {
            leftMotor = leftMotorConfig;
            rightMotor = rightMotorConfig;
            setDefaultButtons();
        }

        public Params setTurnAndMove(boolean turnAndMove) {
            this.turnAndMove = turnAndMove;
            return this;
        }

        public Params setDeadzone(double deadzone) {
            this.deadzone = deadzone;
            return this;
        }

        public Params setSpeedModeModifier(double speedModeModifier) {
            this.speedModeModifier = speedModeModifier;
            return this;
        }

        public Params setConstantSpeedModifier(double constantSpeedModifier) {
            this.constantSpeedModifier = constantSpeedModifier;
            return this;
        }

        public Params setDriveStick(Button driveStick) {
            if(!driveStick.isDouble) {
                throw new NotDoubleInputException("DriveStick must be a double input.");
            }
            buttonsToSet[0] = driveStick;

            return this;
        }

        public Params setTurnStick(Button turnStick) {
            if(!turnStick.isDouble) {
                throw new NotDoubleInputException("TurnStick must be a double input.");
            }
            buttonsToSet[1] = turnStick;
            return this;
        }

        public Params setSpeedModeButton(Button speedModeButton) {
            if(!speedModeButton.isBoolean) {
                throw new NotBooleanInputException("SpeedModeButton must be a boolean input.");
            }
            buttonsToSet[2] = speedModeButton;
            return this;
        }

        private void setDefaultButtons(){
            buttonsToSet[0] = new Button(1, Button.DoubleInputs.left_stick_y);
            buttonsToSet[1] = new Button(1, Button.DoubleInputs.right_stick_x);
            buttonsToSet[2] = new Button(1, Button.DoubleInputs.noButton);
        }
    }
    
    public static final class NumberParams implements BaseParam {

        //Motor config names to be used in TankDrive to set the motors
        private String leftMotor, rightMotor;

        private double deadzone = 0, speedModeModifier = 1, constantSpeedModifier = 1;

        public NumberParams(String leftMotorConfig, String rightMotorConfig) {
            leftMotor = leftMotorConfig;
            rightMotor = rightMotorConfig;
            useSpecific = true;
        }

        public NumberParams setDeadzone(double deadzone) {
            this.deadzone = deadzone;
            return this;
        }

        public NumberParams setSpeedModeModifier(double speedModeModifier) {
            this.speedModeModifier = speedModeModifier;
            return this;
        }

        public NumberParams setConstantSpeedModifier(double constantSpeedModifier) {
            this.constantSpeedModifier = constantSpeedModifier;
            return this;
        }
    }
}