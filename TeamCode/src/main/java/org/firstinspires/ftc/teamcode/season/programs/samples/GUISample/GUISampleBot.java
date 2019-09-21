package org.firstinspires.ftc.teamcode.season.programs.samples.GUISample;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.menus.DisplayMenu;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.util.misc.Button;

public class GUISampleBot extends Robot {

    public DisplayMenu displayMenu1;
    public DisplayMenu displayMenu2;

    /**
     * Ctor for robot.
     *
     * @param opMode - The opmode the robot is currently running.
     */
    public GUISampleBot(OpMode opMode) {
        super(opMode);

        //Creates the new displaymenus
        displayMenu1 = new DisplayMenu(gui);
        displayMenu2 = new DisplayMenu(gui);

        //starts the GUI, allows robot to render menus. (Must be called before adding any menus)
        startGui(new Button(1, Button.BooleanInputs.back));
        //This adds a DisplayMenu to the list of menus in gui.
        //In many cases this is done from within a subsystem, but this robot only uses menus so we're just doing it here.
        gui.addMenu("DisplayMenu1", displayMenu1);
        //This adds a second Display menu that can be switched to at any point
        gui.addMenu("DisplayMenu2", displayMenu2);
    }
}
