package org.firstinspires.ftc.teamcode.system.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.system.menus.DisplayMenu;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.system.source.GUI.BaseDisplayMenu;
import org.firstinspires.ftc.teamcode.system.source.GUI.Menu;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;


public class ReallyProject extends SubSystem {
    private final String DRIVESTICK = "driveStick", STRAFESTICK = "strafeStick", TURNSTICK = "turnStick", GUI = "interface";
    String fl;
    String bl;
    String fr;
    String br;

    DcMotor topLeft;
    DcMotor bottomLeft;
    DcMotor topRight;
    DcMotor bottomRight;

    public ReallyProject (Robot r, String bl, String br, String fl, String fr){
        super(r);
        this.fl = fl;
        this.fr = fr;
        this.bl = bl;
        this.br = br;
        topLeft = r.hardwareMap.dcMotor.get(fl);
        bottomLeft = r.hardwareMap.dcMotor.get(bl);
        topRight = r.hardwareMap.dcMotor.get(fr);
        bottomRight = r.hardwareMap.dcMotor.get(br);
    }
    public CustomizableGamepad gpad;


    public void turnBot(double power, double ms) {
        double startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime <= ms) {
            bottomRight.setPower(-power);
            topLeft.setPower(power);
            topRight.setPower(-power);
            bottomLeft.setPower(power);
        }
        bottomRight.setPower(0);
        topLeft.setPower(0);
        topRight.setPower(0);
        bottomLeft.setPower(0);
    }
    public void waitt(double ms) {
        double startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime <= ms) { }
    }

    @Override
    public void init() throws InterruptedException {
        topLeft.setDirection(DcMotor.Direction.REVERSE);
        topRight.setDirection(DcMotor.Direction.FORWARD);
        bottomLeft.setDirection(DcMotor.Direction.REVERSE);
        bottomRight.setDirection(DcMotor.Direction.FORWARD);
        gpad = new CustomizableGamepad(robot);
        gpad.addButton(DRIVESTICK, new Button(1, Button.DoubleInputs.left_stick_y, .1));
        gpad.addButton(STRAFESTICK, new Button(1, Button.DoubleInputs.left_stick_x, .1));
        gpad.addButton(TURNSTICK, new Button(1, Button.DoubleInputs.right_stick_x, .1));
        robot.gui.addMenu(GUI, new DisplayMenu(robot.gui));
    }

    @Override
    public void init_loop() throws InterruptedException {

    }

    @Override
    public void start() throws InterruptedException {

    }

    @Override
    public void handle() throws InterruptedException {
        double ly = gpad.getDoubleInput(DRIVESTICK);
        double lx = gpad.getDoubleInput(STRAFESTICK);
        double rx = gpad.getDoubleInput(TURNSTICK);
        if(rx != 0) {
            bottomRight.setPower(rx);
            topLeft.setPower(-rx);
            topRight.setPower(rx);
            bottomLeft.setPower(-rx);
        }
        else {
            if (lx != 0) {
                bottomRight.setPower(lx);
                topLeft.setPower(lx);
                topRight.setPower(-lx);
                bottomLeft.setPower(-lx);
            } else {
                bottomLeft.setPower(ly);
                bottomRight.setPower(ly);
                topLeft.setPower(ly);
                topRight.setPower(ly);
            }
        }
    }

    @Override
    public void stop() throws InterruptedException {

    }
}