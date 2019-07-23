/*
 * Filename: CustomizableGamepad.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/18/19
 */

package org.firstinspires.ftc.teamcode.util.misc;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.system.source.Robot;

import java.util.HashMap;
import java.util.Map;


//TODO Do the javadocs
public class CustomizableGamepad {

    private Robot robot;


    private Map<String, Button> inputs;

    public CustomizableGamepad(Robot robot) {
        this.robot = robot;
        this.inputs = new HashMap<>();
    }

    public void addButton(String buttonName, Button.DoubleInputs button, int gamepadNumber) {
        inputs.put(buttonName, new Button(gamepadNumber, button));
    }

    public void addButton(String buttonName, Button.BooleanInputs button, int gamepadNumber) {
        inputs.put(buttonName, new Button(gamepadNumber, button));
    }

    public void addButton(String buttonName, Button button) {
        inputs.put(buttonName, button);
    }

    public void addButton(String buttonName, Button.BooleanInputs button, int gamepadNumber, double deadzone) {
        inputs.put(buttonName, new Button(gamepadNumber, button));
        inputs.get(buttonName).setDeadzone(deadzone);
    }

    public void addButton(String buttonName, Button button, double deadzone) {
        inputs.put(buttonName, button);
        inputs.get(buttonName).setDeadzone(deadzone);
    }

    public void removeButton(String buttonName) {
        inputs.remove(buttonName);
    }

    public boolean checkNoButton(String buttonName) {
        if (inputs.get(buttonName).getInputEnum() == Button.DoubleInputs.noButton || inputs.get(buttonName).getInputEnum() == Button.BooleanInputs.noButton) {
            return true;
        }
        return false;
    }

    public boolean getBooleanInput(String buttonName) {
        if (inputs.get(buttonName).gamepadNumber == 1) {
            switch ((Button.BooleanInputs) inputs.get(buttonName).getInputEnum()) {

                case a: return robot.gamepad1.a;
                case b: return robot.gamepad1.b;
                case x: return robot.gamepad1.x;
                case y: return robot.gamepad1.y;
                case back: return robot.gamepad1.back;
                case start: return robot.gamepad1.start;
                case guide: return robot.gamepad1.guide;
                case dpad_up: return robot.gamepad1.dpad_up;
                case dpad_down: return robot.gamepad1.dpad_down;
                case dpad_left: return robot.gamepad1.dpad_left;
                case dpad_right: return robot.gamepad1.dpad_right;
                case left_bumper: return robot.gamepad1.left_bumper;
                case right_bumper: return robot.gamepad1.right_bumper;
                case left_stick_button: return robot.gamepad1.left_stick_button;
                case right_stick_button: return robot.gamepad1.right_stick_button;
                case bool_left_trigger: return robot.gamepad1.left_trigger > inputs.get(buttonName).deadzone;
                case bool_right_trigger: return robot.gamepad1.right_trigger > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_up: return -robot.gamepad1.left_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_x_left: return robot.gamepad1.left_stick_x < inputs.get(buttonName).deadzone;
                case bool_left_stick_x_right: return robot.gamepad1.left_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_up: return -robot.gamepad1.right_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_down: return -robot.gamepad1.left_stick_y < inputs.get(buttonName).deadzone;
                case bool_right_stick_x_left: return robot.gamepad1.right_stick_x < inputs.get(buttonName).deadzone;
                case bool_right_stick_x_right: return robot.gamepad1.right_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_down: return -robot.gamepad1.right_stick_y < inputs.get(buttonName).deadzone;
                case bool_left_stick_x: return Math.abs(robot.gamepad1.left_stick_x) > inputs.get(buttonName).deadzone;
                case bool_left_stick_y: return Math.abs(robot.gamepad1.left_stick_y) > inputs.get(buttonName).deadzone;
                case bool_right_stick_x: return Math.abs(robot.gamepad1.right_stick_x) > inputs.get(buttonName).deadzone;
                case bool_right_stick_y: return Math.abs(robot.gamepad1.right_stick_y) > inputs.get(buttonName).deadzone;

                default:
                    return false;
            }
        } else {
            switch ((Button.BooleanInputs) inputs.get(buttonName).getInputEnum()) {

                case a: return robot.gamepad2.a;
                case b: return robot.gamepad2.b;
                case x: return robot.gamepad2.x;
                case y: return robot.gamepad2.y;
                case back: return robot.gamepad2.back;
                case start: return robot.gamepad2.start;
                case guide: return robot.gamepad2.guide;
                case dpad_up: return robot.gamepad2.dpad_up;
                case dpad_down: return robot.gamepad2.dpad_down;
                case dpad_left: return robot.gamepad2.dpad_left;
                case dpad_right: return robot.gamepad2.dpad_right;
                case left_bumper: return robot.gamepad2.left_bumper;
                case right_bumper: return robot.gamepad2.right_bumper;
                case left_stick_button: return robot.gamepad2.left_stick_button;
                case right_stick_button: return robot.gamepad2.right_stick_button;
                case bool_left_trigger: return robot.gamepad2.left_trigger > inputs.get(buttonName).deadzone;
                case bool_right_trigger: return robot.gamepad2.right_trigger > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_up: return -robot.gamepad2.left_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_x_left: return robot.gamepad2.left_stick_x < inputs.get(buttonName).deadzone;
                case bool_left_stick_x_right: return robot.gamepad2.left_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_up: return -robot.gamepad2.right_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_down: return -robot.gamepad2.left_stick_y < inputs.get(buttonName).deadzone;
                case bool_right_stick_x_left: return robot.gamepad2.right_stick_x < inputs.get(buttonName).deadzone;
                case bool_right_stick_x_right: return robot.gamepad2.right_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_down: return -robot.gamepad2.right_stick_y < inputs.get(buttonName).deadzone;
                case bool_left_stick_x: return Math.abs(robot.gamepad2.left_stick_x) > inputs.get(buttonName).deadzone;
                case bool_left_stick_y: return Math.abs(robot.gamepad2.left_stick_y) > inputs.get(buttonName).deadzone;
                case bool_right_stick_x: return Math.abs(robot.gamepad2.right_stick_x) > inputs.get(buttonName).deadzone;
                case bool_right_stick_y: return Math.abs(robot.gamepad2.right_stick_y) > inputs.get(buttonName).deadzone;

                default:
                    return false;


            }
        }
    }

    public boolean getBooleanInput(String buttonName, boolean defaultReturn) {
        if (inputs.get(buttonName).gamepadNumber == 1) {
            switch ((Button.BooleanInputs) inputs.get(buttonName).getInputEnum()) {

                case a: return robot.gamepad1.a;
                case b: return robot.gamepad1.b;
                case x: return robot.gamepad1.x;
                case y: return robot.gamepad1.y;
                case back: return robot.gamepad1.back;
                case start: return robot.gamepad1.start;
                case guide: return robot.gamepad1.guide;
                case dpad_up: return robot.gamepad1.dpad_up;
                case dpad_down: return robot.gamepad1.dpad_down;
                case dpad_left: return robot.gamepad1.dpad_left;
                case dpad_right: return robot.gamepad1.dpad_right;
                case left_bumper: return robot.gamepad1.left_bumper;
                case right_bumper: return robot.gamepad1.right_bumper;
                case left_stick_button: return robot.gamepad1.left_stick_button;
                case right_stick_button: return robot.gamepad1.right_stick_button;
                case bool_left_trigger: return robot.gamepad1.left_trigger > inputs.get(buttonName).deadzone;
                case bool_right_trigger: return robot.gamepad1.right_trigger > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_up: return -robot.gamepad1.left_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_x_left: return robot.gamepad1.left_stick_x < -inputs.get(buttonName).deadzone;
                case bool_left_stick_x_right: return robot.gamepad1.left_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_up: return -robot.gamepad1.right_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_down: return -robot.gamepad1.left_stick_y < -inputs.get(buttonName).deadzone;
                case bool_right_stick_x_left: return robot.gamepad1.right_stick_x < -inputs.get(buttonName).deadzone;
                case bool_right_stick_x_right: return robot.gamepad1.right_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_down: return -robot.gamepad1.right_stick_y < -inputs.get(buttonName).deadzone;
                case bool_left_stick_x: return Math.abs(robot.gamepad1.left_stick_x) > inputs.get(buttonName).deadzone;
                case bool_left_stick_y: return Math.abs(robot.gamepad1.left_stick_y) > inputs.get(buttonName).deadzone;
                case bool_right_stick_x: return Math.abs(robot.gamepad1.right_stick_x) > inputs.get(buttonName).deadzone;
                case bool_right_stick_y: return Math.abs(robot.gamepad1.right_stick_y) > inputs.get(buttonName).deadzone;

                default:
                    return defaultReturn;
            }
        } else {
            switch ((Button.BooleanInputs) inputs.get(buttonName).getInputEnum()) {

                case a: return robot.gamepad2.a;
                case b: return robot.gamepad2.b;
                case x: return robot.gamepad2.x;
                case y: return robot.gamepad2.y;
                case back: return robot.gamepad2.back;
                case start: return robot.gamepad2.start;
                case guide: return robot.gamepad2.guide;
                case dpad_up: return robot.gamepad2.dpad_up;
                case dpad_down: return robot.gamepad2.dpad_down;
                case dpad_left: return robot.gamepad2.dpad_left;
                case dpad_right: return robot.gamepad2.dpad_right;
                case left_bumper: return robot.gamepad2.left_bumper;
                case right_bumper: return robot.gamepad2.right_bumper;
                case left_stick_button: return robot.gamepad2.left_stick_button;
                case right_stick_button: return robot.gamepad2.right_stick_button;
                case bool_left_trigger: return robot.gamepad2.left_trigger > inputs.get(buttonName).deadzone;
                case bool_right_trigger: return robot.gamepad2.right_trigger > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_up: return -robot.gamepad2.left_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_x_left: return robot.gamepad2.left_stick_x < inputs.get(buttonName).deadzone;
                case bool_left_stick_x_right: return robot.gamepad2.left_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_up: return -robot.gamepad2.right_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_down: return -robot.gamepad2.left_stick_y < inputs.get(buttonName).deadzone;
                case bool_right_stick_x_left: return robot.gamepad2.right_stick_x < inputs.get(buttonName).deadzone;
                case bool_right_stick_x_right: return robot.gamepad2.right_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_down: return -robot.gamepad2.right_stick_y < inputs.get(buttonName).deadzone;
                case bool_left_stick_x: return Math.abs(robot.gamepad2.left_stick_x) > inputs.get(buttonName).deadzone;
                case bool_left_stick_y: return Math.abs(robot.gamepad2.left_stick_y) > inputs.get(buttonName).deadzone;
                case bool_right_stick_x: return Math.abs(robot.gamepad2.right_stick_x) > inputs.get(buttonName).deadzone;
                case bool_right_stick_y: return Math.abs(robot.gamepad2.right_stick_y) > inputs.get(buttonName).deadzone;

                default:
                    return defaultReturn;
            }
        }
    }

    public double getDoubleInput(String buttonName) {
        if (inputs.get(buttonName).gamepadNumber == 1) {
            switch ((Button.DoubleInputs) inputs.get(buttonName).getInputEnum()) {
                case left_stick_x: return robot.gamepad1.left_stick_x;
                case left_trigger: return robot.gamepad1.left_trigger;
                case left_stick_y: return -robot.gamepad1.left_stick_y;
                case right_stick_x: return robot.gamepad1.right_stick_x;
                case right_trigger: return robot.gamepad1.right_trigger;
                case right_stick_y: return -robot.gamepad1.right_stick_y;

                default:
                    return 0;
            }
        } else {
            switch ((Button.DoubleInputs) inputs.get(buttonName).getInputEnum()) {
                case left_stick_x: return robot.gamepad2.left_stick_x;
                case left_trigger: return robot.gamepad2.left_trigger;
                case left_stick_y: return -robot.gamepad2.left_stick_y;
                case right_stick_x: return robot.gamepad2.right_stick_x;
                case right_trigger: return robot.gamepad2.right_trigger;
                case right_stick_y: return -robot.gamepad2.right_stick_y;

                default:
                    return 0;
            }
        }
    }


    public double getDoubleInput(String buttonName, double defaultReturn) {
        if (inputs.get(buttonName).gamepadNumber == 1) {
            switch ((Button.DoubleInputs) inputs.get(buttonName).getInputEnum()) {
                case left_stick_x: return robot.gamepad1.left_stick_x;
                case left_trigger: return robot.gamepad1.left_trigger;
                case left_stick_y: return -robot.gamepad1.left_stick_y;
                case right_stick_x: return robot.gamepad1.right_stick_x;
                case right_trigger: return robot.gamepad1.right_trigger;
                case right_stick_y: return -robot.gamepad1.right_stick_y;

                default:
                    return defaultReturn;
            }
        } else {
            switch ((Button.DoubleInputs) inputs.get(buttonName).getInputEnum()) {
                case left_stick_x: return robot.gamepad2.left_stick_x;
                case left_trigger: return robot.gamepad2.left_trigger;
                case left_stick_y: return -robot.gamepad2.left_stick_y;
                case right_stick_x: return robot.gamepad2.right_stick_x;
                case right_trigger: return robot.gamepad2.right_trigger;
                case right_stick_y: return -robot.gamepad2.right_stick_y;

                default:
                    return defaultReturn;
            }
        }
    }
}

