package org.firstinspires.ftc.teamcode.system.menus;

import org.firstinspires.ftc.teamcode.system.source.BaseDisplayMenu;
import org.firstinspires.ftc.teamcode.system.source.GUI;
import org.firstinspires.ftc.teamcode.system.subsystems.cursors.DefaultCursor;
import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;
import org.firstinspires.ftc.teamcode.util.misc.Button;

import java.util.ArrayList;
import java.util.List;

public class DisplayMenu extends BaseDisplayMenu {

    public DisplayMenu(GUI gui) {
        super(gui, new DefaultCursor(gui.robot,500), new GuiLine[]{});
    }

    @Override
    public void onSelect() {
        menuDown();
    }

    @Override
    public void onButton(String name, Button button) { }

    /**
     * Adds a single line to the end of the list of lines in the menu.
     */
    public void addLine(String caption, Object data){
        List<GuiLine> newLines = lines;
        lines.add(new GuiLine("", caption+": "+data.toString(),""));
        super.setSelectionZoneHeight(super.getSelectionZoneHeight() + 1, newLines);
    }

    public void clear() {
        setSelectionZoneHeight(0,new GuiLine[]{});
    }
}
