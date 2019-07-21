/*
 * Filename: ScrollingListMenu.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.system.source;

import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;

import java.util.ArrayList;

public abstract class ScrollingListMenu extends Menu {

    private int menuNumber;

    public ScrollingListMenu(GUI gui, ArrayList<GuiLine> staringLines, int selectionZoneWidth, int selectionZoneHeight){
        super(gui, staringLines, selectionZoneWidth, selectionZoneHeight);
        menuNumber = 0;
    }

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

    public abstract void onSelect();

    public void menuUp(){
        if(menuNumber == 0) {
            menuNumber = (int) Math.floor((lines.size() * 1.0) / Menu.MAXLINESPERSCREEN);
            cursor.y = lines.size() - 1;
        }
        else {
            menuNumber--;
        }
    }

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

    private void displayCurrentMenu(){
        for (int i = menuNumber * Menu.MAXLINESPERSCREEN; i < menuNumber * Menu.MAXLINESPERSCREEN + Menu.MAXLINESPERSCREEN; i++) {
            super.displayLine(lines.get(i), i);
        }
    }
}
