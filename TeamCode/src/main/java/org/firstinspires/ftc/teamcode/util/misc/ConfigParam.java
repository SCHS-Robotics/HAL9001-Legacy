package org.firstinspires.ftc.teamcode.util.misc;

import android.util.Log;

import org.firstinspires.ftc.teamcode.util.exceptions.NotAGodException;
import org.firstinspires.ftc.teamcode.util.exceptions.NotARealGamepadException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigParam {
    public String name;
    public String currentOption;
    public List<String> options;
    public String defaultOption;
    public String defaultGamepadOption;
    public String currentGamepadOption;
    public List<String> gamepadOptions;
    public boolean usesGamepad;

    private boolean isBoolButton;

    public ConfigParam(String name, Button.BooleanInputs defaultOption) {
        this.name = name;
        String[] boolOptions = new String[] {"noButton","a","b","x","y","dpad_left","dpad_right","dpad_up","dpad_down","left_bumper","right_bumper","bool_left_trigger","bool_right_trigger","left_stick_button","right_stick_button","bool_left_stick_x","bool_left_stick_y","bool_right_stick_x","bool_right_stick_y","bool_left_stick_x_left","bool_left_stick_x_right","bool_left_stick_y_up","bool_left_stick_y_down","bool_right_stick_x_left","bool_right_stick_x_right","bool_right_stick_y_up","bool_right_stick_y_down","back","start","guide"};
        options = new ArrayList<>(Arrays.asList(boolOptions));
        this.defaultOption = defaultOption.name();
        currentOption = this.defaultOption;

        isBoolButton = true;

        usesGamepad = true;

        String[] gamepadOpts = new String[] {"Gamepad 1", "Gamepad 2"};
        gamepadOptions = new ArrayList<>(Arrays.asList(gamepadOpts));
        defaultGamepadOption = "Gamepad 1";
        currentGamepadOption = this.defaultGamepadOption;
    }

    public ConfigParam(String name, Button.BooleanInputs defaultOption, int gamepadDefault) {
        this.name = name;
        String[] boolOptions = new String[] {"noButton","a","b","x","y","dpad_left","dpad_right","dpad_up","dpad_down","left_bumper","right_bumper","bool_left_trigger","bool_right_trigger","left_stick_button","right_stick_button","bool_left_stick_x","bool_left_stick_y","bool_right_stick_x","bool_right_stick_y","bool_left_stick_x_left","bool_left_stick_x_right","bool_left_stick_y_up","bool_left_stick_y_down","bool_right_stick_x_left","bool_right_stick_x_right","bool_right_stick_y_up","bool_right_stick_y_down","back","start","guide"};
        options = new ArrayList<>(Arrays.asList(boolOptions));
        this.defaultOption = defaultOption.name();
        currentOption = this.defaultOption;

        isBoolButton = true;

        usesGamepad = true;

        if(gamepadDefault != 1 && gamepadDefault != 2) {
            throw new NotARealGamepadException("Unless you are violating FTC rules, that isn't a real gamepad number!");
        }

        String[] gamepadOpts = new String[] {"Gamepad 1", "Gamepad 2"};
        gamepadOptions = new ArrayList<>(Arrays.asList(gamepadOpts));
        defaultGamepadOption = "Gamepad " + gamepadDefault;
        currentGamepadOption = this.defaultGamepadOption;
    }

    public ConfigParam(String name, Button.DoubleInputs defaultOption) {
        this.name = name;
        String[] doubleOptions = new String[] {"noButton","left_stick_x","left_stick_y","right_stick_x","right_stick_y","left_trigger","right_trigger"};
        options = new ArrayList<>(Arrays.asList(doubleOptions));
        this.defaultOption = defaultOption.name();
        currentOption = this.defaultOption;

        isBoolButton = false;

        usesGamepad = true;

        String[] gamepadOpts = new String[] {"Gamepad 1", "Gamepad 2"};
        gamepadOptions = new ArrayList<>(Arrays.asList(gamepadOpts));
        defaultGamepadOption = "Gamepad 1";
        currentGamepadOption = this.defaultGamepadOption;
    }

    public ConfigParam(String name, Button.DoubleInputs defaultOption, int gamepadDefault) {
        this.name = name;
        String[] doubleOptions = new String[] {"noButton","left_stick_x","left_stick_y","right_stick_x","right_stick_y","left_trigger","right_trigger"};
        options = new ArrayList<>(Arrays.asList(doubleOptions));
        this.defaultOption = defaultOption.name();
        currentOption = this.defaultOption;

        isBoolButton = false;

        usesGamepad = true;

        if(gamepadDefault != 1 && gamepadDefault != 2) {
            throw new NotARealGamepadException("Unless you are violating FTC rules, that isn't a real gamepad number!");
        }

        String[] gamepadOpts = new String[] {"Gamepad 1", "Gamepad 2"};
        gamepadOptions = new ArrayList<>(Arrays.asList(gamepadOpts));
        defaultGamepadOption = "Gamepad 1";
        currentGamepadOption = this.defaultGamepadOption;
    }

    public ConfigParam(String name, ArrayList<String> options, String defaultOption) {
        this.name = name;
        this.options = options;
        this.defaultOption = defaultOption;
        currentOption = this.defaultOption;

        isBoolButton = false;

        usesGamepad = false;
    }

    public ConfigParam(String name, String[] options, String defaultOption) {
        this.name = name;
        this.options = new ArrayList<>(Arrays.asList(options));
        this.defaultOption = defaultOption;
        currentOption = this.defaultOption;

        isBoolButton = false;

        usesGamepad = false;
    }

    private ConfigParam(String name, List<String> options, String defaultOption, String currentOption, List<String> gamepadOptions, String defaultGamepadOption, String currentGamepadOption, boolean usesGamepad, boolean isBoolButton) {
        this.name = name;
        this.options = options;
        this.defaultOption = defaultOption;
        this.currentOption = currentOption;
        this.gamepadOptions = gamepadOptions;
        this.defaultGamepadOption = defaultGamepadOption;
        this.currentGamepadOption = currentGamepadOption;
        this.usesGamepad = usesGamepad;
        this.isBoolButton = isBoolButton;
    }

    public Button toButton() {
        if(usesGamepad) {
            if(isBoolButton) {
                return new Button(currentGamepadOption.equals("Gamepad 1") ? 1 : currentGamepadOption.equals("Gamepad 2") ? 2 : -1, Button.BooleanInputs.valueOf(currentOption));
            }
            else {
                return new Button(currentGamepadOption.equals("Gamepad 1") ? 1 : currentGamepadOption.equals("Gamepad 2") ? 2 : -1, Button.DoubleInputs.valueOf(currentOption));
            }
        }
        else {
           throw new NotAGodException("I'm sorry dave, but I can't do that. This variable isn't a real button on the gamepad.");
        }
    }

    @Override
    public String toString() {
        return usesGamepad ? name+':'+currentOption+':'+currentGamepadOption : name+':'+currentOption;
    }

    @Override
    public ConfigParam clone() {
        return new ConfigParam(name,options,defaultOption,currentOption,gamepadOptions,defaultGamepadOption,currentGamepadOption,usesGamepad,isBoolButton);
    }
}
