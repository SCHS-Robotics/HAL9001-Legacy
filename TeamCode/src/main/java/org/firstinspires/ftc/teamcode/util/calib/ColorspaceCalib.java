/*
 * Filename: ColorspaceCalib.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: 7/18/19
 */
package org.firstinspires.ftc.teamcode.util.calib;

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

    //The upper and lower limits for the x, y, z values for the color spaces.
    public int x_lower, y_lower, z_lower, x_upper, y_upper, z_upper;
    //Controls the change in x, y, z values.
    private int increment;
    //The collection of user selected inputs.
    private CustomizableGamepad inputs;
    //Toggle class that toggles slow mode and upper limit on and off.
    private Toggle slowModeToggle, upperLimit;
    //Key names used with the CustomizableGamepad.
    private final String SLOWMODE = "slowMode", X_INCREMENT = "XUp", X_DECREMENT = "XDown", Y_INCREMENT = "YUp", Y_DECREMENT = "YDown", Z_INCREMENT = "ZUp", Z_DECREMENT = "ZDown", CHANGELIMIT = "ChangeLimit";
    //todo Cole plz do this one
    private Function<Mat,Mat> converter;
    //Selected colorspace that will be used.
    private ColorSpace colorSpace;
    //Used to only allow the increment to change the x,y,z values every deleyMs milliseconds.
    private long lastActivatedTimestamp;
    //DelayMs time between changes to x, y, z values in Milliseconds. todo chanelIdx cole do this one too
    private int channelIdx, delayMs;
    //todo cole do this one as well
    private ImageType imageType;

    //List of already supported 3 chanel color spaces.
    public enum ColorSpace {
        HSV, RGB, Lab, YUV, BGR, HLS, HSV_FULL, HLS_FULL, LUV, XYZ, YCrCb, CUSTOM
    }

    //todo cole do this
    public enum ImageType {
        SINGLE_CHANNEL, COLOR;
    }

    /**
     * Constructor that uses default keys.
     *
     * @param robot - The robot running the program.
     * @param colorSpace - Enum that determine what 3 chanel color space to use.
     */
    public ColorspaceCalib(Robot robot, ColorSpace colorSpace){
        super(robot);

        initVars();

        //default keys are all on gamepad1 and are dpad_up for x_increment, dpad_down for x decrement, left_stick_up for y_increment, left_stick_down for y decrement, right_stick_up for z_increment, right_stick_down for z decrement, a for slowMode, and b for changeLimit.
        setInputs(new Button(1, Button.BooleanInputs.dpad_up), new Button(1, Button.BooleanInputs.dpad_down), new Button(1, Button.BooleanInputs.bool_left_stick_y_up), new Button(1, Button.BooleanInputs.bool_left_stick_y_down), new Button(1, Button.BooleanInputs.bool_right_stick_y_up), new Button(1, Button.BooleanInputs.bool_right_stick_y_down), new Button(1, Button.BooleanInputs.a), new Button(1, Button.BooleanInputs.x));

        this.colorSpace = colorSpace;
        this.imageType = ImageType.COLOR;
    }

    /**
     * Constructor that uses default keys and todo cole hey.
     *
     * @param robot - The robot running the program.
     * @param colorSpace - Enum that determine what 3 chanel color space to use.
     * @param imageType - todo cole do this one too
     * @param channelIdx - todo cole here too
     */
    public ColorspaceCalib(Robot robot, ColorSpace colorSpace, ImageType imageType, int channelIdx){
        super(robot);

        initVars();

        //default keys are all on gamepad1 and are dpad_up for x_increment, dpad_down for x decrement, left_stick_up for y_increment, left_stick_down for y decrement, right_stick_up for z_increment, right_stick_down for z decrement, a for slowMode, and b for changeLimit.
        setInputs(new Button(1, Button.BooleanInputs.dpad_up), new Button(1, Button.BooleanInputs.dpad_down), new Button(1, Button.BooleanInputs.bool_left_stick_y_up), new Button(1, Button.BooleanInputs.bool_left_stick_y_down), new Button(1, Button.BooleanInputs.bool_right_stick_y_up), new Button(1, Button.BooleanInputs.bool_right_stick_y_down), new Button(1, Button.BooleanInputs.a), new Button(1, Button.BooleanInputs.x));

        this.colorSpace = colorSpace;
        this.imageType = imageType;
        this.channelIdx = channelIdx;
    }

    /**
     * Constructor that uses default keys and todo cole hey buddy.
     *
     * @param robot - The robot running the program.
     * @param converter - todo cole here
     */
    public ColorspaceCalib(Robot robot, Function<Mat,Mat> converter){
        super(robot);

        initVars();

        setInputs(new Button(1, Button.BooleanInputs.dpad_up), new Button(1, Button.BooleanInputs.dpad_down), new Button(1, Button.BooleanInputs.bool_left_stick_y_up), new Button(1, Button.BooleanInputs.bool_left_stick_y_down), new Button(1, Button.BooleanInputs.bool_right_stick_y_up), new Button(1, Button.BooleanInputs.bool_right_stick_y_down), new Button(1, Button.BooleanInputs.a), new Button(1, Button.BooleanInputs.x));

        this.colorSpace = ColorSpace.CUSTOM;
        this.imageType = ImageType.COLOR;
        this.converter = converter;
    }

    /**
     * Constructor that uses default keys and todo hey cole hows it going.
     *
     * @param robot - The robot running the program.
     * @param converter - todo cole here
     * @param imageType - todo cole do this one too
     * @param channelIdx - todo cole here too
     */
    public ColorspaceCalib(Robot robot, Function<Mat,Mat> converter, ImageType imageType, int channelIdx){
        super(robot);

        initVars();

        setInputs(new Button(1, Button.BooleanInputs.dpad_up), new Button(1, Button.BooleanInputs.dpad_down), new Button(1, Button.BooleanInputs.bool_left_stick_y_up), new Button(1, Button.BooleanInputs.bool_left_stick_y_down), new Button(1, Button.BooleanInputs.bool_right_stick_y_up), new Button(1, Button.BooleanInputs.bool_right_stick_y_down), new Button(1, Button.BooleanInputs.a), new Button(1, Button.BooleanInputs.x));

        this.colorSpace = ColorSpace.CUSTOM;
        this.imageType = imageType;
        this.converter = converter;
        this.channelIdx = channelIdx;
    }

    /**
     * Constructor that lets you set the keys.
     *
     * @param robot - The robot running the program.
     * @param XUp - Button to increment X values.
     * @param XDown - Button to decrement X values.
     * @param YUp - Button to increment Y values.
     * @param YDown - Button to decrement Y values.
     * @param ZUp - Button to increment Z values.
     * @param ZDown - Button to decrement Z values.
     * @param slowMode - Toggle button for slow mode.
     * @param changeLimit - Toggle button for changing between changing upper values and lower values.
     * @param colorSpace - Enum that determine what 3 chanel color space to use.
     */
    public ColorspaceCalib(Robot robot, Button XUp, Button XDown, Button YUp, Button YDown, Button ZUp, Button ZDown, Button slowMode, Button changeLimit, ColorSpace colorSpace){
        super(robot);

        initVars();

        setInputs(XUp, XDown, YUp, YDown, ZUp, ZDown, slowMode, changeLimit);

        this.colorSpace = colorSpace;
        this.imageType = ImageType.COLOR;
    }

    /**
     * Constructor that lets you set the keys and todo you know.
     *
     * @param robot - The robot running the program.
     * @param XUp - Button to increment X values.
     * @param XDown - Button to decrement X values.
     * @param YUp - Button to increment Y values.
     * @param YDown - Button to decrement Y values.
     * @param ZUp - Button to increment Z values.
     * @param ZDown - Button to decrement Z values.
     * @param slowMode - Toggle button for slow mode.
     * @param changeLimit - Toggle button for changing between changing upper values and lower values.
     * @param colorSpace - Enum that determine what 3 chanel color space to use.
     * @param imageType - todo cole here as well
     * @param channelIdx - todo cole hows it going
     */
    public ColorspaceCalib(Robot robot, Button XUp, Button XDown, Button YUp, Button YDown, Button ZUp, Button ZDown, Button slowMode, Button changeLimit, ColorSpace colorSpace, ImageType imageType, int channelIdx){
        super(robot);

        initVars();

        setInputs(XUp, XDown, YUp, YDown, ZUp, ZDown, slowMode, changeLimit);

        this.colorSpace = colorSpace;
        this.imageType = imageType;
        this.channelIdx = channelIdx;
    }

    /**
     * Constructor that lets you set the keys and todo hey cole.
     *
     * @param robot - The robot running the program.
     * @param XUp - Button to increment X values.
     * @param XDown - Button to decrement X values.
     * @param YUp - Button to increment Y values.
     * @param YDown - Button to decrement Y values.
     * @param ZUp - Button to increment Z values.
     * @param ZDown - Button to decrement Z values.
     * @param slowMode - Toggle button for slow mode.
     * @param changeLimit - Toggle button for changing between changing upper values and lower values.
     * @param converter - todo cole you know what to do
     */
    public ColorspaceCalib(Robot robot, Button XUp, Button XDown, Button YUp, Button YDown, Button ZUp, Button ZDown, Button slowMode, Button changeLimit, Function<Mat,Mat> converter){
        super(robot);

        initVars();

        setInputs(XUp, XDown, YUp, YDown, ZUp, ZDown, slowMode, changeLimit);

        this.colorSpace = ColorSpace.CUSTOM;
        this.imageType = ImageType.COLOR;
        this.converter = converter;
    }

    /**
     * Constructor that lets you set the keys and todo just do it.
     *
     * @param robot - The robot running the program.
     * @param XUp - Button to increment X values.
     * @param XDown - Button to decrement X values.
     * @param YUp - Button to increment Y values.
     * @param YDown - Button to decrement Y values.
     * @param ZUp - Button to increment Z values.
     * @param ZDown - Button to decrement Z values.
     * @param slowMode - Toggle button for slow mode.
     * @param changeLimit - Toggle button for changing between changing upper values and lower values.
     * @param converter - todo cole
     * @param imageType - todo yea
     * @param channelIdx- todo umm
     */
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
        robot.telemetry.addData("x_upper: ", x_upper);
        robot.telemetry.addData("x_lower: ", x_lower);
        robot.telemetry.addData("y_upper: ", y_upper);
        robot.telemetry.addData("y_lower: ", y_lower);
        robot.telemetry.addData("z_upper: ", z_upper);
        robot.telemetry.addData("z_lower: ", z_lower);

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

        robot.telemetry.update();
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

    /**
     * Blacks out pixels outside of the color space range and displays image on phones.
     */
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

    /**
     * Returns lower bounds for x, y, and z in an array.
     */
    public int[] getLowerBound() {
        return new int[] {x_lower,y_lower,z_lower};
    }

    /**
     * Returns upper bounds for x, y, and z in an array.
     */
    public int[] getUpperBound() {
        return new int[] {x_upper,y_upper,z_upper};
    }

    /**
     * Returns lower bounds for x, y, and z in an array.
     *
     * @param src - Source image that needs converting.
     * @param dst - Output of the image conversion
     */
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

    /**
     * Delay between each x, y, or z change.
     *
     * @param delayMs - delay in Ms between x, y, or z changes.
     */
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
        inputs = new CustomizableGamepad(robot);
    }

    /**
     * Starts openCV(call startVision to use this).
     */
    private void startOpenCV(CameraBridgeViewBase.CvCameraViewListener2 cameraViewListener) {
        FtcRobotControllerActivity.turnOnCameraView.obtainMessage(1, cameraViewListener).sendToTarget();
    }

    /**
     * Stops openCV(call stopVision to use this).
     */
    private void stopOpenCV() {
        FtcRobotControllerActivity.turnOffCameraView.obtainMessage().sendToTarget();
    }

    /**
     * Call this function to start openCV.
     */
    public void startVision() {
        startOpenCV(this);
    }

    /**
     * Call this function to stop openCV.
     */
    public void stopVision() {
        stopOpenCV();
    }

    /**
     * Sets inputs to be used.
     *
     * @param XUp - Button to increment X values.
     * @param XDown - Button to decrement X values.
     * @param YUp - Button to increment Y values.
     * @param YDown - Button to decrement Y values.
     * @param ZUp - Button to increment Z values.
     * @param ZDown - Button to decrement Z values.
     * @param slowMode - Toggle button for slow mode.
     * @param changeLimit - Toggle button for changing between changing upper values and lower values.
     */
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
