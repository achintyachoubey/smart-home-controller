package com.smarthome.exception;

/**
 * Thrown when something is added to a fixed-size array that is already full
 * (a full room, a home with the maximum number of rooms or scenes, or a
 * scene with the maximum number of steps).
 *
 * <p>OOP concepts: inheritance (extends {@link Exception}) and custom
 * <b>checked</b> exceptions.</p>
 */
public class CapacityExceededException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with a message that is shown to the user.
     *
     * @param message explanation of which array is full
     */
    public CapacityExceededException(String message) {
        super(message);
    }
}
