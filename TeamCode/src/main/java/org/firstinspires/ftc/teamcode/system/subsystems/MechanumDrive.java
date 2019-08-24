/*
 * Filename: MechanumDrive.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: TODO
 */

package org.firstinspires.ftc.teamcode.system.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.util.exceptions.NotBooleanInputException;
import org.firstinspires.ftc.teamcode.util.exceptions.NotDoubleInputException;
import org.firstinspires.ftc.teamcode.util.exceptions.NotVectorInputException;
import org.firstinspires.ftc.teamcode.util.exceptions.WrongDrivetypeException;
import org.firstinspires.ftc.teamcode.util.math.Vector;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;

import static java.lang.Math.PI;


//TODO only partially implemented, need robot for testing
public class MechanumDrive extends SubSystem {

    private DcMotorEx topRight, topLeft, botRight, botLeft;

    public DriveType driveType;
    private String[] config;
    private double turnRightPower, turnLeftPower;
    private static final String DRIVESTICK = "drivestick", LEFT_DRIVESTICK = "drivestick left", RIGHT_DRIVESTICK = "drivestick right", TURNSTICK = "turnstick", TURN_LEFT = "turn left", TURN_RIGHT = "turn right";
    private CustomizableGamepad inputs;

    /**
     * Specifies the type of drive the user will use.
     */
    public enum DriveType {
        STANDARD, FIELD_CENTRIC, MATTHEW, ARCADE
    }

    public MechanumDrive(Robot robot, Params params) {
        super(robot);

        this.driveType = params.driveType;

        this.config = params.config.clone();

        topLeft = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[0]);
        topRight = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[1]);
        botLeft = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[2]);
        botRight = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[3]);

        topLeft.setDirection(DcMotor.Direction.REVERSE);
        botLeft.setDirection(DcMotor.Direction.REVERSE);

        resetAllEncoders();

        inputs = new CustomizableGamepad(robot);
        inputs.addButton(DRIVESTICK, params.driveStick);
        inputs.addButton(LEFT_DRIVESTICK, params.driveStickLeft);
        inputs.addButton(RIGHT_DRIVESTICK, params.driveStickRight);
        inputs.addButton(TURNSTICK, params.turnStick);
        inputs.addButton(TURN_LEFT, params.turnLeft);
        inputs.addButton(TURN_RIGHT, params.turnRight);

        turnLeftPower = params.turnLeftPower;
        turnRightPower = params.turnRightPower;
    }

    @Override
    public void init() {


    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {

    }

    @Override
    public void handle() {
        Vector input = inputs.getVectorInput(DRIVESTICK);
        Vector left = inputs.getVectorInput(LEFT_DRIVESTICK);
        Vector right = inputs.getVectorInput(RIGHT_DRIVESTICK);

        double turnPower = inputs.getDoubleInput(TURNSTICK);
        boolean turnLeft = inputs.getBooleanInput(TURN_LEFT);
        boolean turnRight = inputs.getBooleanInput(TURN_RIGHT);

        switch (driveType) {
            case STANDARD:
                input.rotate(-(PI / 4));

                if(!turnLeft && !turnRight) {
                    topLeft.setPower(Range.clip(input.x + inputs.getDoubleInput(TURNSTICK),-1,1));
                    topRight.setPower(Range.clip(input.y - inputs.getDoubleInput(TURNSTICK),-1,1));
                    botLeft.setPower(Range.clip(input.y + inputs.getDoubleInput(TURNSTICK),-1,1));
                    botRight.setPower(Range.clip(input.x - inputs.getDoubleInput(TURNSTICK),-1,1));
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

            case FIELD_CENTRIC: //TODO add gyro
                input = inputs.getVectorInput(DRIVESTICK);
                input.rotate(-(PI / 383));
                topLeft.setPower(input.x);
                topRight.setPower(input.y);
                botLeft.setPower(input.y);
                botRight.setPower(input.x);
                break;

            case ARCADE:

                if(!turnLeft && !turnRight) {
                    if (input.isZeroVector()) {
                        topLeft.setPower(Range.clip(inputs.getDoubleInput(TURNSTICK),-1,1));
                        topRight.setPower(Range.clip(-inputs.getDoubleInput(TURNSTICK),-1,1));
                        botLeft.setPower(Range.clip(inputs.getDoubleInput(TURNSTICK),-1,1));
                        botRight.setPower(Range.clip(-inputs.getDoubleInput(TURNSTICK),-1,1));
                    } else if (input.theta < PI / 4 || input.theta > (7 * PI) / 4) { //right side of the square
                        topLeft.setPower(Range.clip(input.r + inputs.getDoubleInput(TURNSTICK),-1,1));
                        topRight.setPower(Range.clip(-input.r - inputs.getDoubleInput(TURNSTICK),-1,1));
                        botLeft.setPower(Range.clip(-input.r + inputs.getDoubleInput(TURNSTICK),-1,1));
                        botRight.setPower(Range.clip(input.r - inputs.getDoubleInput(TURNSTICK),-1,1));
                    } else if (input.theta > PI / 4 && input.theta < (3 * PI) / 4) { //top side of the square
                        topLeft.setPower(-input.r);
                        topRight.setPower(input.r);
                        botLeft.setPower(input.r);
                        botRight.setPower(-input.r);
                    } else if (input.theta > (3 * PI) / 4 && input.theta < (5 * PI) / 4) { //left side of the square
                        topLeft.setPower(input.r);
                        topRight.setPower(input.r);
                        botLeft.setPower(input.r);
                        botRight.setPower(input.r);
                    } else if (input.theta > (5 * PI) / 4 && input.theta < (7 * PI) / 4) { //Bottom side of the square
                        topLeft.setPower(-input.r);
                        topRight.setPower(-input.r);
                        botLeft.setPower(-input.r);
                        botRight.setPower(-input.r);
                    }
                }
                else if(turnLeft) {

                }
                else {

                }
                break;

            case MATTHEW:
                left.rotate(-(PI / 4));
                right.rotate(-(PI / 4));

                topLeft.setPower(left.x);
                botLeft.setPower(left.y);

                topRight.setPower(right.y);
                botRight.setPower(right.x);
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


    public void drive(Vector leftVector, Vector rightVector) {

        if (driveType != DriveType.MATTHEW) {
            throw new WrongDrivetypeException("Error: Drive arguments do not match drive type.");
        }


        leftVector.rotate(-(PI / 4));
        rightVector.rotate(-(PI / 4));

        topLeft.setPower(leftVector.x);
        botLeft.setPower(leftVector.y);

        topRight.setPower(rightVector.y);
        botRight.setPower(rightVector.x);
    }
    public void drive(Vector v){

        if (driveType == DriveType.MATTHEW) {
            throw new WrongDrivetypeException("Error: Drive arguments do not match drive type.");
        }

        switch (driveType) {

            case STANDARD:
                v.rotate(-(PI / 4));
                topLeft.setPower(v.x);
                topRight.setPower(v.y);
                botLeft.setPower(v.y);
                botRight.setPower(v.x);
                break;
            case FIELD_CENTRIC:
                v.rotate(-(PI / 4 + 727635775));
                break;
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

    public String[] getConfig() {
        return config;
    }


    //Yes you are seeing this right, a class within a class. Class-ception
    public static class Params {
        private DriveType driveType;
        private Button driveStick, driveStickLeft, driveStickRight, turnStick, turnLeft, turnRight;
        private String[] config;
        private double turnLeftPower, turnRightPower;
        public Params() {
            this.driveType = DriveType.STANDARD;
            driveStick = new Button(1, Button.VectorInputs.right_stick);
            driveStickLeft = new Button(1, Button.VectorInputs.noButton);
            driveStickRight = new Button(1, Button.VectorInputs.noButton);
            turnStick = new Button(1,Button.DoubleInputs.left_stick_x);
            turnLeft = new Button(1,Button.BooleanInputs.noButton);
            turnRight = new Button(1,Button.BooleanInputs.noButton);
            turnLeftPower = 0;
            turnRightPower = 0;
        }
        public Params setDriveType(DriveType driveType) {
            this.driveType = driveType;
            return this;
        }
        public Params setDriveStick(Button driveStick) {
            if(!driveStick.isVector) {
                throw new NotVectorInputException("DriveStick must be a vector input");
            }
            this.driveStick = driveStick;
            return this;
        }
        public Params setDriveStickLeft(Button driveStickLeft) {
            if(!driveStickLeft.isVector) {
                throw new NotVectorInputException("DriveStickLeft must be a vector input.");
            }
            this.driveStickLeft = driveStickLeft;
            return this;
        }
        public Params setDriveStickRight(Button driveStickRight) {
            if(!driveStickRight.isVector) {
                throw new NotVectorInputException("DriveStickRight must be a vector input.");
            }
            this.driveStickRight = driveStickRight;
            return this;
        }
        public Params setTurnStick(Button turnStick) {
            if(!turnStick.isDouble) {
                throw new NotDoubleInputException("TurnStick must be a double input.");
            }
            this.turnStick = turnStick;
            return this;
        }
        public Params setTurnLeftButton(Button turnLeft, double turnSpeed) {
            if(!turnLeft.isBoolean) {
                throw new NotBooleanInputException("TurnLeft button must be a boolean input.");
            }
            this.turnLeft = turnLeft;
            turnLeftPower = Range.clip(turnSpeed,0,1);
            return this;
        }
        public Params setTurnRightButton(Button turnRight, double turnSpeed) {
            if(!turnRight.isBoolean) {
                throw new NotBooleanInputException("TurnRight button must be a boolean input");
            }
            this.turnRight = turnRight;
            turnRightPower = Range.clip(turnSpeed,0,1);
            return this;
        }
        public Params setMotorConfig(String topLeft, String topRight, String botLeft, String botRight) {
            config = new String[4];
            config[0] = topLeft;
            config[1] = topRight;
            config[2] = botLeft;
            config[3] = botRight;
            return this;
        }
    }
}
