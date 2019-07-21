/*
 * Filename: ColorCalibBot.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/20/19
 */

package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.util.calib.ColorspaceCalib;

public class ColorCalibBot extends Robot {
    public ColorCalibBot(OpMode opmode) {
        super(opmode);
        putSubSystem("Calib", new ColorspaceCalib(this, ColorspaceCalib.ColorSpace.HSV));
    }
}
