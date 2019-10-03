package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.subsystems.MechanumDrive;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.GillSubSystem;
import org.firstinspires.ftc.teamcode.util.misc.Button;

public class BillBot extends Robot {
    public MechanumDrive mD = new MechanumDrive (this, new MechanumDrive.Params("topLeft", "topRight", "bottomLeft", "bottomRight")
            .setDriveType(MechanumDrive.DriveType.STANDARD)
            .setDriveStick(new Button(1, Button.VectorInputs.left_stick))
            .setTurnStick(new Button(1, Button.DoubleInputs.right_stick_x))
    );

    public BillBot(OpMode opMode) {
        super(opMode);
        super.putSubSystem("GarrettGod", mD);
    
    }
}