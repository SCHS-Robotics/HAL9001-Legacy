package org.firstinspires.ftc.teamcode.system.subsystems.RobotSubsystems;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.util.annotations.AutonomousConfig;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;

import java.util.Map;

public class AutonomousSelectorSubsystemUsingConfig extends SubSystem {

    public String autonomous;
    public String color;
    public String startPos;
    public AutonomousSelectorSubsystemUsingConfig(Robot r) {
        super(r);
        usesConfig = true;
    }

    @Override
    public void init() throws InterruptedException {

    }

    @Override
    public void init_loop() throws InterruptedException {

    }

    @Override
    public void start() throws InterruptedException {
        Map<String, Object> params = robot.pullNonGamepad(this);
        autonomous = (String) params.get("Autonomous");
        color = (String) params.get("Color");
        startPos = (String) params.get("StartPosition");
    }

    @Override
    public void handle() throws InterruptedException {

    }

    @Override
    public void stop() throws InterruptedException {

    }
    @AutonomousConfig
    public static ConfigParam[] autoConfig() {
        return new ConfigParam[] {
                new ConfigParam("Autonomous", new String[] {
                        "ParkOnBridge",
                        "MoveFoundationPark",
                        "MaxPoints"},
                        "ParkOnBridgeResource"),
                new ConfigParam("Color", new String[] {
                        "Blue",
                        "Red"},
                        "Blue"),
                new ConfigParam("StartPosition", new String[]{
                        "Resourse",
                        "Construction"},
                        "Resourse")
        };
    }
}
