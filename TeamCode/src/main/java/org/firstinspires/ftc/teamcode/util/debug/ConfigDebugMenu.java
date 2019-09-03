package org.firstinspires.ftc.teamcode.util.debug;

import android.os.Environment;
import android.util.Log;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.GUI.GUI;
import org.firstinspires.ftc.teamcode.system.source.GUI.GuiLine;
import org.firstinspires.ftc.teamcode.system.source.GUI.ScrollingListMenu;
import org.firstinspires.ftc.teamcode.system.subsystems.cursors.ConfigCursor;
import org.firstinspires.ftc.teamcode.util.annotations.AutonomousConfig;
import org.firstinspires.ftc.teamcode.util.annotations.TeleopConfig;
import org.firstinspires.ftc.teamcode.util.functional_interfaces.BiFunction;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigDebugMenu extends ScrollingListMenu {

    private enum MenuState {
        START, ROBOTDIRSELECTED, DELETECONFIG, EDITNEWCONFIG, SELECTSUBSYSTEMCONFIG, NEWCONFIG, CONFIGOPTIONS, CREATENEWCONFIG, DELETEROBOT, TELEOP_AUTO_SELECT
    }

    private enum ConfigurationState {
        AUTONOMOUS, TELEOP
    }

    private static final String SUPPORTED_CHARS = "#abcdefghijklmnopqrstuvwxyz0123456789";
    private MenuState menuState;
    private String currentFilepath;
    private final ArrayList<Character> VALID_CHARS = getValidChars();
    private String selectedConfigPath;
    private String selectedSubsystemName;
    private Map<String,List<ConfigParam>> config;
    private boolean creatingNewConfig = false;
    private GuiLine nameLine;
    private BiFunction<Integer,Integer,Integer> customMod = (Integer x, Integer m) -> (x % m + m) % m;
    private String robotFilepath;
    private ConfigurationState configState;

    public ConfigDebugMenu(GUI gui) {
        super(gui, new ConfigCursor(gui.robot,500), genInitialLines(Environment.getExternalStorageDirectory().getPath()+"/System64/"),1,genInitialLines(Environment.getExternalStorageDirectory().getPath()+"/System64/").size());
        menuState = MenuState.START;
        currentFilepath = Environment.getExternalStorageDirectory().getPath()+"/System64/";
        config = new HashMap<>();
        configState = ConfigurationState.AUTONOMOUS;
    }

    @Override
    public void onSelect() {

    }

    @Override
    public void onButton(String name, Button button) {

        switch (menuState) {

            //startup, shows all robot directories.
            case START:
                if(name.equals(ConfigCursor.SELECT)) {
                    if(!lines.get(cursor.y).postSelectionText.equals("Delete")) {
                        menuState = MenuState.TELEOP_AUTO_SELECT;
                        currentFilepath += lines.get(cursor.y).postSelectionText;

                        robotFilepath = currentFilepath;

                        initRobotConfig();

                        resetCursorPos();
                        setFolderSelectLines();
                    }
                    else if(new File(currentFilepath).listFiles().length > 0){
                        menuState = MenuState.DELETEROBOT;

                        resetCursorPos();
                        setRootDeleteLines();
                    }
                }
                break;

            case DELETEROBOT:
                if(name.equals(ConfigCursor.SELECT)) {
                    menuState = MenuState.START;

                    deleteDirectory(currentFilepath+lines.get(cursor.y).postSelectionText);

                    resetCursorPos();
                    setRootDirLines();
                }
                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.START;

                    resetCursorPos();
                    setRootDirLines();
                }
                break;

            case TELEOP_AUTO_SELECT:
                if(name.equals(ConfigCursor.SELECT)) {
                    menuState = MenuState.ROBOTDIRSELECTED;

                    configState = lines.get(cursor.y).postSelectionText.equals("autonomous") ? ConfigurationState.AUTONOMOUS : ConfigurationState.TELEOP;

                    currentFilepath += '/'+lines.get(cursor.y).postSelectionText;

                    resetCursorPos();
                    setRobotDirLines();
                }
                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.START;

                    currentFilepath = Environment.getExternalStorageDirectory().getPath()+"/System64/";

                    resetCursorPos();
                    setRootDirLines();
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
                }

                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.TELEOP_AUTO_SELECT;

                    currentFilepath = robotFilepath;

                    resetCursorPos();
                    setFolderSelectLines();
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
                    else {
                        menuState = MenuState.SELECTSUBSYSTEMCONFIG;

                        updateConfigMapSubsystem(lines,selectedSubsystemName);

                        resetCursorPos();
                        setSubsystemSelectionLines();
                    }
                }
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

    private void setRootDirLines() {
        List<GuiLine> newLines = genLines(currentFilepath);
        newLines.add(new GuiLine("#","Delete"));
        super.setSelectionZoneHeight(newLines.size(), newLines);
    }

    private void setRootDeleteLines() {
        List<GuiLine> newLines = genLines(currentFilepath);
        super.setSelectionZoneHeight(newLines.size(), newLines);
    }

    private void setFolderSelectLines() {
        List<GuiLine> newLines = genLines(currentFilepath, "robot_info.txt");
        super.setSelectionZoneHeight(newLines.size(),newLines);
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
    }

    private void deleteDirectory(String filePath) {
        File dir = new File(filePath);
        File[] contents = dir.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDirectory(f.getPath()); //recursion :)
            }
        }
        dir.delete();
    }

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

    private void initRobotConfig() {
        FileInputStream fis;

        try {

            fis = new FileInputStream(currentFilepath + "/robot_info.txt");

            FileReader fReader;
            BufferedReader bufferedReader;

            try {
                fReader = new FileReader(fis.getFD());
                bufferedReader = new BufferedReader(fReader);

                String classname;
                while((classname = bufferedReader.readLine()) != null) {
                    Class subsystem = Class.forName(classname);
                    Method[] methods = subsystem.getDeclaredMethods();

                    boolean foundTeleopConfig = false;
                    boolean foundAutonomousConfig = false;

                    for(Method m : methods) {
                        //method must be annotated as TeleopConfig, have no parameters, be public and static, and return an array of config params
                        if(!foundTeleopConfig && m.isAnnotationPresent(TeleopConfig.class) && m.getReturnType() == ConfigParam[].class && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                            Robot.teleopConfig.put(subsystem.getSimpleName(), Arrays.asList((ConfigParam[]) m.invoke(null)));
                            foundTeleopConfig = true;
                        }

                        //method must be annotated as AutonomousConfig, have no parameters, be public and static, and return an array of config params
                        if(!foundAutonomousConfig && m.isAnnotationPresent(AutonomousConfig.class) && m.getReturnType() == ConfigParam[].class && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                            Robot.autonomousConfig.put(subsystem.getSimpleName(),Arrays.asList((ConfigParam[]) m.invoke(null)));
                            foundAutonomousConfig = true;
                        }

                        if(foundTeleopConfig && foundAutonomousConfig) {
                            break;
                        }
                    }
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

            sb.delete(sb.length()-2,sb.length()); //removes trailing \r\n characters so there isn't a blank line at the end of the file
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
        for(File dir : dirs) {
            startingLines.add(new GuiLine("#",dir.getName()));
        }
        startingLines.add(new GuiLine("#","Delete"));
        return startingLines;
    }

    private static ArrayList<GuiLine> genLines(String filePath) {
        File rootDirectory = new File(filePath);
        File[] dirs = rootDirectory.listFiles();
        ArrayList<GuiLine> startingLines = new ArrayList<>();
        for(File dir : dirs) {
            startingLines.add(new GuiLine("#",dir.getName().replace(".txt","")));
        }
        return startingLines;
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
}
