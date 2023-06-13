package com.phazejeff.mcgpt.game;

/**
 * Represents an xyz position in Minecraft.
 * Cannot be edited once instantiated.
 * 
 * @author phazejeff
 * @version 1.0
 */
public class Pos {
    private int x;
    private int y;
    private int z;

    /**
     * Constructs a new `Pos` object with the specified coordinates.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     */
    public Pos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Retrieves the X coordinate.
     *
     * @return The X coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the Y coordinate.
     *
     * @return The Y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Retrieves the Z coordinate.
     *
     * @return The Z coordinate.
     */
    public int getZ() {
        return z;
    }
}
