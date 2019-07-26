/*
 * Filename: EncoderToDistance.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/19/19
 */

package org.firstinspires.ftc.teamcode.util.math;

/**
 * A calculator that calculates encoder ticks per meter.
 */
public class EncoderToDistance {
    //Conversion of 1 encoder per meter.
    double encoderPerMeter;

    //Supported input units(Megameters is the same as millimeters because memes).
    public enum Units{
        METERS, CENTIMETERS, MEGAMETERS, MILLIMETERS, FOOTS, FEET, INCH, MILES, YARDS
    }

    /**
     * Constructor that sets theoretical encoderPerMeter using default encoderPerRotation(less accurate).
     *
     * @param diameter - Diameter of wheel.
     * @param unit - Units of the diameter.
     */
    public EncoderToDistance(double diameter, Units unit){
        encoderPerMeter = 1440 / ((Math.PI * (diameter/2) * (diameter/2)) * getConversion(unit));
    }

    /**
     * Constructor that sets theoretical encoderPerMeter using given encoderPerRotation(less accurate).
     *
     * @param diameter - Diameter of wheel.
     * @param encoderPerRotation - number of encoders per one rotation of the wheel.
     * @param unit - Units of the diameter.
     * */
    public EncoderToDistance(double diameter, int encoderPerRotation, Units unit){
        encoderPerMeter = encoderPerRotation / ((Math.PI * (diameter/2) * (diameter/2)) * getConversion(unit));
    }

    /**
     * Constructor that uses encoderPerMeter given by calibration program.
     *
     * @param encoderPerMeter - Experimentally gotten value.
     * */
    public EncoderToDistance(double encoderPerMeter){
        this.encoderPerMeter = encoderPerMeter;
    }

    /**
     * Returns encoder amount to go given distance.
     *
     * @param distance - Distance wished to travel.
     * @param unit - Units of distance.
     * */
    public int getEncoderAmount(double distance, Units unit){
        return (int) Math.round(getConversion(unit) * distance * encoderPerMeter);
    }

    /**
     * returns distance from encoder amount.
     *
     * @param encoderAmount - Amount of encoder.
     * @param unit - Units of returned distance.
     * */
    public double getDistanceFromEncoders(int encoderAmount, Units unit){
        return (encoderAmount/encoderPerMeter)* getConversion(unit);
    }

    /**
     * Returns conversion multiplyer for given unit.
     *
     * @param unit - Unit to get conversion for.
     * */
    private double getConversion(Units unit){
        switch(unit){
            case MILLIMETERS:
            case MEGAMETERS:
                return .001;
            case CENTIMETERS:
                return .01;
            case METERS:
                return 1;
            case INCH:
                return 0.0254;
            case FEET:
            case FOOTS:
                return 0.3048;
            case YARDS:
                return 0.9144;
            case MILES:
                return 1609.34;
        }
        return 1;
    }
}
