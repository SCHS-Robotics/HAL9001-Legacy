package org.firstinspires.ftc.teamcode.system.menus;

import android.util.Log;

import org.firstinspires.ftc.teamcode.system.source.BaseAutonomous;
import org.firstinspires.ftc.teamcode.system.source.BaseTeleop;
import org.firstinspires.ftc.teamcode.system.source.GUI;
import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.system.source.ScrollingListMenu;
import org.firstinspires.ftc.teamcode.system.source.SubSystem;
import org.firstinspires.ftc.teamcode.system.subsystems.cursors.ConfigCursor;
import org.firstinspires.ftc.teamcode.util.functional_interfaces.BiFunction;
import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigMenu extends ScrollingListMenu {

    private enum MenuState {
        ROBOTDIRSELECTED, DELETECONFIG, EDITNEWCONFIG, SELECTSUBSYSTEMCONFIG, NEWCONFIG, CONFIGOPTIONS, CREATENEWCONFIG, DELETEROBOT, DONE, AUTO_RUN
    }

    private enum ConfigurationState {
        AUTONOMOUS, TELEOP
    }

    private static final String SUPPORTED_CHARS = "#abcdefghijklmnopqrstuvwxyz0123456789";
    private MenuState menuState;
    private ConfigurationState configState;
    private String currentFilepath;
    private final ArrayList<Character> VALID_CHARS = getValidChars();
    private String selectedConfigPath;
    private String selectedSubsystemName;
    private Map<String,List<ConfigParam>> config;
    private boolean creatingNewConfig = false;
    private GuiLine nameLine;
    private BiFunction<Integer,Integer,Integer> customMod = (Integer x, Integer m) -> (x % m + m) % m;
    private boolean singleFolder;
    private String robotFolder;

    public boolean isDone = false;

    public ConfigMenu(GUI gui, String filePath, boolean singleFolder) {
        super(gui, new ConfigCursor(gui.robot,500), genInitialLines(singleFolder ? filePath : gui.robot.getOpMode() instanceof BaseAutonomous ? filePath + "/autonomous" : filePath + "/teleop"),1,genInitialLines(singleFolder ? filePath : gui.robot.getOpMode() instanceof BaseAutonomous ? filePath + "/autonomous" : filePath + "/teleop").size());

        menuState = MenuState.ROBOTDIRSELECTED;
        configState = gui.robot.getOpMode() instanceof BaseAutonomous ? ConfigurationState.AUTONOMOUS : ConfigurationState.TELEOP;
        config = new HashMap<>();

        if(singleFolder) {
            currentFilepath = filePath;
        }
        else if(configState == ConfigurationState.AUTONOMOUS){
            robotFolder = filePath;
            currentFilepath = robotFolder + "/autonomous";
        }
        else {
            robotFolder = filePath;
            currentFilepath = robotFolder + "/teleop";
            String autorunFilepath = scanRobotInfo();
            if(!autorunFilepath.equals("")) {
                File autorun = new File(autorunFilepath);
                if(autorun.exists()) {
                    menuState = MenuState.AUTO_RUN;
                    String autorunName = getConfigNameFromFilepath(autorunFilepath);
                    String message = "Auto using config " + autorunName + ".\r\nPress center button to change.";
                    cursor.setDoBlink(false);
                    exportConfigFile(autorunFilepath);
                    super.setSelectionZoneWidthAndHeight(message.length(),1,new GuiLine[]{new GuiLine(message,"","")});
                }
                else {
                    writeData(currentFilepath+"/robot_info.txt","");
                }
            }
        }

        this.singleFolder = singleFolder;
    }

    @Override
    public void onSelect() {

    }

    @Override
    public void onButton(String name, Button button) {

        switch (menuState) {

            case AUTO_RUN:

                if(name.equals(ConfigCursor.DISABLE_AUTORUN)) {
                    menuState = MenuState.ROBOTDIRSELECTED;

                    cursor.setDoBlink(true);

                    resetCursorPos();
                    setRobotDirLines();
                }

                break;

            //robot directory has been selected, showing all config files in the directory and new/edit/delete options.
            case ROBOTDIRSELECTED:
                if(name.equals(ConfigCursor.SELECT)) {
                    if ((lines.get(cursor.y).postSelectionText.equals("Delete Config") || lines.get(cursor.y).postSelectionText.equals("Edit Config")) && genLines(currentFilepath,"robot_info.txt").size() > 0) {
                        menuState = lines.get(cursor.y).postSelectionText.equals("Delete Config") ? MenuState.DELETECONFIG : MenuState.EDITNEWCONFIG;

                        genDefaultConfigMap();
                        resetCursorPos();
                        setConfigListLines();

                    } else if (lines.get(cursor.y).postSelectionText.equals("New Config")) {
                        menuState = MenuState.NEWCONFIG;

                        genDefaultConfigMap();
                        resetCursorPos();
                        setConfigNamingLines();
                    }
                    else if(singleFolder) {
                        menuState = MenuState.DONE;

                        exportConfigFile(currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt");

                        cursor.setDoBlink(false);
                        super.setSelectionZoneHeight(1,new GuiLine[]{new GuiLine(" ","","")});

                        isDone = true;
                    }


                    else if(configState == ConfigurationState.AUTONOMOUS && genLines(currentFilepath,"robot_info.txt").size() > 0){

                        exportConfigFile(currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt");

                        configState = ConfigurationState.TELEOP;
                        genDefaultConfigMap();

                        currentFilepath = robotFolder + "/teleop";

                        resetCursorPos();
                        setRobotDirLines();
                    }
                    else if(configState == ConfigurationState.TELEOP && genLines(currentFilepath,"robot_info.txt").size() > 0) {
                        menuState = MenuState.DONE;

                        exportConfigFile(currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt");
                        writeData(robotFolder+"/teleop/robot_info.txt",currentFilepath+'/'+lines.get(cursor.y).postSelectionText+".txt");

                        cursor.setDoBlink(false);
                        super.setSelectionZoneHeight(1,new GuiLine[]{new GuiLine(" ","","")});

                        isDone = true;
                    }
                }
                break;

            //delete option selected. Selected config will be deleted.
            case DELETECONFIG:
                if(name.equals(ConfigCursor.SELECT)) {
                    menuState = MenuState.ROBOTDIRSELECTED;

                    String configPath = currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt";
                    File configFile = new File(configPath);

                    if (!configFile.delete()) {
                        Log.e("File Issues", "Problem deleting file at " + configPath);
                    }

                    resetCursorPos();
                    setRobotDirLines();
                }

                else if(name.equals(ConfigCursor.BACK_BUTTON)) {

                    menuState = MenuState.ROBOTDIRSELECTED;

                    resetCursorPos();
                    setRobotDirLines();
                }
                break;

            //new option selected. will prompt user to enter a name for the config file.
            case NEWCONFIG:

                if(name.equals(ConfigCursor.SELECT)) {
                    if (!lines.get(cursor.y).postSelectionText.equals("Done")) {
                        ((ConfigCursor) cursor).setWriteMode(true);
                        char[] currentNameText = lines.get(0).selectionZoneText.toCharArray();
                        currentNameText[cursor.x] = VALID_CHARS.get((VALID_CHARS.indexOf(currentNameText[cursor.x]) + 1) % VALID_CHARS.size());

                        setConfigNamingLines(new GuiLine(new String(currentNameText), "Config Name"));
                    }
                    else {
                        ((ConfigCursor) cursor).setWriteMode(false);

                        String newConfigName = parseName(lines.get(0).selectionZoneText);

                        if (!newConfigName.equals("") && !newConfigName.equals("robot_info")) {

                            creatingNewConfig = true;
                            nameLine = lines.get(0);

                            selectedConfigPath = currentFilepath + '/' + newConfigName + ".txt";

                            menuState = MenuState.SELECTSUBSYSTEMCONFIG;

                            resetCursorPos();
                            setSubsystemSelectionLines();
                        }

                        else {
                            menuState = MenuState.ROBOTDIRSELECTED;
                            Log.e("Oh No", "Hacker Alert!");
                            resetCursorPos();
                            setRobotDirLines();
                        }
                    }
                }

                else if(name.equals(ConfigCursor.REVERSE_SELECT) && !lines.get(cursor.y).postSelectionText.equals("Done")) {
                    ((ConfigCursor) cursor).setWriteMode(true);
                    char[] currentNameText = lines.get(0).selectionZoneText.toCharArray();
                    currentNameText[cursor.x] = VALID_CHARS.get(customMod.apply((VALID_CHARS.indexOf(currentNameText[cursor.x]) - 1),VALID_CHARS.size()));

                    setConfigNamingLines(new GuiLine(new String(currentNameText), "Config Name"));
                }

                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.ROBOTDIRSELECTED;

                    resetCursorPos();
                    setRobotDirLines();
                }

                break;

            //edit option selected. Selected config will be edited
            case EDITNEWCONFIG:

                if(name.equals(ConfigCursor.SELECT)) {
                    
                    selectedConfigPath = currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt";

                    readConfigFile(selectedConfigPath);

                    menuState = MenuState.SELECTSUBSYSTEMCONFIG;

                    resetCursorPos();
                    setSubsystemSelectionLines();
                }

                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.ROBOTDIRSELECTED;

                    resetCursorPos();
                    setRobotDirLines();
                }

                break;

            case SELECTSUBSYSTEMCONFIG:
                if(name.equals(ConfigCursor.SELECT)) {
                    if (!lines.get(cursor.y).postSelectionText.equals("Done")) {
                        menuState = MenuState.CONFIGOPTIONS;
                        selectedSubsystemName = lines.get(cursor.y).postSelectionText;

                        resetCursorPos();
                        setSubsystemOptionsLines();
                    } else {
                        menuState = MenuState.ROBOTDIRSELECTED;
                        creatingNewConfig = false;

                        writeConfigFile();

                        resetCursorPos();
                        setRobotDirLines();
                    }
                }
                if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    if(creatingNewConfig) {
                        menuState = MenuState.NEWCONFIG;
                        creatingNewConfig = false;

                        genDefaultConfigMap();
                        resetCursorPos();
                        setConfigNamingLines(nameLine);
                    }
                    else {
                        menuState = MenuState.EDITNEWCONFIG;

                        resetCursorPos();
                        setConfigListLines();
                    }
                }
                break;

            case CONFIGOPTIONS:
                if(name.equals(ConfigCursor.SELECT)) {
                    if (!lines.get(cursor.y).postSelectionText.equals("Done")) {

                        String unparsedLine = lines.get(cursor.y).postSelectionText;
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

                        List<ConfigParam> subsystemParams = config.get(selectedSubsystemName);
                        ConfigParam currentParam = new ConfigParam("", new String[]{}, "");

                        for (ConfigParam param : subsystemParams) {
                            if (param.name.equals(currentOptionName)) {
                                currentParam = param;
                                break;
                            }
                        }

                        lines.set(cursor.y, new GuiLine("#", currentParam.usesGamepad ? currentOptionName + " | " + currentParam.options.get((currentParam.options.indexOf(currentOptionValue) + 1) % currentParam.options.size()) + " | " + currentGamepadOptionValue : currentOptionName + " | " + currentParam.options.get((currentParam.options.indexOf(currentOptionValue) + 1) % currentParam.options.size())));
                    }
                    else {
                        menuState = MenuState.SELECTSUBSYSTEMCONFIG;

                        updateConfigMapSubsystem(lines);

                        resetCursorPos();
                        setSubsystemSelectionLines();
                    }
                }
                else if(name.equals(ConfigCursor.REVERSE_SELECT) && !lines.get(cursor.y).postSelectionText.equals("Done")) {
                    String unparsedLine = lines.get(cursor.y).postSelectionText;
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

                    List<ConfigParam> subsystemParams = config.get(selectedSubsystemName);
                    ConfigParam currentParam = new ConfigParam("", new String[]{}, "");

                    for (ConfigParam param : subsystemParams) {
                        if (param.name.equals(currentOptionName)) {
                            currentParam = param;
                            break;
                        }
                    }

                    lines.set(cursor.y, new GuiLine("#", currentParam.usesGamepad ? currentOptionName + " | " + currentParam.options.get(customMod.apply((currentParam.options.indexOf(currentOptionValue)-1), currentParam.options.size())) + " | " + currentGamepadOptionValue : currentOptionName + " | " + currentParam.options.get(customMod.apply((currentParam.options.indexOf(currentOptionValue)-1), currentParam.options.size()))));
                }

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
                    menuState = MenuState.SELECTSUBSYSTEMCONFIG;

                    resetCursorPos();
                    setSubsystemSelectionLines();
                }
                break;
        }
    }

    private void setRobotDirLines() {
        List<GuiLine> newLines = new ArrayList<>();
        newLines.add(new GuiLine("#", "New Config"));
        newLines.add(new GuiLine("#", "Edit Config"));
        newLines.add(new GuiLine("#", "Delete Config"));
        newLines.addAll(genLines(currentFilepath, "robot_info.txt"));

        super.setSelectionZoneWidthAndHeight(1,newLines.size(), newLines);
    }

    private void setSubsystemOptionsLines() {

        List<GuiLine> newLines = new ArrayList<>();

        for(ConfigParam param : config.get(selectedSubsystemName)) {
            newLines.add(new GuiLine("#",param.usesGamepad ? param.name+ " | " + param.currentOption + " | " + param.currentGamepadOption : param.name+ " | " + param.currentOption));
        }
        newLines.add(new GuiLine("#","Done"));

        super.setSelectionZoneHeight(newLines.size(), newLines);
    }

    private void setConfigListLines() {
        List<GuiLine> newLines = genLines(currentFilepath,"robot_info.txt");
        super.setSelectionZoneHeight(newLines.size(), newLines);
    }

    private void setConfigNamingLines() {
        List<GuiLine> newLines = new ArrayList<>();
        newLines.add(new GuiLine("###############", "Config Name"));
        newLines.add(new GuiLine("###############", "Done"));
        super.setSelectionZoneWidthAndHeight(newLines.get(0).selectionZoneText.length(), newLines.size(), newLines);
    }

    private void setConfigNamingLines(GuiLine initName) {
        List<GuiLine> newLines = new ArrayList<>();
        newLines.add(initName);
        newLines.add(new GuiLine("###############", "Done"));
        super.setSelectionZoneWidthAndHeight(newLines.get(0).selectionZoneText.length(), newLines.size(), newLines);
    }

    private void setSubsystemSelectionLines() {
        List<GuiLine> newLines = new ArrayList<>();
        for(String subsystem : config.keySet()) {
            newLines.add(new GuiLine("#",subsystem));
        }
        newLines.add(new GuiLine("#", "Done"));
        super.setSelectionZoneWidthAndHeight(1, newLines.size(), newLines);
    }

    private void resetCursorPos() {
        cursor.setX(0);
        cursor.setY(0);
        menuNumber = 0;
    }


    private void readConfigFile(String filename) {

        FileInputStream fis;

        try {

            fis = new FileInputStream(filename);

            FileReader fReader;
            BufferedReader bufferedReader;

            try {
                fReader = new FileReader(fis.getFD());
                bufferedReader = new BufferedReader(fReader);

                String line;
                int i = 0;
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

    private void updateConfigMapSubsystem(List<GuiLine> newConfig) {
        removeDone(newConfig); //gets rid of the Done line

        for(int i = 0; i < newConfig.size(); i++) {
            String unparsedLine = lines.get(i).postSelectionText;
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

            config.get(selectedSubsystemName).get(i).name = currentOptionName;
            config.get(selectedSubsystemName).get(i).currentOption = currentOptionValue;

            if(config.get(selectedSubsystemName).get(i).usesGamepad) {
                config.get(selectedSubsystemName).get(i).currentGamepadOption = currentGamepadOptionValue;
            }
        }
    }

    private void removeDone(List<GuiLine> lines) {
        for(GuiLine line : lines) {
            if(line.postSelectionText.equals("Done")) {
                lines.remove(line);
                break;
            }
        }
    }

    private void writeConfigFile() {

        File configFile = new File(selectedConfigPath);

        FileOutputStream fos;

        try {
            configFile.delete();
            configFile.createNewFile();

            fos = new FileOutputStream(selectedConfigPath, true);

            FileWriter fWriter;

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

            try {
                fWriter = new FileWriter(fos.getFD());

                fWriter.write(sb.toString());

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

    private void writeData(String filePath, String data) {

        FileOutputStream fos;

        try {

            File file = new File(filePath);
            if(file.exists()) {
                file.delete();
                file.createNewFile();
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

    private String parseName(String input) {

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


    private ArrayList<Character> getValidChars() {
        ArrayList<Character> outputList = new ArrayList<>();
        for(char c : SUPPORTED_CHARS.toCharArray()) {
            outputList.add(c);
        }
        return outputList;
    }

    private static ArrayList<GuiLine> genInitialLines(String filePath) {
        File rootDirectory = new File(filePath);
        File[] dirs = rootDirectory.listFiles();
        ArrayList<GuiLine> startingLines = new ArrayList<>();

        startingLines.add(new GuiLine("#", "New Config"));
        startingLines.add(new GuiLine("#", "Edit Config"));
        startingLines.add(new GuiLine("#", "Delete Config"));

        for(File dir : dirs) {
            if(!dir.getName().equals("robot_info.txt")) {
                startingLines.add(new GuiLine("#", dir.getName().replace(".txt","")));
            }
        }
        return startingLines;
    }

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

    private String scanRobotInfo() {
        String outputFilepath = "";

        FileInputStream fis;

        try {

            fis = new FileInputStream(currentFilepath + "/robot_info.txt");

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

    private String getConfigNameFromFilepath(String filePath) {
        String[] data = filePath.split("/");
        return data[data.length-1].replace(".txt","");
    }

    private static ArrayList<GuiLine> genLines(String filePath, String exclude) {
        File rootDirectory = new File(filePath);
        File[] dirs = rootDirectory.listFiles();
        ArrayList<GuiLine> startingLines = new ArrayList<>();
        for(File dir : dirs) {
            if (!dir.getName().equals(exclude)) {
                startingLines.add(new GuiLine("#",dir.getName().replace(".txt","")));
            }
        }
        return startingLines;
    }
}
