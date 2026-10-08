package com.smarthome.model;

/**
 * The kinds of step a {@link Scene} can perform on a device.
 *
 * <p>OOP concept: <b>enum</b>, a fixed set of named constants. Using an enum
 * instead of plain strings means a misspelt action cannot compile, and it
 * can be used directly in a {@code switch} statement.</p>
 */
public enum ActionType {
    /** Switch the device on. */
    TURN_ON,
    /** Switch the device off. */
    TURN_OFF,
    /** Flip the device between on and off. */
    TOGGLE,
    /** Set the level of an {@link Adjustable} device (uses the step's value). */
    SET_LEVEL,
    /** Lock a {@link DoorLock}. */
    LOCK
}
