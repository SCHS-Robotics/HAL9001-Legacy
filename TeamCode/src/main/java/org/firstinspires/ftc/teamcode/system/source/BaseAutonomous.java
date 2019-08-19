/*
 * Filename: BaseAutonomous.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package org.firstinspires.ftc.teamcode.system.source;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import java.lang.InterruptedException;
import org.firstinspires.ftc.teamcode.util.functional_interfaces.BiFunction;

/**
 * An abstract class used to more easily create opmodes.
 */
public abstract class BaseAutonomous extends LinearOpMode {

    //The robot running the opmode.
    private Robot robot;

    /**
     * An abstract method that is used to instantiate the robot.
     *
     * @return - The robot being used in the opmode.
     */
    protected abstract Robot buildRobot();

    /**
     * An abstract method that contains the code for the robot to run.
     *
     * @throws InterruptedException - Throws this exception if the program is unexpectedly interrupted.
     */
    public abstract void main() throws InterruptedException;

    @Override
    public void runOpMode() {
        robot = buildRobot();

        try {
            robot.init();
        } catch (Exception ex) {
            telemetry.addData("ERROR!!!", ex.getMessage());
            Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
        }

        try {
            main();
        } catch (Exception ex) {
            telemetry.addData("ERROR!", ex.getMessage());
            Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
        }
    }

    /**
     * Gets the robot.
     *
     * @return - The robot.
     */
    protected final Robot getRobot() {
        return robot;
    }

    /**
     * Waits for a specified number of milliseconds.
     *
     * @param millis - The number of milliseconds to wait.
     */
    protected final void waitFor(long millis) {
        long stopTime = System.currentTimeMillis() + millis;
        while (opModeIsActive() && System.currentTimeMillis() < stopTime);
    }

    /**
     * Waits for a boolean function with two inputs to return true.
     *
     * @param condition - An arbitrary function taking two inputs and outputting a boolean.
     * @param param1 - The function's first parameter.
     * @param param2 - The function's second parameter.
     * @param <T> - The first parameter's object type.
     * @param <X> - The second parameter's object type.
     */
    protected final <T,X> void waitFor(BiFunction<T,X,Boolean> condition, T param1, X param2) {
        while (opModeIsActive() && !condition.apply(param1,param2));
    }
}
