package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.menus.ScrollingListTestMenu;
import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.custom_cursors.DefaultCursor;
import org.firstinspires.ftc.teamcode.util.misc.Button;

public class MenuBot extends Robot {
    public MenuBot(OpMode opMode) {
        super(opMode);
        startGui(new DefaultCursor(0, 0, 625), new Button(gamepad1, Button.BooleanInputs.b));
        gui.addMenu("Menu 1", new ScrollingListTestMenu(gui));
        gui.addMenu("Menu 2", new ScrollingListTestMenu(gui));
        gui.addMenu("Menu 3", new ScrollingListTestMenu(gui));
    }
}
