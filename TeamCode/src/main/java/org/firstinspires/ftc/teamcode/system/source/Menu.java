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

/**
 * An abstract class representing a menu that can be displayed on the driver station.
 */
public abstract class Menu {
    public GUI gui;
    private int selectionZoneWidth, selectionZoneHeight;
    public Cursor cursor;
    protected ArrayList<GuiLine> lines;
    protected static final int MAXLINESPERSCREEN = 8;

    /**
     * Ctor for menu class.
     *
     * @param gui - The GUI that will be used to render the menu.
     * @param startingLines - The list of lines that will be displayed when the menu is first rendered.
     * @param selectionZoneWidth - The maximum x value that the cursor will be able to travel to inside the selection zone.
     *                             Note: This is not the actual width of the zone itself, but a boundary for the index.
     * @param selectionZoneHeight - The maximum y value that the cursor will be able to travel to inside the selection zone.
     *                              Note: This is not the actual height of the zone itself, but a boundary for the index.
     *
     * @throws InvalidSelectionZoneException - Throws this exception if the provided selection zone is impossible.
     */
    public Menu(GUI gui, GuiLine[] startingLines, int selectionZoneWidth, int selectionZoneHeight) {
        this.gui = gui;
        if(selectionZoneWidth < 0 || selectionZoneHeight < 0) {
            throw new InvalidSelectionZoneException("Error: Invalid selection zone");
        }
        this.selectionZoneWidth = selectionZoneWidth;
        this.selectionZoneHeight = selectionZoneHeight;

        setLines(startingLines);
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
     *
     * @throws InvalidSelectionZoneException - Throws this exception if the provided selection zone is impossible.
     */
    public Menu(GUI gui, ArrayList<GuiLine> startingLines, int selectionZoneWidth, int selectionZoneHeight) {
        this.gui = gui;
        if(selectionZoneWidth < 0 || selectionZoneHeight < 0) {
            throw new InvalidSelectionZoneException("Error: Invalid selection zone");
        }
        this.selectionZoneWidth = selectionZoneWidth;
        this.selectionZoneHeight = selectionZoneHeight;

        setLines(startingLines);
    }

    /**
     * Abstract method that is called whenever a menu is initialized.
     *
     * @param cursor - The cursor object the menu will use.
     */
    protected abstract void init(Cursor cursor); //runs once when first created

    /**
     * Abstract method that is called whenever the menu is opened.
     */
    protected abstract void open(); //runs when menu is opened

    /**
     * Abstract method that is called whenever the cursor select button is pressed.
     */
    public abstract void onSelect();

    /**
     * Abstract method that is called every frame to render the menu.
     */
    protected abstract void render(); //runs every frame of update

    /**
     * Abstract method that is called when the gui is stopped.
     */
    protected abstract void stop(); //runs when gui is stopped.

    /**
     * Displays a line to the screen. Calls an identically-named method in GUI.
     *
     * @param line - The line to display.
     * @param lineNumber - The index of the line on the screen.
     */
    protected void displayLine(GuiLine line, int lineNumber){
        gui.displayLine(line, lineNumber);
    }

    /**
     * Gets the selection zone width.
     *
     * @return - The selection zone width.
     */
    public int getSelectionZoneWidth() {
        return selectionZoneWidth;
    }

    /**
     * Gets the selection zone height.
     *
     * @return - The selection zone height.
     */
    public int getSelectionZoneHeight() {
        return selectionZoneHeight;
    }

    /**
     * Sets the selection zone width.
     *
     * @param selectionZoneWidth - The desired selection zone width.
     * @param newLines - The list of lines to display on the menu with the new format.
     *
     * @throws InvalidSelectionZoneException - Throws this exception if the selection zone width is not equal to the length of the
     *                                         text to display in the selection zone.
     */
    public void setSelectionZoneWidth(int selectionZoneWidth, ArrayList<GuiLine> newLines){
        this.selectionZoneWidth = selectionZoneWidth;
        for(GuiLine line: newLines) {
            if (!line.checkSelectionSize(this.getSelectionZoneWidth())) {
                throw new InvalidSelectionZoneException("Selection zone text width must match menu selection zone width");
            }
        }
        setLines(newLines);
    }

    /**
     * Sets the selection zone width.
     *
     * @param selectionZoneWidth - The desired selection zone width.
     * @param newLines - The list of lines to display on the menu with the new format.
     *
     * @throws InvalidSelectionZoneException - Throws this exception if the selection zone width is not equal to the length of the
     *                                         text to display in the selection zone.
     */
    public void setSelectionZoneWidth(int selectionZoneWidth, GuiLine[] newLines){
        this.selectionZoneWidth = selectionZoneWidth;
        for(GuiLine line: newLines) {
            if (!line.checkSelectionSize(this.getSelectionZoneWidth())) {
                throw new InvalidSelectionZoneException("Selection zone text width must match menu selection zone width");
            }
        }
        setLines(newLines);
    }


    /**
     * Sets the selection zone height.
     *
     * @param selectionZoneHeight - The desired selection zone height.
     * @param newLines - The list of lines to display on the menu with the new format.
     *
     * @throws WrongSkyscraperBlueprintException - Throws this exception if there are not enough lines in newLines to fill the selection zone.
     */
    public void setSelectionZoneHeight(int selectionZoneHeight, ArrayList<GuiLine> newLines) {
        this.selectionZoneHeight = selectionZoneHeight;
        if(newLines.size() != selectionZoneHeight){
            throw new WrongSkyscraperBlueprintException("New lines do not match the height of selection zone");
        }
        setLines(newLines);
    }

    /**
     * Sets the selection zone height.
     *
     * @param selectionZoneHeight - The desired selection zone height.
     * @param newLines - The list of lines to display on the menu with the new format.
     *
     * @throws WrongSkyscraperBlueprintException - Throws this exception if there are not enough lines in newLines to fill the selection zone.
     */
    public void setSelectionZoneHeight(int selectionZoneHeight, GuiLine[] newLines) {
        this.selectionZoneHeight = selectionZoneHeight;
        if(newLines.length != selectionZoneHeight){
            throw new WrongSkyscraperBlueprintException("New lines do not match the height of selection zone");
        }
        setLines(newLines);
    }

    /**
     * Updates the menu's lines with new values.
     *
     * @param lines - The list of lines that the menu will display.
     *
     * @throws InvalidSelectionZoneException - Throws this exception if the selection zone width is not equal to the length of the
     *                                         text to display in the selection zone.
     * @throws WrongSkyscraperBlueprintException - Throws this exception if there are not enough lines in newLines to fill the selection zone.
     */
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

    /**
     * Updates the menu's lines with new values.
     *
     * @param lines - The list of lines that the menu will display.
     *
     * @throws InvalidSelectionZoneException - Throws this exception if the selection zone width is not equal to the length of the
     *                                         text to display in the selection zone.
     * @throws WrongSkyscraperBlueprintException - Throws this exception if there are not enough lines in newLines to fill the selection zone.
     */
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