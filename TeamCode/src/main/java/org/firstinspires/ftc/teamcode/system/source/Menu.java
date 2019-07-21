/*
 * Filename: Menu.java
 * Author: Cole Savage and Dylan Zueck
 * Team Name: Level Up, Crow Force
 * Date: 7/20/19
 */
package org.firstinspires.ftc.teamcode.system.source;

import org.firstinspires.ftc.teamcode.util.exceptions.InvalidSelectionZoneException;
import org.firstinspires.ftc.teamcode.util.exceptions.WrongSkyscraperBlueprintException;
import org.firstinspires.ftc.teamcode.util.gui_lib.GuiLine;

import java.util.ArrayList;

public abstract class Menu {
    public GUI gui;
    private int selectionZoneWidth, selectionZoneHeight;
    public Cursor cursor;
    protected ArrayList<GuiLine> lines;
    public static final int MAXLINESPERSCREEN = 8;

    public Menu(GUI gui, GuiLine[] startingLines, int selectionZoneWidth, int selectionZoneHeight) {
        this.gui = gui;
        if(selectionZoneWidth < 0 || selectionZoneHeight < 0) {
            throw new InvalidSelectionZoneException("Error: Invalid selection zone");
        }
        this.selectionZoneWidth = selectionZoneWidth;
        this.selectionZoneHeight = selectionZoneHeight;

        setLines(startingLines);
    }

    public Menu(GUI gui, ArrayList<GuiLine> startingLines, int selectionZoneWidth, int selectionZoneHeight) {
        this.gui = gui;
        if(selectionZoneWidth < 0 || selectionZoneHeight < 0) {
            throw new InvalidSelectionZoneException("Error: Invalid selection zone");
        }
        this.selectionZoneWidth = selectionZoneWidth;
        this.selectionZoneHeight = selectionZoneHeight;

        setLines(startingLines);
    }

    protected abstract void init(Cursor cursor); //runs once when first created

    protected abstract void open(); //runs when menu is opened

    public abstract void onSelect();

    protected abstract void render(); //runs every frame of update

    protected abstract void stop(); //runs when gui is stopped.

    protected void displayLine(GuiLine line, int lineNumber){
        gui.displayLine(line, lineNumber);
    }

    public int getSelectionZoneWidth() {
        return selectionZoneWidth;
    }

    public int getSelectionZoneHeight() {
        return selectionZoneHeight;
    }

    public void setSelectionZoneWidth(int selectionZoneWidth, ArrayList<GuiLine> newLines){
        this.selectionZoneWidth = selectionZoneWidth;
        for(GuiLine line: newLines) {
            if (!line.checkSelectionSize(this.getSelectionZoneWidth())) {
                throw new InvalidSelectionZoneException("Selection zone text width must match menu selection zone width");
            }
        }
        setLines(newLines);
    }

    public void setSelectionZoneWidth(int selectionZoneWidth, GuiLine[] newLines){
        this.selectionZoneWidth = selectionZoneWidth;
        for(GuiLine line: newLines) {
            if (!line.checkSelectionSize(this.getSelectionZoneWidth())) {
                throw new InvalidSelectionZoneException("Selection zone text width must match menu selection zone width");
            }
        }
        setLines(newLines);
    }

    public void setSelectionZoneHeight(int selectionZoneHeight, ArrayList<GuiLine> newLines) {
        this.selectionZoneHeight = selectionZoneHeight;
        if(newLines.size() != selectionZoneHeight){
            throw new WrongSkyscraperBlueprintException("New lines do not match the height of selection zone");
        }
        setLines(newLines);
    }

    public void setSelectionZoneHeight(int selectionZoneHeight, GuiLine[] newLines) {
        this.selectionZoneHeight = selectionZoneHeight;
        if(newLines.length != selectionZoneHeight){
            throw new WrongSkyscraperBlueprintException("New lines do not match the height of selection zone");
        }
        setLines(newLines);
    }

    public void setLines(GuiLine[] lines){
        if(lines.length == selectionZoneHeight) {
            this.lines = new ArrayList<>();
            for (GuiLine line : lines) {
                if (!line.checkSelectionSize(this.getSelectionZoneHeight())) {
                    throw new InvalidSelectionZoneException("Selection zone text width must match menu selection zone width");
                }
                this.lines.add(line);
            }
        }
        else {
            throw new WrongSkyscraperBlueprintException("New lines do not match the height of selection zone");
        }
    }

    public void setLines(ArrayList<GuiLine> lines){
        if(lines.size() == selectionZoneHeight) {
            for(GuiLine line: lines) {
                if (!line.checkSelectionSize(this.getSelectionZoneWidth())) {
                    throw new InvalidSelectionZoneException("Selection zone text width must match menu selection zone width");
                }
            }
            this.lines = lines;
        }
        else {
            throw new WrongSkyscraperBlueprintException("New lines do not match the height of selection zone");
        }
    }
}