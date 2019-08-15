package org.firstinspires.ftc.teamcode.util.debug;

import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.internal.android.dex.util.FileUtils;
import org.firstinspires.ftc.teamcode.system.source.GUI;
import org.firstinspires.ftc.teamcode.system.source.Menu;
import org.firstinspires.ftc.teamcode.system.source.ScrollingListMenu;
import org.firstinspires.ftc.teamcode.system.source.SubSystem;
import org.firstinspires.ftc.teamcode.system.subsystems.cursors.ConfigCursor;
import org.firstinspires.ftc.teamcode.system.subsystems.cursors.DefaultCursor;
import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigDebugMenu extends ScrollingListMenu {

    private enum MenuState {
        START, ROBOTDIRSELECTED, DELETECONFIG, EDITNEWCONFIG, SELECTSUBSYSTEMCONFIG, NEWCONFIG, CONFIGOPTIONS, CREATENEWCONFIG
    }

    private static final String SUPPORTED_CHARS = "#abcdefghijklmnopqrstuvwxyz0123456789";
    private MenuState menuState;
    private String currentFilepath;
    private final ArrayList<Character> VALID_CHARS = getValidChars();
    private String selectedConfigPath;
    private String selectedSubsystemName;
    private Map<String,List<String>> config;

    public ConfigDebugMenu(GUI gui) {
        super(gui, new ConfigCursor(gui.robot,500), genLines(Environment.getExternalStorageDirectory().getPath()+"/System64/"),1,genLines(Environment.getExternalStorageDirectory().getPath()+"/System64/").size());
        menuState = MenuState.START;
        currentFilepath = Environment.getExternalStorageDirectory().getPath()+"/System64/";
        config = new HashMap<>();
    }

    @Override
    public void onSelect() {
        ArrayList<GuiLine> newLines = new ArrayList<>();
        switch (menuState) {

            //startup, shows all robot directories.
            case START:

                menuState = MenuState.ROBOTDIRSELECTED;
                currentFilepath += lines.get(cursor.y).postSelectionText;

                newLines.add(new GuiLine("#", "New Config"));
                newLines.add(new GuiLine("#", "Edit Config"));
                newLines.add(new GuiLine("#", "Delete Config"));
                newLines.addAll(genLines(currentFilepath, "robot_info.txt"));

                super.setSelectionZoneHeight(newLines.size(), newLines);
                cursor.y = 0;

                break;

            //robot directory has been selected, showing all config files in the directory and new/edit/delete options.
            case ROBOTDIRSELECTED:
                if (lines.get(cursor.y).postSelectionText.equals("Delete Config") || lines.get(cursor.y).postSelectionText.equals("Edit Config")) {
                    menuState = lines.get(cursor.y).postSelectionText.equals("Delete Config") ? MenuState.DELETECONFIG : MenuState.EDITNEWCONFIG;

                    newLines = genLines(currentFilepath, "robot_info.txt");

                    if (newLines.isEmpty()) {
                        newLines.add(new GuiLine(" ", " ", " "));
                    }

                    super.setSelectionZoneHeight(newLines.size(), newLines);
                    cursor.y = 0;
                } else if (lines.get(cursor.y).postSelectionText.equals("New Config")) {
                    menuState = MenuState.NEWCONFIG;

                    newLines.add(new GuiLine("###############", "Config Name"));
                    newLines.add(new GuiLine("###############", "Done"));
                    super.setSelectionZoneWidthAndHeight(newLines.get(0).selectionZoneText.length(), newLines.size(), newLines);
                    cursor.y = 0;
                }

                break;

            //delete option selected. Selected config will be deleted.
            case DELETECONFIG:
                menuState = MenuState.ROBOTDIRSELECTED;

                String configPath = currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt";
                File config = new File(configPath);

                if (!config.delete()) {
                    Log.i("File Issues", "Problem deleting file at " + configPath);
                }

                newLines.add(new GuiLine("#", "New Config"));
                newLines.add(new GuiLine("#", "Edit Config"));
                newLines.add(new GuiLine("#", "Delete Config"));
                newLines.addAll(genLines(currentFilepath, "robot_info.txt"));

                super.setSelectionZoneHeight(newLines.size(), newLines);
                cursor.y = 0;
                break;

            //new option selected. will prompt user to enter a name for the config file.
            case NEWCONFIG:

                if (!lines.get(cursor.y).postSelectionText.equals("Done")) {
                    char[] currentNameText = lines.get(0).selectionZoneText.toCharArray();
                    currentNameText[cursor.x] = VALID_CHARS.get((VALID_CHARS.indexOf(currentNameText[cursor.x]) + 1) % VALID_CHARS.size());
                    newLines.add(new GuiLine(new String(currentNameText), "Config Name"));
                    newLines.add(new GuiLine("###############", "Done"));
                    super.setLines(newLines);
                } else {
                    menuState = MenuState.ROBOTDIRSELECTED;

                    String newConfigName = parseName(lines.get(0).selectionZoneText);

                    if (!newConfigName.equals("robot_info") && !newConfigName.equals("")) {
                        File configFile = new File(currentFilepath + "/" + newConfigName + ".txt");
                        try {
                            FileOutputStream fileoutput = new FileOutputStream(configFile);
                            PrintStream ps = new PrintStream(fileoutput);
                            ps.println();
                            ps.close();
                            fileoutput.close();
                            selectedConfigPath = configFile.getPath(); //VERY IMPORTANT
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (newConfigName.equals("robot_info")) {
                        Log.e("Oh No", "Hacker Alert");
                    }

                    newLines.add(new GuiLine("#", "New Config"));
                    newLines.add(new GuiLine("#", "Edit Config"));
                    newLines.add(new GuiLine("#", "Delete Config"));
                    newLines.addAll(genLines(currentFilepath, "robot_info.txt"));
                    super.setSelectionZoneWidthAndHeight(newLines.get(0).selectionZoneText.length(), newLines.size(), newLines);
                    cursor.x = 0;
                    cursor.y = 0;
                }

                break;

            //edit option selected. Selected config will be edited
            case EDITNEWCONFIG:

                selectedConfigPath = currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt";


                genDefaultConfigMap();

                //remember to fix this line so it works with newconfig also

                menuState = MenuState.SELECTSUBSYSTEMCONFIG;
                for (String str : SubSystem.configs.keySet()) {
                    newLines.add(new GuiLine("#", str));
                }
                newLines.add(new GuiLine("#", "Done"));
                super.setSelectionZoneHeight(newLines.size(), newLines);
                cursor.x = 0;
                cursor.y = 0;

                break;

            case SELECTSUBSYSTEMCONFIG:
                if (!lines.get(cursor.y).postSelectionText.equals("Done")) {
                    menuState = MenuState.CONFIGOPTIONS;
                    selectedSubsystemName = lines.get(cursor.y).postSelectionText;
                    newLines = genConfigLines(selectedSubsystemName);
                    super.setSelectionZoneHeight(newLines.size(), newLines);
                } else {
                    menuState = MenuState.ROBOTDIRSELECTED;

                    writeConfigFile();

                    newLines.add(new GuiLine("#", "New Config"));
                    newLines.add(new GuiLine("#", "Edit Config"));
                    newLines.add(new GuiLine("#", "Delete Config"));
                    newLines.addAll(genLines(currentFilepath, "robot_info.txt"));
                    super.setSelectionZoneHeight(newLines.size(), newLines);
                    cursor.y = 0;
                }
                break;

            case CONFIGOPTIONS:
                if (!lines.get(cursor.y).postSelectionText.equals("Done")) {
                    String unparsedLine = lines.get(cursor.y).postSelectionText;
                    String currentOptionValue = unparsedLine.substring(unparsedLine.indexOf('|') + 1).replace(" ", "");
                    String currentOptionName = unparsedLine.substring(0, unparsedLine.indexOf('|')).replace(" ", "");
                    List<ConfigParam> subsystemParams = SubSystem.configs.get(selectedSubsystemName);
                    ConfigParam currentParam = new ConfigParam("", new String[]{}, "");
                    for (ConfigParam param : subsystemParams) {
                        if (param.name.equals(currentOptionName)) {
                            currentParam = param;
                            break;
                        }
                    }

                    lines.set(cursor.y, new GuiLine("#", currentOptionName + " | " + currentParam.options.get((currentParam.options.indexOf(currentOptionValue) + 1) % currentParam.options.size())));

                } else {
                    menuState = MenuState.SELECTSUBSYSTEMCONFIG;

                    updateConfigMap(lines);

                    for (String str : SubSystem.configs.keySet()) {
                        newLines.add(new GuiLine("#", str));
                    }
                    newLines.add(new GuiLine("#", "Done"));
                    super.setSelectionZoneHeight(newLines.size(), newLines);
                    cursor.y = 0;

                    selectedSubsystemName = "";
                }

                break;
        }
    }

    @Override
    public void onButton(String name, Button button) {



        if(name.equals(ConfigCursor.SELECT)) {

        }
    }


    /*
    *
    *
    *
    *   Functions below this point
    *
    *
    *
    */

    private void genDefaultConfigMap() {
        for(String subsystem : SubSystem.configs.keySet()) {
            List<String> settings = new ArrayList<>();
            for(ConfigParam param : SubSystem.configs.get(subsystem)) {
                settings.add(param.name+':'+param.defaultOption);
            }
            config.put(subsystem,settings);
        }
    }

    private void updateConfigMap(ArrayList<GuiLine> newConfig) {
        newConfig.remove(newConfig.size()-1); //gets rid of the Done line

        List<String> parsedConfig = new ArrayList<>();

        for(GuiLine line : newConfig) {
            String unparsedLine = line.postSelectionText;
            String currentOptionName = unparsedLine.substring(0,unparsedLine.indexOf('|')).replace(" ","");
            String currentOptionValue = unparsedLine.substring(unparsedLine.indexOf('|')+1).replace(" ","");

            parsedConfig.add(currentOptionName+':'+currentOptionValue);
        }

        config.remove(selectedSubsystemName);
        config.put(selectedSubsystemName,parsedConfig);
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
                for(String setting : config.get(subsystem)) {
                    sb.append(subsystem);
                    sb.append(':');
                    sb.append(setting);
                    sb.append("\r\n");
                }
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

/*
        FileWriter fileWriter = null;

        try {

            File configFile = new File(selectedConfigPath);

            while(!configFile.delete());

            fileWriter = new FileWriter(selectedConfigPath,true);

            for(String subsystem : config.keySet()) {
                for(String setting : config.get(subsystem)) {
                    fileWriter.write(subsystem+':'+setting+"\r\n");
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try {fileWriter.close();}
            catch(Exception e) {
                e.printStackTrace();
                Log.wtf(":(",":(");
            }
        }
        */
    }

    private ArrayList<GuiLine> genConfigLines(String subSystemName) {
        ArrayList<GuiLine> output = new ArrayList<>();
        for(ConfigParam param : SubSystem.configs.get(subSystemName)) {
            output.add(new GuiLine("#",param.name+ " | " + param.defaultOption));
        }
        output.add(new GuiLine("#","Done"));
        return output;
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
}
