/*
 * Filename: QuadWheelDrive.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/7/19
 */

package org.firstinspires.ftc.teamcode.system.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.system.source.SubSystem;
import org.firstinspires.ftc.teamcode.util.control.PIDController;
import org.firstinspires.ftc.teamcode.util.exceptions.NotBooleanInputException;
import org.firstinspires.ftc.teamcode.util.exceptions.NotDoubleInputException;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;
import org.firstinspires.ftc.teamcode.util.misc.Toggle;

public class QuadWheelDrive extends SubSystem {

    //The drivetrain's four motors
    private DcMotor topLeft, topRight, bottomLeft, bottomRight;

    //A boolean determining if the robot should be allowed to turn and move simultaneously
    private boolean turnAndMove;

    //A toggle object that detects if a boolean input changes twice (like a square pulse)
    private Toggle speedToggle = new Toggle(false);

    //Modifiers for speed and joystick operations
    private double deadzone = 0, currentSpeedModeModifier = 1, speedModeModifier = 1, constantSpeedModifier = 1;

    private CustomizableGamepad inputs;

    //Button names CustomizableGamepad will use.
    private final String SPEEDMODEBUTTON = "speedModeButton", DRIVESTICK = "driveStick", TURNSTICK = "turnStick";


    /**
     * This constructor should be used when using this program for TeleOp.
     *
     * @param robot - The robot we will be using.
     * @param motorConfig - A string array of size 2 holding the names of the motor configurations. The first element is the left motor and the second element is the right motor.
     * @param robotConfig - A double array of size 3 used to set configuration values. The first element is the deadzone or the value at which must the input must surpass to be used.
     *                    The second element is the speedModeModifier which multiplies the power by itself when speed mode is activated. The third element is the constantSpeedModifier
     *                    which does the same thing as speedModeModifier but is instead always active (set to 1 to do nothing).
     * @param driveStick - The button for forward and backwards movement that is a double (noButton to disable).
     * @param turnStick - The button for turning right and left that is a double (noButton to disable).
     * @param speedModeButton - The button for toggling speedMode that is a boolean (noButton to disable).
     * @param turnAndMove - A boolean that if true allows the robot to move and turn at the same time.
     */
    public QuadWheelDrive(Robot robot, String[] motorConfig, double[] robotConfig, Button driveStick, Button turnStick, Button speedModeButton, boolean turnAndMove){
        super(robot);

        inputs = new CustomizableGamepad(robot);

        setMotorConfiguration(motorConfig[0], motorConfig[1], motorConfig[2], motorConfig[3]);
        setDeadzone(robotConfig[0]);
        setSpeedModeModifier(robotConfig[1]);
        setConstantSpeedModifier(robotConfig[2]);
        setTurnAndMove(turnAndMove);
        setDriveStick(driveStick);
        setTurnStick(turnStick);
        setSpeedMode(speedModeButton);
    }

    /**
     * This constructor should be used when using this program for autonomous.
     *
     * @param robot - The robot we will be using
     * @param motorConfig - A string array of size 2 holding the names of the motor configurations. The first element is the left motor and the second element is the right motor.
     * @param robotConfig - A double array of size 3 used to set configuration values. The first element is the deadzone or the value at which must the input must surpass to be used.
     *                    The second element is the speedModeModifier which multiplies the power by itself when speed mode is activated. The third element is the constantSpeedModifier
     *                    which does the same thing as speedModeModifier but is instead always active (set to 1 to do nothing).
     */
    public QuadWheelDrive(Robot robot, String[] motorConfig, double[] robotConfig){
        super(robot);

        inputs = new CustomizableGamepad(robot);

        setMotorConfiguration(motorConfig[0], motorConfig[1],motorConfig[2],motorConfig[3]);
        setDeadzone(robotConfig[0]);
        setSpeedModeModifier(robotConfig[1]);
        setConstantSpeedModifier(robotConfig[2]);
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
                turnRight(inputs.getDoubleInput(TURNSTICK));
            }
        }
        //drives forward and turns but not at the same time
        else {
            if (inputs.getDoubleInput(TURNSTICK) > deadzone) {
                turnRight(inputs.getDoubleInput(TURNSTICK));
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
        topLeft.setDirection(DcMotor.Direction.FORWARD);
        bottomLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        topRight.setDirection(DcMotor.Direction.REVERSE);
        bottomRight.setDirection(DcMotor.Direction.REVERSE);
    }

    /**
     * Sets the direction of the robot(default direction).
     */
    public void normalDirection(){
        topLeft.setDirection(DcMotor.Direction.REVERSE);
        bottomLeft.setDirection(DcMotor.Direction.REVERSE);
        topRight.setDirection(DcMotor.Direction.FORWARD);
        bottomRight.setDirection(DcMotor.Direction.FORWARD);
    }

    /**
     * Resents the encoders.
     */
    public void resetEncoders(){
        topLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bottomLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        topRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bottomRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        topLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bottomLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        topRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bottomRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Makes the robot drive forward.
     *
     * @param speed - Speed to drive forward(-1)-(1).
     */
    public void driveForward(double speed){
        topLeft.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
        bottomLeft.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
        topRight.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
        bottomRight.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
    }

    /**
     * Makes the robot turn.
     *
     * @param speed - Speed to turn at(-1)-(1) (positive speed is turn right & negative speed is turn left).
     */
    public void turnRight(double speed){
        topLeft.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
        bottomLeft.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
        topRight.setPower(-((speed * constantSpeedModifier) * currentSpeedModeModifier));
        bottomRight.setPower(-((speed * constantSpeedModifier) * currentSpeedModeModifier));
    }

    /**
     * Moves forward and turns at the same time.
     *
     * @param forwardComponent - double from (-1)-(1) of intensity of forward movement in the turn move
     * @param rightComponent - double from (-1)-(1) of intensity of turn movement in the turn move(positive for right negative for left).
     */
    public void turnAndMoveForwardRight(double forwardComponent, double rightComponent){
        topLeft.setPower(((forwardComponent/2 + rightComponent/2) * constantSpeedModifier) * currentSpeedModeModifier);
        bottomLeft.setPower(((forwardComponent/2 + rightComponent/2) * constantSpeedModifier) * currentSpeedModeModifier);
        topRight.setPower(((forwardComponent/2 - rightComponent/2) * constantSpeedModifier) * currentSpeedModeModifier);
        bottomRight.setPower(((forwardComponent/2 - rightComponent/2) * constantSpeedModifier) * currentSpeedModeModifier);
    }

    /**
     * Stops all movement.
     */
    public void stopMovement(){
        topLeft.setPower(0);
        bottomLeft.setPower(0);
        topRight.setPower(0);
        bottomRight.setPower(0);
    }

    /**
     * Sets the power of the left motor.
     *
     * @param speed - Power to set the motor to(-1)-(1).
     */
    public void setPowerLeft(double speed){
        topLeft.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
        bottomLeft.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
    }

    /**
     * Sets the power of the right motor.
     *
     * @param speed - Power to set the motor to(-1)-(1).
     */
    public void setPowerRight(double speed){
        topRight.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
        bottomRight.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
    }

    /**
     * Moves forward or backwards for a set time(runs on a different thread so other things can happen in parallel).
     *
     * @param timeMs - time to drive for in milliseconds.
     * @param power - power to drive at(-1)-(1).
     */
    public void moveForwardTime(double timeMs, double power){
        double startTime = System.currentTimeMillis();
        Thread b  = new Thread() {
            @Override
            public void run() {
                driveForward(power);

                while(System.currentTimeMillis() - startTime <= timeMs);
                stopMovement();
            }
        };
        b.start();
    }

    /**
     * Turns for a set time(runs on a different thread so other things can happen in parallel) (positive power for right negative power for left).
     *
     * @param timeMs - time to turn for in milliseconds.
     * @param power - power to turn at (-1)-(1).
     */
    public void turnRightTime(double timeMs, double power){
        double startTime = System.currentTimeMillis();
        Thread b  = new Thread() {
            @Override
            public void run() {
                turnRight(power);
                while(System.currentTimeMillis() - startTime <= timeMs);
                stopMovement();
            }
        };
        b.start();
    }

    /**
     * Drives and turns for a set time(runs on a different thread so other things can happen in parallel) (positive power for right or forward negative power for left or backwards).
     *
     * @param timeMs - time to turn and drive for in milliseconds.
     * @param forwardComponent - double from (-1)-(1) of intensity of forward movement in the turn move
     * @param rightComponent - double from (-1)-(1) of intensity of turn movement in the turn move(positive for right negative for left).
     */
    public void turnAndMoveRightTime(double timeMs, double forwardComponent, double rightComponent){
        double startTime = System.currentTimeMillis();
        Thread b  = new Thread() {
            @Override
            public void run() {
                turnAndMoveForwardRight(forwardComponent, rightComponent);
                while(System.currentTimeMillis() - startTime <= timeMs);
                stopMovement();
            }
        };
        b.start();
    }

    /**
     * Dives using encoders.
     *
     * @param encoderDistance - Encoder distance to travel.
     * @param power - Double from (-1)-(1) of intensity of the movement.
     */
    public void moveForwardEncoders(int encoderDistance, double power){
        int startEncoderPos = topLeft.getCurrentPosition();
        Thread b  = new Thread() {
            @Override
            public void run() {
                driveForward(power);
                while(Math.abs(topLeft.getCurrentPosition() - startEncoderPos) <= encoderDistance);
                stopMovement();
            }
        };
        b.start();
    }

    /**
     * Turns using encoders.
     *
     * @param encoderDistance - Encoder distance to travel.
     * @param power - double from (-1)-(1) of intensity of turn in the turn move(positive for right negative for left).
     */
    public void turnRightEncoders(int encoderDistance, double power){
        int leftStartEncoderPos = topLeft.getCurrentPosition();
        int rightStartEncoderPos = topRight.getCurrentPosition();
        Thread b  = new Thread() {
            @Override
            public void run() {
                if(power > 0) {
                    turnRight(power);
                    while (Math.abs(topLeft.getCurrentPosition() - leftStartEncoderPos) <= encoderDistance) ;
                    stopMovement();
                }
                if (power < 0){
                    turnRight(power);
                    while (Math.abs(topRight.getCurrentPosition() - rightStartEncoderPos) <= encoderDistance) ;
                    stopMovement();
                }
            }
        };
        b.start();
    }

    /**
     * Turn while driving using encoders.
     *
     * @param encoderDistance - Encoder distance to travel.
     * @param forwardComponent - double from (-1)-(1) of intensity of forward movement in the turn move
     * @param rightComponent - double from (-1)-(1) of intensity of turn movement in the turn move(positive for right negative for left).
     */
    public void turnAndMoveRightEncoders(int encoderDistance, double forwardComponent, double rightComponent){
        int leftStartEncoderPos = topLeft.getCurrentPosition();
        int rightStartEncoderPos = topRight.getCurrentPosition();
        Thread b  = new Thread() {
            @Override
            public void run() {
                turnAndMoveForwardRight(forwardComponent, rightComponent);
                while(Math.abs((topLeft.getCurrentPosition() - leftStartEncoderPos)/2 + (topRight.getCurrentPosition() - rightStartEncoderPos)/2) <= encoderDistance);
                stopMovement();
            }
        };
        b.start();
    }


    /**TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
     * WIP
     */
    @Deprecated
    public void PIDDrive(double kp, double ki, double kd, double targetAngle){
        PIDController pid = new PIDController(kp, ki, kd);
    }

    /**
     * Configures the motors.
     *
     * @param topLeftConfigurationName - The top left motor config name.
     * @param bottomLeftConfigurationName - The bottom left motor config name.
     * @param topRightConfigurationName - The top right motor config name.
     * @param bottomRightConfigurationName - The bottom right motor config name.
     */
    public void setMotorConfiguration(String topLeftConfigurationName, String bottomLeftConfigurationName, String topRightConfigurationName, String bottomRightConfigurationName){
        topLeft = robot.hardwareMap.dcMotor.get(topLeftConfigurationName);
        bottomLeft = robot.hardwareMap.dcMotor.get(bottomLeftConfigurationName);
        topRight = robot.hardwareMap.dcMotor.get(topRightConfigurationName);
        bottomRight = robot.hardwareMap.dcMotor.get(bottomRightConfigurationName);
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
 }
