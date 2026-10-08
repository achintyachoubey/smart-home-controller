package com.smarthome.model;

import com.smarthome.exception.InvalidInputException;

/**
 * A split air conditioner with a set temperature and three modes.
 *
 * <p>OOP concepts: <b>inheritance</b> ({@code extends Device}),
 * <b>interface implementation</b> ({@code implements Adjustable}, the level is
 * the temperature), <b>constructor chaining</b>, <b>method overriding</b>
 * (its power formula is different from every other device), a classic
 * <b>switch</b> on the mode, and string <b>constants</b> for the modes.</p>
 */
public class AirConditioner extends Device implements Adjustable {

    /** ID prefix for air conditioners. */
    public static final String ID_PREFIX = "AC";
    /** Default power rating in watts. */
    public static final double DEFAULT_RATING = 1500.0;
    /** Lowest set temperature in Celsius. */
    public static final int MIN_TEMPERATURE = 16;
    /** Highest set temperature in Celsius. */
    public static final int MAX_TEMPERATURE = 30;
    /** Temperature of a new AC; also the reference point of the power formula. */
    public static final int DEFAULT_TEMPERATURE = 24;
    /** Cooling mode. */
    public static final String MODE_COOL = "COOL";
    /** Dehumidify mode. */
    public static final String MODE_DRY = "DRY";
    /** Fan-only mode. */
    public static final String MODE_FAN = "FAN";

    private static final String[] MODES = {MODE_COOL, MODE_DRY, MODE_FAN};

    private int temperature;
    private String mode;

    /**
     * Creates an AC with default rating, 24 C and COOL mode.
     *
     * @param name display name
     * @throws InvalidInputException if the name is invalid
     */
    public AirConditioner(String name) throws InvalidInputException {
        this(name, DEFAULT_RATING, DEFAULT_TEMPERATURE, MODE_COOL);
    }

    /**
     * Creates an AC with every setting given.
     *
     * @param name display name
     * @param rating power rating in watts
     * @param temperature 16 to 30 C
     * @param mode COOL, DRY or FAN (any case)
     * @throws InvalidInputException if any value is invalid
     */
    public AirConditioner(String name, double rating, int temperature, String mode) throws InvalidInputException {
        super(ID_PREFIX, name, rating);
        this.temperature = checkTemperature(temperature);
        this.mode = checkMode(mode);
    }

    /**
     * Re-creates an AC loaded from a file.
     *
     * @param id saved ID
     * @param name display name
     * @param isOn saved on/off state
     * @param rating power rating in watts
     * @param temperature 16 to 30 C
     * @param mode COOL, DRY or FAN
     * @throws InvalidInputException if any value is invalid
     */
    public AirConditioner(String id, String name, boolean isOn, double rating, int temperature, String mode)
            throws InvalidInputException {
        super(id, name, isOn, rating);
        this.temperature = checkTemperature(temperature);
        this.mode = checkMode(mode);
    }

    /** @return a copy of the list of allowed modes */
    public static String[] getModeOptions() {
        String[] copy = new String[MODES.length];
        for (int i = 0; i < MODES.length; i++) {
            copy[i] = MODES[i];
        }
        return copy;
    }

    /** @return the set temperature in Celsius */
    public int getTemperature() {
        return temperature;
    }

    /**
     * Changes the set temperature without changing on/off.
     *
     * @param temperature 16 to 30 C
     * @throws InvalidInputException if out of range (the old value is kept)
     */
    public void setTemperature(int temperature) throws InvalidInputException {
        this.temperature = checkTemperature(temperature);
    }

    /** @return the mode: COOL, DRY or FAN */
    public String getMode() {
        return mode;
    }

    /**
     * Changes the mode.
     *
     * @param mode COOL, DRY or FAN in any letter case; stored in upper case
     * @throws InvalidInputException if the mode is not one of the three
     */
    public void setMode(String mode) throws InvalidInputException {
        this.mode = checkMode(mode);
    }

    @Override
    public void setLevel(int level) throws InvalidInputException {
        setTemperature(level);
        turnOn();
    }

    @Override
    public int getLevel() {
        return temperature;
    }

    @Override
    public int getMinLevel() {
        return MIN_TEMPERATURE;
    }

    @Override
    public int getMaxLevel() {
        return MAX_TEMPERATURE;
    }

    @Override
    public String getLevelName() {
        return "Temperature";
    }

    @Override
    public String getDeviceType() {
        return "AC";
    }

    @Override
    public String getStatus() {
        return temperature + " C, " + mode;
    }

    /**
     * Live power depends on the mode. COOL: every degree below 24 C costs 5% more
     * (and every degree above saves 5%), clamped to 50%..150% of the rating.
     * DRY uses 60% and FAN 30% of the rating. Returns 0 when off.
     */
    @Override
    public double getCurrentPowerWatts() {
        if (!isOn()) {
            return 0.0;
        }
        double rating = getPowerRatingWatts();
        double watts;
        switch (mode) {
            case MODE_COOL:
                watts = rating * (1 + 0.05 * (DEFAULT_TEMPERATURE - temperature));
                watts = Math.max(0.5 * rating, Math.min(1.5 * rating, watts));
                break;
            case MODE_DRY:
                watts = 0.6 * rating;
                break;
            default: // MODE_FAN: checkMode guarantees no other value is stored
                watts = 0.3 * rating;
                break;
        }
        return watts;
    }

    @Override
    public String toFileString() {
        return baseFileString() + "|" + temperature + "|" + mode;
    }

    private static int checkTemperature(int value) throws InvalidInputException {
        if (value < MIN_TEMPERATURE || value > MAX_TEMPERATURE) {
            throw new InvalidInputException("Temperature must be between " + MIN_TEMPERATURE
                    + " and " + MAX_TEMPERATURE + " C.");
        }
        return value;
    }

    /** Accepts the mode in any case and returns the upper-case constant. */
    private static String checkMode(String value) throws InvalidInputException {
        if (value != null) {
            String upper = value.trim().toUpperCase();
            for (String allowed : MODES) {
                if (allowed.equals(upper)) {
                    return allowed;
                }
            }
        }
        throw new InvalidInputException("Mode must be COOL, DRY or FAN.");
    }
}
