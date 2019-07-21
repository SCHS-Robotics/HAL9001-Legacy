/*
 * Filename: BiFunction.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/18/19
 */

package org.firstinspires.ftc.teamcode.util.functional_interfaces;

public interface BiFunction<T,R,S> {

    S apply(T arg1, R arg2);
}
