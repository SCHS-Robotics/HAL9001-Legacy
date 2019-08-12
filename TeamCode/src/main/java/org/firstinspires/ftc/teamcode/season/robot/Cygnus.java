/*
 * Filename: Cygnus.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: TODO
 */

package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.menus.DisplayMenu;
import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.cursors.DefaultCursor;
import org.firstinspires.ftc.teamcode.util.calib.AnglePIDTuner;
import org.firstinspires.ftc.teamcode.util.misc.Button;

public class Cygnus extends Robot {
    public Cygnus(OpMode opMode) {
        super(opMode);
        //putSubSystem("Drive",new TankDrive(this,new String[] {"leftMotor", "rightMotor"},new double[] {0,0,0}));
        startGui(new DefaultCursor(this,0, 0, 500), new Button(1, Button.BooleanInputs.b));
        gui.addMenu("display",new DisplayMenu(gui));
        putSubSystem("temp",new AnglePIDTuner(this,"display",Math.PI/4));
    }
}
