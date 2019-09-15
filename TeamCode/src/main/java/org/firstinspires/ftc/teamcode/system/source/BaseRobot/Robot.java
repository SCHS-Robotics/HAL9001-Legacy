/*
 * Filename: Robot.java
 * Author: Andrew Liang, Dylan Zueck, Cole Savage
 * Team Name: Level Up
 * Date: 2017
 */

package org.firstinspires.ftc.teamcode.system.source.BaseRobot;

import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.system.menus.ConfigMenu;
import org.firstinspires.ftc.teamcode.system.source.GUI.GUI;
import org.firstinspires.ftc.teamcode.util.annotations.AutonomousConfig;
import org.firstinspires.ftc.teamcode.util.annotations.StandAlone;
import org.firstinspires.ftc.teamcode.util.annotations.TeleopConfig;
import org.firstinspires.ftc.teamcode.util.exceptions.DumpsterFireException;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract class representing the physical robot.
 */
public abstract class Robot {

    //A map relating the name of each subsystem in the robot to that subsystem's corresponding autonomous config
    public static Map<String, List<ConfigParam>> autonomousConfig = new HashMap<>();
    //A map relating the name of each subsystem in the robot to that subsystem's corresponding teleop config
    public static Map<String, List<ConfigParam>> teleopConfig = new HashMap<>();
    //A hashmap mapping the name of a subsystem to the actual subsystem object.
    private final Map<String, SubSystem> subSystems;
    //The opmode the robot is running.
    private OpMode opMode;
    //A boolean value specifying whether or not to use a GUI, whether or not to use a config, and whether or not to close the current config GUI.
    private boolean useGui;
    //A boolean value specifying whether or not to use the config system.
    private boolean useConfig;
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
        useConfig = false;
    }

    /**
     * Adds a subsystem to the robot's hashmap of subsystems and, if the subsystem uses config, load the default config.
     *
     * @param name - The name of the subsystem.
     * @param subSystem - The subsystem object.
     */
    protected void putSubSystem(String name, SubSystem subSystem)
    {
        if(subSystem == null) {
            throw new DumpsterFireException("Subsystem Cannot be Null");
        }

        subSystems.put(name, subSystem);
        if(subSystem.usesConfig) {

            boolean foundTeleopConfig = false;
            boolean foundAutonomousConfig = false;

            try {

                Method[] methods = subSystem.getClass().getDeclaredMethods();
                for(Method m : methods) {

                    //method must be annotated as TeleopConfig, have no parameters, be public and static, and return an array of config params
                    if(!foundTeleopConfig && m.isAnnotationPresent(TeleopConfig.class) && m.getReturnType() == ConfigParam[].class && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                        teleopConfig.put(subSystem.getClass().getSimpleName(),Arrays.asList((ConfigParam[]) m.invoke(null)));
                        if(!useGui) {
                            gui = new GUI(this, new Button(1, Button.BooleanInputs.noButton));
                            useGui = true;
                        }
                        useConfig = true;
                        foundTeleopConfig = true;
                    }

                    //method must be annotated as AutonomousConfig, have no parameters, be public and static, and return an array of config params
                    if(!foundAutonomousConfig && m.isAnnotationPresent(AutonomousConfig.class) && m.getReturnType() == ConfigParam[].class && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                        autonomousConfig.put(subSystem.getClass().getSimpleName(),Arrays.asList((ConfigParam[]) m.invoke(null)));
                        if(!useGui) {
                            gui = new GUI(this, new Button(1, Button.BooleanInputs.noButton));
                            useGui = true;
                        }
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
    public void startGui(Button cycleButton) {
        if(!useGui) {
            gui = new GUI(this, cycleButton);
            useGui = true;
        }
        else {
            gui.overrideCycleButton(cycleButton);
        }
    }

    /**
     * Returns whether the robot has already been set up to use the GUI.
     *
     * @return - Whether the GUI has been instantiated.
     */
    public boolean usesGUI() {
        return useGui;
    }

    /**
     * Runs all the initialization methods of every subsystem and the GUI. Also starts the config and creates the config file tree if needed.
     */
    public final void init()
    {

        this.gamepad1 = opMode.gamepad1;
        this.gamepad2 = opMode.gamepad2;

        if(useConfig) {

            //create overall robot folder
            File robotConfigDirectory = new File(Environment.getExternalStorageDirectory().getPath()+"/System64/robot_"+this.getClass().getSimpleName());
            if(!robotConfigDirectory.exists()) {
                Log.i("File Creation",robotConfigDirectory.mkdir() ? "Directory created!" : "File error, couldn't create directory");
                writeFile(robotConfigDirectory.getPath() + "/robot_info.txt", "");
            }

            //create autonomous directory in robot folder
            File autoDir = new File(robotConfigDirectory.getPath() + "/autonomous");
            if(!autoDir.exists()) {
                Log.i("File Creation",autoDir.mkdir() ? "Directory created!" : "File error, couldn't create directory");
                writeFile(autoDir.getPath() + "/robot_info.txt", "");
            }
            //create teleop directory in robot folder
            File teleopDir = new File(robotConfigDirectory.getPath() + "/teleop");
            if(!teleopDir.exists()) {
                Log.i("File Creation",teleopDir.mkdir() ? "Directory created!" : "File error, couldn't create directory");
                writeFile(teleopDir.getPath() + "/robot_info.txt", "");
            }

            //Get all names of subsystem objects being used in this robot and write it to the outer robot_info.txt file
            //These names will be used in the config debugger
            StringBuilder sb = new StringBuilder();
            for(SubSystem subSystem : subSystems.values()) {
                if(subSystem.usesConfig) {
                    sb.append(subSystem.getClass().getName());
                    sb.append("\r\n");
                }
            }
            if(sb.length() > 2) {
                sb.delete(sb.length() - 2, sb.length()); //removes trailing \r\n characters so there isn't a blank line at the end of the file
            }

            writeFile(robotConfigDirectory.getPath() + "/robot_info.txt", sb.toString());

            //If the opmode is annotated as StandAlone, add the config menu in standalone mode.
            if(opMode.getClass().isAnnotationPresent(StandAlone.class)) {
                if(opMode instanceof BaseAutonomous) {
                    gui.addMenu("config",new ConfigMenu(gui,autoDir.getPath(),true));
                }
                else if(opMode instanceof BaseTeleop) {
                    gui.addMenu("config",new ConfigMenu(gui,teleopDir.getPath(),true));
                }
            }
            //Otherwise, add the config menu in non-standalone mode.
            else {
                gui.addMenu("config",new ConfigMenu(gui,robotConfigDirectory.getPath(),false));
            }

            gui.setActiveMenu("config");
        }

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
                telemetry.clearAll();
                telemetry.addData("ERROR!!!", ex.getMessage());
                telemetry.update();
                Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
            }
        }
    }

    /**
     * Runs methods in a loop during init. Runs all subsystem init_loop() methods and draws the configuration menu.
     */
    public final void init_loop() {

        this.gamepad1 = opMode.gamepad1;
        this.gamepad2 = opMode.gamepad2;

        if(useGui) {
            gui.drawCurrentMenuInit();

            if(useConfig) {
                if(((ConfigMenu) gui.getMenu("config")).isDone) {
                    gui.removeMenu("config");
                    useConfig = false;
                }
            }
        }

        for (SubSystem subSystem : subSystems.values()) {

            try {
                subSystem.init_loop();
            }
            catch (Exception ex) {
                telemetry.clearAll();
                telemetry.addData("ERROR!!!", ex.getMessage());
                telemetry.update();
                Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
            }
        }
    }

    /**
     * Runs this method when the user presses the start button.
     */
    public final void onStart() {
        this.gamepad1 = opMode.gamepad1;
        this.gamepad2 = opMode.gamepad2;

        if(useGui) {
            if(gui.isMenuPresent("config")) {
                gui.removeMenu("config");
            }
            gui.onStart();
        }

        for(SubSystem subSystem : subSystems.values()) {
            try {
                subSystem.start();
            }
            catch (Exception ex) {
                telemetry.clearAll();
                telemetry.addData("ERROR!!!", ex.getMessage());
                telemetry.update();
                Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
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
                telemetry.clearAll();
                telemetry.addData("ERROR!!!", ex.getMessage());
                telemetry.update();
                Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
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
                telemetry.clearAll();
                telemetry.addData("ERROR!!!", ex.getMessage());
                telemetry.update();
                Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
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

    /**
     * Gets the opmode the robot is currently running.
     *
     * @return - The opmode the robot is running.
     */
    public final OpMode getOpMode() {
        return opMode;
    }

    /**
     * Pulls a customizable gamepad object from the teleop config map. Allows for easily getting gamepad data from the configuration.
     *
     * @param subsystem - The subsystem to pull the gamepad controls for.
     * @return - A customizable gamepad containing the configured controls for that subsystem.
     */
    public CustomizableGamepad pullControls(SubSystem subsystem) {
        return pullControls(subsystem.getClass().getSimpleName());
    }

    /**
     * Pulls a customizable gamepad object from the teleop config map. Allows for easily getting gamepad data from the configuration.
     *
     * @param subsystem - The name of the subsystem to pull the gamepad controls for.
     * @return - A customizable gamepad containing the configured controls for that subsystem.
     */
    public CustomizableGamepad pullControls(String subsystem) {
        List<ConfigParam> configParams = teleopConfig.get(subsystem);
        CustomizableGamepad gamepad = new CustomizableGamepad(this);
        for(ConfigParam param : configParams) {
            if(param.usesGamepad) {
                gamepad.addButton(param.name,param.toButton());
            }
        }
        return gamepad;
    }

    /**
     * Pulls a map of all non-gamepad-related config settings from the global config. The map format is (option name) -> (option value)
     *
     * @param subsystem - The subsystem to pull the gamepad controls for.
     * @return - A map relating the name of each non-gamepad option to that option's value.
     */
    public Map<String,Object> pullNonGamepad(SubSystem subsystem) {
        return pullNonGamepad(subsystem.getClass().getSimpleName());
    }

    /**
     * Pulls a map of all non-gamepad-related config settings from the global config. The map format is (option name) -> (option value)
     *
     * @param subsystem - The name of the subsystem to pull the gamepad controls for.
     * @return - A map relating the name of each non-gamepad option to that option's value.
     */
    public Map<String,Object> pullNonGamepad(String subsystem) {

        List<ConfigParam> configParamsTeleop = new ArrayList<>();
        List<ConfigParam> configParamsAuto = new ArrayList<>();

        if(teleopConfig.keySet().contains(subsystem)) {
            configParamsTeleop = teleopConfig.get(subsystem);
        }
        if(autonomousConfig.keySet().contains(subsystem)) {
            configParamsAuto = autonomousConfig.get(subsystem);
        }

        Map<String,Object> output = new HashMap<>();

        for (ConfigParam param : configParamsAuto) {
            if (!param.usesGamepad) {
                output.put(param.name, param.vals.get(param.options.indexOf(param.currentOption)));
            }
        }

        for (ConfigParam param : configParamsTeleop) {
            if (!param.usesGamepad) {
                output.put(param.name, param.vals.get(param.options.indexOf(param.currentOption)));
            }
        }

        return output;
    }

    /**
     * Gets if the program the robot is running is a teleop program.
     *
     * @return Whether the program being run is a teleop program.
     */
    public boolean isTeleop() {
        return opMode instanceof BaseTeleop;
    }

    public boolean isAutonomous() {
        return opMode instanceof BaseAutonomous;
    }

    /**
     * Writes data to a specified filepath. Creates the file if it doesn't exist, overwrites it if it does.
     *
     * @param filePath - The path of the file to write to.
     * @param data - The data to write to the file/
     */
    private void writeFile(String filePath, String data) {

        FileOutputStream fos;

        try {
            File file = new File(filePath);
            if(file.exists()) {
                boolean fileDeleted = file.delete();
                if(!fileDeleted) {
                    Log.e("File Error", "Could not delete file at "+filePath);
                }

                boolean fileCreated = file.createNewFile();
                if(!fileCreated) {
                    Log.e("File Error","Could not create file at "+filePath);
                }
            }

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
