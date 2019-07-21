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

public class ScrollingListTestMenu extends ScrollingListMenu {

    private static final GuiLine[] STARTING_LINES = new GuiLine[] {
        new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: ")
    };

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
    
    private void addLine(){
        ArrayList<GuiLine> newLines = lines;
        lines.add(new GuiLine("X", "Lines: "));
        super.setSelectionZoneHeight(super.getSelectionZoneHeight() + 1, newLines);
    }
    
    private void removeLine(){
        ArrayList<GuiLine> newLines = lines;
        lines.add(new GuiLine("X", "Lines: "));
        super.setSelectionZoneHeight(super.getSelectionZoneHeight() - 1, newLines);
    }
}
