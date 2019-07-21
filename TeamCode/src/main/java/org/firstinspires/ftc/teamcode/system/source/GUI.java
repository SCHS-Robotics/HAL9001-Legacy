/*
 * Filename: GUI.java
 * Author: Cole Savage and Dylan Zueck
 * Team Name: Level Up, Crow Force
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.system.source;

import android.util.ArrayMap;

import org.firstinspires.ftc.teamcode.season.robot.Cygnus;
import org.firstinspires.ftc.teamcode.util.exceptions.NotBooleanInputException;
import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GUI {

    private Menu activeMenu;
    private Map<String,Menu> menus;
    public Robot robot;
    private Cursor cursor;

    private int cursorBlinkState, activeMenuIdx;
    private double lastBlinkTimeMs;

    private ArrayList<String> menuKeys;

    private boolean flag;

    private CustomizableGamepad inputs = new CustomizableGamepad();

    private static final String CYCLE_MENUS = "CycleMenus";

    public GUI(Robot robot, Cursor cursor, Button flipMenu) {
        this.cursor = cursor;
        this.robot = robot;
        this.menus = new HashMap<>();

        menuKeys = new ArrayList<>();
        
        if(flipMenu.isBoolean) {
            this.inputs.addButton(CYCLE_MENUS, flipMenu);
        }
        else {
            throw new NotBooleanInputException("A non-boolean input was passed to the controller as a boolean input");
        }

        cursorBlinkState = 0;
        lastBlinkTimeMs = System.currentTimeMillis();
        flag = true;
        activeMenuIdx = 0;
    }

    protected void start(){
        for(Menu m : menus.values()) {
            m.init(cursor);
        }
    }

    protected void drawCurrentMenu(){
        cursor.update();
        if(inputs.getBooleanInput(CYCLE_MENUS) && flag){
            activeMenuIdx++;
            activeMenuIdx = activeMenuIdx % menuKeys.size();
            setActiveMenu(menuKeys.get(activeMenuIdx));
        }
        else if(!inputs.getBooleanInput(CYCLE_MENUS) && !flag) {
            flag = true;
        }
        activeMenu.render();
        robot.telemetry.update();
    }

    protected void stop() {
        for (Menu m : menus.values()) {
            m.stop();
        }
        clearScreen();
    }

    public void addMenu(String name, Menu menu){
        menus.put(name, menu);
        menuKeys.add(name);
    }

    public void removeMenu(String name) {
        if(menuKeys.indexOf(name) > activeMenuIdx && activeMenuIdx != menuKeys.size()-1){
            activeMenuIdx--;
        }
        menuKeys.remove(name);
        menus.remove(name);
        activeMenuIdx = activeMenuIdx % menuKeys.size();
        setActiveMenu(menuKeys.get(activeMenuIdx));
    }

    public void setActiveMenu(String menuName){
        this.activeMenu = menus.get(menuName);
        menus.get(menuName).open();
        cursor.setCurrentMenu(menus.get(menuName));
    }

    //TODO, make this not burn our eyes
    private void blinkCursor(GuiLine line) {
        ArrayList<Character> cursorLineChars = new ArrayList<>();
        char[] chars = line.selectionZoneText.toCharArray();
        for(int i = 0; i < chars.length; i++) {
            if(i == cursor.getX()) {
                switch (cursorBlinkState) {
                    case 0:
                        cursorLineChars.add(cursor.getCursorIcon());
                        break;
                    case 1:
                    case 3:
                        cursorLineChars.add(' ');
                        break;
                    default:
                        cursorLineChars.add(chars[i]);
                        break;
                }
            }
            else {
                cursorLineChars.add(chars[i]);
            }
        }
        StringBuilder sb = new StringBuilder();
        for(Character ch: cursorLineChars){
            sb.append(ch);
        }
        String selectionZoneText = sb.toString();
        robot.telemetry.addLine(line.getLineTextReplaceSelectionZoneText(selectionZoneText));

        if(System.currentTimeMillis() - lastBlinkTimeMs >= cursor.getBlinkSpeedMs()){
            cursorBlinkState++;
            cursorBlinkState = cursorBlinkState % 4;
            lastBlinkTimeMs = System.currentTimeMillis();
        }
    }

    protected void clearScreen() {
        robot.telemetry.update();
        robot.telemetry.update();
    }

    protected void displayLine(GuiLine line, int lineNumber){
        if(cursor.getY() == lineNumber){
            blinkCursor(line);
        }
        else {
            robot.telemetry.addLine(line.getLineText());
        }
    }
}
