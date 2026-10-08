package com.smarthome.model;

import com.smarthome.exception.CapacityExceededException;
import com.smarthome.exception.DeviceNotFoundException;
import com.smarthome.exception.InvalidInputException;
import com.smarthome.util.InputHelper;

/**
 * A room that holds up to {@link #MAX_DEVICES} devices.
 *
 * <p>OOP concepts: <b>composition</b> (a room <i>has</i> devices),
 * <b>arrays</b> (a fixed-size {@code Device[]} with a separate count, with
 * hand-written add, search, remove-with-shift and copy), <b>method
 * overloading</b> (two {@code findDevice} methods), and <b>runtime
 * polymorphism</b> ({@link #getTotalPowerWatts()} calls
 * {@code getCurrentPowerWatts()} through {@code Device} references and Java
 * picks each subclass's own formula).</p>
 */
public class Room {

    /** Maximum number of devices in one room. */
    public static final int MAX_DEVICES = 10;

    private String name;
    private final Device[] devices = new Device[MAX_DEVICES];
    private int deviceCount;

    /**
     * Creates an empty room.
     *
     * @param name room name (not empty, at most 30 characters, no '|')
     * @throws InvalidInputException if the name is invalid
     */
    public Room(String name) throws InvalidInputException {
        this.name = InputHelper.validateName(name);
    }

    /** @return the room name */
    public String getName() {
        return name;
    }

    /**
     * Renames the room. (Uniqueness across the home is checked by {@link Home#renameRoom}.)
     *
     * @param name new name
     * @throws InvalidInputException if the name is invalid
     */
    public void setName(String name) throws InvalidInputException {
        this.name = InputHelper.validateName(name);
    }

    /**
     * Adds a device to the next free slot.
     *
     * @param d the device to add
     * @throws CapacityExceededException if the room already has 10 devices
     */
    public void addDevice(Device d) throws CapacityExceededException {
        if (d == null) {
            throw new IllegalArgumentException("Device must not be null");
        }
        if (deviceCount == MAX_DEVICES) {
            throw new CapacityExceededException("Room '" + name + "' is full (" + MAX_DEVICES + " devices).");
        }
        devices[deviceCount] = d;
        deviceCount++;
    }

    /**
     * Removes the device with the given ID and closes the gap.
     *
     * @param id device ID (any case)
     * @return the removed device
     * @throws DeviceNotFoundException if no device in this room has that ID
     */
    public Device removeDevice(String id) throws DeviceNotFoundException {
        int index = indexOf(id);
        if (index == -1) {
            throw new DeviceNotFoundException("No device with ID " + id + " in " + name + ".");
        }
        Device removed = devices[index];
        // shift every later device one place to the left to close the gap
        for (int i = index; i < deviceCount - 1; i++) {
            devices[i] = devices[i + 1];
        }
        devices[deviceCount - 1] = null; // the last used slot is now a duplicate
        deviceCount--;
        return removed;
    }

    /**
     * Finds a device by ID, ignoring letter case.
     *
     * @param id device ID such as "L001" or "l001"
     * @return the device
     * @throws DeviceNotFoundException if no device in this room has that ID
     */
    public Device findDevice(String id) throws DeviceNotFoundException {
        int index = indexOf(id);
        if (index == -1) {
            throw new DeviceNotFoundException("No device with ID " + id + " in " + name + ".");
        }
        return devices[index];
    }

    /**
     * Finds a device by its position in the room (overload of {@link #findDevice(String)}).
     *
     * @param index position, starting at 0
     * @return the device
     * @throws DeviceNotFoundException if the position is empty or out of range
     */
    public Device findDevice(int index) throws DeviceNotFoundException {
        if (index < 0 || index >= deviceCount) {
            throw new DeviceNotFoundException("No device at position " + index + " in " + name + ".");
        }
        return devices[index];
    }

    /** @return a copy of the devices, trimmed to the number actually stored */
    public Device[] getDevices() {
        Device[] copy = new Device[deviceCount];
        for (int i = 0; i < deviceCount; i++) {
            copy[i] = devices[i];
        }
        return copy;
    }

    /** @return how many devices are in the room */
    public int getDeviceCount() {
        return deviceCount;
    }

    /** @return the live power of every device in the room added together, in watts */
    public double getTotalPowerWatts() {
        double total = 0;
        for (int i = 0; i < deviceCount; i++) {
            total += devices[i].getCurrentPowerWatts(); // runtime polymorphism
        }
        return total;
    }

    /** Switches off every device except door locks, which must stay powered. */
    public void turnAllOff() {
        for (int i = 0; i < deviceCount; i++) {
            if (devices[i] instanceof DoorLock) {
                continue; // a lock must keep its power so it can still be controlled
            }
            devices[i].turnOff();
        }
    }

    /** Linear search for an ID (case-insensitive); returns -1 when not found. */
    private int indexOf(String id) {
        for (int i = 0; i < deviceCount; i++) {
            if (devices[i].getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return -1;
    }
}
