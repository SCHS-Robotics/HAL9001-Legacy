/*
 * Filename: BaseOpMode.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package org.firstinspires.ftc.teamcode.system.source;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.util.functional_interfaces.BiFunction;

public abstract class BaseOpMode extends LinearOpMode {
    private Robot robot;

    protected abstract Robot buildRobot();

    public abstract void main()throws InterruptedException;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = buildRobot();

        try {
            robot.init();
        } catch (Exception ex) {
            telemetry.addData("ERROR!!!", ex.getMessage());
        }


        try {
            main();
        } catch (Exception ex) {
            telemetry.addData("ERROR!", ex.getMessage());
        }
    }
    protected final Robot getRobot() {
        return robot;
    }

    protected final void waitFor(long millis) {
        long stopTime = System.currentTimeMillis() + millis;
        while (opModeIsActive() && System.currentTimeMillis() < stopTime);
    }

    protected final <T,X> void waitFor(BiFunction<T,X,Boolean> condition, T param1, X param2) {
        while (opModeIsActive() && !condition.apply(param1,param2));
    }
}
