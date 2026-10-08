package com.smarthome.model;

import com.smarthome.exception.InvalidInputException;

/**
 * A smart television with volume and channel.
 *
 * <p>OOP concepts: <b>inheritance</b> ({@code extends Device}),
 * <b>interface implementation</b> ({@code implements Adjustable}, the level is
 * the volume), <b>constructor chaining</b> and <b>method overriding</b>.</p>
 */
public class SmartTV extends Device implements Adjustable {

    /** ID prefix for TVs. */
    public static final String ID_PREFIX = "TV";
    /** Default power rating in watts. */
    public static final double DEFAULT_RATING = 100.0;
    /** Lowest volume. */
    public static final int MIN_VOLUME = 0;
    /** Highest volume. */
    public static final int MAX_VOLUME = 100;
    /** Volume of a new TV. */
    public static final int DEFAULT_VOLUME = 30;
    /** Lowest channel number. */
    public static final int MIN_CHANNEL = 1;
    /** Highest channel number. */
    public static final int MAX_CHANNEL = 999;
    /** Channel of a new TV. */
    public static final int DEFAULT_CHANNEL = 1;

    private int volume;
    private int channel;

    /**
     * Creates a TV with default rating, volume and channel.
     *
     * @param name display name
     * @throws InvalidInputException if the name is invalid
     */
    public SmartTV(String name) throws InvalidInputException {
        this(name, DEFAULT_RATING, DEFAULT_VOLUME, DEFAULT_CHANNEL);
    }

    /**
     * Creates a TV with every setting given.
     *
     * @param name display name
     * @param rating power rating in watts
     * @param volume 0 to 100
     * @param channel 1 to 999
     * @throws InvalidInputException if any value is invalid
     */
    public SmartTV(String name, double rating, int volume, int channel) throws InvalidInputException {
        super(ID_PREFIX, name, rating);
        this.volume = checkVolume(volume);
        this.channel = checkChannel(channel);
    }

    /**
     * Re-creates a TV loaded from a file.
     *
     * @param id saved ID
     * @param name display name
     * @param isOn saved on/off state
     * @param rating power rating in watts
     * @param volume 0 to 100
     * @param channel 1 to 999
     * @throws InvalidInputException if any value is invalid
     */
    public SmartTV(String id, String name, boolean isOn, double rating, int volume, int channel)
            throws InvalidInputException {
        super(id, name, isOn, rating);
        this.volume = checkVolume(volume);
        this.channel = checkChannel(channel);
    }

    /** @return the volume (0 to 100) */
    public int getVolume() {
        return volume;
    }

    /**
     * Changes the volume without changing on/off.
     *
     * @param volume 0 to 100
     * @throws InvalidInputException if out of range (the old value is kept)
     */
    public void setVolume(int volume) throws InvalidInputException {
        this.volume = checkVolume(volume);
    }

    /** @return the channel (1 to 999) */
    public int getChannel() {
        return channel;
    }

    /**
     * Changes the channel.
     *
     * @param channel 1 to 999
     * @throws InvalidInputException if out of range (the old value is kept)
     */
    public void setChannel(int channel) throws InvalidInputException {
        this.channel = checkChannel(channel);
    }

    @Override
    public void setLevel(int level) throws InvalidInputException {
        setVolume(level);
        turnOn();
    }

    @Override
    public int getLevel() {
        return volume;
    }

    @Override
    public int getMinLevel() {
        return MIN_VOLUME;
    }

    @Override
    public int getMaxLevel() {
        return MAX_VOLUME;
    }

    @Override
    public String getLevelName() {
        return "Volume";
    }

    @Override
    public String getDeviceType() {
        return "TV";
    }

    @Override
    public String getStatus() {
        return "Vol " + volume + ", Ch " + channel;
    }

    /** Live power = full rating while on, 0 when off. */
    @Override
    public double getCurrentPowerWatts() {
        return isOn() ? getPowerRatingWatts() : 0.0;
    }

    @Override
    public String toFileString() {
        return baseFileString() + "|" + volume + "|" + channel;
    }

    private static int checkVolume(int value) throws InvalidInputException {
        if (value < MIN_VOLUME || value > MAX_VOLUME) {
            throw new InvalidInputException("Volume must be between " + MIN_VOLUME + " and " + MAX_VOLUME + ".");
        }
        return value;
    }

    private static int checkChannel(int value) throws InvalidInputException {
        if (value < MIN_CHANNEL || value > MAX_CHANNEL) {
            throw new InvalidInputException("Channel must be between " + MIN_CHANNEL + " and " + MAX_CHANNEL + ".");
        }
        return value;
    }
}
