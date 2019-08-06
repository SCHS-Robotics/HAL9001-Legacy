package org.firstinspires.ftc.teamcode.util.calib;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode.system.menus.DisplayMenu;
import org.firstinspires.ftc.teamcode.system.source.Robot;
import org.firstinspires.ftc.teamcode.system.source.SubSystem;
import org.firstinspires.ftc.teamcode.util.control.PIDController;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;
import org.firstinspires.ftc.teamcode.util.misc.Grapher;
import org.firstinspires.ftc.teamcode.util.misc.Toggle;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

public class AnglePIDTuner extends SubSystem implements CameraBridgeViewBase.CvCameraViewListener2 {

    private PIDController pidTuner;
    private CustomizableGamepad inputs;

    private Toggle slowModeToggle;

    private double increment;
    private long lastActivatedTimestamp;
    private int delayMs;

    private Grapher grapher;

    private double kp,ki,kd;

    private double setPoint;

    private BNO055IMU imu;

    private String displayName;
    private DisplayMenu display;

    private final String SLOWMODE = "slowMode", P_INCREMENT = "PUp", P_DECREMENT = "PDown", I_INCREMENT = "IUp", I_DECREMENT = "IDown", D_INCREMENT = "DUp", D_DECREMENT = "DDown";

    public enum DriveType {
        TWOWHEEL, FOURWHEEL
    }

    public AnglePIDTuner(Robot robot, String menuName, double setPoint) {
        super(robot);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE,new Button(1, Button.BooleanInputs.x));

        initVars(setPoint);

        pidTuner = new PIDController(kp,ki,kd);
        pidTuner.setSetpoint(setPoint);

        display = (DisplayMenu) robot.gui.getMenu(menuName);
        displayName = menuName;
    }

    public AnglePIDTuner(Robot robot, String menuName, double setPoint, PIDController.Type type) {
        super(robot);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE,new Button(1, Button.BooleanInputs.x));

        initVars(setPoint);

        pidTuner = new PIDController(kp,ki,kd,type);
        pidTuner.setSetpoint(setPoint);

        display = (DisplayMenu) robot.gui.getMenu(menuName);
        displayName = menuName;
    }

    public AnglePIDTuner(Robot robot, String menuName, double setPoint, PIDController.Type type, double iClampLower, double iClampUpper) {
        super(robot);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE,new Button(1, Button.BooleanInputs.x));

        initVars(setPoint);

        pidTuner = new PIDController(kp,ki,kd,type);
        pidTuner.setSetpoint(setPoint);
        pidTuner.setIClamp(iClampLower,iClampUpper);

        display = (DisplayMenu) robot.gui.getMenu(menuName);
        displayName = menuName;
    }

    public AnglePIDTuner(Robot robot, String menuName, double setPoint, PIDController.Type type, double iClampLower, double iClampUpper, double outputClampLower, double outputClampUpper) {
        super(robot);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE,new Button(1, Button.BooleanInputs.x));

        initVars(setPoint);

        pidTuner = new PIDController(kp,ki,kd,type);
        pidTuner.setSetpoint(setPoint);
        pidTuner.setIClamp(iClampLower,iClampUpper);
        pidTuner.setOutputClamp(outputClampLower,outputClampUpper);

        display = (DisplayMenu) robot.gui.getMenu(menuName);
        displayName = menuName;
    }

    public AnglePIDTuner(Robot robot, String menuName, double setPoint, PIDController.Type type, double iClampLower, double iClampUpper, double outputClampLower, double outputClampUpper, double PonMClampLower, double PonMClampUpper) {
        super(robot);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE,new Button(1, Button.BooleanInputs.x));

        initVars(setPoint);

        pidTuner = new PIDController(kp,ki,kd,type);
        pidTuner.setSetpoint(setPoint);
        pidTuner.setIClamp(iClampLower,iClampUpper);
        pidTuner.setOutputClamp(outputClampLower,outputClampUpper);
        pidTuner.setPonMClamp(PonMClampLower,PonMClampUpper);

        display = (DisplayMenu) robot.gui.getMenu(menuName);
        displayName = menuName;
    }

    @Override
    public void init() throws InterruptedException {

        imu = robot.hardwareMap.get(BNO055IMU.class,"imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu.initialize(parameters);

        startOpenCV(this);
    }

    @Override
    public void handle() {
        slowModeToggle.updateToggle(inputs.getBooleanInput(SLOWMODE));

        if (slowModeToggle.getCurrentState()) {
            increment = 0.01;
        } else {
            increment = 0.1;
        }

        if(System.currentTimeMillis() - lastActivatedTimestamp >= delayMs) {

            if (inputs.getBooleanInput(P_INCREMENT)) {
                kp += increment;
            } else if (inputs.getBooleanInput(P_DECREMENT)) {
                kp -= increment;
            }
            if (inputs.getBooleanInput(I_INCREMENT)) {
                ki += increment;
            } else if (inputs.getBooleanInput(I_DECREMENT)) {
                ki -= increment;
            }
            if (inputs.getBooleanInput(D_INCREMENT)) {
                kd += increment;
            } else if (inputs.getBooleanInput(D_DECREMENT)) {
                kd -= increment;
            }
            lastActivatedTimestamp = System.currentTimeMillis();
            pidTuner.setTunings(kp,ki,kd);

            display.clear();
            display.addLine("kp",kp);
            display.addLine("ki",ki);
            display.addLine("kd",kd);
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
        inputFrame.rgba().release();

        return grapher.getNextFrame(setPoint-imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle);
    }

    private void initVars(double setPoint) {
        slowModeToggle = new Toggle(false);

        increment = 0.1;
        lastActivatedTimestamp = 0;
        delayMs = 200;

        kp = 0;
        ki = 0;
        kd = 0;

        grapher = new Grapher(10,2*Math.PI);

        this.setPoint = setPoint;
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
}