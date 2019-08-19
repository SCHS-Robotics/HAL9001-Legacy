/*
 * Filename: BaseDisplayMenu.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/5/19
 */

package org.firstinspires.ftc.teamcode.system.source;

import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;

import java.util.ArrayList;

/**
 * The base menu class for DisplayMenus to extend.
 */
public abstract class BaseDisplayMenu extends Menu {

    //The current "level" of screen in the menu. If the number of lines in the menu exceeds the maximum number, menunumber will increase by one for every screen the menu takes up.
    private int menuNumber;
    //The customizable gamepad storing the menu's scroll button.
    private CustomizableGamepad inputs;
    //The name of the menu's scroll button.
    private final String SELECT = "select";

    /**
     * Constructor for BaseDisplayMenu.
     *
     * @param gui - The GUI being used to render the menu.
     * @param cursor - The cursor being used in the menu.
     * @param startingLines - The menu's initial set of GuiLines.
     */
    public BaseDisplayMenu(GUI gui, Cursor cursor, GuiLine[] startingLines) {
        super(gui, cursor, startingLines,0,0);
        menuNumber = 0;

        inputs = new CustomizableGamepad(gui.robot);
        inputs.addButton(SELECT,new Button(1, Button.BooleanInputs.a));
    }

    /**
     * Constructor for BaseDisplayMenu.
     *
     * @param gui - The GUI being used to render the menu.
     * @param cursor - The cursor being used in the menu.
     * @param startingLines - The menu's initial set of GuiLines.
     */
    public BaseDisplayMenu(GUI gui, Cursor cursor, ArrayList<GuiLine> startingLines) {
        super(gui,cursor, startingLines,0,0);
        menuNumber = 0;

        inputs = new CustomizableGamepad(gui.robot);
        inputs.addButton(SELECT,new Button(1, Button.BooleanInputs.a));
    }

    @Override
    protected void init() {
        super.cursor.doBlink = false;
    }

    @Override
    protected void open() {
        super.cursor.doBlink = false;
    }

    @Override
    protected void render() {
        displayCurrentMenu();
    }

    @Override
    protected void stop() {}


    @Override
    public void menuUp(){

        menuNumber--;

        if(menuNumber == -1) {
            menuNumber = (int) Math.floor((lines.size() * 1.0) / Menu.MAXLINESPERSCREEN);
            cursor.y = Math.min(lines.size() - 1,(menuNumber*Menu.MAXLINESPERSCREEN)-1);
        }
    }

    @Override
    public void menuDown(){

        menuNumber++;

        if(menuNumber == (int) Math.ceil((lines.size() * 1.0) / Menu.MAXLINESPERSCREEN)) {
            menuNumber = 0;
            cursor.y = 0;
        }
    }

    /**
     * Displays the current menu.
     */
    private void displayCurrentMenu(){
        for (int i = menuNumber * Menu.MAXLINESPERSCREEN; i < Math.min(lines.size(),(menuNumber+1)*Menu.MAXLINESPERSCREEN); i++) {
            super.displayLine(lines.get(i), i);
        }
    }

    public void setSelect(Button selectButton) {
        inputs.removeButton(SELECT);
        inputs.addButton(SELECT,selectButton);
    }
}