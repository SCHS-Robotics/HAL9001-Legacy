/*
 * Filename: BaseOpModeIterative.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package org.firstinspires.ftc.teamcode.system.source;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public abstract class BaseOpModeIterative extends OpMode {
    private Robot robot;

    protected abstract Robot buildRobot();

    protected void onStart(){}
    protected void onUpdate(){}
    protected void onStop(){}

    @Override
    public final void init() {
        robot = buildRobot();

        try {
            robot.init();
        }catch (Exception ex){
            telemetry.addData("ERROR!!!", ex.getMessage());
        }
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
    }

    protected final Robot getRobot(){
        return robot;
    }
}
