/*
 * Filename: Button.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.util.misc;

/**
 * A class representing a button on the gamepad.
 */
public class Button {

    //IsBoolean is ture if is is a boolean input button, isDouble is true if it is a double input button.
    public boolean isBoolean, isDouble, isVector;
    //Number of gamepad to use 1 or 2.
    public int gamepadNumber;
    //Double input to use if it is a double input button.
    public DoubleInputs doubleInput;
    //Boolean input to use if it is a boolean input button.
    public BooleanInputs booleanInput;

    public VectorInputs vectorInput;
    //Deadzone to use for the boolean version of the double inputs.
    public double deadzone = .05;

    /**
     * Represents the allowed input methods for controls that return double values.
     */
    public enum DoubleInputs{
        left_stick_x, left_stick_y, left_trigger, right_stick_x, right_stick_y, right_trigger, noButton
    }

    /**
     * Represents the allowed input methods for controls that return boolean values.
     */
    public enum BooleanInputs{
        a, b, back, dpad_down, dpad_left, dpad_right, dpad_up, guide, left_bumper, left_stick_button, right_bumper, right_stick_button, start, x, y, bool_left_stick_x, bool_right_stick_x,  bool_left_stick_y, bool_right_stick_y, bool_left_trigger, bool_right_trigger, bool_left_stick_x_right, bool_right_stick_x_right,  bool_left_stick_y_up, bool_right_stick_y_up, bool_left_stick_x_left, bool_right_stick_x_left,  bool_left_stick_y_down, bool_right_stick_y_down, noButton
    }

    public enum VectorInputs{
        left_stick, right_stick, noButton
    }

    /**
     * Constructor for button that makes a double button.
     *
     * @param gamepadNumber - Number of gamepad this button will use.
     * @param inputName - DoubleInput that this button will output.
     */
    public Button(int gamepadNumber, DoubleInputs inputName){
        this.gamepadNumber = gamepadNumber;
        this.doubleInput = inputName;
        this.isDouble = true;
        this.isBoolean = false;
        this.isVector = false;

    }

    /**
     * Constructor for button that makes a boolean button.
     *
     * @param gamepadNumber - Number of gamepad this button will use.
     * @param inputName - BooleanInput that this button will output.
     */
    public Button(int gamepadNumber, BooleanInputs inputName){
        this.gamepadNumber = gamepadNumber;
        this.booleanInput = inputName;
        this.isDouble = false;
        this.isBoolean = true;
        this.isVector = false;
    }

    public Button(int gamepadNumber, VectorInputs inputName){
        this.gamepadNumber = gamepadNumber;
        this.vectorInput = inputName;
        this.isDouble = false;
        this.isBoolean = false;
        this.isVector = true;
    }

    /**
     * Sets the deadzone for the boolean version of the double inputs.
     *
     * @param deadzone - Double between 0 and 1 that sets the deadzone.
     */
    public void setDeadzone(double deadzone){
        this.deadzone = deadzone;
    }

    /**
     * Returns the enum for this button.
     */
    public Enum getInputEnum(){
        if(isBoolean){
            return booleanInput;
        }
        else {
            return doubleInput;
        }
    }
}
