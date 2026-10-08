package com.smarthome.model;

import com.smarthome.exception.InvalidInputException;

/**
 * One step of a scene, for example "turn L001 off" or "set AC002 to 26".
 *
 * <p>OOP concepts: <b>enum</b> ({@link ActionType}) used in a classic
 * <b>switch</b> statement, <b>runtime type checks and casts</b>
 * ({@code instanceof Adjustable} then {@code (Adjustable) d}), and
 * <b>polymorphism</b>: the same {@code setLevel} call sets brightness, speed,
 * temperature or volume depending on the real object.</p>
 */
public class SceneAction {

    private final String deviceId;
    private final ActionType type;
    private final int value;

    /**
     * Creates a step.
     *
     * @param deviceId ID of the target device (stored in upper case)
     * @param type what to do
     * @param value the level for SET_LEVEL; ignored (stored as 0) for other types
     * @throws InvalidInputException if the ID is empty or contains '|', or the type is missing
     */
    public SceneAction(String deviceId, ActionType type, int value) throws InvalidInputException {
        if (deviceId == null || deviceId.trim().isEmpty() || deviceId.indexOf('|') >= 0) {
            throw new InvalidInputException("A scene step needs a valid device ID.");
        }
        if (type == null) {
            throw new InvalidInputException("A scene step needs an action type.");
        }
        this.deviceId = deviceId.trim().toUpperCase();
        this.type = type;
        this.value = (type == ActionType.SET_LEVEL) ? value : 0;
    }

    /** @return the ID of the target device */
    public String getDeviceId() {
        return deviceId;
    }

    /** @return the action type */
    public ActionType getType() {
        return type;
    }

    /** @return the level used by SET_LEVEL (0 for other types) */
    public int getValue() {
        return value;
    }

    /**
     * Performs this step on a device.
     *
     * @param d the device whose ID matches {@link #getDeviceId()}
     * @return a short description such as "L001 Ceiling Light -> OFF"
     * @throws InvalidInputException if the device does not support the action or the value is out of range
     */
    public String execute(Device d) throws InvalidInputException {
        String result;
        switch (type) {
            case TURN_ON:
                d.turnOn();
                result = "ON";
                break;
            case TURN_OFF:
                d.turnOff();
                result = "OFF";
                break;
            case TOGGLE:
                d.toggle();
                result = d.isOn() ? "ON" : "OFF";
                break;
            case SET_LEVEL:
                if (!(d instanceof Adjustable)) {
                    throw new InvalidInputException(d.getId() + " " + d.getName() + " is not adjustable");
                }
                Adjustable adjustable = (Adjustable) d;
                adjustable.setLevel(value);
                result = adjustable.getLevelName() + " " + value;
                break;
            case LOCK:
                if (!(d instanceof DoorLock)) {
                    throw new InvalidInputException(d.getId() + " " + d.getName() + " is not a door lock");
                }
                ((DoorLock) d).lock();
                result = "LOCKED";
                break;
            default:
                throw new InvalidInputException("Unsupported action " + type);
        }
        return d.getId() + " " + d.getName() + " -> " + result;
    }

    /** @return the action in words for menus, e.g. "TURN_OFF" or "SET_LEVEL 26" */
    public String describe() {
        return type == ActionType.SET_LEVEL ? type + " " + value : type.toString();
    }

    /** @return the line saved in scenes.txt, e.g. "ACTION|L001|TURN_OFF|0" */
    public String toFileString() {
        return "ACTION|" + deviceId + "|" + type + "|" + value;
    }
}
