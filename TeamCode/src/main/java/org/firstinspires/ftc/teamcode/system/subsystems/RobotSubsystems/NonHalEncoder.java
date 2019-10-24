
package org.firstinspires.ftc.teamcode.system.subsystems.RobotSubsystems;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class NonHalEncoder extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor topLeft = null;
    private DcMotor topRight = null;
    private DcMotor bottomLeft = null;
    private DcMotor bottomRight = null;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.update();


        topLeft = hardwareMap.get(DcMotor.class, "topLeft");
        topRight = hardwareMap.get(DcMotor.class, "topRight");
        bottomLeft = hardwareMap.get(DcMotor.class, "bottomLeft");
        bottomRight = hardwareMap.get(DcMotor.class, "bottomRight");

        // Sets motor

        topLeft.setDirection(DcMotor.Direction.REVERSE);
        topRight.setDirection(DcMotor.Direction.FORWARD);
        bottomLeft.setDirection(DcMotor.Direction.REVERSE);
        bottomRight.setDirection(DcMotor.Direction.FORWARD);

        waitForStart();
        runtime.reset();

        topLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        topLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        if (topLeft.getCurrentPosition() < 10000) {
            topLeft.setPower(1);
            topRight.setPower(1);
            bottomLeft.setPower(1);
            bottomRight.setPower(1);
        } else {
            topLeft.setPower(0);
            topRight.setPower(0);
            bottomLeft.setPower(0);
            bottomRight.setPower(0);
        }
        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());

        telemetry.update();
    }
}

