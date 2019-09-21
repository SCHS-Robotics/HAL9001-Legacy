/*
 * Filename: BaseTeleop.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package org.firstinspires.ftc.teamcode.system.source.BaseRobot;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * An abstract class used to more easily create teleop programs
 */
public abstract class BaseTeleop extends OpMode {

    //The robot running the opmode.
    private Robot robot;

    /**
     * An abstract method that is used to instantiate the robot.
     *
     * @return - The robot being used in the opmode.
     */
    protected abstract Robot buildRobot();

    /**
     * Method that runs when the robot is initialized. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onInit() {};

    /**
     * Method that runs in a loop after the robot is initialized. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onInitLoop() {};

    /**
     * Method that runs when the robot is started. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onStart() {}

    /**
     * Method that runs every loop cycle. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onUpdate(){}

    /**
     * Method that runs when the robot is stopped. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onStop(){}

    @Override
    public final void init() {
        robot = buildRobot();

        try {
            robot.init();
            onInit();
        }
        catch (Exception ex){
            telemetry.clearAll();
            telemetry.addData("ERROR!!!", ex.getMessage());
            telemetry.update();
            Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
        }
    }

    @Override
    public final void init_loop() {
        try {
            robot.init_loop();
            onInitLoop();
        }
        catch (Exception ex){
            telemetry.clearAll();
            telemetry.addData("ERROR!!!", ex.getMessage());
            telemetry.update();
            Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
        }
    }

    @Override
    public final void start() {
        try {
            robot.onStart();
            onStart();
        }
        catch (Exception ex){
            telemetry.clearAll();
            telemetry.addData("ERROR!!!", ex.getMessage());
            telemetry.update();
            Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
        }
    }

    @Override
    public final void loop() {
        try {
            robot.driverControlledUpdate();
            onUpdate();
        }
        catch (Exception ex){
            telemetry.clearAll();
            telemetry.addData("ERROR!!!", ex.getMessage());
            telemetry.update();
            Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
        }
    }

    @Override
    public final void stop() {
        try {
            onStop();
            robot.stopAllComponents();
        }
        catch (Exception ex){
            telemetry.clearAll();
            telemetry.addData("ERROR!!!", ex.getMessage());
            telemetry.update();
            Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
        }
    }

    /**
     * Gets the robot.
     *
     * @return - The robot.
     */
    protected final Robot getRobot(){
        return robot;
    }
}
