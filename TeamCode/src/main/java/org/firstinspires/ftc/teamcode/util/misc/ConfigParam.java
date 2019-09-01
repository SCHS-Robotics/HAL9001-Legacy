/*
 * Filename: ConfigParam.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/13/19
 */

package org.firstinspires.ftc.teamcode.util.misc;

import android.util.Pair;

import org.firstinspires.ftc.teamcode.util.exceptions.NotAnAlchemistException;
import org.firstinspires.ftc.teamcode.util.exceptions.NotARealGamepadException;
import org.opencv.core.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for storing configuration options.
 */
public class ConfigParam {

    public static Map<String, Object> booleanMap = new HashMap<String,Object>() {{
        put("true",true);
        put("false",false);
    }};

    //The name of the option
    public String name;
    //The option's default value.
    public String defaultOption;
    //The current value of the option.
    public String currentOption;
    //A list of possible values the option can take on.
    public List<String> options;
    //A list of values used for conversion when pulling inputs.
    public List<Object> vals;
    //The option's default gamepad to use.
    public String defaultGamepadOption;
    //The gamepad that the option is currently using.
    public String currentGamepadOption;
    //The list of possible gamepads for the option to use.
    public List<String> gamepadOptions;
    //A boolean value specifying if the option uses a gamepad.
    public boolean usesGamepad;

    //A boolean value specifying if the option is a boolean button on a gamepad.
    private boolean isBoolButton;
    private boolean isDoubleButton;

    /**
     * Constructor for ConfigParam that provides default settings for a boolean button on the gamepad.
     *
     * @param name - The name of the option.
     * @param defaultOption - The default value of the option.
     */
    public ConfigParam(String name, Button.BooleanInputs defaultOption) {
        this.name = name;
        String[] boolOptions = new String[] {"noButton","a","b","x","y","dpad_left","dpad_right","dpad_up","dpad_down","left_bumper","right_bumper","bool_left_trigger","bool_right_trigger","left_stick_button","right_stick_button","bool_left_stick_x","bool_left_stick_y","bool_right_stick_x","bool_right_stick_y","bool_left_stick_x_left","bool_left_stick_x_right","bool_left_stick_y_up","bool_left_stick_y_down","bool_right_stick_x_left","bool_right_stick_x_right","bool_right_stick_y_up","bool_right_stick_y_down","back","start","guide"};
        options = new ArrayList<>(Arrays.asList(boolOptions));
        this.defaultOption = defaultOption.name();
        currentOption = this.defaultOption;

        vals = new ArrayList<>();

        isBoolButton = true;
        isDoubleButton = false;

        usesGamepad = true;

        String[] gamepadOpts = new String[] {"Gamepad 1", "Gamepad 2"};
        gamepadOptions = new ArrayList<>(Arrays.asList(gamepadOpts));
        defaultGamepadOption = "Gamepad 1";
        currentGamepadOption = this.defaultGamepadOption;
    }

    /**
     * Constructor for ConfigParam that provides default settings for a boolean button on the gamepad and asks for a default gamepad setting.
     *
     * @param name - The name of the option.
     * @param defaultOption - The default value of the option.
     * @param gamepadDefault - The default gamepad value.
     */
    public ConfigParam(String name, Button.BooleanInputs defaultOption, int gamepadDefault) {
        this.name = name;
        String[] boolOptions = new String[] {"noButton","a","b","x","y","dpad_left","dpad_right","dpad_up","dpad_down","left_bumper","right_bumper","bool_left_trigger","bool_right_trigger","left_stick_button","right_stick_button","bool_left_stick_x","bool_left_stick_y","bool_right_stick_x","bool_right_stick_y","bool_left_stick_x_left","bool_left_stick_x_right","bool_left_stick_y_up","bool_left_stick_y_down","bool_right_stick_x_left","bool_right_stick_x_right","bool_right_stick_y_up","bool_right_stick_y_down","back","start","guide"};
        options = new ArrayList<>(Arrays.asList(boolOptions));
        this.defaultOption = defaultOption.name();
        currentOption = this.defaultOption;

        vals = new ArrayList<>();

        isBoolButton = true;
        isDoubleButton = false;

        usesGamepad = true;

        if(gamepadDefault != 1 && gamepadDefault != 2) {
            throw new NotARealGamepadException("Unless you are violating FTC rules, that isn't a real gamepad number!");
        }

        String[] gamepadOpts = new String[] {"Gamepad 1", "Gamepad 2"};
        gamepadOptions = new ArrayList<>(Arrays.asList(gamepadOpts));
        defaultGamepadOption = "Gamepad " + gamepadDefault;
        currentGamepadOption = this.defaultGamepadOption;
    }

    /**
     * Constructor for ConfigParam that provides default settings for a double button on the gamepad.
     *
     * @param name - The name of the option.
     * @param defaultOption - The default value of the option.
     */
    public ConfigParam(String name, Button.DoubleInputs defaultOption) {
        this.name = name;
        String[] doubleOptions = new String[] {"noButton","left_stick_x","left_stick_y","right_stick_x","right_stick_y","left_trigger","right_trigger"};
        options = new ArrayList<>(Arrays.asList(doubleOptions));
        this.defaultOption = defaultOption.name();
        currentOption = this.defaultOption;

        vals = new ArrayList<>();

        isBoolButton = false;
        isDoubleButton = true;

        usesGamepad = true;

        String[] gamepadOpts = new String[] {"Gamepad 1", "Gamepad 2"};
        gamepadOptions = new ArrayList<>(Arrays.asList(gamepadOpts));
        defaultGamepadOption = "Gamepad 1";
        currentGamepadOption = this.defaultGamepadOption;
    }

    /**
     * Constructor for ConfigParam that provides default settings for a double button on the gamepad and asks for a default gamepad setting.
     *
     * @param name - The name of the option.
     * @param defaultOption - The default value of the option.
     * @param gamepadDefault - The default gamepad value.
     */
    public ConfigParam(String name, Button.DoubleInputs defaultOption, int gamepadDefault) {
        this.name = name;
        String[] doubleOptions = new String[] {"noButton","left_stick_x","left_stick_y","right_stick_x","right_stick_y","left_trigger","right_trigger"};
        options = new ArrayList<>(Arrays.asList(doubleOptions));
        this.defaultOption = defaultOption.name();
        currentOption = this.defaultOption;

        vals = new ArrayList<>();

        isBoolButton = false;
        isDoubleButton = true;

        usesGamepad = true;

        if(gamepadDefault != 1 && gamepadDefault != 2) {
            throw new NotARealGamepadException("Unless you are violating FTC rules, that isn't a real gamepad number!");
        }

        String[] gamepadOpts = new String[] {"Gamepad 1", "Gamepad 2"};
        gamepadOptions = new ArrayList<>(Arrays.asList(gamepadOpts));
        defaultGamepadOption = "Gamepad " + gamepadDefault;
        currentGamepadOption = this.defaultGamepadOption;
    }

    public ConfigParam(String name, Button.VectorInputs defaultOption) {
        this.name = name;
        String[] doubleOptions = new String[] {"noButton","left_stick", "right_stick"};
        options = new ArrayList<>(Arrays.asList(doubleOptions));
        this.defaultOption = defaultOption.name();
        currentOption = this.defaultOption;

        vals = new ArrayList<>();

        isBoolButton = false;
        isDoubleButton = false;

        usesGamepad = true;

        String[] gamepadOpts = new String[] {"Gamepad 1", "Gamepad 2"};
        gamepadOptions = new ArrayList<>(Arrays.asList(gamepadOpts));
        defaultGamepadOption = "Gamepad 1";
        currentGamepadOption = this.defaultGamepadOption;
    }

    public ConfigParam(String name, Button.VectorInputs defaultOption, int gamepadDefault) {
        this.name = name;
        String[] doubleOptions = new String[] {"noButton","left_stick", "right_stick"};
        options = new ArrayList<>(Arrays.asList(doubleOptions));
        this.defaultOption = defaultOption.name();
        currentOption = this.defaultOption;

        vals = new ArrayList<>();

        isBoolButton = false;
        isDoubleButton = false;

        usesGamepad = true;

        String[] gamepadOpts = new String[] {"Gamepad 1", "Gamepad 2"};
        gamepadOptions = new ArrayList<>(Arrays.asList(gamepadOpts));
        defaultGamepadOption = "Gamepad " + gamepadDefault;
        currentGamepadOption = this.defaultGamepadOption;
    }

    /**
     * Constructor for ConfigParam for non-gamepad options.
     *
     * @param name - The name of the option.
     * @param options - An ArrayList of all possible values the option could take on.
     * @param defaultOption - The option's default value.
     */
    public ConfigParam(String name, ArrayList<String> options, String defaultOption) {
        this.name = name;
        this.options = options;
        this.defaultOption = defaultOption;
        currentOption = this.defaultOption;

        vals = new ArrayList<>(options);

        isBoolButton = false;
        isDoubleButton = false;

        usesGamepad = false;
    }

    /**
     * Constructor for ConfigParam for non-gamepad options.
     *
     * @param name - The name of the option.
     * @param options - An array of all possible values the option could take on.
     * @param defaultOption - The option's default value.
     */
    public ConfigParam(String name, String[] options, String defaultOption) {
        this.name = name;
        this.options = new ArrayList<>(Arrays.asList(options));
        this.defaultOption = defaultOption;
        currentOption = this.defaultOption;

        vals = new ArrayList<>(Arrays.asList(options));

        isBoolButton = false;
        isDoubleButton = false;

        usesGamepad = false;
    }

    public ConfigParam(String name, Map<String,Object> map, String defaultOption) {
        this.name = name;

        this.options = new ArrayList<>(map.keySet());
        this.defaultOption = defaultOption;
        currentOption = this.defaultOption;

        vals = new ArrayList<>(map.values());

        isBoolButton = false;
        isDoubleButton = false;

        usesGamepad = false;
    }

    public ConfigParam(String name, Map<String,Object> map, Object defaultOption) {
        this.name = name;

        this.options = new ArrayList<>(map.keySet());
        this.defaultOption = defaultOption.toString();
        currentOption = this.defaultOption;

        vals = new ArrayList<>(map.values());

        isBoolButton = false;
        isDoubleButton = false;

        usesGamepad = false;
    }

    /**
     * A private constructor for ConfigParam that is only used for cloning.
     *
     * @param name - The name of the option.
     * @param options - The list of all possible values that the option could take on.
     * @param vals - The list of all possible values the option could take on, but in actual object form instead of string form.
     * @param defaultOption - The option's default value.
     * @param currentOption - The option's current value.
     * @param gamepadOptions - The list of all possible gamepad values.
     * @param defaultGamepadOption - The option's default gamepad value.
     * @param currentGamepadOption - The option's current gamepad value.
     * @param usesGamepad - Whether or not the option uses the gamepad.
     * @param isBoolButton - Whether or not the option is a boolean button on the gamepad.
     */
    private ConfigParam(String name, List<String> options, List<Object> vals, String defaultOption, String currentOption, List<String> gamepadOptions, String defaultGamepadOption, String currentGamepadOption, boolean usesGamepad, boolean isBoolButton, boolean isDoubleButton) {
        this.name = name;
        this.options = options;
        this.vals = vals;
        this.defaultOption = defaultOption;
        this.currentOption = currentOption;
        this.gamepadOptions = gamepadOptions;
        this.defaultGamepadOption = defaultGamepadOption;
        this.currentGamepadOption = currentGamepadOption;
        this.usesGamepad = usesGamepad;
        this.isBoolButton = isBoolButton;
        this.isDoubleButton = isDoubleButton;
    }

    /**
     * Converts the option to a button object if possible.
     *
     * @return - The button representation of the option.
     * @throws NotAnAlchemistException - Throws this exception if it is not possible to convert the option into a button.
     */
    public Button toButton() {
        if(usesGamepad) {
            if(isBoolButton) {
                return new Button(currentGamepadOption.equals("Gamepad 1") ? 1 : currentGamepadOption.equals("Gamepad 2") ? 2 : -1, Button.BooleanInputs.valueOf(currentOption));
            }
            else if(isDoubleButton){
                return new Button(currentGamepadOption.equals("Gamepad 1") ? 1 : currentGamepadOption.equals("Gamepad 2") ? 2 : -1, Button.DoubleInputs.valueOf(currentOption));
            }
            else {
                return new Button(currentGamepadOption.equals("Gamepad 1") ? 1 : currentGamepadOption.equals("Gamepad 2") ? 2 : -1, Button.VectorInputs.valueOf(currentOption));
            }
        }
        else {
           throw new NotAnAlchemistException("I'm sorry, but I can't do that. This variable isn't a real button on the gamepad.");
        }
    }

    public static Map<String,Object> numberMap(double start, double end, double increment) {
        Map<String,Object> numMap = new HashMap<>();
        for (double i = start; i < end ; i+= increment) {
            numMap.put(Double.toString(i), i);
        }
        numMap.put(Double.toString(end),end);
        return numMap;
    }

    @Override
    public String toString() {
        return usesGamepad ? name+':'+currentOption+':'+currentGamepadOption : name+':'+currentOption;
    }

    @Override
    public ConfigParam clone() {
        return new ConfigParam(name,options,vals,defaultOption,currentOption,gamepadOptions,defaultGamepadOption,currentGamepadOption,usesGamepad,isBoolButton,isDoubleButton);
    }
}