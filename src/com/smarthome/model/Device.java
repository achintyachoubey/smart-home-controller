package com.smarthome.model;

import com.smarthome.exception.InvalidInputException;
import com.smarthome.util.IdGenerator;
import com.smarthome.util.InputHelper;

/**
 * Base class for every smart device in the home.
 *
 * <p>OOP concepts:</p>
 * <ul>
 *   <li><b>Abstraction</b>: the class is {@code abstract}; it describes what
 *       every device can do, but "a device" on its own cannot be created.
 *       The four abstract methods must be written by each subclass.</li>
 *   <li><b>Encapsulation</b>: all fields are {@code private}; they are read
 *       through getters and changed only through validating setters.</li>
 *   <li><b>Inheritance</b>: Light, Fan, AirConditioner, SmartTV and DoorLock
 *       extend this class and reuse its ID, name, power and on/off logic.</li>
 *   <li><b>Constructors</b>: two overloaded {@code protected} constructors,
 *       one for brand-new devices and one for devices loaded from a file.</li>
 *   <li><b>Method overriding / runtime polymorphism</b>: {@link #toString()}
 *       calls the abstract methods, so the subclass version runs at runtime.</li>
 * </ul>
 */
public abstract class Device {

    /** Largest power rating accepted for any device, in watts. */
    public static final double MAX_RATING_WATTS = 5000.0;

    /** Column layout shared by {@link #toString()} and {@link #TABLE_HEADER}. */
    private static final String ROW_FORMAT = "%-6s %-5s %-18s %-5s %-28s %8.1f W";

    /** Column headings that line up with the rows produced by {@link #toString()}. */
    public static final String TABLE_HEADER = String.format("%-6s %-5s %-18s %-5s %-28s %10s",
            "ID", "TYPE", "NAME", "STATE", "SETTINGS", "POWER");

    private final String id;
    private String name;
    private boolean isOn;
    private double powerRatingWatts;

    /**
     * Creates a brand-new device (switched off) with a freshly generated ID.
     *
     * @param idPrefix ID prefix of the device type, e.g. "L"
     * @param name display name
     * @param powerRatingWatts rated power, more than 0 and at most 5000 W
     * @throws InvalidInputException if the name or rating is invalid
     */
    protected Device(String idPrefix, String name, double powerRatingWatts) throws InvalidInputException {
        this.name = InputHelper.validateName(name);
        this.powerRatingWatts = checkRating(powerRatingWatts);
        // the ID is generated only after validation, so a failed creation does not use up a number
        this.id = IdGenerator.nextId(idPrefix);
        this.isOn = false;
    }

    /**
     * Re-creates a device that was saved to a file, keeping its original ID.
     *
     * @param existingId the saved ID, e.g. "L007"
     * @param name display name
     * @param isOn saved on/off state
     * @param powerRatingWatts rated power, more than 0 and at most 5000 W
     * @throws InvalidInputException if the ID, name or rating is invalid
     */
    protected Device(String existingId, String name, boolean isOn, double powerRatingWatts)
            throws InvalidInputException {
        this.name = InputHelper.validateName(name);
        this.powerRatingWatts = checkRating(powerRatingWatts);
        try {
            IdGenerator.registerExisting(existingId);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid device ID '" + existingId + "': " + e.getMessage());
        }
        this.id = existingId;
        this.isOn = isOn;
    }

    /** @return the unique device ID, e.g. "L001" */
    public String getId() {
        return id;
    }

    /** @return the display name */
    public String getName() {
        return name;
    }

    /**
     * Renames the device.
     *
     * @param name new name (not empty, at most 30 characters, no '|')
     * @throws InvalidInputException if the name is invalid
     */
    public void setName(String name) throws InvalidInputException {
        this.name = InputHelper.validateName(name);
    }

    /** @return true if the device is switched on */
    public boolean isOn() {
        return isOn;
    }

    /** @return the rated (maximum) power in watts */
    public double getPowerRatingWatts() {
        return powerRatingWatts;
    }

    /**
     * Changes the rated power.
     *
     * @param powerRatingWatts more than 0 and at most 5000 W
     * @throws InvalidInputException if the rating is out of range
     */
    public void setPowerRatingWatts(double powerRatingWatts) throws InvalidInputException {
        this.powerRatingWatts = checkRating(powerRatingWatts);
    }

    /** Switches the device on. */
    public void turnOn() {
        isOn = true;
    }

    /** Switches the device off. */
    public void turnOff() {
        isOn = false;
    }

    /** Flips the device between on and off. */
    public void toggle() {
        isOn = !isOn;
    }

    /** @return the device type code used in files and on the dashboard: LIGHT, FAN, AC, TV or LOCK */
    public abstract String getDeviceType();

    /** @return the type-specific settings shown on the dashboard, e.g. "Brightness 75%" */
    public abstract String getStatus();

    /** @return the power the device is drawing right now in watts (0.0 when off) */
    public abstract double getCurrentPowerWatts();

    /** @return one pipe-separated line describing the device for home_state.txt */
    public abstract String toFileString();

    /**
     * Builds the part of the file line that every device shares:
     * {@code DEVICE|type|id|name|isOn|rating}. Subclasses append their own fields.
     *
     * @return the common prefix of the file line
     */
    protected String baseFileString() {
        return "DEVICE|" + getDeviceType() + "|" + id + "|" + name + "|" + isOn + "|" + powerRatingWatts;
    }

    /** @return one formatted dashboard row: ID, type, name, ON/OFF, settings and live power */
    @Override
    public String toString() {
        return String.format(ROW_FORMAT, id, getDeviceType(), name, isOn ? "ON" : "OFF",
                getStatus(), getCurrentPowerWatts());
    }

    private static double checkRating(double watts) throws InvalidInputException {
        if (!(watts > 0 && watts <= MAX_RATING_WATTS)) {
            throw new InvalidInputException("Power rating must be more than 0 and at most "
                    + MAX_RATING_WATTS + " W.");
        }
        return watts;
    }
}
