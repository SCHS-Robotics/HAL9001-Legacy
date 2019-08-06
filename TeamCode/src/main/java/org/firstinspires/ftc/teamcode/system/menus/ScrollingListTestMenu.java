/*
 * Filename: ScrollingListTestMenu.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.system.menus;

import org.firstinspires.ftc.teamcode.system.source.GUI;
import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;
import org.firstinspires.ftc.teamcode.system.source.ScrollingListMenu;

import java.util.ArrayList;

/**
 * A scrolling list menu created for testing purposes.
 */
public class ScrollingListTestMenu extends ScrollingListMenu {

    //The initial lines to be displayed on the menu.
    private static final GuiLine[] STARTING_LINES = new GuiLine[] {
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: ")
    };

    /**
     * Ctor for ScrollingListTestMenu.
     *
     * @param gui - The gui used to render the menu.
     */
    public ScrollingListTestMenu(GUI gui) {
        super(gui, STARTING_LINES, 1, 6);
    }
    
    @Override
    public void onSelect() {
        if(cursor.y == 0){
            addLine();
        }
        else if(cursor.y == 1 && super.getSelectionZoneHeight() > 2){
            removeLine();
        }
    }

    /**
     * Adds a single line to the end of the list of lines in the menu.
     */
    private void addLine(){
        ArrayList<GuiLine> newLines = lines;
        lines.add(new GuiLine("Y", "Lines: "));
        super.setSelectionZoneHeight(super.getSelectionZoneHeight() + 1, newLines);
    }

    /**
     * Removes a single line to the end of the list of lines in the menu.
     */
    private void removeLine(){
        ArrayList<GuiLine> newLines = lines;
        lines.remove(cursor.getY());
        super.setSelectionZoneHeight(super.getSelectionZoneHeight() - 1, newLines);
    }
}
