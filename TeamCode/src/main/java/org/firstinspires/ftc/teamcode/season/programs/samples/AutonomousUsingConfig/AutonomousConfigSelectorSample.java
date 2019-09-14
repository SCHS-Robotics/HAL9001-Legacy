package org.firstinspires.ftc.teamcode.season.programs.samples.AutonomousUsingConfig;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.util.annotations.AutonomousConfig;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;

import java.util.HashMap;
import java.util.Map;


//must extend SubSystem to use the config.
public class AutonomousConfigSelectorSample extends SubSystem {

    //The string to store which autonomous program to use.
    public String autonomousMode;

    /**
     * Ctor for subsystem.
     *
     * @param robot - The robot the subsystem is contained within.
     */
    AutonomousConfigSelectorSample(Robot robot){
        super(robot);
        //MUST SET USES CONFIG TO TRUE
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
        //Sets the autonomous mode to be used in opMode
        Map<String, Object> params = robot.pullNonGamepad(this);
        autonomousMode = (String) params.get("Autonomous");
    }

    @Override
    public void handle() throws InterruptedException {

    }

    @Override
    public void stop() throws InterruptedException {

    }

    //This is used in the tellyOp to determine what autonomous to run.
    @AutonomousConfig
    public static ConfigParam[] autoConfig() {
        return new ConfigParam[] {
                new ConfigParam("Autonomous", new String[] {
                        "NameOfFirstAutonomous", 
                        "NameOfSecondAutonomous", 
                        "NameOfThirdAutonomous"}, 
                        //This can continue for each autonomous you have.
                        "NameOfFirstAutonomous"),
        };
    }
}
