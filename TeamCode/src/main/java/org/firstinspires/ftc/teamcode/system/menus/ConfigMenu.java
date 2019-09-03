/*
 * Filename: ConfigMenu.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/17/19
 */

package org.firstinspires.ftc.teamcode.system.menus;

import android.util.Log;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.GUI.GUI;
import org.firstinspires.ftc.teamcode.system.source.GUI.GuiLine;
import org.firstinspires.ftc.teamcode.system.source.GUI.ScrollingListMenu;
import org.firstinspires.ftc.teamcode.system.subsystems.cursors.ConfigCursor;
import org.firstinspires.ftc.teamcode.util.functional_interfaces.BiFunction;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A menu class used for configuring robots.
 */
public class ConfigMenu extends ScrollingListMenu {

    /*
    Internal state of the menu. Controls how it will react when a button is pressed.
    Menu State Key:

    ROOT_DIR: The menu is currently in the root directory of the config (either the teleop or autonomous directory). Has options to edit, delete, or make a new config. Can also select a config to run.
    DELETE_CONFIG: Select a config from a list to delete. Will not transition to this state if there are no configs.
    CHOOSE_EDIT_CONFIG: Select a config from a list of configs to edit. Will not transition to this state if there are no configs.
    NEW_CONFIG: Create a new config. Type (as best you can) a name into the upper bar, then press done to submit the name. If name is robot_info or "" it will transition back to ROOT_DIR without doing anything.
    SELECT_SUBSYSTEM: Select a subsystem to configure from a list of subsystems.
    EDIT_CONFIG: Cycle through all config options. Press select Done when you have chosen desired options.
    DONE: Configuration is complete, clear menu screen and mark as done so the GUI can close the menu.
    AUTO_RUN: Program being run is not standalone and has a config pre-loaded. Do not allow the user to change the config unless they press the center button to confirm they want to.
     */
    private enum MenuState {
        ROOT_DIR, DELETE_CONFIG, CHOOSE_EDIT_CONFIG, SELECT_SUBSYSTEM, NEW_CONFIG, CONFIGURE_SUBSYSTEM, DONE, AUTO_RUN
    }
    private MenuState menuState;

    //Internally tracks whether autonomous or teleop is being configured.
    private enum ConfigurationState {
        AUTONOMOUS, TELEOP
    }
    private ConfigurationState configState;

    //A string containing all characters supported by the configuration naming program.
    private static final String SUPPORTED_CHARS = "#abcdefghijklmnopqrstuvwxyz0123456789";
    //List representation of SUPPORTED_CHARS
    private static final List<Character> VALID_CHARS = getValidChars();
    //Various filepaths that the menu needs to internally keep track of. currentFilepath and selectedConfigPath change throughout the program.
    private String currentFilepath, selectedConfigPath, robotFolder;
    //An internal variable that stores the name of the currently selected subsystem while the user is configuring that subsystem.
    private String selectedSubsystemName;
    //The current configuration. It maps the name of the subsystem to a list of ConfigParams representing the current config settings of that subsystem.
    private Map<String,List<ConfigParam>> config;
    //A boolean value tracking whether a new config file was recently created. Used for back button functionality while creating new configs.
    private boolean createdNewConfig = false;
    //The GuiLine containing the name of the config file being created by the menu. Used for back button functionality while creating new configs.
    private GuiLine nameLine;
    //A custom implementation of the modulo function where negative numbers wrap around to m-1.
    private BiFunction<Integer,Integer,Integer> customMod = (Integer x, Integer m) -> (x % m + m) % m;
    //A boolean to track if the menu is being run in standalone mode.
    private boolean standAloneMode;
    //A boolean value tracking whether the menu is finished configuring.
    public boolean isDone = false;

    /**
     * A constructor for ConfigMenu.
     *
     * @param gui - The GUI being used to render the menu.
     * @param filePath - The filepath where the menu is to start.
     * @param standAloneMode - Whether or not the menu is being run in standAloneMode
     */
    public ConfigMenu(GUI gui, String filePath, boolean standAloneMode) {

        /*
           If the program is being run in standalone mode, generate the initial lines from the given filepath.
           If it is not being run in standalone mode and is being run from an autonomous program generate the initial lines
           from the autonomous folder filepath. If the situation is the same, except it is being run from a teleop program,
           generate the initial lines from the teleop folder filepath.
        */
        super(gui, new ConfigCursor(gui.robot,500), genInitialLines(standAloneMode ? filePath : gui.robot.isAutonomous() ? filePath + "/autonomous" : filePath + "/teleop"),1,genInitialLines(standAloneMode ? filePath : gui.robot.isAutonomous() ? filePath + "/autonomous" : filePath + "/teleop").size());

        menuState = MenuState.ROOT_DIR;
        configState = gui.robot.isAutonomous() ? ConfigurationState.AUTONOMOUS : ConfigurationState.TELEOP;
        config = new HashMap<>();

        //Is in standalone mode.
        if(standAloneMode) {
            currentFilepath = filePath;
        }
        //Is not in standalone mode and is being run from autonomous.
        else if(configState == ConfigurationState.AUTONOMOUS) {
            robotFolder = filePath;
            currentFilepath = robotFolder + "/autonomous";
        }
        //Is not in standalone mode and is being run from teleop.
        else {
            robotFolder = filePath;
            currentFilepath = robotFolder + "/teleop";
            String autorunFilepath = getAutorunFilepath(currentFilepath);

            if(!autorunFilepath.equals("")) { //If the robot_info file in the teleop folder is not blank.
                File autorun = new File(autorunFilepath);

                //If the file to autorun actually exists.
                if(autorun.exists()) {
                    menuState = MenuState.AUTO_RUN;

                    String autorunName = filepath2ConfigName(autorunFilepath);
                    String message = "Auto using config " + autorunName + ".\r\nPress center button to change.";
                    cursor.setDoBlink(false);

                    exportConfigFile(autorunFilepath);

                    super.setSelectionZoneWidthAndHeight(message.length(),1,new GuiLine[]{new GuiLine(message,"","")});
                }
                //If the file to autorun does not exist, reset the robot_info file to being blank.
                else {
                    writeData(currentFilepath+"/robot_info.txt","");
                }
            }
        }

        this.standAloneMode = standAloneMode;
    }

    @Override
    public void render() {}

    @Override
    public void initLoopRender() {displayCurrentMenu();}

    @Override
    public void onSelect() {}

    @Override
    public void onButton(String name, Button button) {

        switch (menuState) {

            //Not in standalone mode. Wait for disable autorun button to be pressed before transitioning to another state.
            case AUTO_RUN:
                if(name.equals(ConfigCursor.DISABLE_AUTORUN)) {
                    menuState = MenuState.ROOT_DIR;

                    cursor.setDoBlink(true);

                    resetCursorPos();
                    setRootDirLines();
                }
                break;

            //Showing all config files in the directory and new/edit/delete options.
            case ROOT_DIR:
                if(name.equals(ConfigCursor.SELECT)) {
                    //If delete config or edit config is pressed and there are not 0 config files, transition to either choose_edit_config or delete config.
                    if ((lines.get(cursor.y).postSelectionText.equals("Delete Config") || lines.get(cursor.y).postSelectionText.equals("Edit Config")) && genConfigLines(currentFilepath).size() > 0) {
                        menuState = lines.get(cursor.y).postSelectionText.equals("Delete Config") ? MenuState.DELETE_CONFIG : MenuState.CHOOSE_EDIT_CONFIG;

                        genDefaultConfigMap();
                        resetCursorPos();
                        setConfigListLines();
                    }
                    //If new config is pressed, transition to new_config.
                    else if (lines.get(cursor.y).postSelectionText.equals("New Config")) {
                        menuState = MenuState.NEW_CONFIG;

                        genDefaultConfigMap();
                        resetCursorPos();
                        setNewConfigLines();
                    }
                    //If in standalone mode and a config is selected (must be a config because is not new/edit/delete options and some configs exist) transition to done and export the config.
                    else if(standAloneMode && genConfigLines(currentFilepath).size() > 0) {
                        menuState = MenuState.DONE;

                        exportConfigFile(currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt");

                        cursor.setDoBlink(false);

                        displayNothing();

                        isDone = true;
                    }
                    //If not in standalone mode, running autonomous config, and a config is selected, export that config, then transition back to root_dir and switch to teleop configuration.
                    else if(configState == ConfigurationState.AUTONOMOUS && genConfigLines(currentFilepath).size() > 0){

                        exportConfigFile(currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt");

                        configState = ConfigurationState.TELEOP;
                        genDefaultConfigMap();

                        currentFilepath = robotFolder + "/teleop";

                        resetCursorPos();
                        setRootDirLines();
                    }
                    //If not in standalone mode, running teleop config, and a config is selected, export that config, then write the location of that config file to robot_info.txt in the teleop folder and transition to done.
                    else if(configState == ConfigurationState.TELEOP && genConfigLines(currentFilepath).size() > 0) {
                        menuState = MenuState.DONE;

                        exportConfigFile(currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt");
                        writeData(robotFolder+"/teleop/robot_info.txt",currentFilepath+'/'+lines.get(cursor.y).postSelectionText+".txt");

                        cursor.setDoBlink(false);
                        super.setSelectionZoneHeight(1,new GuiLine[]{new GuiLine(" ","","")});

                        isDone = true;
                    }
                }
                break;

            //Delete option selected. Selected config will be deleted. Currently showing list of all configs
            case DELETE_CONFIG:
                if(name.equals(ConfigCursor.SELECT)) {
                    menuState = MenuState.ROOT_DIR;

                    String configPath = currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt";
                    File configFile = new File(configPath);

                    if (!configFile.delete()) {
                        Log.e("File Issues", "Problem deleting file at " + configPath);
                    }

                    resetCursorPos();
                    setRootDirLines();
                }
                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.ROOT_DIR;

                    resetCursorPos();
                    setRootDirLines();
                }
                break;

            //New config option selected. will prompt user to enter a name for the config file.
            case NEW_CONFIG:
                if(name.equals(ConfigCursor.SELECT)) {
                    //If currently entering a name, cycle the character forward one. The modulo function wraps around so when the last char is reached it goes back to the beginning.
                    if (!lines.get(cursor.y).postSelectionText.equals("Done")) {
                        ((ConfigCursor) cursor).setWriteMode(true);
                        char[] currentNameText = lines.get(0).selectionZoneText.toCharArray();
                        currentNameText[cursor.x] = VALID_CHARS.get((VALID_CHARS.indexOf(currentNameText[cursor.x]) + 1) % VALID_CHARS.size());

                        setNewConfigLines(new GuiLine(new String(currentNameText), "Config Name"));
                    }
                    //If done writing the name, transition to select_subsystem and set the selected config filepath.
                    else {
                        ((ConfigCursor) cursor).setWriteMode(false);
                        String newConfigName = parseName(lines.get(0).selectionZoneText);

                        if (!newConfigName.equals("") && !newConfigName.equals("robot_info")) {
                            createdNewConfig = true;
                            nameLine = lines.get(0);

                            selectedConfigPath = currentFilepath + '/' + newConfigName + ".txt";

                            menuState = MenuState.SELECT_SUBSYSTEM;

                            resetCursorPos();
                            setSelectSubsystemLines();
                        }
                        else {
                            menuState = MenuState.ROOT_DIR;
                            Log.e("Oh No", "Hacker Alert!");
                            resetCursorPos();
                            setRootDirLines();
                        }
                    }
                }
                //If reverse_select is pressed and the user is entering the name cycle the characters backward. Note the use of custom mod.
                else if(name.equals(ConfigCursor.REVERSE_SELECT) && !lines.get(cursor.y).postSelectionText.equals("Done")) {
                    ((ConfigCursor) cursor).setWriteMode(true);
                    char[] currentNameText = lines.get(0).selectionZoneText.toCharArray();
                    currentNameText[cursor.x] = VALID_CHARS.get(customMod.apply((VALID_CHARS.indexOf(currentNameText[cursor.x]) - 1),VALID_CHARS.size()));

                    setNewConfigLines(new GuiLine(new String(currentNameText), "Config Name"));
                }
                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.ROOT_DIR;

                    resetCursorPos();
                    setRootDirLines();
                }
                break;

            //Edit option selected. Selected config will be edited.
            case CHOOSE_EDIT_CONFIG:
                if(name.equals(ConfigCursor.SELECT)) {
                    selectedConfigPath = currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt";

                    readConfigFile(selectedConfigPath);

                    menuState = MenuState.SELECT_SUBSYSTEM;

                    resetCursorPos();
                    setSelectSubsystemLines();
                }

                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.ROOT_DIR;

                    resetCursorPos();
                    setRootDirLines();
                }
                break;

            //Select the subsystem on the robot to configure.
            case SELECT_SUBSYSTEM:
                if(name.equals(ConfigCursor.SELECT)) {
                    //If done isn't selected, transition to configure_subsystem
                    if (!lines.get(cursor.y).postSelectionText.equals("Done")) {
                        menuState = MenuState.CONFIGURE_SUBSYSTEM;
                        selectedSubsystemName = lines.get(cursor.y).postSelectionText;

                        resetCursorPos();
                        setConfigureSubsystemLines();
                    }
                    //If done is selected, transition back to root_dir and make a new config file.
                    else {
                        menuState = MenuState.ROOT_DIR;
                        createdNewConfig = false;

                        writeConfigFile(selectedConfigPath);

                        resetCursorPos();
                        setRootDirLines();
                    }
                }
                if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    if(createdNewConfig) {
                        menuState = MenuState.NEW_CONFIG;
                        createdNewConfig = false;

                        genDefaultConfigMap();
                        resetCursorPos();
                        setNewConfigLines(nameLine);
                    }
                    else {
                        menuState = MenuState.CHOOSE_EDIT_CONFIG;

                        resetCursorPos();
                        setConfigListLines();
                    }
                }
                break;

            //Select what options in the subsystem have what values.
            case CONFIGURE_SUBSYSTEM:
                if(name.equals(ConfigCursor.SELECT)) {
                    //If done isn't selected, cycle the option forward by 1.
                    if (!lines.get(cursor.y).postSelectionText.equals("Done")) {
                        String[] data = parseOptionLine(lines.get(cursor.y));

                        List<ConfigParam> subsystemParams = config.get(selectedSubsystemName);
                        ConfigParam currentParam = new ConfigParam("", new String[]{}, "");

                        for (ConfigParam param : subsystemParams) {
                            if (param.name.equals(data[0])) {
                                currentParam = param;
                                break;
                            }
                        }

                        lines.set(cursor.y, new GuiLine("#", currentParam.usesGamepad ? data[0] + " | " + currentParam.options.get((currentParam.options.indexOf(data[1]) + 1) % currentParam.options.size()) + " | " + data[2] : data[0] + " | " + currentParam.options.get((currentParam.options.indexOf(data[1]) + 1) % currentParam.options.size())));
                    }
                    //If done is selected, update the config map and transition back to select_subsystem.
                    else {
                        menuState = MenuState.SELECT_SUBSYSTEM;

                        updateConfigMapSubsystem(lines, selectedSubsystemName);

                        resetCursorPos();
                        setSelectSubsystemLines();
                    }
                }
                //If done isn't selected and the reverse select button is pressed, cycle the option backward by 1.
                else if(name.equals(ConfigCursor.REVERSE_SELECT) && !lines.get(cursor.y).postSelectionText.equals("Done")) {
                    String[] data = parseOptionLine(lines.get(cursor.y));

                    List<ConfigParam> subsystemParams = config.get(selectedSubsystemName);
                    ConfigParam currentParam = new ConfigParam("", new String[]{}, "");

                    for (ConfigParam param : subsystemParams) {
                        if (param.name.equals(data[0])) {
                            currentParam = param;
                            break;
                        }
                    }

                    lines.set(cursor.y, new GuiLine("#", currentParam.usesGamepad ? data[0] + " | " + currentParam.options.get(customMod.apply((currentParam.options.indexOf(data[1])-1), currentParam.options.size())) + " | " + data[2] : data[0] + " | " + currentParam.options.get(customMod.apply((currentParam.options.indexOf(data[1])-1), currentParam.options.size()))));
                }
                //If done isn't selected and the cycle gamepad button is pressed, cycle the setting's gamepad option if possible.
                else if(name.equals(ConfigCursor.SWITCH_GAMEPAD) && !lines.get(cursor.y).postSelectionText.equals("Done")) {
                    String unparsedLine = lines.get(cursor.y).postSelectionText;
                    String currentOptionName = unparsedLine.substring(0, unparsedLine.indexOf('|')).replace(" ", "");

                    int tempIdx = unparsedLine.substring(unparsedLine.indexOf('|') + 1).indexOf('|'); //This number is the index of the vertical bar in the substring formed by taking all the text after the first vertical bar.

                    String currentOptionValue;
                    String currentGamepadOptionValue;

                    if (tempIdx != -1) {
                        currentOptionValue = unparsedLine.substring(unparsedLine.indexOf('|') + 1, unparsedLine.indexOf('|') + tempIdx).replace(" ", "");
                        currentGamepadOptionValue = unparsedLine.substring(unparsedLine.indexOf('|') + tempIdx + 3);
                        List<ConfigParam> subsystemParams = config.get(selectedSubsystemName);
                        ConfigParam currentParam = new ConfigParam("", new String[]{}, "");

                        for (ConfigParam param : subsystemParams) {
                            if (param.name.equals(currentOptionName)) {
                                currentParam = param;
                                break;
                            }
                        }

                        lines.set(cursor.y, new GuiLine("#", currentParam.usesGamepad ? currentOptionName + " | " + currentOptionValue + " | " + currentParam.gamepadOptions.get((currentParam.gamepadOptions.indexOf(currentGamepadOptionValue) + 1) % currentParam.gamepadOptions.size()) : currentOptionName + " | " + currentOptionValue));
                    }
                }
                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.SELECT_SUBSYSTEM;

                    resetCursorPos();
                    setSelectSubsystemLines();
                }
                break;
        }
    }

    /**
     * Returns an ArrayList representation of SUPPORTED_CHARS.
     *
     * @return - ArrayList representation of SUPPORTED_CHARS.
     */
    private static ArrayList<Character> getValidChars() {
        ArrayList<Character> outputList = new ArrayList<>();
        for(char c : SUPPORTED_CHARS.toCharArray()) {
            outputList.add(c);
        }
        return outputList;
    }

    /**
     * Extract config file name from the file's path.
     *
     * @param filePath - The path to the config file.
     * @return - The config file's name.
     */
    private static String filepath2ConfigName(String filePath) {
        String[] data = filePath.split("/");
        return data[data.length-1].replace(".txt","");
    }

    /**
     * Parses the new_config name line. Removes all #s from the end and beginning of the line, and turns the ones in the middle into '_'s
     *
     * @param input - The string obtained from the name line in new_config.
     * @return - The name of the config file to be created.
     */
    private static String parseName(String input) {
        int startIdx = 0;
        int endIdx = 0;

        //removes '#'s at beginning of string
        for(int i = 0; i < input.length(); i++) {
            if(input.charAt(i) != '#') {
                startIdx = i;
                break;
            }
        }

        //removes '#'s at end of string
        for(int i = input.length()-1; i >= 0; i--) {
            if(input.charAt(i) != '#') {
                endIdx = i;
                break;
            }
        }

        String parsedString = input.substring(startIdx,endIdx+1);
        parsedString = parsedString.equals("#") ? "" : parsedString;

        return parsedString.replace('#','_');
    }

    /**
     * Parse a GuiLine that represents a ConfigParam.
     *
     * @param line - The GuiLine to be parsed.
     * @return - A string array containing the name of the config param, the config param's current option, and, if applicable, the config param's current gamepad option.
     */
    private static String[] parseOptionLine(GuiLine line) {
        String unparsedLine = line.postSelectionText;
        String currentOptionName = unparsedLine.substring(0, unparsedLine.indexOf('|')).replace(" ", "");

        int tempIdx = unparsedLine.substring(unparsedLine.indexOf('|') + 1).indexOf('|'); //This number is the index of the vertical bar in the substring formed by taking all the text after the first vertical bar.

        String currentOptionValue;
        String currentGamepadOptionValue;

        if(tempIdx != -1) {
            currentOptionValue = unparsedLine.substring(unparsedLine.indexOf('|') + 1, unparsedLine.indexOf('|') + tempIdx).replace(" ","");
            currentGamepadOptionValue = unparsedLine.substring(unparsedLine.indexOf('|') + tempIdx + 3);
        }
        else {
            currentOptionValue = unparsedLine.substring(unparsedLine.indexOf('|') + 1).replace(" ","");
            currentGamepadOptionValue = "";
        }
        return new String[] {currentOptionName,currentOptionValue,currentGamepadOptionValue};
    }

    /**
     * Generates a list of GuiLines from the names of all the config files in the given folder.
     *
     * @param filePath - The path to the folder containing the config files.
     * @return - A list of GuiLines generated from the names of all the config files.
     */
    private static ArrayList<GuiLine> genConfigLines(String filePath) {
        File rootDirectory = new File(filePath);
        File[] dirs = rootDirectory.listFiles();
        ArrayList<GuiLine> startingLines = new ArrayList<>();
        for(File dir : dirs) {
            if (!dir.getName().equals("robot_info.txt")) {
                startingLines.add(new GuiLine("#",dir.getName().replace(".txt","")));
            }
        }
        return startingLines;
    }

    /**
     * Generates the menu's initial lines. Basically a static version of setRootDirLines(). Only exists so that it can be passed into super() in the constructor as an initial set of lines.
     *
     * @param filePath - The path to the folder where the config file are located.
     * @return - The menu's initial GuiLines.
     */
    private static ArrayList<GuiLine> genInitialLines(String filePath) {
        ArrayList<GuiLine> startingLines = new ArrayList<>();

        startingLines.add(new GuiLine("#", "New Config"));
        startingLines.add(new GuiLine("#", "Edit Config"));
        startingLines.add(new GuiLine("#", "Delete Config"));

        startingLines.addAll(genConfigLines(filePath));

        return startingLines;
    }

    /**
     * Loads the lines used in the root_dir state. (all config file names and new/edit/delete options).
     */
    private void setRootDirLines() {
        List<GuiLine> newLines = new ArrayList<>();
        newLines.add(new GuiLine("#", "New Config"));
        newLines.add(new GuiLine("#", "Edit Config"));
        newLines.add(new GuiLine("#", "Delete Config"));
        newLines.addAll(genConfigLines(currentFilepath));

        super.setSelectionZoneWidthAndHeight(1,newLines.size(), newLines);
    }

    /**
     * Loads the lines used in the configure_subsystem state. (All subsystem configparam options and a done line).
     */
    private void setConfigureSubsystemLines() {
        List<GuiLine> newLines = new ArrayList<>();

        for(ConfigParam param : config.get(selectedSubsystemName)) {
            newLines.add(new GuiLine("#",param.usesGamepad ? param.name+ " | " + param.currentOption + " | " + param.currentGamepadOption : param.name+ " | " + param.currentOption));
        }
        newLines.add(new GuiLine("#","Done"));

        super.setSelectionZoneHeight(newLines.size(), newLines);
    }

    /**
     * Loads the lines used when listing out all config files (like in the edit or delete states).
     */
    private void setConfigListLines() {
        List<GuiLine> newLines = genConfigLines(currentFilepath);
        super.setSelectionZoneHeight(newLines.size(), newLines);
    }

    /**
     * Loads the lines used when creating a new config. (2 lines, each 15 characters. 1st line is where the name is typed, 2nd line is a done line).
     */
    private void setNewConfigLines() {
        List<GuiLine> newLines = new ArrayList<>();
        newLines.add(new GuiLine("###############", "Config Name"));
        newLines.add(new GuiLine("###############", "Done"));
        super.setSelectionZoneWidthAndHeight(newLines.get(0).selectionZoneText.length(), newLines.size(), newLines);
    }

    /**
     * Loads the lines used when creating a new config. (2 lines, each 15 characters. 1st line is where the name is typed, 2nd line is a done line).
     *
     * @param initName - The GuiLine to initialize the naming line as.
     */
    private void setNewConfigLines(GuiLine initName) {
        List<GuiLine> newLines = new ArrayList<>();
        newLines.add(initName);
        newLines.add(new GuiLine("###############", "Done"));
        super.setSelectionZoneWidthAndHeight(newLines.get(0).selectionZoneText.length(), newLines.size(), newLines);
    }

    /**
     * Loads the lines used when selecting a subsystem to configure. (List of all configurable subsystems).
     */
    private void setSelectSubsystemLines() {
        List<GuiLine> newLines = new ArrayList<>();
        for(String subsystem : config.keySet()) {
            newLines.add(new GuiLine("#",subsystem));
        }
        newLines.add(new GuiLine("#", "Done"));
        super.setSelectionZoneWidthAndHeight(1, newLines.size(), newLines);
    }

    /**
     * Resets the cursor to the top of the menu.
     */
    private void resetCursorPos() {
        cursor.setX(0);
        cursor.setY(0);
    }

    /**
     * Removes the first Done line in a list of GuiLines.
     *
     * @param lines - The list of GuiLines to search.
     */
    private void removeDone(List<GuiLine> lines) {
        for(GuiLine line : lines) {
            if(line.postSelectionText.equals("Done")) {
                lines.remove(line);
                break;
            }
        }
    }

    /**
     * Pulls default config from Robot's global teleop and autonomous config maps.
     */
    private void genDefaultConfigMap() {
        config = new HashMap<>();

        if(configState == ConfigurationState.AUTONOMOUS) {
            for (String subsystem : Robot.autonomousConfig.keySet()) {
                List<ConfigParam> params = new ArrayList<>();
                for (ConfigParam param : Robot.autonomousConfig.get(subsystem)) {
                    params.add(param.clone());
                }
                config.put(subsystem, params);
            }
        }
        else  {
            for (String subsystem : Robot.teleopConfig.keySet()) {
                List<ConfigParam> params = new ArrayList<>();
                for (ConfigParam param : Robot.teleopConfig.get(subsystem)) {
                    params.add(param.clone());
                }
                config.put(subsystem, params);
            }
        }
    }

    /**
     * Updates a subsystem entry in the config map with a list of updated ConfigParams.
     *
     * @param newConfig - The raw GuiLines containing all of the data used in updating the ConfigParams.
     */
    private void updateConfigMapSubsystem(List<GuiLine> newConfig, String subsystemName) {
        removeDone(newConfig); //gets rid of the Done line

        for(int i = 0; i < newConfig.size(); i++) {
            String[] data = parseOptionLine(newConfig.get(i));

            config.get(subsystemName).get(i).name = data[0];
            config.get(subsystemName).get(i).currentOption = data[1];

            if(config.get(subsystemName).get(i).usesGamepad) {
                config.get(subsystemName).get(i).currentGamepadOption = data[2];
            }
        }
    }

    /**
     * Export the data in a config file to the Robot autonomous and teleop config maps.
     *
     * @param filepath - The path to the config file being exported.
     */
    private void exportConfigFile(String filepath) {

        if(config.isEmpty()) {
            genDefaultConfigMap();
        }

        readConfigFile(filepath);

        if(configState == ConfigurationState.AUTONOMOUS) {
            Robot.autonomousConfig = new HashMap<>();
            for (String subsystem : config.keySet()) {
                List<ConfigParam> params = new ArrayList<>();
                for (ConfigParam param : config.get(subsystem)) {
                    params.add(param.clone());
                }
                Robot.autonomousConfig.put(subsystem, params);
            }
        }
        else {
            Robot.teleopConfig = new HashMap<>();
            for (String subsystem : config.keySet()) {
                List<ConfigParam> params = new ArrayList<>();
                for (ConfigParam param : config.get(subsystem)) {
                    params.add(param.clone());
                }
                Robot.teleopConfig.put(subsystem, params);
            }
        }
    }

    /**
     * Reads a config file and loads it into the internal config map.
     *
     * @param filepath - The path to the config file.
     */
    private void readConfigFile(String filepath) {
        FileInputStream fis;

        try {
            fis = new FileInputStream(filepath);

            FileReader fReader;
            BufferedReader bufferedReader;

            try {
                fReader = new FileReader(fis.getFD());
                bufferedReader = new BufferedReader(fReader);

                int i = 0;
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    String[] data = line.split(":");
                    config.get(data[0]).get(i).name = data[1];
                    config.get(data[0]).get(i).currentOption = data[2];
                    if(config.get(data[0]).get(i).usesGamepad) {
                        config.get(data[0]).get(i).currentGamepadOption = data[3].substring(0,data[3].indexOf('d')+1) + ' ' + data[3].substring(data[3].indexOf('d')+1); //adds a space in between the d in gamepad and the number
                    }
                    i++;
                }

                bufferedReader.close();
                fReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fis.getFD().sync();
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the filepath of the config to autorun during teleop if not in standalone mode.
     *
     * @param teleopFolderPath - The path to the teleop folder.
     * @return - The filepath stored in the teleop/robot_info.txt file. If no filepath is found it will return an empty string.
     */
    private String getAutorunFilepath(String teleopFolderPath) {
        String outputFilepath = "";

        FileInputStream fis;
        try {

            fis = new FileInputStream(teleopFolderPath + "/robot_info.txt");

            FileReader fReader;
            BufferedReader bufferedReader;

            try {
                fReader = new FileReader(fis.getFD());
                bufferedReader = new BufferedReader(fReader);

                outputFilepath = bufferedReader.readLine();

                if(outputFilepath == null) {
                    outputFilepath = "";
                }

                bufferedReader.close();
                fReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fis.getFD().sync();
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputFilepath;
    }

    /**
     * Writes a config file to the specified path.
     *
     * @param configPath - The path of the new config file.
     */
    private void writeConfigFile(String configPath) {
        StringBuilder sb = new StringBuilder();
        for(String subsystem : config.keySet()) {
            for(ConfigParam param : config.get(subsystem)) {
                sb.append(subsystem);
                sb.append(':');
                sb.append(param.name);
                sb.append(':');
                sb.append(param.currentOption);
                if(param.usesGamepad) {
                    sb.append(':');
                    sb.append(param.currentGamepadOption.replace(" ","")); //I don't trust spaces. They make me suspicious.
                }
                sb.append("\r\n");
            }
        }

        if(sb.length() > 2) {
            sb.delete(sb.length() - 2, sb.length()); //removes trailing \r\n characters so there isn't a blank line at the end of the file
        }

        writeData(configPath,sb.toString());
    }

    /**
     * Writes data to a specified file. If the file doesn't exist, it creates it, otherwise, it overwrites it.
     *
     * @param filePath - The path to write the data to.
     * @param data - The data to write.
     */
    private void writeData(String filePath, String data) {
        FileOutputStream fos;
        try {

            File file = new File(filePath);
            if(file.exists()) {
                boolean fileDeleted = file.delete();
                if(!fileDeleted) {
                    Log.e("File Error", "Could not delete file at "+filePath);
                }

                boolean fileCreated = file.createNewFile();
                if(!fileCreated) {
                    Log.e("File Error","Could not create file at "+filePath);
                }
            }

            fos = new FileOutputStream(filePath, true);

            FileWriter fWriter;

            try {
                fWriter = new FileWriter(fos.getFD());

                fWriter.write(data);

                fWriter.flush();
                fWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fos.getFD().sync();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
