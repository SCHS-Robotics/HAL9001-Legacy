/*
 * Filename: Robot.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package org.firstinspires.ftc.teamcode.system.source;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.debug.ConfigManager;
import org.firstinspires.ftc.teamcode.util.misc.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract class representing the physical robot.
 */
public abstract class Robot {

    public String rId;

    //A hashmap mapping the name of a subsystem to the actual subsystem object.
    public final Map<String, SubSystem> subSystems;
    //The opmode the robot is running.
    private OpMode opMode;
    //A boolean value specifying whether or not to use a GUI.
    private boolean useGui, useConfig = false;
    //The GUI the robot uses to render the menus.
    public GUI gui;
    //The gamepads used to control the robot.
    public volatile Gamepad gamepad1, gamepad2;
    //The telemetry used to print lines to the driver station.
    public final Telemetry telemetry;
    //The hardwaremap used to map software representations of hardware to the actual hardware.
    public final HardwareMap hardwareMap;

    /**
     * Ctor for robot.
     *
     * @param opMode - The opmode the robot is currently running.
     */
    public Robot(OpMode opMode)
    {
        this.opMode = opMode;
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        subSystems = new HashMap<>();

        useGui = false;
    }

    /**
     * Adds a subsystem to the robot's hashmap of subsystems.
     *
     * @param name - The name of the subsystem.
     * @param subSystem - The subsystem object.
     */
    protected void putSubSystem(String name, SubSystem subSystem)
    {
        subSystems.put(name, subSystem);
    }

    /**
     * Instantiates the GUI and allows the robot to use a GUI.
     *
     * @param cursor - The cursor used for all menus in the GUI.
     * @param cycleButton - The button used to cycle through multiple menus in GUI.
     */
    protected void startGui(Cursor cursor, Button cycleButton) {
        gui = new GUI(this, cursor, cycleButton);
        telemetry.setAutoClear(false);
        useGui = true;
    }

    public void useConfig(String rId){
        useConfig = true;
        this.rId = rId;

        File robotConfigDirectory = new File(Environment.getExternalStorageDirectory().getPath()+"/System64/robot_"+rId);

        if(!robotConfigDirectory.exists()) {
            robotConfigDirectory.mkdirs();
        }

        try {
            File file = new File(robotConfigDirectory.getPath()+"/robot_info.txt");
            FileOutputStream fileoutput = new FileOutputStream(file);
            PrintStream ps = new PrintStream(fileoutput);

            for (SubSystem system: subSystems.values()) {
                //TODO if subsystem uses config
                ps.println(system.getClass().getName()+"\r\n");
            }

            ps.close();
            fileoutput.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //File[] posDirectories = new File(DATABASEPos).listFiles();
        //check if there is a folder called System64
        //is there a folder called robot_rId
        //if yes check header file
        //if no make folder/header file
    }

    /**
     * Runs all the initialization methods of every subsystem and the GUI.
     */
    public final void init() throws InterruptedException
    {
        if(useGui) {
            gui.start();
        }

        if(useConfig){
            //check is there a folder with spec
            //create config menu
            //while(!connfigMenu.isSelected){}
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

    /**
     * Runs subsystem handle() methods and GUI drawCurrentMenu() every frame in driver controlled programs.
     */
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

    /**
     * Runs the stop functions for all subsystems and the GUI.
     */
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

    /**
     * Gets a specific subsystem from the hashmap.
     *
     * @param name - The name of the subsystem.
     * @return - The subsystem with the given identifier in the hashmap.
     */
    public final SubSystem getSubSystem(String name)
    {
        return subSystems.get(name);
    }

    /**
     * Replaces a subsystem already in the hashmap with another subsystem.
     *
     * @param name - The name of the subsystem to be replaced.
     * @param subSystem - The new subsystem.
     * @return - The new subsystem that was passed in as a parameter.
     */
    public final SubSystem eOverrideSubSystem(String name, SubSystem subSystem)
    {
        subSystems.put(name, subSystem);
        return subSystem;
    }
}
