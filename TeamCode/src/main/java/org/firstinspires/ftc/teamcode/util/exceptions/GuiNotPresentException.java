/*
 * Filename: GuiNotPresentException.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 8/31/19
 */

package org.firstinspires.ftc.teamcode.util.exceptions;

/**
 * An exception thrown if a channel in a color image does not exist.
 */
public class GuiNotPresentException extends RuntimeException {

    /**
     * Ctor for ChannelDoesNotExistException.
     *
     * @param message - The message to print to the screen.
     */
    public GuiNotPresentException(String message) {
        super(message);
    }
}
