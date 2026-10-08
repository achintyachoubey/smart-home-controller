package com.smarthome.model;

import com.smarthome.exception.CapacityExceededException;
import com.smarthome.exception.DeviceNotFoundException;
import com.smarthome.exception.InvalidInputException;
import com.smarthome.util.InputHelper;

/**
 * The whole home: up to {@link #MAX_ROOMS} rooms and {@link #MAX_SCENES} scenes.
 *
 * <p>OOP concepts: <b>composition</b> (a home <i>has</i> rooms and scenes),
 * <b>arrays</b> with hand-written add, search, remove-with-shift and copy,
 * <b>encapsulation</b> (getters return copies so the internal arrays cannot
 * be changed from outside), and <b>exception handling</b> (custom checked
 * exceptions for "full", "duplicate" and "not found").</p>
 *
 * <p>Lookup rule used everywhere in this class: {@code findRoom} and
 * {@code findScene} never return {@code null}; when the name does not exist
 * they throw {@link InvalidInputException}.</p>
 */
public class Home {

    /** Maximum number of rooms. */
    public static final int MAX_ROOMS = 8;
    /** Maximum number of scenes. */
    public static final int MAX_SCENES = 10;

    private final String name;
    private final Room[] rooms = new Room[MAX_ROOMS];
    private int roomCount;
    private final Scene[] scenes = new Scene[MAX_SCENES];
    private int sceneCount;

    /**
     * Creates an empty home.
     *
     * @param name home name (not empty, at most 30 characters, no '|')
     * @throws InvalidInputException if the name is invalid
     */
    public Home(String name) throws InvalidInputException {
        this.name = InputHelper.validateName(name);
    }

    /** @return the home name */
    public String getName() {
        return name;
    }

    // ------------------------------------------------------------------ rooms

    /**
     * Adds a room.
     *
     * @param r the room to add
     * @throws CapacityExceededException if the home already has 8 rooms
     * @throws InvalidInputException if a room with the same name (any case) exists
     */
    public void addRoom(Room r) throws CapacityExceededException, InvalidInputException {
        if (indexOfRoom(r.getName()) != -1) {
            throw new InvalidInputException("A room named '" + r.getName() + "' already exists.");
        }
        if (roomCount == MAX_ROOMS) {
            throw new CapacityExceededException("The home already has the maximum of " + MAX_ROOMS + " rooms.");
        }
        rooms[roomCount] = r;
        roomCount++;
    }

    /**
     * Removes a room (and therefore all of its devices) and closes the gap.
     *
     * @param roomName name of the room (any case)
     * @return the removed room
     * @throws InvalidInputException if no room has that name
     */
    public Room removeRoom(String roomName) throws InvalidInputException {
        int index = indexOfRoom(roomName);
        if (index == -1) {
            throw new InvalidInputException("No room named '" + roomName + "'.");
        }
        Room removed = rooms[index];
        // shift later rooms left by one place
        for (int i = index; i < roomCount - 1; i++) {
            rooms[i] = rooms[i + 1];
        }
        rooms[roomCount - 1] = null;
        roomCount--;
        return removed;
    }

    /**
     * Finds a room by name, ignoring case.
     *
     * @param roomName name to look for
     * @return the room (never null)
     * @throws InvalidInputException if no room has that name
     */
    public Room findRoom(String roomName) throws InvalidInputException {
        int index = indexOfRoom(roomName);
        if (index == -1) {
            throw new InvalidInputException("No room named '" + roomName + "'.");
        }
        return rooms[index];
    }

    /**
     * Renames a room, keeping room names unique.
     *
     * @param oldName current name (any case)
     * @param newName new name
     * @throws InvalidInputException if the room does not exist, the new name is invalid or already used
     */
    public void renameRoom(String oldName, String newName) throws InvalidInputException {
        Room room = findRoom(oldName);
        int clash = indexOfRoom(newName);
        if (clash != -1 && rooms[clash] != room) {
            throw new InvalidInputException("A room named '" + rooms[clash].getName() + "' already exists.");
        }
        room.setName(newName);
    }

    /** @return a copy of the rooms, trimmed to the number actually stored */
    public Room[] getRooms() {
        Room[] copy = new Room[roomCount];
        for (int i = 0; i < roomCount; i++) {
            copy[i] = rooms[i];
        }
        return copy;
    }

    // ---------------------------------------------------------------- devices

    /**
     * Searches every room for a device.
     *
     * @param id device ID (any case)
     * @return the device
     * @throws DeviceNotFoundException if no room contains that ID
     */
    public Device findDeviceAnywhere(String id) throws DeviceNotFoundException {
        return findRoomOfDevice(id).findDevice(id);
    }

    /**
     * Finds which room contains a device.
     *
     * @param id device ID (any case)
     * @return the room holding the device
     * @throws DeviceNotFoundException if no room contains that ID
     */
    public Room findRoomOfDevice(String id) throws DeviceNotFoundException {
        for (int i = 0; i < roomCount; i++) {
            try {
                rooms[i].findDevice(id);
                return rooms[i];
            } catch (DeviceNotFoundException e) {
                // not in this room: keep looking in the next one
            }
        }
        throw new DeviceNotFoundException("No device with ID " + id + ".");
    }

    /** @return the number of devices in all rooms */
    public int getTotalDeviceCount() {
        int total = 0;
        for (int i = 0; i < roomCount; i++) {
            total += rooms[i].getDeviceCount();
        }
        return total;
    }

    /** @return how many devices are currently switched on */
    public int getOnDeviceCount() {
        int count = 0;
        for (int i = 0; i < roomCount; i++) {
            for (Device d : rooms[i].getDevices()) {
                if (d.isOn()) {
                    count++;
                }
            }
        }
        return count;
    }

    /** @return the live power of the whole home in watts */
    public double getTotalPowerWatts() {
        double total = 0;
        for (int i = 0; i < roomCount; i++) {
            total += rooms[i].getTotalPowerWatts();
        }
        return total;
    }

    // ----------------------------------------------------------------- scenes

    /**
     * Adds a scene.
     *
     * @param s the scene to add
     * @throws CapacityExceededException if the home already has 10 scenes
     * @throws InvalidInputException if a scene with the same name (any case) exists
     */
    public void addScene(Scene s) throws CapacityExceededException, InvalidInputException {
        if (indexOfScene(s.getName()) != -1) {
            throw new InvalidInputException("A scene named '" + s.getName() + "' already exists.");
        }
        if (sceneCount == MAX_SCENES) {
            throw new CapacityExceededException("The home already has the maximum of " + MAX_SCENES + " scenes.");
        }
        scenes[sceneCount] = s;
        sceneCount++;
    }

    /**
     * Finds a scene by name, ignoring case.
     *
     * @param sceneName name to look for
     * @return the scene (never null)
     * @throws InvalidInputException if no scene has that name
     */
    public Scene findScene(String sceneName) throws InvalidInputException {
        int index = indexOfScene(sceneName);
        if (index == -1) {
            throw new InvalidInputException("No scene named '" + sceneName + "'.");
        }
        return scenes[index];
    }

    /**
     * Removes a scene and closes the gap.
     *
     * @param sceneName name of the scene (any case)
     * @return the removed scene
     * @throws InvalidInputException if no scene has that name
     */
    public Scene removeScene(String sceneName) throws InvalidInputException {
        int index = indexOfScene(sceneName);
        if (index == -1) {
            throw new InvalidInputException("No scene named '" + sceneName + "'.");
        }
        Scene removed = scenes[index];
        // shift later scenes left by one place
        for (int i = index; i < sceneCount - 1; i++) {
            scenes[i] = scenes[i + 1];
        }
        scenes[sceneCount - 1] = null;
        sceneCount--;
        return removed;
    }

    /** @return a copy of the scenes, trimmed to the number actually stored */
    public Scene[] getScenes() {
        Scene[] copy = new Scene[sceneCount];
        for (int i = 0; i < sceneCount; i++) {
            copy[i] = scenes[i];
        }
        return copy;
    }

    private int indexOfRoom(String roomName) {
        for (int i = 0; i < roomCount; i++) {
            if (rooms[i].getName().equalsIgnoreCase(roomName.trim())) {
                return i;
            }
        }
        return -1;
    }

    private int indexOfScene(String sceneName) {
        for (int i = 0; i < sceneCount; i++) {
            if (scenes[i].getName().equalsIgnoreCase(sceneName.trim())) {
                return i;
            }
        }
        return -1;
    }
}
