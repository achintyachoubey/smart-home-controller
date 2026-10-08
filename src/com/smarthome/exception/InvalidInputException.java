package com.smarthome.exception;

/**
 * Thrown by constructors and setters when a value is out of range or not
 * allowed (bad brightness, bad temperature, bad mode, bad PIN, empty name,
 * a name containing '|', and so on).
 *
 * <p>OOP concepts: inheritance (extends {@link Exception}), custom
 * <b>checked</b> exceptions, and encapsulation (setters refuse invalid
 * values by throwing this exception instead of storing them).</p>
 */
public class InvalidInputException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with a message that is shown to the user.
     *
     * @param message explanation of what was invalid
     */
    public InvalidInputException(String message) {
        super(message);
    }
}
