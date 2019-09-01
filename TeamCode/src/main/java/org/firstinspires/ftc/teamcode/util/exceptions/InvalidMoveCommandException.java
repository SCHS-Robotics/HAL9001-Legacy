/*
 * Filename: NotARealGamepadException.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/21/19
 */

package org.firstinspires.ftc.teamcode.util.exceptions;

public class InvalidMoveCommandException extends RuntimeException {
    public InvalidMoveCommandException(String message) {
        super(message);
    }
}
