/*
 * Filename: Vector.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/11/17
 */

package org.firstinspires.ftc.teamcode.util.math;

public class Vector {
    public enum CoordinateType {
        CARTESIAN, POLAR
    }

    public double x,y,r,theta;

    public Vector(double inx, double iny, CoordinateType inCoord) {
        if (inCoord == CoordinateType.CARTESIAN) {
            this.x = inx;
            this.y = iny;
            this.r = Math.sqrt(Math.pow(inx,2)+Math.pow(iny,2));

            this.theta = inx > 0 ? Math.atan(iny/inx) : inx < 0 ? Math.atan(iny/inx) + Math.PI : inx == 0 && iny > 0 ? Math.PI/2 : inx == 0 && iny < 0 ? -Math.PI/2 : 0;
            this.theta = this.theta > 0 ? this.theta : this.theta + 2 * Math.PI; //To make everything positive, because I don't like negative angles as much
            this.r = Math.sqrt(Math.pow(inx,2)+Math.pow(iny,2));
        }
        else if(inCoord == CoordinateType.POLAR) {
            this.r = inx;
            this.theta = iny;
            this.x = inx*Math.cos(iny);
            this.y = inx*Math.sin(iny);
            this.theta = this.theta > 0 ? this.theta : this.theta + 2 * Math.PI; //To make everything positive, because I don't like negative angles as much
        }
    }

    public Vector(double inx, double iny) {
        this.x = inx;
        this.y = iny;

        this.theta = inx > 0 ? Math.atan(iny/inx) : inx < 0 ? Math.atan(iny/inx) + Math.PI : inx == 0 && iny > 0 ? Math.PI/2 : inx == 0 && iny < 0 ? -Math.PI/2 : 0;
        this.theta = this.theta > 0 ? this.theta : this.theta + 2*Math.PI; //To make everything positive, because I don't like negative angles as much
        this.r = Math.sqrt(Math.pow(inx,2)+Math.pow(iny,2));
    }

    //Counterclockwise is positive, clockwise is negative
    //must be in radians
    public void rotate(double theta) {
        double rotx = this.x*Math.cos(theta)-this.y*Math.sin(theta);
        double roty = this.x*Math.sin(theta)+this.y*Math.cos(theta);
        this.x = rotx;
        this.y = roty;

        this.theta += theta;
        this.theta = this.theta > 0 ? this.theta : this.theta + 2 * Math.PI; //To make everything positive, because I don't like negative angles as much
    }
    public boolean isZeroVector() {
        return (this.x == 0.0) && (this.y == 0.0);
    }

    public void normalize(double length) {
        this.x = length*Math.cos(this.theta);
        this.y = length*Math.sin(this.theta);
        this.r = length;
    }
    public void normalize() {
        normalize(1.0);
    }
}
