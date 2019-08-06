package org.firstinspires.ftc.teamcode.system.source;

import android.util.Log;

import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;

import java.util.ArrayList;

public abstract class BaseDisplayMenu extends Menu {

    private int menuNumber;

    private CustomizableGamepad inputs;

    private final String SELECT = "select";

    public BaseDisplayMenu(GUI gui, GuiLine[] startingLines) {
        super(gui,startingLines,0,0);
        menuNumber = 0;

        inputs = new CustomizableGamepad(gui.robot);
        inputs.addButton(SELECT,new Button(1, Button.BooleanInputs.a));
    }

    public BaseDisplayMenu(GUI gui, ArrayList<GuiLine> startingLines) {
        super(gui,startingLines,0,0);
        menuNumber = 0;

        inputs = new CustomizableGamepad(gui.robot);
        inputs.addButton(SELECT,new Button(1, Button.BooleanInputs.a));
    }

    @Override
    protected void init(Cursor cursor) {
        super.cursor = cursor;
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