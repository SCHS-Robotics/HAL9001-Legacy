/*
 * Filename: BaseOpModeIterative.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package org.firstinspires.ftc.teamcode.system.source;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * An abstract class used to more easily create teleop programs
 */
public abstract class BaseOpModeIterative extends OpMode {

    //The robot running the opmode.
    private Robot robot;

    /**
     * An abstract method that is used to instantiate the robot.
     *
     * @return - The robot being used in the opmode.
     */
    protected abstract Robot buildRobot();

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
        }catch (Exception ex){
            telemetry.addData("ERROR!!!", ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void init_loop() {
        robot.init_loop();
    }

    @Override
    public final void start() {
        onStart();
    }

    @Override
    public final void loop() {
        robot.driverControlledUpdate();
        onUpdate();
    }

    @Override
    public void stop() {
        onStop();
        robot.stopAllComponents();
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
