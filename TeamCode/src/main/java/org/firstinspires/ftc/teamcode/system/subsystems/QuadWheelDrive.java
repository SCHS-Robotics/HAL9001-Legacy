/*
 * Filename: QuadWheelDrive.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: TODO
 */

package org.firstinspires.ftc.teamcode.system.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.internal.opengl.models.MeshObject;
import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.system.source.SubSystem;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.Toggle;

public class QuadWheelDrive extends SubSystem {

    //The drivetrain's four motors
    private DcMotor topleft, topRight, bottomLeft, bottomRight;

    //A boolean determining if the robot should be allowed to turn and move simultaneously
    private boolean turnAndMove;

    //A toggle object that detects if a boolean input changes twice (like a square pulse)
    private Toggle speedToggle = new Toggle(false);

    //Modifiers for speed and joystick operations
    private double deadzone = 0, currentSpeedModeModifier = 1, speedModeModifier = 1, constantSpeedModifier = 1;

    //Gamepad used to drive the robot
    private Gamepad gamepad;

    //Boolean input responsible for toggling speedMode.
    Button.BooleanInputs speedMode;

    //Double input responsible for (driveStick) moving forward and backwards or (turnStick) turning right and left
    Button.DoubleInputs driveStick, turnStick;

    /**
     * This constructor should be used when using this program for TeleOp.
     *
     * @param robot - The robot we will be using.
     * @param motorConfig - A string array of size 2 holding the names of the motor configurations. The first element is the topLeft motor, the second element is the topRight motor,
     *                    third element is the bottomLeft motor, and the fourth element is the bottomRight motor.
     * @param robotConfig - A double array of size 3 used to set configuration values. The first element is the deadzone or the value at which must the input must surpass to be used.
     *                    The second element is the speedModeModifier which multiplies the power by itself when speed mode is activated. The third element is the constantSpeedModifier
     *                    which does the same thing as speedModeModifier but is instead always active (set to 1 to do nothing).
     * @param driveStick - An enum from DoubleInputs that sets the input for forward and backwards to any gamepad input that is a double (noButton to disable).
     * @param turnStick - An enum from DoubleInputs that sets the input for turning to any gamepad input that is a double (noButton to disable).
     * @param speedModeButton - An enum from BooleanInputs that sets the input for speedMode to any gamepad input that is a boolean (noButton to disable).
     * @param turnAndMove - A boolean that if true allows the robot to move and turn at the same time.
     * @param gamepad - give it gamepad1 or gamepad2 depending on which controller you wish to use.
     */
    public QuadWheelDrive(Robot robot, String[] motorConfig, double[] robotConfig, Button.DoubleInputs driveStick, Button.DoubleInputs turnStick, Button.BooleanInputs speedModeButton, boolean turnAndMove, Gamepad gamepad){
        super(robot);
        //setMotorConfiguration(motorConfig[0], motorConfig[1], motorConfig[2], motorConfig[3]);
       //setDeadzone(robotConfig[0]);
        //setSpeedModeModifier(robotConfig[1]);
        //setConstantSpeedModifier(robotConfig[2]);
        //setGamepad(gamepad);
       // setTurnAndMove(turnAndMove);
       // setDriveStick(driveStick);
       // setTurnStick(turnStick);
       // setSpeedMode(speedModeButton);
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
        //setMotorConfiguration(motorConfig[0], motorConfig[1]);
        //setDeadzone(robotConfig[0]);
        //setSpeedModeModifier(robotConfig[1]);
        //setConstantSpeedModifier(robotConfig[2]);
    }

    @Override
    public void init() {

    }

    @Override
    public void handle() {

    }

    @Override
    public void stop() {

    }
 }
