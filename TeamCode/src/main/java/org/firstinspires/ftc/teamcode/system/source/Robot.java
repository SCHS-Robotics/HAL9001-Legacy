/*
 * Filename: Robot.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package org.firstinspires.ftc.teamcode.system.source;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.misc.Button;

import java.util.HashMap;
import java.util.Map;

public abstract class Robot {
    public final Map<String, SubSystem> subSystems;
    private OpMode opMode;

    private boolean useGui;

    public GUI gui;

    public volatile Gamepad gamepad1, gamepad2;
    public final Telemetry telemetry;
    public final HardwareMap hardwareMap;

    public Robot(OpMode opMode)
    {
        this.opMode = opMode;
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        subSystems = new HashMap<>();

        useGui = false;
    }

    protected void putSubSystem(String name, SubSystem subSystem)
    {
        subSystems.put(name, subSystem);
    }

    protected void startGui(Cursor cursor, Button cycleButton) {
        gui = new GUI(this, cursor, cycleButton);
        useGui = true;
    }

    public final void init()
    {
        if(useGui) {
            gui.start();
        }

        for (SubSystem subSystem : subSystems.values()){
            try
            {
                subSystem.init();
            }
            catch (Exception ex)
            {
                telemetry.addData("Error!!!", ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    public final void driverControlledUpdate()
    {
        this.gamepad1 = opMode.gamepad1;
        this.gamepad2 = opMode.gamepad2;

        if(useGui) {
            gui.drawCurrentMenu();
        }

        for (SubSystem subSystem : subSystems.values())
        {
            try {
                subSystem.handle();
            }
            catch (Exception ex)
            {
                telemetry.addData("Error!!!", ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public final void stopAllComponents(){

        if(useGui) {
            gui.stop();
        }

        for (SubSystem subSystem : subSystems.values())
        {
            try
            {
                subSystem.stop();
            }
            catch (Exception ex)
            {
                telemetry.addData("Error!!!", ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public final SubSystem getSubSystem(String name)
    {
        return subSystems.get(name);
    }

    public final SubSystem eOverrideSubSystem(String name, SubSystem subSystem)
    {
        subSystems.put(name, subSystem);
        return subSystem;
    }
}
