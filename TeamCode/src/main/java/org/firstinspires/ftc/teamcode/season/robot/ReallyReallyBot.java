package org.firstinspires.ftc.teamcode.season.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.subsystems.MechanumDrive;
import org.firstinspires.ftc.teamcode.system.subsystems.ReallyProject;
import org.firstinspires.ftc.teamcode.system.subsystems.ServoProject;
import org.firstinspires.ftc.teamcode.util.misc.Button;

public class ReallyReallyBot extends Robot {
    public static MechanumDrive asd;
    public ReallyReallyBot(OpMode opmode) {
        super(opmode);
        startGui(new Button(1, Button.BooleanInputs.back));
        asd = new MechanumDrive(this, new MechanumDrive.Params("topLeft", "topRight", "bottomLeft", "bottomRight")
                .setDriveType(MechanumDrive.DriveType.STANDARD)
                .setDriveStick(new Button(1, Button.VectorInputs.left_stick))
                .setTurnStick(new Button(1, Button.DoubleInputs.right_stick_x))
        );
        //ReallyProject asd = new ReallyProject(this, "bottomLeft", "bottomRight", "topLeft", "topRight");
        super.putSubSystem("Reallybot", asd);
    }
    public static void gyro() throws InterruptedException {
        asd.setUseGyro(true);
    }
}