/*
 * Filename: Robot.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package org.firstinspires.ftc.teamcode.system.source;

import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;
import org.firstinspires.ftc.teamcode.util.misc.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
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

    private GUI configGui;

    private String currentFilename = Environment.getExternalStorageDirectory().getPath()+"/System64/robot_"+this.getClass().getSimpleName();

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
        if(subSystem.usesConfig) {
            configGui = new GUI(this, new Button(1, Button.BooleanInputs.noButton));
            useConfig = true;
        }
    }

    /**
     * Instantiates the GUI and allows the robot to use a GUI.
     *
     * @param cycleButton - The button used to cycle through multiple menus in GUI.
     */
    protected void startGui(Button cycleButton) {
        gui = new GUI(this, cycleButton);
        useGui = true;
    }


    /**
     * Runs all the initialization methods of every subsystem and the GUI.
     */
    public final void init() throws InterruptedException
    {
        if(useGui) {
            gui.start();
        }

        if(useConfig) {
            rId = this.getClass().getSimpleName();

            //create overall robot folder
            File robotConfigDirectory = new File(Environment.getExternalStorageDirectory().getPath()+"/System64/robot_"+rId);
            if(!robotConfigDirectory.exists()) {
                robotConfigDirectory.mkdirs();
            }

            //create teleop directory in robot folder
            File teleopDir = new File(robotConfigDirectory.getPath()+"/teleop");
            if(!teleopDir.exists()) {
                teleopDir.mkdir();
            }

            //create robot_info.txt file in teleop folder
            File robotInfoTeleop = new File(teleopDir.getPath()+"/robot_info.txt");
            if(!robotInfoTeleop.exists()) {
                try {
                    robotInfoTeleop.createNewFile();
                }
                catch(IOException e) {
                    Log.e("IO Error","Problem creating robot_info.txt file for "+rId+" teleop!");
                    e.printStackTrace();
                }
            }

            //create autonomous directory in robot folder
            File autoDir = new File(robotConfigDirectory.getPath()+"/autonomous");
            if(!autoDir.exists()) {
                autoDir.mkdir();
            }

            //create robot_info.txt file in autonomous folder
            File robotInfoAuto = new File(autoDir.getPath()+"/robot_info.txt");
            if(!robotInfoTeleop.exists()) {
                try {
                    robotInfoAuto.createNewFile();
                }
                catch(IOException e) {
                    Log.e("IO Error","Problem creating robot_info.txt file for "+rId+" teleop!");
                    e.printStackTrace();
                }
            }

            //create robot_info.txt file in autonomous folder
            File robotInfo = new File(robotConfigDirectory.getPath()+"/robot_info.txt");
            if(!robotInfo.exists()) {
                try {
                    robotInfo.createNewFile();
                }
                catch(IOException e) {
                    Log.e("IO Error","Problem creating robot_info.txt file for "+rId+'!');
                    e.printStackTrace();
                }
            }

            if(opMode.getClass().isAnnotationPresent(StandAlone.class)) {

                if (opMode instanceof BaseAutonomous) {

                }
                if (opMode instanceof BaseTeleop) {
                    Log.e("test", "it worked!");
                }
            }
        }

        /*
        autonomous
            standalone:
                go into autonomous configs and select one (do usual new delete edit stuff ect.)

            not:
                go into autonomous, choose autonomous config
                go into teleop, chose teleop config (filename gets writen to robot_info)

        teleop
            standalone:
                go into teleop configs and select one

            not:
                check robot info
                if there is a config in robot info (robot info file not empty) select that one.
                menu shows "automatically using __", press x or something to change
                on stop clear robot info
                if there is no config refer to standalone
         */

        if(useConfig && opMode instanceof BaseAutonomous){
            //configGui.addMenu("configurator",new ConfigMenu);

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

    public final void init_loop() {
        for (SubSystem subSystem : subSystems.values()) {



            try {
                subSystem.init_loop();
            }
            catch (Exception ex) {
                telemetry.addData("Error!!!",ex.getMessage());
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
