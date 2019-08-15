package org.firstinspires.ftc.teamcode.util.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigParam {
    public String name;
    public ArrayList<String> options;
    public String defaultOption;

    public ConfigParam(String name, Button.BooleanInputs defaultOption) {
        this.name = name;
        String[] boolOptions = new String[] {"noButton","a","b","x","y","dpad_left","dpad_right","dpad_up","dpad_down","left_bumper","right_bumper","bool_left_trigger","bool_right_trigger","left_stick_button","right_stick_button","bool_left_stick_x","bool_left_stick_y","bool_right_stick_x","bool_right_stick_y","bool_left_stick_x_left","bool_left_stick_x_right","bool_left_stick_y_up","bool_left_stick_y_down","bool_right_stick_x_left","bool_right_stick_x_right","bool_right_stick_y_up","bool_right_stick_y_down","back","start","guide"};
        options = new ArrayList<>(Arrays.asList(boolOptions));
        this.defaultOption = defaultOption.name();
    }

    public ConfigParam(String name, Button.DoubleInputs defaultOption) {
        this.name = name;
        String[] doubleOptions = new String[] {"noButton","left_stick_x","left_stick_y","right_stick_x","right_stick_y","left_trigger","right_trigger"};
        options = new ArrayList<>(Arrays.asList(doubleOptions));
        this.defaultOption = defaultOption.name();
    }

    public ConfigParam(String name, ArrayList<String> options, String defaultOption) {
        this.name = name;
        this.options = options;
        this.defaultOption = defaultOption;
    }

    public ConfigParam(String name, String[] options, String defaultOption) {
        this.name = name;
        this.options = new ArrayList<>(Arrays.asList(options));
        this.defaultOption = defaultOption;
    }

}
