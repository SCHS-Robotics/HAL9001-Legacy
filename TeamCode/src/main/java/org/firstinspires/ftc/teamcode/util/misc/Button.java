/*
 * Filename:Button.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.util.misc;

import com.qualcomm.robotcore.hardware.Gamepad;

public class Button {

    public boolean isBoolean;
    public boolean isDouble;
    public volatile Gamepad gamepad;
    public DoubleInputs doubleInput;
    public BooleanInputs booleanInput;
    public double deadzone = .05;


    /**
     Represents the allowed input methods for controls that return double values.
     */
    public enum DoubleInputs{
        left_stick_x, left_stick_y, left_trigger, right_stick_x, right_stick_y, right_trigger, noButton
    }

    /**
     Represents the allowed input methods for controls that return boolean values.
     */
    public enum BooleanInputs{
        a, b, back, dpad_down, dpad_left, dpad_right, dpad_up, guide, left_bumper, left_stick_button, right_bumper, right_stick_button, start, x, y, bool_left_stick_x, bool_right_stick_x,  bool_left_stick_y, bool_right_stick_y, bool_left_trigger, bool_right_trigger, bool_left_stick_x_right, bool_right_stick_x_right,  bool_left_stick_y_up, bool_right_stick_y_up, bool_left_stick_x_left, bool_right_stick_x_left,  bool_left_stick_y_down, bool_right_stick_y_down, noButton
    }

    public Button(Gamepad gamepad, DoubleInputs inputName){
        this.gamepad = gamepad;
        this.doubleInput = inputName;
        this.isDouble = true;
        this.isBoolean = false;

    }

    public Button(Gamepad gamepad, BooleanInputs inputName){
        this.gamepad = gamepad;
        this.booleanInput = inputName;
        this.isDouble = false;
        this.isBoolean = true;
    }

    public void setDeadzone(double deadzone){
        this.deadzone = deadzone;
    }

    public Enum getInputEnum(){
        if(isBoolean){
            return booleanInput;
        }
        else {
            return doubleInput;
        }
    }
}
