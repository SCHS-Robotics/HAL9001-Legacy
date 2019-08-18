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
import org.firstinspires.ftc.teamcode.system.menus.ConfigMenu;
import org.firstinspires.ftc.teamcode.util.annotations.AutonomousConfig;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;
import org.firstinspires.ftc.teamcode.util.annotations.TeleopConfig;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
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

            boolean foundTeleopConfig = false;
            boolean foundAutonomousConfig = false;

            try {

                Method[] methods = Class.forName(subSystem.getClass().getName()).getDeclaredMethods();

                for(Method m : methods) {

                    //method must be annotated as TeleopConfig, have no parameters, be public and static, and return an array of config params
                    if(!foundTeleopConfig && m.isAnnotationPresent(TeleopConfig.class) && m.getReturnType() == ConfigParam[].class && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                        SubSystem.configs.put(subSystem.getClass().getSimpleName(), Arrays.asList((ConfigParam[]) (m.invoke(null))));
                        SubSystem.teleopConfig.put(subSystem.getClass().getSimpleName(),Arrays.asList((ConfigParam[]) m.invoke(null)));
                        configGui = new GUI(this, new Button(1, Button.BooleanInputs.noButton));
                        useConfig = true;
                        foundTeleopConfig = true;
                    }

                    //method must be annotated as AutonomousConfig, have no parameters, be public and static, and return an array of config params
                    if(!foundAutonomousConfig && m.isAnnotationPresent(AutonomousConfig.class) && m.getReturnType() == ConfigParam[].class && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                        SubSystem.configs.put(subSystem.getClass().getSimpleName(), Arrays.asList((ConfigParam[]) (m.invoke(null))));
                        SubSystem.autonomousConfig.put(subSystem.getClass().getSimpleName(),Arrays.asList((ConfigParam[]) m.invoke(null)));
                        configGui = new GUI(this, new Button(1, Button.BooleanInputs.noButton));
                        useConfig = true;
                        foundAutonomousConfig = true;
                    }

                    if(foundTeleopConfig && foundAutonomousConfig) {
                        break;
                    }

                }
            }
            catch (Exception e) {
                Log.e("Error","Problem loading config for subsystem "+subSystem.getClass().getSimpleName(),e);
            }
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

        this.gamepad1 = opMode.gamepad1;
        this.gamepad2 = opMode.gamepad2;

        if(useGui) {
            gui.start();
        }

        if(useConfig) {

            Log.d("teleop",SubSystem.teleopConfig.toString());
            Log.d("autonomous",SubSystem.autonomousConfig.toString());

            rId = this.getClass().getSimpleName();

            //create overall robot folder
            File robotConfigDirectory = new File(Environment.getExternalStorageDirectory().getPath()+"/System64/robot_"+rId);
            if(!robotConfigDirectory.exists()) {
                robotConfigDirectory.mkdir();
                writeFile(robotConfigDirectory.getPath() + "/robot_info.txt", "");
            }

            //create autonomous directory in robot folder
            File autoDir = new File(robotConfigDirectory.getPath() + "/autonomous");
            if(!autoDir.exists()) {
                autoDir.mkdir();
                writeFile(autoDir.getPath() + "/robot_info.txt", "");
            }
            //create teleop directory in robot folder
            File teleopDir = new File(robotConfigDirectory.getPath() + "/teleop");
            if(!teleopDir.exists()) {
                teleopDir.mkdir();
                writeFile(teleopDir.getPath() + "/robot_info.txt", "");
            }

            //Get all names of subsystem objects being used in this robot and write it to the outer robot_info.txt file
            //These names will be used in the config debugger
            StringBuilder sb = new StringBuilder();
            for(SubSystem subSystem : subSystems.values()) {
                sb.append(subSystem.getClass().getName());
                sb.append("\r\n");
            }
            sb.delete(sb.length()-2,sb.length()); //removes trailing \r\n characters so there isn't a blank line at the end of the file
            writeFile(robotConfigDirectory.getPath() + "/robot_info.txt", sb.toString());

            if(opMode.getClass().isAnnotationPresent(StandAlone.class)) {
                if(opMode instanceof BaseAutonomous) {
                    configGui.addMenu("config",new ConfigMenu(configGui,autoDir.getPath(),true));
                    configGui.start();
                }
                else if(opMode instanceof BaseTeleop) {
                    configGui.addMenu("config",new ConfigMenu(configGui,teleopDir.getPath(),true));
                    configGui.start();
                }
            }
            else {
                if(opMode instanceof BaseAutonomous) {

                }
                else if(opMode instanceof BaseTeleop) {

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

        this.gamepad1 = opMode.gamepad1;
        this.gamepad2 = opMode.gamepad2;

        for (SubSystem subSystem : subSystems.values()) {

            if(useConfig) {
                configGui.drawCurrentMenu();
            }

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

        if(useConfig) {
            configGui.stop();
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


    private void writeFile(String filePath, String data) {

        FileOutputStream fos;

        try {

            fos = new FileOutputStream(filePath, true);

            FileWriter fWriter;

            try {
                fWriter = new FileWriter(fos.getFD());

                fWriter.write(data);

                fWriter.flush();
                fWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fos.getFD().sync();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
