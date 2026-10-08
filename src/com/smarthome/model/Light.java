package com.smarthome.model;

import com.smarthome.exception.InvalidInputException;

/**
 * A dimmable, colour-changing light.
 *
 * <p>OOP concepts: <b>inheritance</b> ({@code extends Device}),
 * <b>interface implementation</b> ({@code implements Adjustable}, the level is
 * the brightness), <b>constructor chaining</b> ({@code this(...)} from the
 * short constructor to the full one, {@code super(...)} to {@link Device}),
 * <b>method overriding</b> of the abstract methods, and
 * <b>encapsulation</b> (validated setters).</p>
 */
public class Light extends Device implements Adjustable {

    /** ID prefix for lights. */
    public static final String ID_PREFIX = "L";
    /** Default power rating in watts. */
    public static final double DEFAULT_RATING = 10.0;
    /** Lowest brightness in percent. */
    public static final int MIN_BRIGHTNESS = 0;
    /** Highest brightness in percent. */
    public static final int MAX_BRIGHTNESS = 100;
    /** Brightness of a new light. */
    public static final int DEFAULT_BRIGHTNESS = 100;
    /** Colour of a new light. */
    public static final String DEFAULT_COLOUR = "Warm White";

    private static final String[] COLOURS = {"Warm White", "Cool White", "Daylight", "Red", "Blue", "Green"};

    private int brightness;
    private String colour;

    /**
     * Creates a light with default rating, brightness and colour.
     *
     * @param name display name
     * @throws InvalidInputException if the name is invalid
     */
    public Light(String name) throws InvalidInputException {
        this(name, DEFAULT_RATING, DEFAULT_BRIGHTNESS, DEFAULT_COLOUR);
    }

    /**
     * Creates a light with every setting given.
     *
     * @param name display name
     * @param rating power rating in watts
     * @param brightness 0 to 100 percent
     * @param colour one of the allowed colours (any case)
     * @throws InvalidInputException if any value is invalid
     */
    public Light(String name, double rating, int brightness, String colour) throws InvalidInputException {
        super(ID_PREFIX, name, rating);
        this.brightness = checkBrightness(brightness);
        this.colour = checkColour(colour);
    }

    /**
     * Re-creates a light loaded from a file.
     *
     * @param id saved ID
     * @param name display name
     * @param isOn saved on/off state
     * @param rating power rating in watts
     * @param brightness 0 to 100 percent
     * @param colour one of the allowed colours
     * @throws InvalidInputException if any value is invalid
     */
    public Light(String id, String name, boolean isOn, double rating, int brightness, String colour)
            throws InvalidInputException {
        super(id, name, isOn, rating);
        this.brightness = checkBrightness(brightness);
        this.colour = checkColour(colour);
    }

    /** @return a copy of the list of allowed colours (a copy, so callers cannot change it) */
    public static String[] getColourOptions() {
        String[] copy = new String[COLOURS.length];
        for (int i = 0; i < COLOURS.length; i++) {
            copy[i] = COLOURS[i];
        }
        return copy;
    }

    /** @return brightness in percent */
    public int getBrightness() {
        return brightness;
    }

    /**
     * Changes the brightness without changing on/off.
     *
     * @param brightness 0 to 100 percent
     * @throws InvalidInputException if out of range (the old value is kept)
     */
    public void setBrightness(int brightness) throws InvalidInputException {
        this.brightness = checkBrightness(brightness);
    }

    /** @return the current colour */
    public String getColour() {
        return colour;
    }

    /**
     * Changes the colour.
     *
     * @param colour one of the allowed colours, in any letter case
     * @throws InvalidInputException if the colour is not allowed
     */
    public void setColour(String colour) throws InvalidInputException {
        this.colour = checkColour(colour);
    }

    @Override
    public void setLevel(int level) throws InvalidInputException {
        setBrightness(level);
        turnOn();
    }

    @Override
    public int getLevel() {
        return brightness;
    }

    @Override
    public int getMinLevel() {
        return MIN_BRIGHTNESS;
    }

    @Override
    public int getMaxLevel() {
        return MAX_BRIGHTNESS;
    }

    @Override
    public String getLevelName() {
        return "Brightness";
    }

    @Override
    public String getDeviceType() {
        return "LIGHT";
    }

    @Override
    public String getStatus() {
        return "Brightness " + brightness + "%, " + colour;
    }

    /** Live power = rating x brightness / 100, or 0 when off. */
    @Override
    public double getCurrentPowerWatts() {
        if (!isOn()) {
            return 0.0;
        }
        return getPowerRatingWatts() * brightness / 100.0;
    }

    @Override
    public String toFileString() {
        return baseFileString() + "|" + brightness + "|" + colour;
    }

    private static int checkBrightness(int value) throws InvalidInputException {
        if (value < MIN_BRIGHTNESS || value > MAX_BRIGHTNESS) {
            throw new InvalidInputException("Brightness must be between " + MIN_BRIGHTNESS
                    + " and " + MAX_BRIGHTNESS + ".");
        }
        return value;
    }

    /** Returns the colour spelt exactly as in the list, whatever case was typed. */
    private static String checkColour(String value) throws InvalidInputException {
        if (value != null) {
            for (String allowed : COLOURS) {
                if (allowed.equalsIgnoreCase(value.trim())) {
                    return allowed;
                }
            }
        }
        throw new InvalidInputException("Colour must be one of: " + String.join(", ", COLOURS) + ".");
    }
}
