package com.rogerio.scripts_conversao;

/**
 * A container for values in ENU (East, North, Up) coordination system.
 */
public class EnuValues {

    /**
     * East Coordinates in local ENU
     */
    public final double enuEast;

    /**
     * North Coordinates in local ENU
     */
    public final double enuNorth;

    /**
     * Up Coordinates in local ENU
     */
    public final double enuUP;

    /**
     * Constructor
     */
    public EnuValues(double enuEast, double enuNorth, double enuUP){
        this.enuEast = enuEast;
        this.enuNorth = enuNorth;
        this.enuUP = enuUP;
    }

    @Override
    public String toString() {
        return  this.enuEast  + "; " +
                this.enuNorth + "; " +
                this.enuUP;
    }
}
