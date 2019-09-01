package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.menus.ScrollingListTestMenu;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.MechanumDrive;
import org.firstinspires.ftc.teamcode.system.subsystems.cursors.DefaultCursor;
import org.firstinspires.ftc.teamcode.util.misc.Button;

public class MenuBot extends Robot {
    public MenuBot(OpMode opMode) {
        super(opMode);
        startGui(new Button(1, Button.BooleanInputs.b));
        gui.addMenu("Menu 1", new ScrollingListTestMenu(gui, new DefaultCursor(this, new DefaultCursor.Params().setBlinkSpeedMs(500))));
        gui.addMenu("Menu 2", new ScrollingListTestMenu(gui, new DefaultCursor(this, new DefaultCursor.Params().setBlinkSpeedMs(500))));
        gui.addMenu("Menu 3", new ScrollingListTestMenu(gui, new DefaultCursor(this, new DefaultCursor.Params().setBlinkSpeedMs(500))));
    }
}
