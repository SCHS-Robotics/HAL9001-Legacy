/*
 * Filename: ChannelDoesNotExistException.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.util.exceptions;

public class ChannelDoesNotExistException extends RuntimeException {
    public ChannelDoesNotExistException(String message, Throwable cause) {
        super(message,cause);
    }
}
