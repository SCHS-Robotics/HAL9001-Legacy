package org.firstinspires.ftc.teamcode.util.misc;/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;



/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="BallDetectionTest", group="Linear Opmode")

public class OpenCV_code_from_last_year extends LinearOpMode implements CameraBridgeViewBase.CvCameraViewListener2{

    double numFound = 0;
    double numFound2 = 0;
    double averageX = 0;
    double averageY = 0;
    double averageX2 = 0;
    double averageY2 = 0;
    /*
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor topLeft = null;
    private DcMotor topRight = null;
    private DcMotor bottomLeft = null;
    private DcMotor bottomRight = null;
    private DcMotor leftLift = null;
    private DcMotor rightLift = null;
    private DcMotor intakeVertical = null;
    private DcMotor intakeSpinner = null;
    private Servo flagServo = null;
*/

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
/*
        double g1ly = 0;
        double g1lx = 0;
        double g2ly = 0;
        double g2lx = 0;
        double g1ry = 0;
        double g1rx = 0;
        double g2ry = 0;
        double g2rx = 0;
        double deadzone = .25;

        boolean xToggleFlag = true;
        boolean xToggle = false;

        boolean slowToggleToggle = false;
        boolean slowToggleFlag = true;
        double slowFactor = 1;

        topLeft  = hardwareMap.get(DcMotor.class, "topLeft");
        topRight = hardwareMap.get(DcMotor.class, "topRight");
        bottomLeft  = hardwareMap.get(DcMotor.class, "bottomLeft");
        bottomRight = hardwareMap.get(DcMotor.class, "bottomRight");
        intakeSpinner = hardwareMap.get(DcMotor.class, "intakeSpinner");
        intakeVertical = hardwareMap.get(DcMotor.class, "intakeVertical");
        leftLift = hardwareMap.get(DcMotor.class, "leftLift");
        rightLift = hardwareMap.get(DcMotor.class, "rightLift");
        flagServo = hardwareMap.get(Servo.class, "flagServo");

        topLeft.setDirection(DcMotor.Direction.REVERSE);
        topRight.setDirection(DcMotor.Direction.FORWARD);
        bottomLeft.setDirection(DcMotor.Direction.REVERSE);
        bottomRight.setDirection(DcMotor.Direction.FORWARD);
        leftLift.setDirection(DcMotor.Direction.REVERSE);
        rightLift.setDirection(DcMotor.Direction.FORWARD);
        intakeSpinner.setDirection(DcMotor.Direction.REVERSE);
        intakeVertical.setDirection(DcMotor.Direction.FORWARD);
  */
        waitForStart();



        startOpenCV(this);

        while (opModeIsActive()) {
            telemetry.addData("Average X: ", Double.toString(averageX2));
            telemetry.addData("Average Y: ", Double.toString(averageY2));
            telemetry.update();
        }

        stopOpenCV();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //This instantly releases the grayScale image from the camera as we don't use it in the program.
        inputFrame.gray().release();

        //This gets the input rgb picture and sets it to cameraPicRGBA.
        Mat cameraPicRGBA = inputFrame.rgba();
        //compression of the image to remove some noise and speed it up.
        Imgproc.resize(cameraPicRGBA, cameraPicRGBA, new Size(1280 / 3, 720 / 3));
        //This blurs the image to remove more noise from the image.
        Imgproc.GaussianBlur(cameraPicRGBA, cameraPicRGBA, new Size(3, 3), 0);
        //These two lines are creating the output images for the next steps.
        Mat hsv = new Mat();
        Mat block = new Mat();
        //This sets the variable hsv to the camera image but converted to hsv format.
        Imgproc.cvtColor(cameraPicRGBA, hsv, Imgproc.COLOR_RGB2HSV);
        //This sets block to a black and white image where the white pixels are the pixels with hsv values between the two scalars (HAL can help with this step)
        Core.inRange(hsv, new Scalar(25 / 2, 125, 80), new Scalar(55 / 2, 999999, 99999), block);

        //releases hsv
        hsv.release();

        //gets the input frame and saves it to inputClone
        Mat inputClone = inputFrame.rgba();
        //creates a output mat for later.
        Mat yellowClosed = new Mat();
        //IDK what this does. Ask cole if you need to.
        Mat kernal = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9, 9));
        //Again no idea.
        Imgproc.morphologyEx(block, yellowClosed, Imgproc.MORPH_CLOSE, kernal);
        //Creates edges mat for later.
        Mat edges = new Mat();
        //Not sure what this does.
        Imgproc.Canny(yellowClosed, edges, 0, 255);
        //Creates a list of points of interest.
        List<MatOfPoint> contours = new ArrayList<>();
        //Finds all shapes in the black and white image.
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        //Not sure what this does.
        Imgproc.cvtColor(block, block, Imgproc.COLOR_GRAY2RGB);
        //goes through all
        for (MatOfPoint shape : contours) {
            //things
            MatOfPoint2f shapef = new MatOfPoint2f(shape.toArray());
            MatOfPoint2f approx = new MatOfPoint2f();
            //idk
            double perimeter = Imgproc.arcLength(shapef, true);
            //takes the shapes and kills the low perimiter ones. kinda like bluring the perimiter of the shapes.
            Imgproc.approxPolyDP(shapef, approx, 0.04 * perimeter, true);
            List<MatOfPoint> approxlist = new ArrayList<>();
            approxlist.add(new MatOfPoint(approx.toArray()));
            //removes all shapes that are too small (noise reduction)
            if (Imgproc.contourArea(shape) < 300) {
                continue;
            }
            //gets the x and y position of the shape
            Moments M = Imgproc.moments(shape);
            double cX = M.get_m10() / M.get_m00();
            double cY = M.get_m01() / M.get_m00();
            //ignores all shapes too high on the screen.
            if (cY < 105) {
                continue;
            }
            //ignores all shapes that don't have 6 or 4 sides.
            if (approx.toList().size() == 6 || approx.toList().size() == 4) {
                //draws a line around the shape
                Imgproc.drawContours(block, approxlist, 0, new Scalar(0, 255, 0), 10);
                //adds x and y position to the average.
                averageX += cX;
                averageY += cY;
                //adds to number of times shape was found
                numFound++;
                numFound2 = numFound;
            }
        }
        //ignore this .
        averageX2 = averageX / numFound;
        averageY2 = averageY / numFound;

        //creates a circle where it thinks the block is
        Imgproc.circle(block, new Point(averageX2, averageY2), 7, new Scalar(0, 0, 255), -1);
        //undoes the compreshion.
        Imgproc.resize(block, block, new Size(1280, 720));
        //cleans up the system (do this at the end)
        System.gc();
        return block;


    }
    public void startOpenCV(CameraBridgeViewBase.CvCameraViewListener2 cameraViewListener) {
        FtcRobotControllerActivity.turnOnCameraView.obtainMessage(1, cameraViewListener).sendToTarget();
    }

    public void stopOpenCV() {
        FtcRobotControllerActivity.turnOffCameraView.obtainMessage().sendToTarget();
    }
}
