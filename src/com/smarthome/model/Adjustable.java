package com.smarthome.model;

import com.smarthome.exception.InvalidInputException;

/**
 * A device that has one main "level" which can be turned up or down:
 * brightness for a light, speed for a fan, temperature for an AC and
 * volume for a TV.
 *
 * <p>OOP concepts: <b>interface</b> and <b>abstraction</b>. The menu and
 * scenes only need to know "this device has a level between min and max";
 * they do not care which class it is. Light, Fan, AirConditioner and SmartTV
 * implement it; DoorLock does not, because a lock has no level.</p>
 */
public interface Adjustable {

    /**
     * Sets the level and switches the device on.
     *
     * @param level new level between {@link #getMinLevel()} and {@link #getMaxLevel()}
     * @throws InvalidInputException if the level is out of range
     */
    void setLevel(int level) throws InvalidInputException;

    /** @return the current level */
    int getLevel();

    /** @return the smallest allowed level */
    int getMinLevel();

    /** @return the largest allowed level */
    int getMaxLevel();

    /** @return what the level means, e.g. "Brightness", used to label menu prompts */
    String getLevelName();
}
