package org.firstinspires.ftc.teamcode.system.subsystems.RobotSubsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.util.annotations.TeleopConfig;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;

import java.util.Map;

public class IntakeSubSystem extends SubSystem {
    private CustomizableGamepad inputs;
    DcMotor InL;
    DcMotor InR;

    private final String INBUTTON = "InButton", OUTBUTTON = "OutButton";

    public IntakeSubSystem(Robot r, String inl, String inr) {
        super(r);
        InL = robot.hardwareMap.dcMotor.get(inl);
        InR = robot.hardwareMap.dcMotor.get(inr);
        usesConfig = true;
    }

    CustomizableGamepad gpad;

    @Override
    public void init() throws InterruptedException {
        gpad = new CustomizableGamepad(robot);
        gpad.addButton(INBUTTON, new Button(1, Button.BooleanInputs.right_bumper));
        gpad.addButton(OUTBUTTON, new Button(1, Button.BooleanInputs.left_bumper));
    }

    @Override
    public void init_loop() throws InterruptedException {

    }

    @Override
    public void start() throws InterruptedException {
        inputs = robot.pullControls(this);
    }
        @Override
        public void handle () throws InterruptedException {
            if (gpad.getBooleanInput(INBUTTON) && gpad.getBooleanInput(OUTBUTTON)) {
                InL.setPower(0);
                InR.setPower(0);
            } else if (gpad.getBooleanInput(INBUTTON)) {
                InL.setPower(-1);
                InR.setPower(1);
            } else if (gpad.getBooleanInput(OUTBUTTON)) {
                InL.setPower(1);
                InR.setPower(-1);
            } else {
                InL.setPower(0);
                InR.setPower(0);
            }
        }

        @Override
        public void stop () throws InterruptedException {

        }

    @Override
    protected void initVars() {
        super.initVars();
    }


    @TeleopConfig
    public static ConfigParam[] teleopConfig() {
        return new ConfigParam[]{
                new ConfigParam("InButton", Button.BooleanInputs.right_bumper),
                new ConfigParam("OutButton", Button.BooleanInputs.left_bumper)
        };
    }
}

