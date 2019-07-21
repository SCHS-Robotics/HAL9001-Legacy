/*
 * Filename: GuiLine.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.util.gui_lib;

import org.firstinspires.ftc.teamcode.system.source.Menu;
import org.firstinspires.ftc.teamcode.util.exceptions.InvalidSelectionZoneException;

public class GuiLine {
    public String selectionZoneText;
    public String postSelectionText;
    public String divider;
    private boolean hasDivider;

    public GuiLine(Menu menu, String selectionZoneText, String postSelectionText){
        this.selectionZoneText = selectionZoneText;
        this.postSelectionText= postSelectionText;
        hasDivider = false;
        if(menu.getSelectionZoneHeight() != selectionZoneText.length()){
            throw new InvalidSelectionZoneException("The selection zone does not match the given selection zone text size.");
        }
    }

    public GuiLine(String selectionZoneText, String postSelectionText){
        this.selectionZoneText = selectionZoneText;
        this.postSelectionText= postSelectionText;
        hasDivider = false;
    }

    public GuiLine(String selectionZoneText, String postSelectionText, String divider){
        this.selectionZoneText = selectionZoneText;
        this.postSelectionText= postSelectionText;
        this.divider = divider;
        hasDivider = true;
    }

    public GuiLine(Menu menu, String selectionZoneText, String postSelectionText, String divider){
        this.selectionZoneText = selectionZoneText;
        this.postSelectionText= postSelectionText;
        this.divider = divider;
        hasDivider = true;
        if(menu.getSelectionZoneHeight() != selectionZoneText.length()){
            throw new InvalidSelectionZoneException("The selection zone does not match the given selection zone text size.");
        }
    }

    public String getLineText() {

        if (hasDivider) {
            return selectionZoneText + divider + postSelectionText;
        } else {
            return selectionZoneText + "| " + postSelectionText;
        }
    }

    public String getLineTextReplaceSelectionZoneText(String selectionZoneText){
        if(hasDivider){
            return selectionZoneText + divider + postSelectionText;
        }
        else {
            return selectionZoneText + "| " + postSelectionText;
        }
    }

    public boolean checkSelectionSize(Menu menu){
        return menu.getSelectionZoneHeight() == selectionZoneText.length();
    }

    public boolean checkSelectionSize(int wantedSize){
        return wantedSize == selectionZoneText.length();
    }

    public void testSelectionSize(int selectionWidth){

    }
}
