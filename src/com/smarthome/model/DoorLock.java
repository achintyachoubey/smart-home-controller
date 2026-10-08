package com.smarthome.model;

import com.smarthome.exception.InvalidInputException;

/**
 * An electronic door lock protected by a 4-digit PIN. After three wrong PINs
 * the keypad locks itself for the rest of the session.
 *
 * <p>OOP concepts: <b>inheritance</b> ({@code extends Device}) <i>without</i>
 * implementing {@link Adjustable} (a lock has no level, so it is not forced
 * to pretend it has one), <b>method overriding</b> (including
 * {@link #turnOff()}), and strong <b>encapsulation</b>: the PIN has no getter
 * at all, it can only be checked through {@link #unlock(int)} and changed
 * through {@link #changePin(int, int)}.</p>
 *
 * <p>The class is {@code final} (it cannot be extended) because its
 * constructor calls {@code turnOn()}; calling an overridable method from a
 * constructor is only safe when no subclass can exist.</p>
 */
public final class DoorLock extends Device {

    /** ID prefix for door locks. */
    public static final String ID_PREFIX = "DL";
    /** Default power rating in watts. */
    public static final double DEFAULT_RATING = 5.0;
    /** PIN of a new lock. */
    public static final int DEFAULT_PIN = 1234;
    /** Smallest valid 4-digit PIN. */
    public static final int MIN_PIN = 1000;
    /** Largest valid 4-digit PIN. */
    public static final int MAX_PIN = 9999;
    /** Wrong PIN entries allowed before the keypad locks. */
    public static final int MAX_ATTEMPTS = 3;

    private boolean locked;
    private int pin;
    private int failedAttempts;
    private boolean keypadLocked;

    /**
     * Creates a locked, powered-on lock with the default rating and PIN.
     *
     * @param name display name
     * @throws InvalidInputException if the name is invalid
     */
    public DoorLock(String name) throws InvalidInputException {
        this(name, DEFAULT_RATING, DEFAULT_PIN);
    }

    /**
     * Creates a locked, powered-on lock with the given rating and PIN.
     *
     * @param name display name
     * @param rating power rating in watts
     * @param pin 4-digit PIN (1000 to 9999)
     * @throws InvalidInputException if any value is invalid
     */
    public DoorLock(String name, double rating, int pin) throws InvalidInputException {
        super(ID_PREFIX, name, rating);
        this.pin = checkPin(pin);
        this.locked = true;
        turnOn(); // a new lock starts powered on
    }

    /**
     * Re-creates a lock loaded from a file. The keypad always starts unlocked.
     *
     * @param id saved ID
     * @param name display name
     * @param isOn saved on/off state
     * @param rating power rating in watts
     * @param locked saved locked/unlocked state
     * @param pin saved 4-digit PIN
     * @throws InvalidInputException if any value is invalid
     */
    public DoorLock(String id, String name, boolean isOn, double rating, boolean locked, int pin)
            throws InvalidInputException {
        super(id, name, isOn, rating);
        this.pin = checkPin(pin);
        this.locked = locked;
    }

    /** @return true if the door is locked */
    public boolean isLocked() {
        return locked;
    }

    /** @return true if too many wrong PINs were entered this session */
    public boolean isKeypadLocked() {
        return keypadLocked;
    }

    /** @return how many wrong PINs may still be entered before the keypad locks */
    public int getRemainingAttempts() {
        return MAX_ATTEMPTS - failedAttempts;
    }

    /**
     * Locks the door. Always allowed while the lock has power, even if the keypad is locked.
     *
     * @throws InvalidInputException if the lock is powered off
     */
    public void lock() throws InvalidInputException {
        checkPowered();
        locked = true;
    }

    /**
     * Tries to unlock the door with a PIN.
     *
     * @param enteredPin the PIN typed by the user
     * @return true if the door is now unlocked; false if the PIN was wrong or the keypad is locked
     * @throws InvalidInputException if the lock is powered off
     */
    public boolean unlock(int enteredPin) throws InvalidInputException {
        checkPowered();
        if (keypadLocked) {
            return false;
        }
        if (enteredPin == pin) {
            locked = false;
            failedAttempts = 0;
            return true;
        }
        recordFailedAttempt();
        return false;
    }

    /**
     * Changes the PIN. A wrong old PIN counts as a failed attempt, so this
     * cannot be used to guess the PIN without limit.
     *
     * @param oldPin the current PIN
     * @param newPin the new PIN (1000 to 9999)
     * @throws InvalidInputException if powered off, keypad locked, old PIN wrong or new PIN invalid
     */
    public void changePin(int oldPin, int newPin) throws InvalidInputException {
        checkPowered();
        if (keypadLocked) {
            throw new InvalidInputException("Keypad is locked after " + MAX_ATTEMPTS + " wrong PINs.");
        }
        if (oldPin != pin) {
            recordFailedAttempt();
            throw new InvalidInputException("Wrong PIN. " + getRemainingAttempts() + " attempt(s) left.");
        }
        pin = checkPin(newPin);
        failedAttempts = 0;
    }

    /**
     * Cuts power to the lock. The bolt is mechanical, so it deliberately stays
     * in whatever state it was in (LOCKED or UNLOCKED); while powered off,
     * {@link #lock()}, {@link #unlock(int)} and {@link #changePin(int, int)} refuse to work.
     */
    @Override
    public void turnOff() {
        super.turnOff();
    }

    @Override
    public String getDeviceType() {
        return "LOCK";
    }

    @Override
    public String getStatus() {
        String status = locked ? "LOCKED" : "UNLOCKED";
        if (keypadLocked) {
            status += " [KEYPAD LOCKED]";
        }
        return status;
    }

    /** Live power = full rating while powered, 0 when off. */
    @Override
    public double getCurrentPowerWatts() {
        return isOn() ? getPowerRatingWatts() : 0.0;
    }

    /** The keypad lock is deliberately not saved: it resets when the program restarts. */
    @Override
    public String toFileString() {
        return baseFileString() + "|" + locked + "|" + pin;
    }

    private void recordFailedAttempt() {
        failedAttempts++;
        if (failedAttempts >= MAX_ATTEMPTS) {
            keypadLocked = true;
        }
    }

    private void checkPowered() throws InvalidInputException {
        if (!isOn()) {
            throw new InvalidInputException(getName() + " is powered off; turn it on first.");
        }
    }

    private static int checkPin(int value) throws InvalidInputException {
        if (value < MIN_PIN || value > MAX_PIN) {
            throw new InvalidInputException("PIN must be a 4-digit number from " + MIN_PIN + " to " + MAX_PIN + ".");
        }
        return value;
    }
}
