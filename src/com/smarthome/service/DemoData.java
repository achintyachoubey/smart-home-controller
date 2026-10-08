package com.smarthome.service;

import com.smarthome.exception.CapacityExceededException;
import com.smarthome.exception.InvalidInputException;
import com.smarthome.model.ActionType;
import com.smarthome.model.AirConditioner;
import com.smarthome.model.Device;
import com.smarthome.model.DoorLock;
import com.smarthome.model.Fan;
import com.smarthome.model.Home;
import com.smarthome.model.Light;
import com.smarthome.model.Room;
import com.smarthome.model.Scene;
import com.smarthome.model.SceneAction;
import com.smarthome.model.SmartTV;
import com.smarthome.util.IdGenerator;

/**
 * Builds the ready-made demo home used on first run and by "Reset to demo home".
 *
 * <p>OOP concepts: <b>objects</b> created with parameterized constructors,
 * <b>static methods</b> (no DemoData object is needed), <b>upcasting</b>
 * (concrete devices are stored in {@code Device} references), and
 * <b>exception handling</b> (multi-catch turns an impossible checked
 * exception into an {@link IllegalStateException}).</p>
 */
public final class DemoData {

    private DemoData() {
        // utility class: no objects
    }

    /**
     * Creates "My Smart Home" with 3 rooms, 10 devices and 3 scenes.
     * Resets the ID counters first so the demo always gets the same IDs (L001, F001, ...).
     *
     * @return the demo home
     */
    public static Home buildDemoHome() {
        IdGenerator.reset();
        try {
            Home home = new Home("My Smart Home");
            // rooms are built in this order, so the IDs below are always the same
            home.addRoom(buildLivingRoom());
            home.addRoom(buildBedroom());
            home.addRoom(buildEntrance());
            addDemoScenes(home);
            return home;
        } catch (InvalidInputException | CapacityExceededException e) {
            // every value here is a valid constant, so this can only mean a programming error
            throw new IllegalStateException("Demo data is invalid: " + e.getMessage(), e);
        }
    }

    /** L001 Ceiling Light, L002 Floor Lamp, F001 Ceiling Fan, AC001 Split AC, TV001 Smart TV. */
    private static Room buildLivingRoom() throws InvalidInputException, CapacityExceededException {
        Room living = new Room("Living Room");
        Device ceilingLight = new Light("Ceiling Light", Light.DEFAULT_RATING, 75, Light.DEFAULT_COLOUR);
        Device ceilingFan = new Fan("Ceiling Fan", Fan.DEFAULT_RATING, 3);
        ceilingLight.turnOn();
        ceilingFan.turnOn();
        living.addDevice(ceilingLight);
        living.addDevice(new Light("Floor Lamp", Light.DEFAULT_RATING, 40, Light.DEFAULT_COLOUR));
        living.addDevice(ceilingFan);
        living.addDevice(new AirConditioner("Split AC", AirConditioner.DEFAULT_RATING, 24, AirConditioner.MODE_COOL));
        living.addDevice(new SmartTV("Smart TV", SmartTV.DEFAULT_RATING, 40, 101));
        return living;
    }

    /** L003 Bedside Light, F002 Bedroom Fan, AC002 Bedroom AC. */
    private static Room buildBedroom() throws InvalidInputException, CapacityExceededException {
        Room bedroom = new Room("Bedroom");
        Device bedsideLight = new Light("Bedside Light", Light.DEFAULT_RATING, 60, Light.DEFAULT_COLOUR);
        bedsideLight.turnOn();
        bedroom.addDevice(bedsideLight);
        bedroom.addDevice(new Fan("Bedroom Fan", Fan.DEFAULT_RATING, 2));
        bedroom.addDevice(new AirConditioner("Bedroom AC", AirConditioner.DEFAULT_RATING, 24,
                AirConditioner.MODE_COOL));
        return bedroom;
    }

    /** DL001 Main Door Lock (a new lock starts ON and locked), L004 Porch Light. */
    private static Room buildEntrance() throws InvalidInputException, CapacityExceededException {
        Room entrance = new Room("Entrance");
        entrance.addDevice(new DoorLock("Main Door Lock", DoorLock.DEFAULT_RATING, 1234));
        entrance.addDevice(new Light("Porch Light", Light.DEFAULT_RATING, 100, Light.DEFAULT_COLOUR));
        return entrance;
    }

    /** Night Mode, Away Mode and Movie Time, using the fixed demo IDs listed above. */
    private static void addDemoScenes(Home home) throws InvalidInputException, CapacityExceededException {
        Scene night = new Scene("Night Mode");
        String[] nightOff = {"L001", "L002", "F001", "AC001", "TV001"};
        for (String id : nightOff) {
            addStep(night, id, ActionType.TURN_OFF, 0);
        }
        addStep(night, "AC002", ActionType.SET_LEVEL, 26);
        addStep(night, "L003", ActionType.SET_LEVEL, 20);
        addStep(night, "DL001", ActionType.LOCK, 0);
        home.addScene(night);

        Scene away = new Scene("Away Mode");
        String[] everythingButTheLock = {"L001", "L002", "L003", "L004", "F001", "F002", "AC001", "AC002", "TV001"};
        for (String id : everythingButTheLock) {
            addStep(away, id, ActionType.TURN_OFF, 0);
        }
        addStep(away, "DL001", ActionType.LOCK, 0);
        home.addScene(away);

        Scene movie = new Scene("Movie Time");
        addStep(movie, "L001", ActionType.TURN_OFF, 0);
        addStep(movie, "L002", ActionType.SET_LEVEL, 30);
        addStep(movie, "TV001", ActionType.SET_LEVEL, 40);
        addStep(movie, "AC001", ActionType.SET_LEVEL, 24);
        home.addScene(movie);
    }

    private static void addStep(Scene scene, String deviceId, ActionType type, int value)
            throws InvalidInputException, CapacityExceededException {
        scene.addAction(new SceneAction(deviceId, type, value));
    }
}
