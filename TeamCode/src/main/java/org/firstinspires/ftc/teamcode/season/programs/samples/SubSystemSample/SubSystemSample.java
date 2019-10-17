package org.firstinspires.ftc.teamcode.season.programs.samples.SubSystemSample;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;

public class SubSystemSample extends SubSystem {

    /**
     * Constructor for subsystem.
     *
     * @param robot - The robot the subsystem is contained within.
     */
    public SubSystemSample(Robot robot) {
        super(robot);
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
}
