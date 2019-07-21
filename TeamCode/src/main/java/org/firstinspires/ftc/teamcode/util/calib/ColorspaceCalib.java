/*
 * Filename: ColorspaceCalib.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: 7/18/19
 */
package org.firstinspires.ftc.teamcode.util.calib;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.external.Function;
import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.system.source.SubSystem;
import org.firstinspires.ftc.teamcode.util.exceptions.ChannelDoesNotExistException;
import org.firstinspires.ftc.teamcode.util.exceptions.NotBooleanInputException;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;
import org.firstinspires.ftc.teamcode.util.misc.Toggle;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ColorspaceCalib extends SubSystem implements CameraBridgeViewBase.CvCameraViewListener2 {
    public int x_lower, y_lower, z_lower, x_upper, y_upper, z_upper;
    private int increment;
    private CustomizableGamepad inputs;
    private Toggle slowModeToggle, upperLimit;
    private final String SLOWMODE = "slowMode", X_INCREMENT = "XUp", X_DECREMENT = "XDown", Y_INCREMENT = "YUp", Y_DECREMENT = "YDown", Z_INCREMENT = "ZUp", Z_DECREMENT = "ZDown", CHANGELIMIT = "ChangeLimit";
    private Function<Mat,Mat> converter;
    private ColorSpace colorSpace;
    private long lastActivatedTimestamp;
    private int channelIdx, delayMs;
    private ImageType imageType;

    public enum ColorSpace {
        HSV, RGB, Lab, YUV, BGR, HLS, HSV_FULL, HLS_FULL, LUV, XYZ, YCrCb, CUSTOM
    }

    public enum ImageType {
        SINGLE_CHANNEL, COLOR;
    }

    public ColorspaceCalib(Robot robot, ColorSpace colorSpace){
        super(robot);

        Gamepad gamepad = robot.gamepad1;
        setInputs(new Button(gamepad, Button.BooleanInputs.dpad_up), new Button(gamepad, Button.BooleanInputs.dpad_down), new Button(gamepad, Button.BooleanInputs.bool_left_stick_y_up), new Button(gamepad, Button.BooleanInputs.bool_left_stick_y_down), new Button(gamepad, Button.BooleanInputs.bool_right_stick_y_up), new Button(gamepad, Button.BooleanInputs.bool_right_stick_y_down), new Button(gamepad, Button.BooleanInputs.a), new Button(gamepad, Button.BooleanInputs.x));

        this.colorSpace = colorSpace;
        this.imageType = ImageType.COLOR;

        initVars();
    }

    public ColorspaceCalib(Robot robot, ColorSpace colorSpace, ImageType imageType, int channelIdx){
        super(robot);

        Gamepad gamepad = robot.gamepad1;
        setInputs(new Button(gamepad, Button.BooleanInputs.dpad_up), new Button(gamepad, Button.BooleanInputs.dpad_down), new Button(gamepad, Button.BooleanInputs.bool_left_stick_y_up), new Button(gamepad, Button.BooleanInputs.bool_left_stick_y_down), new Button(gamepad, Button.BooleanInputs.bool_right_stick_y_up), new Button(gamepad, Button.BooleanInputs.bool_right_stick_y_down), new Button(gamepad, Button.BooleanInputs.a), new Button(gamepad, Button.BooleanInputs.x));

        this.colorSpace = colorSpace;
        this.imageType = imageType;
        this.channelIdx = channelIdx;

        initVars();
    }

    public ColorspaceCalib(Robot robot, Function<Mat,Mat> converter){
        super(robot);

        Gamepad gamepad = robot.gamepad1;
        setInputs(new Button(gamepad, Button.BooleanInputs.dpad_up), new Button(gamepad, Button.BooleanInputs.dpad_down), new Button(gamepad, Button.BooleanInputs.bool_left_stick_y_up), new Button(gamepad, Button.BooleanInputs.bool_left_stick_y_down), new Button(gamepad, Button.BooleanInputs.bool_right_stick_y_up), new Button(gamepad, Button.BooleanInputs.bool_right_stick_y_down), new Button(gamepad, Button.BooleanInputs.a), new Button(gamepad, Button.BooleanInputs.x));

        this.colorSpace = ColorSpace.CUSTOM;
        this.imageType = ImageType.COLOR;
        this.converter = converter;

        initVars();
    }

    public ColorspaceCalib(Robot robot, Function<Mat,Mat> converter, ImageType imageType, int channelIdx){
        super(robot);

        Gamepad gamepad = robot.gamepad1;
        setInputs(new Button(gamepad, Button.BooleanInputs.dpad_up), new Button(gamepad, Button.BooleanInputs.dpad_down), new Button(gamepad, Button.BooleanInputs.bool_left_stick_y_up), new Button(gamepad, Button.BooleanInputs.bool_left_stick_y_down), new Button(gamepad, Button.BooleanInputs.bool_right_stick_y_up), new Button(gamepad, Button.BooleanInputs.bool_right_stick_y_down), new Button(gamepad, Button.BooleanInputs.a), new Button(gamepad, Button.BooleanInputs.x));

        this.colorSpace = ColorSpace.CUSTOM;
        this.imageType = imageType;
        this.converter = converter;
        this.channelIdx = channelIdx;

        initVars();
    }

    public ColorspaceCalib(Robot robot, Button XUp, Button XDown, Button YUp, Button YDown, Button ZUp, Button ZDown, Button slowMode, Button changeLimit, ColorSpace colorSpace){
        super(robot);

        setInputs(XUp, XDown, YUp, YDown, ZUp, ZDown, slowMode, changeLimit);

        this.colorSpace = colorSpace;
        this.imageType = ImageType.COLOR;

        initVars();
    }

    public ColorspaceCalib(Robot robot, Button XUp, Button XDown, Button YUp, Button YDown, Button ZUp, Button ZDown, Button slowMode, Button changeLimit, ColorSpace colorSpace, ImageType imageType, int channelIdx){
        super(robot);

        setInputs(XUp, XDown, YUp, YDown, ZUp, ZDown, slowMode, changeLimit);

        this.colorSpace = colorSpace;
        this.imageType = imageType;
        this.channelIdx = channelIdx;

        initVars();
    }

    public ColorspaceCalib(Robot robot, Button XUp, Button XDown, Button YUp, Button YDown, Button ZUp, Button ZDown, Button slowMode, Button changeLimit, Function<Mat,Mat> converter){
        super(robot);

        setInputs(XUp, XDown, YUp, YDown, ZUp, ZDown, slowMode, changeLimit);

        this.colorSpace = ColorSpace.CUSTOM;
        this.imageType = ImageType.COLOR;
        this.converter = converter;

        initVars();
    }

    public ColorspaceCalib(Robot robot, Button XUp, Button XDown, Button YUp, Button YDown, Button ZUp, Button ZDown, Button slowMode, Button changeLimit, Function<Mat,Mat> converter, ImageType imageType, int channelIdx){
        super(robot);

        setInputs(XUp, XDown, YUp, YDown, ZUp, ZDown, slowMode, changeLimit);

        this.colorSpace = ColorSpace.CUSTOM;
        this.imageType = imageType;
        this.converter = converter;
        this.channelIdx = channelIdx;

        initVars();
    }

    @Override
    public void init() {

    }

    @Override
    public void handle() {

        slowModeToggle.updateToggle(inputs.getBooleanInput(SLOWMODE));

        if (slowModeToggle.getCurrentState()) {
            increment = 1;
        } else {
            increment = 5;
        }
        
        upperLimit.updateToggle(inputs.getBooleanInput(CHANGELIMIT));

        if(System.currentTimeMillis() - lastActivatedTimestamp >= delayMs) {
            if (upperLimit.getCurrentState()) {
                if (inputs.getBooleanInput(X_INCREMENT)) {
                    x_upper += increment;
                } else if (inputs.getBooleanInput(X_DECREMENT)) {
                    x_upper -= increment;
                }
                if (inputs.getBooleanInput(Y_INCREMENT)) {
                    y_upper += increment;
                } else if (inputs.getBooleanInput(Y_DECREMENT)) {
                    y_upper -= increment;
                }
                if (inputs.getBooleanInput(Z_INCREMENT)) {
                    z_upper += increment;
                } else if (inputs.getBooleanInput(Z_DECREMENT)) {
                    z_upper -= increment;
                }
            } else {
                if (inputs.getBooleanInput(X_INCREMENT)) {
                    x_lower += increment;
                } else if (inputs.getBooleanInput(X_DECREMENT)) {
                    x_lower -= increment;
                }
                if (inputs.getBooleanInput(Y_INCREMENT)) {
                    y_lower += increment;
                } else if (inputs.getBooleanInput(Y_DECREMENT)) {
                    y_lower -= increment;
                }
                if (inputs.getBooleanInput(Z_INCREMENT)) {
                    z_lower += increment;
                } else if (inputs.getBooleanInput(Z_DECREMENT)) {
                    z_lower -= increment;
                }
            }
            lastActivatedTimestamp = System.currentTimeMillis();
        }
    }

    @Override
    public void stop() {
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

        inputFrame.gray().release();

        Mat converted = new Mat();
        Mat rgb = new Mat();

        Imgproc.cvtColor(inputFrame.rgba(),rgb,Imgproc.COLOR_RGBA2RGB);

        inputFrame.rgba().release();

        if(imageType == ImageType.COLOR){

            convertImage(rgb, converted, colorSpace);

            Mat binaryMask = new Mat();
            Core.inRange(converted,new Scalar(x_lower,y_lower,z_lower),new Scalar(x_upper,y_upper,z_upper),binaryMask);

            converted.release();

            Imgproc.cvtColor(binaryMask,binaryMask,Imgproc.COLOR_GRAY2RGB);
            Mat outputMask = new Mat();
            Core.bitwise_and(binaryMask,rgb,outputMask);

            rgb.release();
            binaryMask.release();

            System.gc();

            return outputMask;
        }
        else {

            convertImage(rgb,converted,colorSpace);
            Mat chan = new Mat();
            try {Core.extractChannel(converted,chan,channelIdx);}
            catch(CvException e) {
                if(channelIdx < 0 || channelIdx > (converted.channels()-1)) {
                    throw new ChannelDoesNotExistException("Error: Your channel index does not refer to an actual channel in the image.", e);
                }
                else {
                    throw e;
                }
            }

            converted.release();

            Mat binaryMask = new Mat();
            Imgproc.threshold(chan,binaryMask,z_lower,255, Imgproc.THRESH_BINARY);

            chan.release();

            Mat outputMask = new Mat();
            Imgproc.cvtColor(binaryMask,binaryMask,Imgproc.COLOR_GRAY2RGB);
            Core.bitwise_and(binaryMask,rgb,outputMask);
            binaryMask.release();
            rgb.release();

            return outputMask;
        }
    }

    public int[] getLowerBound() {
        return new int[] {x_lower,y_lower,z_lower};
    }

    public int[] getUpperBound() {
        return new int[] {x_upper,y_upper,z_upper};
    }




    private void convertImage(Mat src, Mat dst, ColorSpace colorSpace){
        switch (colorSpace){
            case HSV: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2HSV); break;
            case BGR: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2BGR); break;
            case HLS: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2HLS); break;
            case Lab: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2Lab); break;
            case LUV: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2Luv); break;
            case RGB: break; //Bruh
            case XYZ: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2XYZ); break;
            case YUV: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2YUV); break;
            case YCrCb: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2YCrCb); break;
            case HLS_FULL: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2HLS_FULL); break;
            case HSV_FULL: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2HSV_FULL); break;
            case CUSTOM: converter.apply(src).copyTo(dst);
            default: break;
        }
    }

    public void setDelay(int delayMs) {
        this.delayMs = delayMs;
    }
    
    private void initVars() {
        x_lower = 0;
        y_lower = 0;
        z_lower = 0;
        x_upper = 255;
        y_upper = 255;
        z_upper = 255;
        slowModeToggle = new Toggle(false);
        increment = 5;
        upperLimit = new Toggle(false);
        lastActivatedTimestamp = 0;
        delayMs = 200;
        inputs = new CustomizableGamepad();
    }

    private void startOpenCV(CameraBridgeViewBase.CvCameraViewListener2 cameraViewListener) {
        FtcRobotControllerActivity.turnOnCameraView.obtainMessage(1, cameraViewListener).sendToTarget();
    }

    private void stopOpenCV() {
        FtcRobotControllerActivity.turnOffCameraView.obtainMessage().sendToTarget();
    }

    public void startVision() {
        startOpenCV(this);
    }

    public void stopVision() {
        stopOpenCV();
    }

    private void setInputs(Button XUp, Button XDown, Button YUp, Button YDown, Button ZUp, Button ZDown, Button slowMode, Button changeLimit){
        if(XUp.isBoolean && XDown.isBoolean && YUp.isBoolean && YDown.isBoolean && ZUp.isBoolean && ZDown.isBoolean && slowMode.isBoolean && changeLimit.isBoolean) {
            inputs.addButton(X_INCREMENT, XUp);
            inputs.addButton(X_DECREMENT, XDown);
            inputs.addButton(Y_INCREMENT, YUp);
            inputs.addButton(Y_DECREMENT, YDown);
            inputs.addButton(Z_INCREMENT, ZUp);
            inputs.addButton(Z_DECREMENT, ZDown);
            inputs.addButton(SLOWMODE, slowMode);
            inputs.addButton(CHANGELIMIT, changeLimit);
        }
        else {
            throw new NotBooleanInputException("Error: All inputs must be a boolean input");
        }
    }
}
