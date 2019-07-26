/*
 * Filename: InvalidSelectionZoneException.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.util.exceptions;

/**
 * An exception thrown if the specified command produces or requires an illegal selection zone.
 */
public class InvalidSelectionZoneException extends RuntimeException {

    /**
     * Ctor for InvalidSelectionZoneException.
     *
     * @param message - The message to print to the screen.
     */
    public InvalidSelectionZoneException(String message) {
        super(message);
    }
}
