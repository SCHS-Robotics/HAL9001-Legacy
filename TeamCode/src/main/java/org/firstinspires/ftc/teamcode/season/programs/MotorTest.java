package org.firstinspires.ftc.teamcode.season.programs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "TestMotor")
public class MotorTest extends LinearOpMode {
    DcMotor motor1;
    DcMotor motor2;
    DcMotor motor3;
    DcMotor motor4;

    boolean motor1Used = true;
    boolean motor2Used = true;
    boolean motor3Used = true;
    boolean motor4Used = true;


    @Override
    public void runOpMode() throws InterruptedException {
        try {
            motor1 = hardwareMap.dcMotor.get("Motor1");
        } catch (Exception e){
            motor1Used = false;
        }
        try{
            motor2 = hardwareMap.dcMotor.get("Motor2");
        } catch (Exception e){
            motor2Used = false;
        }
        try{
            motor3 = hardwareMap.dcMotor.get("Motor3");
        } catch (Exception e){
            motor3Used = false;
        }
        try{
            motor4 = hardwareMap.dcMotor.get("Motor4");
        } catch (Exception e){https:
            motor4Used = false;
        }
        waitForStart();
        while (opModeIsActive()){
            if(motor1Used && Math.abs(gamepad1.left_stick_y) > .05){
                motor1.setPower(gamepad1.left_stick_y);
            }
            else if (motor1Used){
                motor1.setPower(0);
            }
            if(motor2Used && Math.abs(gamepad1.right_stick_y) > .05){
                motor2.setPower(gamepad1.right_stick_y);
            }
            else if (motor2Used){
                motor2.setPower(0);
            }
            if(motor3Used && gamepad1.a){
                motor3.setPower(1);
            }
            else if(motor3Used && gamepad1.b){
                motor3.setPower(-1);
            }
            else if (motor3Used){
                motor3.setPower(0);
            }
            if(motor4Used && gamepad1.x){
                motor4.setPower(1);
            }
            else if(motor4Used && gamepad1.y){
                motor4.setPower(-1);
            }
            else if (motor4Used){
                motor4.setPower(0);
            }
        }
    }
}
