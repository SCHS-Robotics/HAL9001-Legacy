/*
 * Filename: ScrollingListMenu.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.system.source;

import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;

import java.util.ArrayList;

/**
 * An abstract class extending Menu.java that represents a common typ of menu.
 */
public abstract class ScrollingListMenu extends Menu {

    //The current "level" of screen in the menu. If the number of lines in the menu exceeds the maximum number, menunumber will increase by one for every screen the menu takes up.
    private int menuNumber;

    /**
     * Ctor for menu class.
     *
     * @param gui - The GUI that will be used to render the menu.
     * @param startingLines - The list of lines that will be displayed when the menu is first rendered.
     * @param selectionZoneWidth - The maximum x value that the cursor will be able to travel to inside the selection zone.
     *                             Note: This is not the actual width of the zone itself, but a boundary for the index.
     * @param selectionZoneHeight - The maximum y value that the cursor will be able to travel to inside the selection zone.
     *                              Note: This is not the actual height of the zone itself, but a boundary for the index.
     */
    public ScrollingListMenu(GUI gui, ArrayList<GuiLine> startingLines, int selectionZoneWidth, int selectionZoneHeight){
        super(gui, startingLines, selectionZoneWidth, selectionZoneHeight);
        menuNumber = 0;
    }

    /**
     * Ctor for menu class.
     *
     * @param gui - The GUI that will be used to render the menu.
     * @param startingLines - The list of lines that will be displayed when the menu is first rendered.
     * @param selectionZoneWidth - The maximum x value that the cursor will be able to travel to inside the selection zone.
     *                             Note: This is not the actual width of the zone itself, but a boundary for the index.
     * @param selectionZoneHeight - The maximum y value that the cursor will be able to travel to inside the selection zone.
     *                              Note: This is not the actual height of the zone itself, but a boundary for the index.
     */
    public ScrollingListMenu(GUI gui, GuiLine[] startingLines, int selectionZoneWidth, int selectionZoneHeight){
        super(gui, startingLines, selectionZoneWidth, selectionZoneHeight);
        menuNumber = 0;
    }

    @Override
    protected void init(Cursor cursor) {
        super.cursor = cursor;
        super.cursor.doBlink = true;
    }

    @Override
    protected void open() {
        super.cursor.doBlink = true;
        menuNumber = 0;
        cursor.y = 0;
    }

    @Override
    protected void render() {
        displayCurrentMenu();
    }

    @Override
    protected void stop() {

    }

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
}
