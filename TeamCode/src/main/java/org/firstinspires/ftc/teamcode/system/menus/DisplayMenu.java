package org.firstinspires.ftc.teamcode.system.menus;

import android.util.Log;

import org.firstinspires.ftc.teamcode.system.source.BaseDisplayMenu;
import org.firstinspires.ftc.teamcode.system.source.GUI;
import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;

import java.util.ArrayList;

public class DisplayMenu extends BaseDisplayMenu {

    public DisplayMenu(GUI gui) {
        super(gui, new GuiLine[]{});
    }

    @Override
    public void onSelect() {
        menuDown();
    }

    /**
     * Adds a single line to the end of the list of lines in the menu.
     */
    public void addLine(String caption, Object data){
        ArrayList<GuiLine> newLines = lines;
        lines.add(new GuiLine("", caption+": "+data.toString(),""));
        super.setSelectionZoneHeight(super.getSelectionZoneHeight() + 1, newLines);
    }

    public void clear() {
        setSelectionZoneHeight(0,new GuiLine[]{});
    }
}
