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
    }

    @Override
    protected void open() {
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
    public abstract void onSelect();

    /**
     * Cycles to next part of menu if it goes off the top of the screen.
     */
    public void menuUp(){
        if(menuNumber == 0) {
            menuNumber = (int) Math.floor((lines.size() * 1.0) / Menu.MAXLINESPERSCREEN);
            cursor.y = lines.size() - 1;
        }
        else {
            menuNumber--;
        }
    }

    /**
     * Cycles to next part of menu if it goes off the bottom of the screen.
     */
    public void menuDown(){

        menuNumber++;
        menuNumber = menuNumber % Menu.MAXLINESPERSCREEN;

        if(menuNumber == (int) Math.floor((lines.size() * 1.0) / Menu.MAXLINESPERSCREEN)) {
            menuNumber = 0;
            cursor.y = 0;
        }
        else {
            menuNumber++;
        }
    }

    /**
     * Displays the current menu.
     */
    private void displayCurrentMenu(){
        for (int i = menuNumber * Menu.MAXLINESPERSCREEN; i < menuNumber * Menu.MAXLINESPERSCREEN + Menu.MAXLINESPERSCREEN; i++) {
            super.displayLine(lines.get(i), i);
        }
    }
}
