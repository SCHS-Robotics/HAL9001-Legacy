/*
 * Filename: CameraCalib.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/24/19
 */

package org.firstinspires.ftc.teamcode.util.calib;

import android.util.Log;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.BaseRobot.SubSystem;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class CameraCalib extends SubSystem implements CameraBridgeViewBase.CvCameraViewListener2 {


    private CustomizableGamepad inputs;
    private static final String CAPTURE = "capture", DELETE_CAPTURE = "delete capture", CALIBRATE = "calibrate";

    private boolean flag;

    private boolean calibrationBegun = false;
    private boolean calibrated = false;

    private List<Mat> refPoints;
    private List<Mat> capturePoints;

    private Mat intrinsic;
    private Mat distCoeffs;

    private List<Mat> rvecs;
    private List<Mat> tvecs;

    private MatOfPoint3f refCoords;

    private int width;
    private int height;

    private Size size;

    private double reprojError;


    public CameraCalib(Robot robot) {
        super(robot);
        inputs = new CustomizableGamepad(robot);
        inputs.addButton(CAPTURE, new Button(1, Button.BooleanInputs.x));
        inputs.addButton(DELETE_CAPTURE, new Button(1, Button.BooleanInputs.b));
        inputs.addButton(CALIBRATE, new Button(1, Button.BooleanInputs.y));

        refPoints = new ArrayList<>();
        capturePoints = new ArrayList<>();

        size = new Size(9, 6);

        refCoords = new MatOfPoint3f();
        double squareSize = 1;
        Point3[] vp = new Point3[(int) (size.width * size.height)];
        int cnt = 0;
        for (int i = 0; i < size.width; ++i) {
            for (int j = 0; j < size.height; ++j, cnt++) {
                vp[cnt] = new Point3(j * squareSize, i * squareSize, 0.0d);
            }
        }
        refCoords.fromArray(vp);

        intrinsic = Mat.eye(3,3,CvType.CV_64F);
        distCoeffs = Mat.zeros(8,1,CvType.CV_64F);

        rvecs = new ArrayList<>();
        tvecs = new ArrayList<>();

        flag = true;
    }

    @Override
    public void init() {

    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {

    }

    @Override
    public void handle() {

        if(!inputs.getBooleanInput(CAPTURE) && !inputs.getBooleanInput(DELETE_CAPTURE) && inputs.getBooleanInput(CALIBRATE) && !calibrationBegun && capturePoints.size() > 10 && flag) {
            calibrationBegun = true;

            Thread calibrate = new Thread() {
                @Override
                public void run(){
                    reprojError = Calib3d.calibrateCamera(refPoints,capturePoints,new Size(width/2,height/2),intrinsic,distCoeffs,rvecs,tvecs);
                    calibrated = true;
                }
            };

            calibrate.start();

            flag = false;
        }
        else if(!inputs.getBooleanInput(CAPTURE) && !inputs.getBooleanInput(DELETE_CAPTURE) && !inputs.getBooleanInput(CALIBRATE) && !flag) {
            flag = true;
        }
    }

    @Override
    public void stop() {
        for (int i = 0; i < capturePoints.size(); i++) {
            capturePoints.get(i).release();
            refPoints.get(i).release();
        }
        refCoords.release();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        inputFrame.rgba().release();
        Mat gray = new Mat();
        Imgproc.resize(inputFrame.gray(),gray,new Size(width/2,height/2));
        inputFrame.gray().release();

        if(!calibrationBegun) {
            MatOfPoint2f corners = new MatOfPoint2f();
            boolean found = Calib3d.findChessboardCorners(gray, size, corners, Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_NORMALIZE_IMAGE | Calib3d.CALIB_CB_FAST_CHECK);
            if (found) {
                TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 40, 0.001);

                Imgproc.cornerSubPix(gray, corners, new Size(5, 5), new Size(-1, -1), term);

                Imgproc.cvtColor(gray, gray, Imgproc.COLOR_GRAY2RGB);

                Calib3d.drawChessboardCorners(gray, size, corners, true);

                if (inputs.getBooleanInput(CAPTURE) && flag) {
                    refPoints.add(refCoords);
                    capturePoints.add(corners);
                    flag = false;
                } else if (inputs.getBooleanInput(DELETE_CAPTURE) && refPoints.size() > 0 && flag) {
                    refPoints.remove(refPoints.size() - 1);
                    capturePoints.remove(capturePoints.size() - 1);
                    flag = false;
                } else if (!inputs.getBooleanInput(CAPTURE) && !inputs.getBooleanInput(DELETE_CAPTURE) && !flag) {
                    flag = true;
                }

                Log.wtf("test",""+refPoints.size());
            }
        }
        else if(calibrated) {
            Log.wtf("done",intrinsic.dump());
            Log.wtf("done",""+reprojError);

            Mat undistorted = new Mat();

            Imgproc.undistort(gray,undistorted,intrinsic,distCoeffs);

            Imgproc.resize(undistorted,undistorted,new Size(width,height));

            return undistorted;

        }

        Imgproc.resize(gray,gray,new Size(width,height));

        return gray;
    }

    /**
     * Starts OpenCV internally (call startVision to start OpenCV from outside this program).
     */
    private void startOpenCV(CameraBridgeViewBase.CvCameraViewListener2 cameraViewListener) {
        FtcRobotControllerActivity.turnOnCameraView.obtainMessage(1, cameraViewListener).sendToTarget();
    }

    /**
     * Stops OpenCV internally (call stopVision to stop OpenCV from outside this program).
     */
    private void stopOpenCV() {
        FtcRobotControllerActivity.turnOffCameraView.obtainMessage().sendToTarget();
    }

    /**
     * Call this function to start OpenCV outside this program.
     */
    public void startVision() {
        startOpenCV(this);
    }

    /**
     * Call this function to stop OpenCV outside this program.
     */
    public void stopVision() {
        stopOpenCV();
    }
}
