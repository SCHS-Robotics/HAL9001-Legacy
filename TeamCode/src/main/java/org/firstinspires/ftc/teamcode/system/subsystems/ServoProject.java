package org.firstinspires.ftc.teamcode.system.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.system.menus.DisplayMenu;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;
import org.firstinspires.ftc.teamcode.util.misc.Toggle;

public class ServoProject extends SubSystem {

    String a;
    private final String ARMBUTTON = "armButton";
    private final String GUI = "ReallyGUI";
    private DisplayMenu displayMenu;

    Servo arm;

    public ServoProject(Robot r, String a) {
        super(r);
        this.a = a;
        arm = r.hardwareMap.servo.get(a);
    }

    public CustomizableGamepad gpad;
    public Toggle flip;
    @Override
    public void init() throws InterruptedException {
        gpad = new CustomizableGamepad(robot);
        gpad.addButton(ARMBUTTON, new Button(1, Button.BooleanInputs.a));
        flip = new Toggle(Toggle.ToggleTypes.flipToggle, false);
        if(robot.usesGUI()) {
            displayMenu = new DisplayMenu(robot.gui);
            robot.gui.addMenu(GUI, displayMenu);
        }
    }

    @Override
    public void init_loop() throws InterruptedException {

    }

    @Override
    public void start() throws InterruptedException {

    }

    @Override
    public void handle() throws InterruptedException {
        flip.updateToggle(gpad.getBooleanInput(ARMBUTTON));
        if(flip.getCurrentState()) {
            displayMenu.addData("ON", "yes it is");
            arm.setPosition(1);
        }
        else
        {
            displayMenu.addData("OFF", "yes it is");
            arm.setPosition(0);
        }
    }

    @Override
    public void stop() throws InterruptedException {
        arm.setPosition(0);
    }
}
