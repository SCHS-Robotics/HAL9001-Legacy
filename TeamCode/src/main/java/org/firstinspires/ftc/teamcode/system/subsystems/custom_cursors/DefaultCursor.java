/*
 * Filename: DefaultCursor.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.system.subsystems.custom_cursors;

import org.firstinspires.ftc.teamcode.system.source.Cursor;
import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.util.exceptions.NotBooleanInputException;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;

/**
 * A default cursor object with normal movement and selection operations.
 */
public class DefaultCursor extends Cursor {

    //The customizeable set of inputs used to control the cursor.
    private CustomizableGamepad inputs;
    //The names of the controls that are used to interact with the cursor.
    private final String UP = "up", DOWN = "down", LEFT = "left", RIGHT = "right", SELECT = "select";

    //A boolean value used to toggle the controls on and off.
    private boolean flag = true;

    /**
     * Ctor for default cursor.
     *
     * @param robot - The robot the cursor is associataed with.
     * @param x - The cursor's initial x coordinate.
     * @param y - The cursor's initial y coordinate.
     * @param blinkSpeedMs - The cursor's blink speed in milliseconds.
     * @param cursorIcon - The icon used to represent the cursor.
     */
    public DefaultCursor(Robot robot, int x, int y, int blinkSpeedMs, char cursorIcon) {
        super(x, y, blinkSpeedMs, cursorIcon);
        inputs = new CustomizableGamepad(robot);

        inputs.addButton(UP,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(DOWN,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(LEFT,new Button(1, Button.BooleanInputs.dpad_left));
        inputs.addButton(RIGHT,new Button(1, Button.BooleanInputs.dpad_right));
        inputs.addButton(SELECT, new Button(1, Button.BooleanInputs.a));
    }

    /**
     * Ctor for default cursor.
     *
     * @param robot - The robot the cursor is associataed with.
     * @param blinkSpeedMs - The cursor's blink speed in milliseconds.
     * @param cursorIcon - The icon used to represent the cursor.
     */
    public DefaultCursor(Robot robot, int blinkSpeedMs, char cursorIcon) {
        super(blinkSpeedMs, cursorIcon);
        inputs = new CustomizableGamepad(robot);

        inputs.addButton(UP,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(DOWN,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(LEFT,new Button(1, Button.BooleanInputs.dpad_left));
        inputs.addButton(RIGHT,new Button(1, Button.BooleanInputs.dpad_right));
        inputs.addButton(SELECT, new Button(1, Button.BooleanInputs.a));
    }

    /**
     * Ctor for default cursor.
     *
     * @param robot - The robot the cursor is associataed with.
     * @param blinkSpeedMs - The cursor's blink speed in milliseconds.
     */
    public DefaultCursor(Robot robot, int blinkSpeedMs) {
        super(blinkSpeedMs);
        inputs = new CustomizableGamepad(robot);

        inputs.addButton(UP,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(DOWN,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(LEFT,new Button(1, Button.BooleanInputs.dpad_left));
        inputs.addButton(RIGHT,new Button(1, Button.BooleanInputs.dpad_right));
        inputs.addButton(SELECT, new Button(1, Button.BooleanInputs.a));
    }

    /**
     * Ctor for default cursor.
     *
     * @param robot - The robot the cursor is associataed with.
     * @param x - The cursor's initial x coordinate.
     * @param y = The cursor's initial y coordinate.
     * @param blinkSpeedMs - The cursor's blink speed in milliseconds.
     */
    public DefaultCursor(Robot robot, int x, int y, int blinkSpeedMs) {
        super(x, y, blinkSpeedMs);
        inputs = new CustomizableGamepad(robot);

        inputs.addButton(UP,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(DOWN,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(LEFT,new Button(1, Button.BooleanInputs.dpad_left));
        inputs.addButton(RIGHT,new Button(1, Button.BooleanInputs.dpad_right));
        inputs.addButton(SELECT, new Button(1, Button.BooleanInputs.a));
    }

    /**
     * Sets which buttons will be used to control the cursor.
     *
     * @param up - The up button.
     * @param down - The down button.
     * @param left - The left button.
     * @param right - The right button.
     * @param select - The select button
     *
     * @throws NotBooleanInputException - Throws an exception if button does not return boolean values.
     */
    public void setInputs(Button up, Button down, Button left, Button right, Button select){
        if(up.isBoolean && down.isBoolean && left.isBoolean && right.isBoolean && select.isBoolean) {
            inputs.addButton(UP, up);
            inputs.addButton(DOWN, down);
            inputs.addButton(LEFT, left);
            inputs.addButton(RIGHT, right);
            inputs.addButton(SELECT, select);
        }
        else{
            throw new NotBooleanInputException("DefaultCursor requires all boolean inputs");
        }
    }

    @Override
    public void update() {
        if(inputs.getBooleanInput(SELECT) && flag){
            super.currentMenu.onSelect();
            flag = false;
        }
        else if(inputs.getBooleanInput(UP) && y+1 <= currentMenu.getSelectionZoneHeight() && flag){
            y++;
            flag = false;
        }
        else if(inputs.getBooleanInput(DOWN) && y-1 >= 0 && flag){
            y--;
            flag = false;
        }
        else if(inputs.getBooleanInput(LEFT) && x-1 >= 0 && flag){
            x--;
            flag = false;
        }
        else if(inputs.getBooleanInput(RIGHT) && x+1 <= currentMenu.getSelectionZoneWidth() && flag){
            x++;
            flag = false;
        }
        else if(!inputs.getBooleanInput(SELECT) && !inputs.getBooleanInput(UP) && !inputs.getBooleanInput(DOWN) && !inputs.getBooleanInput(LEFT) && !inputs.getBooleanInput(RIGHT) && !flag){
            flag = true;
        }
    }
}
