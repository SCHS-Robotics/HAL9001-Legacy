/*
 * Filename: TriFunction.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/18/19
 */

package org.firstinspires.ftc.teamcode.util.functional_interfaces;

public interface TriFunction<T,R,S,Q> {

    Q apply(T arg1, R arg2, S arg3);
}
