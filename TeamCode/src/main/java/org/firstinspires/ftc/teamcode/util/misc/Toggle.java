/*
 * Filename: Toggle.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/17/19
 */

package org.firstinspires.ftc.teamcode.util.misc;

import org.firstinspires.ftc.robotcore.external.Function;

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
     * Inverts current state if condition changes from false to true from the previous to the current function call.
     *
     * @param condition - A function taking any two objects as input and outputting a boolean. Used as a condition for the toggle to run.
     * @param param1 - First parameter in the condition function.
     * @param param2 - Second parameter in the condition function.
     * @param <T> - The datatype of the first parameter for condition.
     * @param <X> - The datatype of the second parameter for condition.
     */
    //if you have a function with more than 2 inputs feel free to add copies of this function with more parameters. It will work the same way.
    public <T,X> void updateToggle(Function<T,Function<X,Boolean>> condition, T param1, X param2){
        if(condition.apply(param1).apply(param2) && flag){ //when false turns to true
            currentState = !currentState;
            flag = false;
        }
        else if(!condition.apply(param1).apply(param2) && !flag){ //when true turns to false
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