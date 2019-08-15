/*
 * Filename: SubSystem.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package org.firstinspires.ftc.teamcode.system.source;

import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;

import java.lang.InterruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract class representing a subsystem on the robot.
 */
public abstract class SubSystem {

    //The robot the subsystem belongs to.
    protected Robot robot;
    public boolean usesConfig;

    public static Map<String, List<ConfigParam>> configs = new HashMap<>();

    private String[] ButtonNames;

    /**
     * An abstract method containing the code that the subsystem runs when being initialized.
     *
     * @throws InterruptedException - Throws this exception if the program is unexpectedly interrupted.
     */
    public abstract void init() throws InterruptedException;

    /**
     * An abstract method containing the code that the subsystem runs every loop in a teleop program.
     */
    public abstract void handle();

    /**
     * An abstract method containing the code that the subsystem runs when the program is stopped.
     */
    public abstract void stop();

    /**
     * Ctor for subsystem.
     *
     * @param robot - The robot the subsystem is contained within.
     */
    public SubSystem(Robot robot){
        this.robot = robot;
        usesConfig = false;
        initVars();
    }
    
    protected void initVars() {} 
    
    protected void initConfigSettings(ConfigParam[] configParams) {
        configs.put(this.getClass().getSimpleName(),Arrays.asList(configParams));
        usesConfig = true;
    }

    protected void initConfigSettings(ArrayList<ConfigParam> configParams) {
        configs.put(this.getClass().getSimpleName(),configParams);
        usesConfig = true;
    }

    public String[] getButtonNames() {
        return ButtonNames;
    }
}
