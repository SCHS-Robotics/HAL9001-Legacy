package org.firstinspires.ftc.teamcode.system.subsystems.RobotSubsystems;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.util.annotations.TeleopConfig;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;

public class DummySubsystem extends SubSystem {

    public DummySubsystem(Robot r) {
        super(r);
    }

    @Override
    public void init() throws InterruptedException {

    }

    @Override
    public void init_loop() throws InterruptedException {

    }

    @Override
    public void start() throws InterruptedException {

    }

    @Override
    public void handle() throws InterruptedException {

    }

    @Override
    public void stop() throws InterruptedException {

    }
    @Override
    protected void initVars() {
        super.initVars();
    }


    @TeleopConfig
    public static ConfigParam[] teleopConfig() {
        return new ConfigParam[]{
                new ConfigParam("asdasdasd", Button.BooleanInputs.right_bumper),
                new ConfigParam("EasyNameToRemember", Button.BooleanInputs.a),
                new ConfigParam("HeheXD", Button.BooleanInputs.b),
                new ConfigParam("TheBestButton", Button.BooleanInputs.y),
                new ConfigParam("Bill", Button.BooleanInputs.x),
                new ConfigParam("Wowowowowow", Button.BooleanInputs.left_bumper)
        };
    }
}
