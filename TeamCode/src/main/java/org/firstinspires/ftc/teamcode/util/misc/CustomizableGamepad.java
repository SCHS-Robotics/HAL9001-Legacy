/*
 * Filename: CustomizableGamepad.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/18/19
 */

package org.firstinspires.ftc.teamcode.util.misc;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.HashMap;
import java.util.Map;


//TODO Do the javadocs
public class CustomizableGamepad {

    private Map<String, Button> inputs;

    public CustomizableGamepad() {
        this.inputs = new HashMap<>();
    }

    public void addButton(String buttonName, Button.DoubleInputs button, Gamepad gamepad){
        inputs.put(buttonName, new Button(gamepad, button));
    }

    public void addButton(String buttonName, Button.BooleanInputs button, Gamepad gamepad){
        inputs.put(buttonName, new Button(gamepad, button));
    }

    public void addButton(String buttonName, Button button){
        inputs.put(buttonName, button);
    }

    public void addButton(String buttonName, Button.BooleanInputs button, Gamepad gamepad, double deadzone){
        inputs.put(buttonName, new Button(gamepad, button));
        inputs.get(buttonName).setDeadzone(deadzone);
    }

    public void addButton(String buttonName, Button button, double deadzone){
        inputs.put(buttonName, button);
        inputs.get(buttonName).setDeadzone(deadzone);
    }

    public void  removeButton(String buttonName){
        inputs.remove(buttonName);
    }

    public boolean checkNoButton(String buttonName){
        if(inputs.get(buttonName).getInputEnum() == Button.DoubleInputs.noButton || inputs.get(buttonName).getInputEnum() == Button.BooleanInputs.noButton){
            return true;
        }
        return false;
    }

    public boolean getBooleanInput(String buttonName){

        switch((Button.BooleanInputs) inputs.get(buttonName).getInputEnum()) {

            case a: return inputs.get(buttonName).gamepad.a;
            case b: return inputs.get(buttonName).gamepad.b;
            case x: return inputs.get(buttonName).gamepad.x;
            case y: return inputs.get(buttonName).gamepad.y;
            case back: return inputs.get(buttonName).gamepad.back;
            case start: return inputs.get(buttonName).gamepad.start;
            case guide: return inputs.get(buttonName).gamepad.guide;
            case dpad_up: return inputs.get(buttonName).gamepad.dpad_up;
            case dpad_down: return inputs.get(buttonName).gamepad.dpad_down;
            case dpad_left: return inputs.get(buttonName).gamepad.dpad_left;
            case dpad_right: return inputs.get(buttonName).gamepad.dpad_right;
            case left_bumper: return inputs.get(buttonName).gamepad.left_bumper;
            case right_bumper: return inputs.get(buttonName).gamepad.right_bumper;
            case left_stick_button: return inputs.get(buttonName).gamepad.left_stick_button;
            case right_stick_button: return inputs.get(buttonName).gamepad.right_stick_button;

            default: return false;
        }
    }

    public boolean getBooleanInput(String buttonName, boolean defaultReturn){

        switch((Button.BooleanInputs) inputs.get(buttonName).getInputEnum()) {

            case a: return inputs.get(buttonName).gamepad.a;
            case b: return inputs.get(buttonName).gamepad.b;
            case x: return inputs.get(buttonName).gamepad.x;
            case y: return inputs.get(buttonName).gamepad.y;
            case back: return inputs.get(buttonName).gamepad.back;
            case start: return inputs.get(buttonName).gamepad.start;
            case guide: return inputs.get(buttonName).gamepad.guide;
            case dpad_up: return inputs.get(buttonName).gamepad.dpad_up;
            case dpad_down: return inputs.get(buttonName).gamepad.dpad_down;
            case dpad_left: return inputs.get(buttonName).gamepad.dpad_left;
            case dpad_right: return inputs.get(buttonName).gamepad.dpad_right;
            case left_bumper: return inputs.get(buttonName).gamepad.left_bumper;
            case right_bumper: return inputs.get(buttonName).gamepad.right_bumper;
            case left_stick_button: return inputs.get(buttonName).gamepad.left_stick_button;
            case right_stick_button: return inputs.get(buttonName).gamepad.right_stick_button;
            case bool_left_trigger: return inputs.get(buttonName).gamepad.left_trigger > inputs.get(buttonName).deadzone;
            case bool_right_trigger: return inputs.get(buttonName).gamepad.right_trigger > inputs.get(buttonName).deadzone;
            case bool_left_stick_y_up: return -inputs.get(buttonName).gamepad.left_stick_y > inputs.get(buttonName).deadzone;
            case bool_left_stick_x_left: return inputs.get(buttonName).gamepad.left_stick_x < inputs.get(buttonName).deadzone;
            case bool_left_stick_x_right: return inputs.get(buttonName).gamepad.left_stick_x > inputs.get(buttonName).deadzone;
            case bool_right_stick_y_up: return -inputs.get(buttonName).gamepad.right_stick_y > inputs.get(buttonName).deadzone;
            case bool_left_stick_y_down: return -inputs.get(buttonName).gamepad.left_stick_y < inputs.get(buttonName).deadzone;
            case bool_right_stick_x_left: return inputs.get(buttonName).gamepad.right_stick_x < inputs.get(buttonName).deadzone;
            case bool_right_stick_x_right: return inputs.get(buttonName).gamepad.right_stick_x > inputs.get(buttonName).deadzone;
            case bool_right_stick_y_down: return -inputs.get(buttonName).gamepad.right_stick_y < inputs.get(buttonName).deadzone;
            case bool_left_stick_x: return Math.abs(inputs.get(buttonName).gamepad.left_stick_x) > inputs.get(buttonName).deadzone;
            case bool_left_stick_y: return Math.abs(inputs.get(buttonName).gamepad.left_stick_y) > inputs.get(buttonName).deadzone;
            case bool_right_stick_x: return Math.abs(inputs.get(buttonName).gamepad.right_stick_x) > inputs.get(buttonName).deadzone;
            case bool_right_stick_y: return Math.abs(inputs.get(buttonName).gamepad.right_stick_y) > inputs.get(buttonName).deadzone;

            default: return defaultReturn;
        }
    }

    public double getDoubleInput(String buttonName){
        switch((Button.DoubleInputs) inputs.get(buttonName).getInputEnum()) {
            case left_stick_x: return inputs.get(buttonName).gamepad.left_stick_x;
            case left_trigger: return inputs.get(buttonName).gamepad.left_trigger;
            case left_stick_y: return -inputs.get(buttonName).gamepad.left_stick_y;
            case right_stick_x: return inputs.get(buttonName).gamepad.right_stick_x;
            case right_trigger: return inputs.get(buttonName).gamepad.right_trigger;
            case right_stick_y: return -inputs.get(buttonName).gamepad.right_stick_y;

            default: return 0;
        }
    }

    public double getDoubleInput(String buttonName, double defaultReturn){
        switch((Button.DoubleInputs) inputs.get(buttonName).getInputEnum()) {
            case left_stick_x: return inputs.get(buttonName).gamepad.left_stick_x;
            case left_trigger: return inputs.get(buttonName).gamepad.left_trigger;
            case left_stick_y: return -inputs.get(buttonName).gamepad.left_stick_y;
            case right_stick_x: return inputs.get(buttonName).gamepad.right_stick_x;
            case right_trigger: return inputs.get(buttonName).gamepad.right_trigger;
            case right_stick_y: return -inputs.get(buttonName).gamepad.right_stick_y;

            default: return defaultReturn;
        }
    }
}
