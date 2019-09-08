package org.firstinspires.ftc.teamcode.season.programs.samples.SubSystemSample;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.util.annotations.AutonomousConfig;
import org.firstinspires.ftc.teamcode.util.annotations.TeleopConfig;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.ConfigParam;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;

import java.util.LinkedHashMap;
import java.util.Map;

public class SubSystemUsingConfigSample extends SubSystem {

    private CustomizableGamepad inputs; //A customizable gamepad containing the inputs.

    /**
     * Ctor for subsystem.
     *
     * @param robot - The robot the subsystem is contained within.
     */
    public SubSystemUsingConfigSample(Robot robot) {
        super(robot);
        //This is required for the config selection menu to appear on init
        usesConfig = true;
    }

    //This will run once on init
    @Override
    public void init() throws InterruptedException {

    }

    //This will loop on init
    @Override
    public void init_loop() throws InterruptedException {

    }

    //This will run once on start
    @Override
    public void start() throws InterruptedException {
        inputs = robot.pullControls(this); //gets all the gamepad controls from the config
        Map<String,Object> nonGamepad = robot.pullNonGamepad(this);

        //Set shared setting here
        if(robot.isAutonomous()) {
            //Set Autonomous specific setting here
        }
        else if(robot.isTeleop()) {
            //Set TeleOp setting here
            int theSettingOfWisdom = (int) nonGamepad.get("MyCustomOption"); //gets your custom teleop config setting
        }
    }

    //This will loop on start
    @Override
    public void handle() throws InterruptedException {

    }

    //This will run once on stop
    @Override
    public void stop() throws InterruptedException {

    }

    //This is an optional override that will run during the constructor. It should be used to initVars
    @Override
    protected void initVars() {
        super.initVars();
    }
    
    //@TeleopConfig is used for configs required to use this system in teleOp. Use @AutonomousConfig to specify the autonomous configuration settings.
    //Note that the name of the class doesn't matter, you just have to put the annotation on top so that it knows it is a config.
    @TeleopConfig
    //This is where you place the list of configs your program needs. Each config has a name and a list of possible options along with the default option.
    //Numbers lists, booleans, and gamepad Button Types can be auto generated.
    //The function has to be public, static, return an array of configParams, and take no parameters.
    public static ConfigParam[] teleopConfig() {
        return new ConfigParam[] {
                //This is a BooleanButton option. It is auto generated with all boolean inputs and b is the default option
                new ConfigParam("MyBooleanButtonOption", Button.BooleanInputs.b),
                //This is a Number option. It is auto generated with numbers starting at 0 and ending with 10 with an incrament of 1 and a default option of 1
                new ConfigParam("MyNumberOption", ConfigParam.numberMap(0,10,1),1),
                //This is a boolean option and is auto generated with true and false. It defaults to false
                new ConfigParam("MyBooleanOption",ConfigParam.booleanMap,false),
                //This is a custom config option where a string can be paired with any data you choose. The string is what will appear on the phone during configuration.
                //It has a default option of Alpha.
                new ConfigParam("MyCustomOption", new LinkedHashMap<String, Object>() {{
                    put("Alpha",123);
                    put("Beta",456);
                    put("Gamma",789);
                }}, "Alpha")
        };
    }

    //Does the same thing as above but is for autonomous usage rather than teleop usage.
    @AutonomousConfig
    public static ConfigParam[] autoConfig() {
        return new ConfigParam[] {
                new ConfigParam("myAutoConfigSetting", Button.VectorInputs.left_stick),
                new ConfigParam("myIntAutoParam",ConfigParam.numberMap(0,1,0.1),1.0)
        };
    }
}
