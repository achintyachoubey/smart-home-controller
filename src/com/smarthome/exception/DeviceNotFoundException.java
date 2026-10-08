package com.smarthome.exception;

/**
 * Thrown when a device ID or a device position (index) does not exist.
 *
 * <p>OOP concepts: inheritance (extends {@link Exception}) and custom
 * <b>checked</b> exceptions. Because it is checked, the compiler forces
 * every caller to either catch it or declare it with {@code throws}.</p>
 */
public class DeviceNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with a message that is shown to the user.
     *
     * @param message explanation of which device was not found
     */
    public DeviceNotFoundException(String message) {
        super(message);
    }
}
