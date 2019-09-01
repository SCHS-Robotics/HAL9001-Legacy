/*
 * Filename: EncoderDistanceCalibMenu.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 8/31/19
 */

package org.firstinspires.ftc.teamcode.system.menus;

import android.widget.Switch;

import org.firstinspires.ftc.teamcode.system.source.BaseRobot.Robot;
import org.firstinspires.ftc.teamcode.system.source.GUI.GuiLine;
import org.firstinspires.ftc.teamcode.system.source.GUI.Menu;
import org.firstinspires.ftc.teamcode.system.subsystems.cursors.DefaultCursor;
import org.firstinspires.ftc.teamcode.util.calib.EncoderDistanceCalib;
import org.firstinspires.ftc.teamcode.util.exceptions.NotBooleanInputException;
import org.firstinspires.ftc.teamcode.util.math.Units;
import org.firstinspires.ftc.teamcode.util.misc.Button;
import org.firstinspires.ftc.teamcode.util.misc.CustomizableGamepad;

public class EncoderDistanceCalibMenu extends Menu {

    SpeedMode speedMode = SpeedMode.FAST;
    
    CustomizableGamepad inputs;
    
    private static final String SPEED_MODE_TOGGLE = "SpeedModeToggle";

    private EncoderDistanceCalib calib;
    
    private double distance = 0;

    Units unit;

    private enum SpeedMode{
        FAST(25), MEDIUM(10), SLOW(1), PRECISION(.1);
        
        public double increment;
        SpeedMode(double increment) {
            this.increment = increment;
        }
    }

    public EncoderDistanceCalibMenu(Robot robot, Units unit, Button speedToggleButton, EncoderDistanceCalib calib){
        super(robot.gui, new DefaultCursor(robot, new DefaultCursor.Params()), new GuiLine[]{new GuiLine("<#>", ""), new GuiLine("###", "Done  " + "Increment: " + "Fast")},3,2);

        this.unit = unit;

        this.calib = calib;

        inputs = new CustomizableGamepad(robot);
        if(!speedToggleButton.isBoolean){
            throw new NotBooleanInputException("SpeedToggleButton must be a boolean button");
        }
        inputs.addButton(SPEED_MODE_TOGGLE, speedToggleButton);
        
        GuiLine[] newerLines = {
                new GuiLine("<#>", "I traveled: " + "0" + unit.abreviation),
                lines.get(1)
        };
        setLines(newerLines);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void open() {

    }

    @Override
    public void onSelect() {
        if(cursor.y == 1){
            calib.numberSelected(Units.convert(distance, unit, Units.METERS));
        }
    }

    @Override
    public void onButton(String name, Button button) {
        if(name.equals(DefaultCursor.LEFT) || name.equals(DefaultCursor.RIGHT)){
            if(cursor.y == 0 && cursor.x == 0){
                distance -= speedMode.increment;
                cursor.setX(1);
                updateLinesForIncrament();
            }
            else if(cursor.y == 0 && cursor.x == 2){
                distance += speedMode.increment;
                cursor.setX(1);
                updateLinesForIncrament();
            }
        }
        if(name.equals(DefaultCursor.UP)){
            if(cursor.x == 0 || cursor.x == 2){
                cursor.setX(1);
            }
        }

    }

    @Override
    protected void render() {
        if(inputs.getBooleanInput(SPEED_MODE_TOGGLE)){
            switch (speedMode){
                case FAST:
                    speedMode = SpeedMode.MEDIUM;
                    setLines(new GuiLine[]{lines.get(0), new GuiLine(lines.get(1).selectionZoneText, lines.get(1).postSelectionText.substring(0, 6) + "Medium")});
                    break;
                case MEDIUM:
                    speedMode = SpeedMode.SLOW;
                    setLines(new GuiLine[]{lines.get(0), new GuiLine(lines.get(1).selectionZoneText, lines.get(1).postSelectionText.substring(0, 6) + "Slow")});
                    break;
                case SLOW:
                    speedMode = SpeedMode.PRECISION;
                    setLines(new GuiLine[]{lines.get(0), new GuiLine(lines.get(1).selectionZoneText, lines.get(1).postSelectionText.substring(0, 6) + "Precision")});
                    break;
                case PRECISION:
                    speedMode = SpeedMode.FAST;
                    setLines(new GuiLine[]{lines.get(0), new GuiLine(lines.get(1).selectionZoneText, lines.get(1).postSelectionText.substring(0, 6) + "Fast")});
                    break;
            }
        }
        displayLines(lines);
    }

    @Override
    protected void initLoopRender() {

    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void stop() {

    }

    private void updateLinesForIncrament(){
        setLines(new GuiLine[]{
                new GuiLine(lines.get(0).selectionZoneText, "I traveled: " + distance + unit.abreviation),
                lines.get(1)
        });
    }
}
