/*
 * Filename: Cursor.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.system.source;

public abstract class Cursor {
    public int x, y, blinkSpeedMs;
    private char cursorIcon;
    public Menu currentMenu;
    public Cursor(int x, int y, int blinkSpeedMs, char cursorIcon) {
        this.blinkSpeedMs = blinkSpeedMs;
        this.x = x;
        this.y = y;
        this.cursorIcon = cursorIcon;
    }

    public Cursor(int blinkSpeedMs, char cursorIcon) {
        this.blinkSpeedMs = blinkSpeedMs;
        this.x = x;
        this.y = y;
        this.cursorIcon = cursorIcon;
    }

    public Cursor(int blinkSpeedMs) {
        this.blinkSpeedMs = blinkSpeedMs;
        this.x = 0;
        this.y = 0;
        this.cursorIcon = '_';
    }

    public Cursor(int x, int y, int blinkSpeedMs) {
        this.blinkSpeedMs = blinkSpeedMs;
        this.x = x;
        this.y = y;
        this.cursorIcon = '_';
    }

    public char getCursorIcon() {
        return cursorIcon;
    }

    protected void setCurrentMenu(Menu currentMenu) {
        this.currentMenu = currentMenu;
    }

    public void setCursorIcon(char cursorIcon) {
        this.cursorIcon = cursorIcon;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getBlinkSpeedMs() {
        return blinkSpeedMs;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public abstract void update();
}
