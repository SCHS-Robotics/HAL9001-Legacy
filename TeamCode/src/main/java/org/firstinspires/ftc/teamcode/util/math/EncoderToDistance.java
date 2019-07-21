/*
 * Filename: EncoderToDistance.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/19/19
 */

package org.firstinspires.ftc.teamcode.util.math;

public class EncoderToDistance {
    double encoderPerMeter;

    public enum Units{
        METERS, CENTIMETERS, MEGAMETERS, MILIMETERS, FOOTS, FEET, INCH, MILES, YARDS
    }
    //use this one to find theoretical encoderPerMeter
    public EncoderToDistance(double diamater, Units unit){
        encoderPerMeter = 1440 / ((Math.PI * (diamater/2) * (diamater/2)) * getConversion(unit));
    }
    //use this one to set encoderPerMeter with non default encoder per rotation of 1440
    public EncoderToDistance(double diamater, int encoderPerRotation, Units unit){
        encoderPerMeter = encoderPerRotation / ((Math.PI * (diamater/2) * (diamater/2)) * getConversion(unit));
    }
    //this input should be the output of DistanceCalib
    public EncoderToDistance(double encoderPerMeter){
        this.encoderPerMeter = encoderPerMeter;
    }

    public int getEncoderAmount(double distance, Units unit){
        return (int) Math.round(getConversion(unit) * distance * encoderPerMeter);
    }
    public double getDistanceFromEncoders(int encoderAmount, Units unit){
        return (encoderAmount/encoderPerMeter)* getConversion(unit);
    }
    private double getConversion(Units unit){
        switch(unit){
            case MILIMETERS:
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
