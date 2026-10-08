package com.smarthome.model;

import com.smarthome.exception.InvalidInputException;

/**
 * A ceiling or table fan with five speeds.
 *
 * <p>OOP concepts: <b>inheritance</b> ({@code extends Device}),
 * <b>interface implementation</b> ({@code implements Adjustable}, the level is
 * the speed), <b>constructor chaining</b> with {@code this(...)} and
 * {@code super(...)}, and <b>method overriding</b>.</p>
 */
public class Fan extends Device implements Adjustable {

    /** ID prefix for fans. */
    public static final String ID_PREFIX = "F";
    /** Default power rating in watts. */
    public static final double DEFAULT_RATING = 75.0;
    /** Slowest speed. */
    public static final int MIN_SPEED = 1;
    /** Fastest speed. */
    public static final int MAX_SPEED = 5;
    /** Speed of a new fan. */
    public static final int DEFAULT_SPEED = 3;

    private int speed;

    /**
     * Creates a fan with default rating and speed.
     *
     * @param name display name
     * @throws InvalidInputException if the name is invalid
     */
    public Fan(String name) throws InvalidInputException {
        this(name, DEFAULT_RATING, DEFAULT_SPEED);
    }

    /**
     * Creates a fan with every setting given.
     *
     * @param name display name
     * @param rating power rating in watts
     * @param speed 1 to 5
     * @throws InvalidInputException if any value is invalid
     */
    public Fan(String name, double rating, int speed) throws InvalidInputException {
        super(ID_PREFIX, name, rating);
        this.speed = checkSpeed(speed);
    }

    /**
     * Re-creates a fan loaded from a file.
     *
     * @param id saved ID
     * @param name display name
     * @param isOn saved on/off state
     * @param rating power rating in watts
     * @param speed 1 to 5
     * @throws InvalidInputException if any value is invalid
     */
    public Fan(String id, String name, boolean isOn, double rating, int speed) throws InvalidInputException {
        super(id, name, isOn, rating);
        this.speed = checkSpeed(speed);
    }

    /** @return the current speed (1 to 5) */
    public int getSpeed() {
        return speed;
    }

    /**
     * Changes the speed without changing on/off.
     *
     * @param speed 1 to 5
     * @throws InvalidInputException if out of range (the old value is kept)
     */
    public void setSpeed(int speed) throws InvalidInputException {
        this.speed = checkSpeed(speed);
    }

    @Override
    public void setLevel(int level) throws InvalidInputException {
        setSpeed(level);
        turnOn();
    }

    @Override
    public int getLevel() {
        return speed;
    }

    @Override
    public int getMinLevel() {
        return MIN_SPEED;
    }

    @Override
    public int getMaxLevel() {
        return MAX_SPEED;
    }

    @Override
    public String getLevelName() {
        return "Speed";
    }

    @Override
    public String getDeviceType() {
        return "FAN";
    }

    @Override
    public String getStatus() {
        return "Speed " + speed + "/" + MAX_SPEED;
    }

    /** Live power = rating x speed / 5, or 0 when off. */
    @Override
    public double getCurrentPowerWatts() {
        if (!isOn()) {
            return 0.0;
        }
        return getPowerRatingWatts() * speed / MAX_SPEED;
    }

    @Override
    public String toFileString() {
        return baseFileString() + "|" + speed;
    }

    private static int checkSpeed(int value) throws InvalidInputException {
        if (value < MIN_SPEED || value > MAX_SPEED) {
            throw new InvalidInputException("Speed must be between " + MIN_SPEED + " and " + MAX_SPEED + ".");
        }
        return value;
    }
}
