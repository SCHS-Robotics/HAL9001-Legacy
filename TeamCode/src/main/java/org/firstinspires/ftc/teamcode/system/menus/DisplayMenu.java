/*
 * Filename: DisplayMenu.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/5/19
 */

package org.firstinspires.ftc.teamcode.system.menus;

import org.firstinspires.ftc.teamcode.system.source.BaseDisplayMenu;
import org.firstinspires.ftc.teamcode.system.source.GUI;
import org.firstinspires.ftc.teamcode.system.subsystems.cursors.DefaultCursor;
import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;
import org.firstinspires.ftc.teamcode.util.misc.Button;

import java.util.List;

/**
 * A menu class meant for displaying information similarly to telemetry.
 */
public class DisplayMenu extends BaseDisplayMenu {

    /**
     * Constructor for DisplayMenu.
     *
     * @param gui - The GUI used to render the menu.
     */
    public DisplayMenu(GUI gui) {
        super(gui, new DefaultCursor(gui.robot,500), new GuiLine[]{});
    }

    @Override
    public void onSelect() {
        menuDown();
    }

    @Override
    public void onButton(String name, Button button) {}

    /**
     * Adds a line with a caption and a data value to the end of the menu's lines.
     *
     * @param caption - The data's caption.
     * @param data - The data to print to the screen.
     */
    public void addData(String caption, Object data){
        List<GuiLine> newLines = lines;
        lines.add(new GuiLine("", caption+": "+data.toString(),""));
        super.setSelectionZoneHeight(super.getSelectionZoneHeight() + 1, newLines);
    }

    /**
     * Adds a line of text to the end of the menu's lines.
     *
     * @param text - The text to add.
     */
    public void addLine(String text) {
        List<GuiLine> newLines = lines;
        lines.add(new GuiLine("",text,""));
        super.setSelectionZoneHeight(super.getSelectionZoneHeight() + 1, newLines);
    }

    /**
     * Clears the screen.
     */
    public void clear() {
        super.setSelectionZoneHeight(0,new GuiLine[]{});
    }
}
