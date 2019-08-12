package org.firstinspires.ftc.teamcode.util.debug;

import android.os.Environment;
import android.util.Log;

import org.firstinspires.ftc.teamcode.system.source.GUI;
import org.firstinspires.ftc.teamcode.system.source.ScrollingListMenu;
import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfigDebugMenu extends ScrollingListMenu {

    private enum MenuState {
        START, ROBOTDIRSELECTED, DELETECONFIG, EDITCONFIG, NEWCONFIG
    }

    private MenuState menuState;
    private String currentFilepath;
    private final ArrayList<Character> VALID_CHARS = string2ArrayList("#abcdefghijklmnopqrstuvwxyz0123456789");

    public ConfigDebugMenu(GUI gui) {
        super(gui,genLines(Environment.getExternalStorageDirectory().getPath()+"/System64/"),1,genLines(Environment.getExternalStorageDirectory().getPath()+"/System64/").size());
        menuState = MenuState.START;
        currentFilepath = Environment.getExternalStorageDirectory().getPath()+"/System64/";
    }

    /*
    Menu States:
    0 = startup, shows all robot directories
    1 = robot directory has been selected, showing all config files in the directory and new/edit/delete options
    2 = delete option selected. Showing all config files. Selected config will be deleted
    3 = edit option selected. Showing all config files. Selected config will be edited
    4 = new option selected. will prompt user to enter a name for the config file
     */

    @Override
    public void onSelect() {
        if(menuState == MenuState.START) {
            menuState = MenuState.ROBOTDIRSELECTED;
            currentFilepath += lines.get(cursor.y).postSelectionText;
            
            ArrayList<GuiLine> newLines = genLines(currentFilepath,"robot_info.txt");
            newLines.add(new GuiLine("#","New Config"));
            newLines.add(new GuiLine("#","Edit Config"));
            newLines.add(new GuiLine("#","Delete Config"));
            super.setSelectionZoneHeight(newLines.size(),newLines);
            cursor.y = 0;
        }
        else if(menuState == MenuState.ROBOTDIRSELECTED && (lines.get(cursor.y).postSelectionText.equals("Delete Config") || lines.get(cursor.y).postSelectionText.equals("Edit Config"))) {
            menuState = lines.get(cursor.y).postSelectionText.equals("Delete Config") ? MenuState.DELETECONFIG : MenuState.EDITCONFIG;
            ArrayList<GuiLine> newLines = genLines(currentFilepath,"robot_info.txt");
            if(newLines.isEmpty()) {
                newLines.add(new GuiLine(" "," "," "));
            }

            super.setSelectionZoneHeight(newLines.size(),newLines);
            cursor.y = 0;
        }
        else if(menuState == MenuState.ROBOTDIRSELECTED && lines.get(cursor.y).postSelectionText.equals("New Config")) {
            menuState = MenuState.NEWCONFIG;
            ArrayList<GuiLine> lines = new ArrayList<>();
            lines.add(new GuiLine("###############","Config Name"));
            lines.add(new GuiLine("###############","Done"));
            super.setSelectionZoneWidthAndHeight(lines.get(0).selectionZoneText.length(),lines.size(),lines);
            cursor.y = 0;
            /*
            File configFile = new File(currentFilepath+"/test.txt");
            try {
                FileOutputStream fileoutput = new FileOutputStream(configFile);
                PrintStream ps = new PrintStream(fileoutput);
                ps.println();
                ps.close();
                fileoutput.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        */
        }
        else if(menuState == MenuState.NEWCONFIG && !lines.get(cursor.y).postSelectionText.equals("Done")) {
            char[] currentNameText = lines.get(0).selectionZoneText.toCharArray();
            currentNameText[cursor.x] = VALID_CHARS.get((VALID_CHARS.indexOf(currentNameText[cursor.x])+1)%VALID_CHARS.size());
            ArrayList<GuiLine> lines = new ArrayList<>();
            lines.add(new GuiLine(new String(currentNameText),"Config Name"));
            lines.add(new GuiLine("###############","Done"));
            super.setSelectionZoneWidthAndHeight(lines.get(0).selectionZoneText.length(),lines.size(),lines);
        }
        else if(menuState == MenuState.NEWCONFIG && lines.get(cursor.y).postSelectionText.equals("Done")) {
            menuState = MenuState.ROBOTDIRSELECTED;

            String newConfigName = parseName(lines.get(0).selectionZoneText);

            ArrayList<GuiLine> newLines = genLines(currentFilepath,"robot_info.txt");
            newLines.add(new GuiLine("#","New Config"));
            newLines.add(new GuiLine("#","Edit Config"));
            newLines.add(new GuiLine("#","Delete Config"));
            super.setSelectionZoneWidthAndHeight(newLines.get(0).selectionZoneText.length(),newLines.size(),newLines);
            cursor.x = 0;
            cursor.y = 0;

            if(!newConfigName.equals("robot_info") && !newConfigName.equals("")) {
                File configFile = new File(currentFilepath+"/"+newConfigName+".txt");
                try {
                    FileOutputStream fileoutput = new FileOutputStream(configFile);
                    PrintStream ps = new PrintStream(fileoutput);
                    ps.println();
                    ps.close();
                    fileoutput.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String parseName(String input) {
        ArrayList<Character> outputChars = new ArrayList<>();

        boolean betweenLetters = false;

        //removes '#'s at beginning and end of text
        for(int i = 0; i < input.length(); i++) {
            if(input.charAt(i) != '#' && !betweenLetters) {
                outputChars.add(input.charAt(i));
                betweenLetters = true;
            }
            else if(input.charAt(i) != '#' && betweenLetters) {
                outputChars.add(input.charAt(i));
                break;
            }
            else if(betweenLetters) {
                outputChars.add(input.charAt(i));
            }
        }

        String parsedInput = arrayList2String(outputChars);
        parsedInput = parsedInput.replace('#','_');

        return parsedInput;
    }

    private String arrayList2String(ArrayList<Character> input) {
        StringBuilder builder = new StringBuilder(input.size());
        for(char ch : input)
        {
            builder.append(ch);
        }
        return builder.toString();
    }

    private ArrayList<Character> string2ArrayList(String input) {
        ArrayList<Character> output = new ArrayList<>();
        for(char c : input.toCharArray()) {
            output.add(c);
        }
        return output;
    }

    private static ArrayList<GuiLine> genLines(String filePath) {
        File rootDirectory = new File(filePath);
        File[] dirs = rootDirectory.listFiles();
        ArrayList<GuiLine> startingLines = new ArrayList<>();
        for(File dir : dirs) {
            startingLines.add(new GuiLine("#",dir.getName()));
        }
        return startingLines;
    }

    private static ArrayList<GuiLine> genLines(String filePath, String exclude) {
        File rootDirectory = new File(filePath);
        File[] dirs = rootDirectory.listFiles();
        ArrayList<GuiLine> startingLines = new ArrayList<>();
        for(File dir : dirs) {
            if (!dir.getName().equals(exclude)) {
                startingLines.add(new GuiLine("#", dir.getName()));
            }
        }
        return startingLines;
    }
}
