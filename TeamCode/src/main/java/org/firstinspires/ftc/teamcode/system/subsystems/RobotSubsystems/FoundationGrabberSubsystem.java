package org.firstinspires.ftc.teamcode.system.subsystems.RobotSubsystems;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.system.menus.DisplayMenu;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.util.annotations.TeleopConfig;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;
import org.firstinspires.ftc.teamcode.util.misc.Toggle;

public class FoundationGrabberSubsystem extends SubSystem {

    private final String ARMBUTTON = "armToggleButton";
    Servo arm;
    private final int UP = 0;
    private final int DOWN = 1;

    public FoundationGrabberSubsystem (Robot r, String servoName) {
        super(r);
        arm = r.hardwareMap.servo.get(servoName);
        usesConfig = true;
    }

    CustomizableGamepad gpad;
    Toggle toggle;
    DisplayMenu fMenu;

    @Override
    public void init() throws InterruptedException {
        toggle = new Toggle(Toggle.ToggleTypes.flipToggle, false);
        if(robot.usesGUI()) {
            fMenu = new DisplayMenu(robot.gui);
            robot.gui.addMenu("buttonData", fMenu);
        }
    }

    @Override
    public void init_loop() throws InterruptedException {

    }

    @Override
    public void start() throws InterruptedException {
        gpad = robot.pullControls(this);
    }

    @Override
    public void handle() throws InterruptedException {
        fMenu.addData("ArmButton", gpad.getBooleanInput(ARMBUTTON));
        toggle.updateToggle(gpad.getBooleanInput(ARMBUTTON));
        if(toggle.getCurrentState()) {
            arm.setPosition(DOWN);
        }
        else {
            arm.setPosition(UP);
        }
    }

    @Override
    public void stop() throws InterruptedException {

    }


    @Override
    public void initVars() { super.initVars(); }

    public void toggleDown() {
        arm.setPosition(DOWN);
    }

    public void toggleUp() {
        arm.setPosition(UP);
    }

    @TeleopConfig
    public static ConfigParam[] teleopConfig() {
        return new ConfigParam[] {
                new ConfigParam("armToggleButton", Button.BooleanInputs.b)
        };
    }
}