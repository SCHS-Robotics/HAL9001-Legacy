package org.firstinspires.ftc.teamcode.util.math;

/**
 * Supported input units(Megameters is the same as millimeters because memes).
 */
public enum Units {
    MEGAMETERS(0.001,"MM"), MILLIMETERS(0.001,"mm"), CENTIMETERS(0.01,"cm"),  METERS(1.0,"m"), INCH(0.0254,"in"), FOOTS(0.3048,"ft"), FEET(0.3048,"ft"), YARDS(0.9144,"yd"), MILES(1609.34,"mi"), TILES(0.6096,"Ti");
    public double conversionFactor;
    public String abreviation;
    Units(double meterConversion, String abreviation)
    {
        conversionFactor = meterConversion;
        this.abreviation = abreviation;
    }
    public static double convert(double input, Units fromUnit, Units toUnit) {
        double meters = input * fromUnit.conversionFactor;
        return  meters/toUnit.conversionFactor;
    }
}
