/*
 * Filename: Toggle.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/17/19
 */

package org.firstinspires.ftc.teamcode.util.misc;

/**
 * A toggle class used to add toggle switches.
 */
public class Toggle {

    //Boolean values representing the current state of the toggle and whether the toggling button has been released.
    private boolean currentState, flag;
    
    /**
     * Ctor for toggle class.
     * 
     * @param currentState - Initial toggle state.
     */
    public Toggle(boolean currentState){
        this.currentState = currentState;
        flag = true;
    }

    /**
     * Inverts current state if condition changes from false to true from the previous to the current function call.
     *
     * @param condition - The most recent state of the toggling condition.
     */
    public void updateToggle(boolean condition){
        if(condition && flag){ //when false turns to true
            currentState = !currentState;
            flag = false;
        }
        else if(!condition && !flag){ //when true turns to false
            flag = true;
        }
    }

    /**
     * Gets current state of the toggle
     *
     * @return - Returns current toggle state
     */
    public boolean getCurrentState(){
        return currentState;
    }
}