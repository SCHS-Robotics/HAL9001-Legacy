/*
 * Filename: ColorCalibBot.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.menus.DisplayMenu;
import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.cursors.ConfigCursor;
import org.firstinspires.ftc.teamcode.system.subsystems.cursors.DefaultCursor;
import org.firstinspires.ftc.teamcode.util.calib.ColorspaceCalib;
import org.firstinspires.ftc.teamcode.util.debug.ConfigDebugMenu;
import org.firstinspires.ftc.teamcode.util.misc.Button;

/**
 * A robot object containing a subsystem used to calibrate colorspace settings for computer vision algorithms
 */
public class ColorCalibBot extends Robot {

    /**
     * Ctor for the color calibration robot
     *
     * @param opmode - The opmode the robot is running
     */
    public ColorCalibBot(OpMode opmode) {
        super(opmode);
        startGui(new Button(1, Button.BooleanInputs.start));
        gui.addMenu("temp2",new DisplayMenu(gui));
        gui.addMenu("Testing",new ConfigDebugMenu(gui));
        putSubSystem("Calib", new ColorspaceCalib(this, ColorspaceCalib.ColorSpace.HSV,"temp2"));
        useConfig("ColorCalib");
    }
}
