package com.smarthome;

import com.smarthome.exception.CapacityExceededException;
import com.smarthome.exception.DeviceNotFoundException;
import com.smarthome.exception.InvalidInputException;
import com.smarthome.model.ActionType;
import com.smarthome.model.Adjustable;
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
import com.smarthome.service.DemoData;
import com.smarthome.service.EnergyCalculator;
import com.smarthome.service.FileManager;
import com.smarthome.util.InputHelper;

import java.io.IOException;

/**
 * Entry point and console user interface of the Smart Home Device Controller.
 *
 * <p>OOP concepts: <b>static members</b> (the program state and one method
 * per screen), <b>control flow</b> ({@code while} menu loops, classic
 * {@code switch}, {@code for} loops), <b>runtime polymorphism</b> (devices are
 * handled through {@code Device} references, with {@code instanceof} and casts
 * to unlock type-specific options), and <b>exception handling at the UI
 * layer</b>: every custom exception is caught here and its message shown,
 * and an unexpected {@code RuntimeException} is reported without crashing.</p>
 */
public final class Main {

    private static final String RULE = "-------------------------------------------";

    // codes for the "Control a device" sub-menu, which is built per device type
    private static final int OPT_ON = 1;
    private static final int OPT_OFF = 2;
    private static final int OPT_TOGGLE = 3;
    private static final int OPT_LEVEL = 4;
    private static final int OPT_MODE = 5;
    private static final int OPT_CHANNEL = 6;
    private static final int OPT_COLOUR = 7;
    private static final int OPT_LOCK = 8;
    private static final int OPT_UNLOCK = 9;
    private static final int OPT_CHANGE_PIN = 10;
    private static final int MAX_CONTROL_OPTIONS = 10;

    /** Longest period accepted for "energy over N hours" (a 30-day month). */
    private static final double MAX_REPORT_HOURS = 720;

    private static Home home;
    private static FileManager fileManager;
    private static EnergyCalculator energyCalculator;

    private Main() {
        // all members are static: no Main objects are needed
    }

    /**
     * Starts the program: loads (or creates) the home, then shows the main menu
     * until the user exits or the input ends.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        fileManager = new FileManager();
        energyCalculator = new EnergyCalculator();
        printBanner();
        try {
            startup();
            runMainMenu();
        } catch (InputHelper.InputClosedException e) {
            System.out.println();
            System.out.println("End of input detected.");
            if (home != null) {
                saveAndExit();
            } else {
                System.out.println("Nothing to save. Goodbye!");
            }
        }
    }

    // ============================================================== start-up

    private static void printBanner() {
        System.out.println("===========================================");
        System.out.println("       SMART HOME DEVICE CONTROLLER");
        System.out.println("   Java OOP mini project - console edition");
        System.out.println("===========================================");
    }

    /** Loads the saved home, or offers the demo home / an empty home on first run. */
    private static void startup() {
        try {
            home = fileManager.loadHome();
            if (home != null) {
                fileManager.loadScenes(home);
            }
        } catch (IOException e) {
            System.out.println("Could not read the saved data: " + e.getMessage());
            home = null;
        }
        if (home != null) {
            System.out.println("Loaded " + home.getName() + ": " + counts() + ".");
        } else if (InputHelper.readYesNo("No saved home found. Load demo home? (y/n): ")) {
            home = DemoData.buildDemoHome();
            System.out.println("Demo home loaded: " + counts() + ".");
        } else {
            while (home == null) {
                try {
                    home = new Home(InputHelper.readName("Enter a name for your home: "));
                } catch (InvalidInputException e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println("Created empty home '" + home.getName() + "'. Start by adding a room.");
        }
        fileManager.appendLog("SYSTEM", "Program started");
    }

    // ============================================================= main menu

    /** do-while: the menu is always shown at least once, then repeats until option 0. */
    private static void runMainMenu() {
        boolean running = true;
        do {
            printMainMenu();
            int choice = InputHelper.readInt("Choose an option: ", 0, 9);
            try {
                running = handleMainChoice(choice);
            } catch (InputHelper.InputClosedException e) {
                throw e; // end of input is handled in main()
            } catch (RuntimeException e) {
                System.out.println("Unexpected error: " + e.getMessage());
                fileManager.appendLog("SYSTEM", "Unexpected error: " + e);
            }
        } while (running);
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println("---------------- MAIN MENU ----------------");
        System.out.println(" 1. Dashboard            6. Energy report");
        System.out.println(" 2. Manage rooms         7. Activity log");
        System.out.println(" 3. Manage devices       8. Save now");
        System.out.println(" 4. Control a device     9. Reset to demo home");
        System.out.println(" 5. Scenes               0. Save and exit");
        System.out.println(RULE);
    }

    /** Runs one main-menu option; returns false when the program should stop. */
    private static boolean handleMainChoice(int choice) {
        switch (choice) {
            case 1:
                showDashboard();
                break;
            case 2:
                manageRooms();
                break;
            case 3:
                manageDevices();
                break;
            case 4:
                controlDevice();
                break;
            case 5:
                manageScenes();
                break;
            case 6:
                energyReport();
                break;
            case 7:
                showLog();
                break;
            case 8:
                saveNow();
                break;
            case 9:
                resetToDemo();
                break;
            case 0:
                saveAndExit();
                return false;
        }
        return true;
    }

    /** Prints a sub-menu heading, its numbered items and a "0. Back" line. */
    private static void printSubMenu(String title, String[] items) {
        System.out.println();
        System.out.println("--- " + title + " ---");
        for (int i = 0; i < items.length; i++) {
            System.out.println(" " + (i + 1) + ". " + items[i]);
        }
        System.out.println(" 0. Back");
    }

    // ============================================================= dashboard

    private static void showDashboard() {
        System.out.println();
        System.out.println("=============== DASHBOARD: " + home.getName() + " ===============");
        Room[] rooms = home.getRooms();
        if (rooms.length == 0) {
            System.out.println("(no rooms yet - use 2. Manage rooms)");
        }
        for (Room room : rooms) {
            printRoomTable(room);
        }
        System.out.println(RULE);
        System.out.printf("Total live power: %.1f W%n", home.getTotalPowerWatts());
        System.out.println("Devices ON: " + home.getOnDeviceCount() + " of " + home.getTotalDeviceCount());
        InputHelper.pause();
    }

    /** Prints one room: header, column titles, one row per device and the room total. */
    private static void printRoomTable(Room room) {
        System.out.println();
        System.out.println("[ " + room.getName() + " ]");
        if (room.getDeviceCount() == 0) {
            System.out.println("  (no devices)");
            return;
        }
        System.out.println(Device.TABLE_HEADER);
        for (Device d : room.getDevices()) {
            System.out.println(d); // calls the overridden toString(), which uses each subclass's getStatus()
        }
        System.out.printf("Room total: %.1f W%n", room.getTotalPowerWatts());
    }

    // ========================================================== manage rooms

    private static void manageRooms() {
        String[] items = {"Add room", "List rooms", "Rename room", "Remove room",
            "Turn off all devices in a room (locks stay on)"};
        while (true) {
            printSubMenu("MANAGE ROOMS", items);
            int choice = InputHelper.readInt("Choose an option: ", 0, items.length);
            switch (choice) {
                case 1:
                    addRoom();
                    break;
                case 2:
                    listRooms();
                    break;
                case 3:
                    renameRoom();
                    break;
                case 4:
                    removeRoom();
                    break;
                case 5:
                    turnOffRoom();
                    break;
                default:
                    return; // 0 = Back
            }
        }
    }

    private static void addRoom() {
        if (home.getRooms().length == Home.MAX_ROOMS) {
            System.out.println("The home already has the maximum of " + Home.MAX_ROOMS + " rooms.");
            return;
        }
        String name = InputHelper.readName("New room name: ");
        try {
            home.addRoom(new Room(name));
            System.out.println("Room '" + name + "' added.");
            fileManager.appendLog("ROOM", "Room " + name + " added");
        } catch (InvalidInputException | CapacityExceededException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void listRooms() {
        Room[] rooms = home.getRooms();
        if (rooms.length == 0) {
            System.out.println("There are no rooms yet.");
            return;
        }
        for (int i = 0; i < rooms.length; i++) {
            System.out.println(" " + (i + 1) + ". " + rooms[i].getName() + " ("
                    + plural(rooms[i].getDeviceCount(), "device") + ")");
        }
    }

    /** Shows the numbered room list and returns the chosen room, or null for cancel / no rooms. */
    private static Room pickRoom() {
        Room[] rooms = home.getRooms();
        if (rooms.length == 0) {
            System.out.println("There are no rooms yet. Add a room first (2. Manage rooms).");
            return null;
        }
        listRooms();
        int choice = InputHelper.readInt("Room number (0 to cancel): ", 0, rooms.length);
        return (choice == 0) ? null : rooms[choice - 1];
    }

    private static void renameRoom() {
        Room room = pickRoom();
        if (room == null) {
            return;
        }
        String oldName = room.getName();
        String newName = InputHelper.readName("New name for '" + oldName + "': ");
        try {
            home.renameRoom(oldName, newName);
            System.out.println("Room '" + oldName + "' renamed to '" + room.getName() + "'.");
            fileManager.appendLog("ROOM", "Room " + oldName + " renamed to " + room.getName());
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void removeRoom() {
        Room room = pickRoom();
        if (room == null) {
            return;
        }
        int devices = room.getDeviceCount();
        String question = (devices > 0)
                ? "This will also remove " + plural(devices, "device") + ". Continue? (y/n): "
                : "Remove room '" + room.getName() + "'? (y/n): ";
        if (!InputHelper.readYesNo(question)) {
            System.out.println("Nothing removed.");
            return;
        }
        try {
            home.removeRoom(room.getName());
            System.out.println("Room '" + room.getName() + "' removed.");
            fileManager.appendLog("ROOM", "Room " + room.getName() + " removed with "
                    + plural(devices, "device"));
            reportMissingSceneDevices();
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void turnOffRoom() {
        Room room = pickRoom();
        if (room == null) {
            return;
        }
        room.turnAllOff();
        System.out.println("All devices in " + room.getName() + " are off (door locks keep their power).");
        fileManager.appendLog("CONTROL", "All devices in " + room.getName() + " turned OFF");
    }

    /** Lists scene steps whose device no longer exists; they are kept and will fail gracefully. */
    private static void reportMissingSceneDevices() {
        for (Scene scene : home.getScenes()) {
            SceneAction[] steps = scene.getActions();
            for (int i = 0; i < steps.length; i++) {
                if (!deviceExists(steps[i].getDeviceId())) {
                    System.out.println("  Note: scene '" + scene.getName() + "' step " + (i + 1)
                            + " points to missing device " + steps[i].getDeviceId()
                            + " (it will be reported as failed when the scene runs).");
                }
            }
        }
    }

    // ======================================================== manage devices

    private static void manageDevices() {
        String[] items = {"Add device", "Remove device", "List devices in a room",
            "Search device by ID", "Rename device", "Change power rating"};
        while (true) {
            printSubMenu("MANAGE DEVICES", items);
            int choice = InputHelper.readInt("Choose an option: ", 0, items.length);
            switch (choice) {
                case 1:
                    addDevice();
                    break;
                case 2:
                    removeDevice();
                    break;
                case 3:
                    listDevicesInRoom();
                    break;
                case 4:
                    searchDevice();
                    break;
                case 5:
                    renameDevice();
                    break;
                case 6:
                    changeRating();
                    break;
                default:
                    return; // 0 = Back
            }
        }
    }

    private static void addDevice() {
        Room room = pickRoom();
        if (room == null) {
            return;
        }
        if (room.getDeviceCount() == Room.MAX_DEVICES) {
            System.out.println("Room '" + room.getName() + "' is full (" + Room.MAX_DEVICES + " devices).");
            return;
        }
        System.out.println("Device type: 1. Light  2. Fan  3. Air conditioner  4. Smart TV  5. Door lock");
        int type = InputHelper.readInt("Choose type (1-5, 0 to cancel): ", 0, 5);
        if (type == 0) {
            return;
        }
        String name = InputHelper.readName("Device name: ");
        try {
            Device d = createDevice(type, name);
            room.addDevice(d);
            System.out.println("Added " + d.getName() + " to " + room.getName() + " with ID " + d.getId() + ".");
            fileManager.appendLog("DEVICE", d.getId() + " " + d.getName() + " (" + d.getDeviceType()
                    + ") added to " + room.getName());
        } catch (InvalidInputException | CapacityExceededException e) {
            System.out.println("Device not added: " + e.getMessage());
        }
    }

    /** Asks for the power rating and type-specific settings (Enter keeps each default). */
    private static Device createDevice(int type, String name) throws InvalidInputException {
        switch (type) {
            case 1:
                double lightWatts = readRating(Light.DEFAULT_RATING);
                int brightness = InputHelper.readOptionalInt("Brightness 0-100 [default "
                        + Light.DEFAULT_BRIGHTNESS + "]: ", 0, 100, Light.DEFAULT_BRIGHTNESS);
                String colour = chooseOption("Colour", Light.getColourOptions(), Light.DEFAULT_COLOUR);
                return new Light(name, lightWatts, brightness, colour);
            case 2:
                double fanWatts = readRating(Fan.DEFAULT_RATING);
                int speed = InputHelper.readOptionalInt("Speed 1-5 [default " + Fan.DEFAULT_SPEED + "]: ",
                        Fan.MIN_SPEED, Fan.MAX_SPEED, Fan.DEFAULT_SPEED);
                return new Fan(name, fanWatts, speed);
            case 3:
                double acWatts = readRating(AirConditioner.DEFAULT_RATING);
                int temperature = InputHelper.readOptionalInt("Temperature 16-30 C [default "
                        + AirConditioner.DEFAULT_TEMPERATURE + "]: ", AirConditioner.MIN_TEMPERATURE,
                        AirConditioner.MAX_TEMPERATURE, AirConditioner.DEFAULT_TEMPERATURE);
                String mode = chooseOption("Mode", AirConditioner.getModeOptions(), AirConditioner.MODE_COOL);
                return new AirConditioner(name, acWatts, temperature, mode);
            case 4:
                double tvWatts = readRating(SmartTV.DEFAULT_RATING);
                int volume = InputHelper.readOptionalInt("Volume 0-100 [default " + SmartTV.DEFAULT_VOLUME + "]: ",
                        SmartTV.MIN_VOLUME, SmartTV.MAX_VOLUME, SmartTV.DEFAULT_VOLUME);
                int channel = InputHelper.readOptionalInt("Channel 1-999 [default " + SmartTV.DEFAULT_CHANNEL
                        + "]: ", SmartTV.MIN_CHANNEL, SmartTV.MAX_CHANNEL, SmartTV.DEFAULT_CHANNEL);
                return new SmartTV(name, tvWatts, volume, channel);
            default: // 5 = door lock
                double lockWatts = readRating(DoorLock.DEFAULT_RATING);
                int pin = InputHelper.readOptionalInt("4-digit PIN 1000-9999 [default " + DoorLock.DEFAULT_PIN
                        + "]: ", DoorLock.MIN_PIN, DoorLock.MAX_PIN, DoorLock.DEFAULT_PIN);
                return new DoorLock(name, lockWatts, pin);
        }
    }

    private static double readRating(double defaultWatts) {
        return InputHelper.readOptionalDouble("Power rating in watts (0.1-5000) [default " + defaultWatts + "]: ",
                0.1, Device.MAX_RATING_WATTS, defaultWatts);
    }

    /** Shows numbered choices and returns the chosen text; Enter picks the current/default value. */
    private static String chooseOption(String label, String[] options, String defaultOption) {
        int defaultNumber = 1;
        for (int i = 0; i < options.length; i++) {
            System.out.println("   " + (i + 1) + ". " + options[i]);
            if (options[i].equals(defaultOption)) {
                defaultNumber = i + 1;
            }
        }
        int choice = InputHelper.readOptionalInt(label + " 1-" + options.length + " [default "
                + defaultNumber + " = " + defaultOption + "]: ", 1, options.length, defaultNumber);
        return options[choice - 1];
    }

    /**
     * Asks for a device ID until it exists (case-insensitive) or the user types 0.
     *
     * @return the device, or null if the user went back or the home has no devices
     */
    private static Device askForDevice(String prompt) {
        if (home.getTotalDeviceCount() == 0) {
            System.out.println("There are no devices yet. Add one first (3. Manage devices).");
            return null;
        }
        while (true) {
            String id = InputHelper.readId(prompt + " (0 to go back): ");
            if (id.equals("0")) {
                return null;
            }
            try {
                return home.findDeviceAnywhere(id);
            } catch (DeviceNotFoundException e) {
                System.out.println(e.getMessage() + " Please try again.");
            }
        }
    }

    private static boolean deviceExists(String id) {
        try {
            home.findDeviceAnywhere(id);
            return true;
        } catch (DeviceNotFoundException e) {
            return false;
        }
    }

    private static void removeDevice() {
        Device d = askForDevice("ID of the device to remove");
        if (d == null) {
            return;
        }
        System.out.println(Device.TABLE_HEADER);
        System.out.println(d);
        if (!InputHelper.readYesNo("Remove " + d.getId() + " " + d.getName() + "? (y/n): ")) {
            System.out.println("Nothing removed.");
            return;
        }
        try {
            Room room = home.findRoomOfDevice(d.getId());
            room.removeDevice(d.getId());
            System.out.println(d.getId() + " " + d.getName() + " removed from " + room.getName() + ".");
            fileManager.appendLog("DEVICE", d.getId() + " " + d.getName() + " removed from " + room.getName());
            reportMissingSceneDevices();
        } catch (DeviceNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void listDevicesInRoom() {
        Room room = pickRoom();
        if (room != null) {
            printRoomTable(room);
        }
    }

    private static void searchDevice() {
        String id = InputHelper.readId("Device ID to search for: ");
        try {
            Room room = home.findRoomOfDevice(id);
            Device d = room.findDevice(id);
            System.out.println("Found in room: " + room.getName());
            System.out.println(Device.TABLE_HEADER);
            System.out.println(d);
        } catch (DeviceNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void renameDevice() {
        Device d = askForDevice("ID of the device to rename");
        if (d == null) {
            return;
        }
        String oldName = d.getName();
        try {
            d.setName(InputHelper.readName("New name for " + d.getId() + " " + oldName + ": "));
            System.out.println(d.getId() + " renamed to " + d.getName() + ".");
            fileManager.appendLog("DEVICE", d.getId() + " " + oldName + " renamed to " + d.getName());
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void changeRating() {
        Device d = askForDevice("ID of the device");
        if (d == null) {
            return;
        }
        double oldWatts = d.getPowerRatingWatts();
        try {
            d.setPowerRatingWatts(InputHelper.readDouble("New power rating in watts (0.1-5000) [now " + oldWatts
                    + "]: ", 0.1, Device.MAX_RATING_WATTS));
            System.out.println(d.getId() + " " + d.getName() + " is now rated " + d.getPowerRatingWatts() + " W.");
            fileManager.appendLog("DEVICE", d.getId() + " " + d.getName() + " rating changed from " + oldWatts
                    + " W to " + d.getPowerRatingWatts() + " W");
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
        }
    }

    // ===================================================== control a device

    private static void controlDevice() {
        Device d = askForDevice("Device ID to control");
        if (d == null) {
            return;
        }
        String[] labels = new String[MAX_CONTROL_OPTIONS];
        int[] codes = new int[MAX_CONTROL_OPTIONS];
        int count = buildControlOptions(d, labels, codes);
        while (true) {
            System.out.println();
            System.out.println(Device.TABLE_HEADER);
            System.out.println(d);
            System.out.println("--- CONTROL " + d.getId() + " " + d.getName() + " ---");
            for (int i = 0; i < count; i++) {
                System.out.println(" " + (i + 1) + ". " + labels[i]);
            }
            System.out.println(" 0. Back");
            int choice = InputHelper.readInt("Choose an option: ", 0, count);
            if (choice == 0) {
                return;
            }
            performControl(d, codes[choice - 1]);
        }
    }

    /** Fills the parallel label/code arrays with the options this device supports; returns how many. */
    private static int buildControlOptions(Device d, String[] labels, int[] codes) {
        int n = 0;
        labels[n] = "Turn on";
        codes[n++] = OPT_ON;
        labels[n] = "Turn off";
        codes[n++] = OPT_OFF;
        labels[n] = "Toggle";
        codes[n++] = OPT_TOGGLE;
        if (d instanceof Adjustable) {
            Adjustable a = (Adjustable) d;
            labels[n] = "Set " + a.getLevelName() + " (" + a.getMinLevel() + "-" + a.getMaxLevel() + ")";
            codes[n++] = OPT_LEVEL;
        }
        if (d instanceof AirConditioner) {
            labels[n] = "Set mode";
            codes[n++] = OPT_MODE;
        } else if (d instanceof SmartTV) {
            labels[n] = "Set channel";
            codes[n++] = OPT_CHANNEL;
        } else if (d instanceof Light) {
            labels[n] = "Set colour";
            codes[n++] = OPT_COLOUR;
        } else if (d instanceof DoorLock) {
            labels[n] = "Lock";
            codes[n++] = OPT_LOCK;
            labels[n] = "Unlock (PIN)";
            codes[n++] = OPT_UNLOCK;
            labels[n] = "Change PIN";
            codes[n++] = OPT_CHANGE_PIN;
        }
        return n;
    }

    /** Carries out one control option; any validation error is shown, not thrown. */
    private static void performControl(Device d, int code) {
        try {
            switch (code) {
                case OPT_ON:
                    d.turnOn();
                    logControl(d, "turned ON");
                    break;
                case OPT_OFF:
                    d.turnOff();
                    logControl(d, "turned OFF");
                    warnIfLockPoweredOff(d);
                    break;
                case OPT_TOGGLE:
                    d.toggle();
                    logControl(d, "toggled " + (d.isOn() ? "ON" : "OFF"));
                    warnIfLockPoweredOff(d);
                    break;
                case OPT_LEVEL:
                    setLevel((Adjustable) d);
                    break;
                case OPT_MODE:
                    AirConditioner ac = (AirConditioner) d;
                    ac.setMode(chooseOption("Mode", AirConditioner.getModeOptions(), ac.getMode()));
                    logControl(d, "mode set to " + ac.getMode());
                    break;
                case OPT_CHANNEL:
                    SmartTV tv = (SmartTV) d;
                    tv.setChannel(InputHelper.readInt("Channel (1-999): ", SmartTV.MIN_CHANNEL, SmartTV.MAX_CHANNEL));
                    logControl(d, "channel set to " + tv.getChannel());
                    break;
                case OPT_COLOUR:
                    Light light = (Light) d;
                    light.setColour(chooseOption("Colour", Light.getColourOptions(), light.getColour()));
                    logControl(d, "colour set to " + light.getColour());
                    break;
                default:
                    controlLock((DoorLock) d, code); // OPT_LOCK, OPT_UNLOCK, OPT_CHANGE_PIN
                    break;
            }
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void setLevel(Adjustable a) throws InvalidInputException {
        int level = InputHelper.readInt("Set " + a.getLevelName() + " (" + a.getMinLevel() + "-"
                + a.getMaxLevel() + ") [now " + a.getLevel() + "]: ", a.getMinLevel(), a.getMaxLevel());
        a.setLevel(level);
        logControl((Device) a, a.getLevelName().toLowerCase() + " set to " + level);
    }

    private static void warnIfLockPoweredOff(Device d) {
        if (d instanceof DoorLock && !d.isOn()) {
            System.out.println("Lock powered off: it stays " + (((DoorLock) d).isLocked() ? "LOCKED" : "UNLOCKED")
                    + " but cannot be controlled until powered on.");
        }
    }

    private static void controlLock(DoorLock lock, int code) throws InvalidInputException {
        if (code == OPT_LOCK) {
            lock.lock();
            logControl(lock, "LOCKED");
            return;
        }
        if (!lock.isOn()) {
            System.out.println(lock.getName() + " is powered off; turn it on first.");
            return;
        }
        if (lock.isKeypadLocked()) {
            System.out.println("Keypad is locked after " + DoorLock.MAX_ATTEMPTS
                    + " wrong PINs. It resets when the program restarts.");
            return;
        }
        if (code == OPT_UNLOCK) {
            unlockWithPin(lock);
        } else {
            int oldPin = InputHelper.readInt("Current PIN: ", 0, DoorLock.MAX_PIN);
            int newPin = InputHelper.readInt("New PIN (1000-9999): ", DoorLock.MIN_PIN, DoorLock.MAX_PIN);
            try {
                lock.changePin(oldPin, newPin);
            } catch (InvalidInputException e) {
                logControl(lock, "PIN change refused");
                throw e;
            }
            System.out.println("PIN changed.");
            logControl(lock, "PIN changed");
        }
    }

    private static void unlockWithPin(DoorLock lock) throws InvalidInputException {
        int pin = InputHelper.readInt("Enter PIN: ", 0, DoorLock.MAX_PIN);
        if (lock.unlock(pin)) {
            System.out.println("Unlocked.");
            logControl(lock, "UNLOCKED");
        } else if (lock.isKeypadLocked()) {
            System.out.println("Wrong PIN. The keypad is now LOCKED after " + DoorLock.MAX_ATTEMPTS
                    + " wrong attempts.");
            logControl(lock, "keypad locked after " + DoorLock.MAX_ATTEMPTS + " wrong PINs");
        } else {
            System.out.println("Wrong PIN. " + lock.getRemainingAttempts() + " attempt(s) left.");
            logControl(lock, "wrong PIN entered (" + lock.getRemainingAttempts() + " attempt(s) left)");
        }
    }

    private static void logControl(Device d, String what) {
        fileManager.appendLog("CONTROL", d.getId() + " " + d.getName() + " " + what);
    }

    // ================================================================ scenes

    private static void manageScenes() {
        String[] items = {"Create scene", "List scenes", "Run scene", "Delete scene"};
        while (true) {
            printSubMenu("SCENES", items);
            int choice = InputHelper.readInt("Choose an option: ", 0, items.length);
            switch (choice) {
                case 1:
                    createScene();
                    break;
                case 2:
                    listScenes();
                    break;
                case 3:
                    runScene();
                    break;
                case 4:
                    deleteScene();
                    break;
                default:
                    return; // 0 = Back
            }
        }
    }

    private static void createScene() {
        if (home.getScenes().length == Home.MAX_SCENES) {
            System.out.println("The home already has the maximum of " + Home.MAX_SCENES + " scenes.");
            return;
        }
        if (home.getTotalDeviceCount() == 0) {
            System.out.println("Add some devices before creating a scene.");
            return;
        }
        String name = InputHelper.readName("Scene name: ");
        while (sceneExists(name)) {
            System.out.println("A scene named '" + name + "' already exists. Choose another name.");
            name = InputHelper.readName("Scene name: ");
        }
        try {
            Scene scene = new Scene(name);
            if (editSceneSteps(scene)) {
                home.addScene(scene);
                System.out.println("Scene '" + name + "' saved with " + plural(scene.getActionCount(), "step") + ".");
                fileManager.appendLog("SCENE", "Scene " + name + " created with "
                        + plural(scene.getActionCount(), "step"));
            } else {
                System.out.println("Scene discarded.");
            }
        } catch (InvalidInputException | CapacityExceededException e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean sceneExists(String name) {
        try {
            home.findScene(name);
            return true;
        } catch (InvalidInputException e) {
            return false;
        }
    }

    /** Step editor; returns true to save the scene, false to discard it. */
    private static boolean editSceneSteps(Scene scene) throws InvalidInputException, CapacityExceededException {
        String[] items = {"Add a step", "Remove a step", "Finish and save scene"};
        while (true) {
            if (scene.getActionCount() == Scene.MAX_ACTIONS) {
                System.out.println("Maximum of " + Scene.MAX_ACTIONS + " steps reached.");
                return true;
            }
            System.out.println();
            System.out.println("Scene '" + scene.getName() + "' has " + plural(scene.getActionCount(), "step") + ".");
            for (int i = 0; i < items.length; i++) {
                System.out.println(" " + (i + 1) + ". " + items[i]);
            }
            System.out.println(" 0. Cancel (discard scene)");
            int choice = InputHelper.readInt("Choose an option: ", 0, items.length);
            if (choice == 0) {
                return false;
            } else if (choice == 1) {
                addSceneStep(scene);
            } else if (choice == 2) {
                removeSceneStep(scene);
            } else if (scene.getActionCount() == 0) {
                System.out.println("A scene needs at least 1 step.");
            } else {
                return true;
            }
        }
    }

    private static void addSceneStep(Scene scene) throws InvalidInputException, CapacityExceededException {
        Device d = askForDevice("Device ID for this step");
        if (d == null) {
            return;
        }
        ActionType type = chooseActionFor(d);
        int value = 0;
        if (type == ActionType.SET_LEVEL) {
            Adjustable a = (Adjustable) d;
            value = InputHelper.readInt(a.getLevelName() + " (" + a.getMinLevel() + "-" + a.getMaxLevel() + "): ",
                    a.getMinLevel(), a.getMaxLevel());
        }
        SceneAction step = new SceneAction(d.getId(), type, value);
        scene.addAction(step);
        System.out.println("Step " + scene.getActionCount() + " added: " + describeStep(step));
    }

    /** Offers only the actions that make sense for this kind of device. */
    private static ActionType chooseActionFor(Device d) {
        ActionType[] options = new ActionType[ActionType.values().length];
        int n = 0;
        options[n++] = ActionType.TURN_ON;
        options[n++] = ActionType.TURN_OFF;
        options[n++] = ActionType.TOGGLE;
        if (d instanceof Adjustable) {
            options[n++] = ActionType.SET_LEVEL;
        }
        if (d instanceof DoorLock) {
            options[n++] = ActionType.LOCK;
        }
        for (int i = 0; i < n; i++) {
            System.out.println("   " + (i + 1) + ". " + options[i]);
        }
        int choice = InputHelper.readInt("Action (1-" + n + "): ", 1, n);
        return options[choice - 1];
    }

    private static void removeSceneStep(Scene scene) throws InvalidInputException {
        if (scene.getActionCount() == 0) {
            System.out.println("The scene has no steps yet.");
            return;
        }
        printSteps(scene);
        int number = InputHelper.readInt("Step number to remove (0 to cancel): ", 0, scene.getActionCount());
        if (number > 0) {
            SceneAction removed = scene.removeAction(number - 1);
            System.out.println("Removed step: " + describeStep(removed));
        }
    }

    /** "L001 Ceiling Light : TURN_OFF", or "(missing device)" if the ID no longer exists. */
    private static String describeStep(SceneAction step) {
        String deviceName;
        try {
            deviceName = home.findDeviceAnywhere(step.getDeviceId()).getName();
        } catch (DeviceNotFoundException e) {
            deviceName = "(missing device)";
        }
        return step.getDeviceId() + " " + deviceName + " : " + step.describe();
    }

    private static void printSteps(Scene scene) {
        SceneAction[] steps = scene.getActions();
        for (int i = 0; i < steps.length; i++) {
            System.out.println("    " + (i + 1) + ". " + describeStep(steps[i]));
        }
    }

    private static void listScenes() {
        Scene[] scenes = home.getScenes();
        if (scenes.length == 0) {
            System.out.println("There are no scenes yet.");
            return;
        }
        for (int i = 0; i < scenes.length; i++) {
            System.out.println(" " + (i + 1) + ". " + scenes[i].getName() + " ("
                    + plural(scenes[i].getActionCount(), "step") + ")");
            printSteps(scenes[i]);
        }
    }

    /** Shows the numbered scene list and returns the chosen scene, or null. */
    private static Scene pickScene() {
        Scene[] scenes = home.getScenes();
        if (scenes.length == 0) {
            System.out.println("There are no scenes yet.");
            return null;
        }
        for (int i = 0; i < scenes.length; i++) {
            System.out.println(" " + (i + 1) + ". " + scenes[i].getName());
        }
        int choice = InputHelper.readInt("Scene number (0 to cancel): ", 0, scenes.length);
        return (choice == 0) ? null : scenes[choice - 1];
    }

    private static void runScene() {
        Scene scene = pickScene();
        if (scene == null) {
            return;
        }
        System.out.println("Running '" + scene.getName() + "'...");
        System.out.println(scene.run(home));
        fileManager.appendLog("SCENE", scene.getName() + " ran: " + scene.getLastSuccessCount() + " of "
                + scene.getActionCount() + " steps succeeded");
    }

    private static void deleteScene() {
        Scene scene = pickScene();
        if (scene == null || !InputHelper.readYesNo("Delete scene '" + scene.getName() + "'? (y/n): ")) {
            return;
        }
        try {
            home.removeScene(scene.getName());
            System.out.println("Scene '" + scene.getName() + "' deleted.");
            fileManager.appendLog("SCENE", "Scene " + scene.getName() + " deleted");
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
        }
    }

    // ========================================================= energy report

    private static void energyReport() {
        String[] items = {"Live power of a device", "Live power of a room", "Live power of the whole home",
            "Energy and cost of a device for N hours", "Energy and cost of a room for N hours",
            "Energy and cost of the whole home for N hours", "Monthly estimate for the home (flat vs slab)",
            "Change electricity rate (this session)", "Show current rate"};
        while (true) {
            printSubMenu("ENERGY REPORT", items);
            int choice = InputHelper.readInt("Choose an option: ", 0, items.length);
            if (choice == 0) {
                return;
            }
            handleEnergyChoice(choice);
        }
    }

    private static void handleEnergyChoice(int choice) {
        switch (choice) {
            case 1:
                Device d = askForDevice("Device ID");
                if (d != null) {
                    System.out.printf("Live power of %s %s: %.1f W%n", d.getId(), d.getName(), d.getCurrentPowerWatts());
                }
                break;
            case 2:
                Room room = pickRoom();
                if (room != null) {
                    printRoomTable(room);
                }
                break;
            case 3:
                for (Room r : home.getRooms()) {
                    System.out.printf("  %-20s %8.1f W%n", r.getName(), r.getTotalPowerWatts());
                }
                System.out.printf("  %-20s %8.1f W%n", "WHOLE HOME", home.getTotalPowerWatts());
                break;
            case 4:
            case 5:
            case 6:
                energyForHours(choice);
                break;
            case 7:
                monthlyEstimate();
                break;
            case 8:
                changeRate();
                break;
            default: // 9
                System.out.println("Current rate: " + money(energyCalculator.getRatePerKwh()) + " per kWh"
                        + " (default " + money(EnergyCalculator.DEFAULT_RATE_PER_KWH) + ").");
                break;
        }
    }

    /** Options 4, 5 and 6: uses the three overloaded calculateKwh methods. */
    private static void energyForHours(int choice) {
        String label;
        double kwh;
        if (choice == 4) {
            Device d = askForDevice("Device ID");
            if (d == null) {
                return;
            }
            double hours = readHours();
            kwh = energyCalculator.calculateKwh(d, hours);
            label = d.getId() + " " + d.getName() + " for " + hours + " h";
        } else if (choice == 5) {
            Room room = pickRoom();
            if (room == null) {
                return;
            }
            double hours = readHours();
            kwh = energyCalculator.calculateKwh(room, hours);
            label = room.getName() + " for " + hours + " h";
        } else {
            double hours = readHours();
            kwh = energyCalculator.calculateKwh(home, hours);
            label = home.getName() + " for " + hours + " h";
        }
        System.out.printf("%s: %.3f kWh, cost %s at %s per kWh%n", label, kwh,
                money(energyCalculator.calculateCost(kwh)), money(energyCalculator.getRatePerKwh()));
    }

    private static double readHours() {
        return InputHelper.readDouble("Number of hours (0-" + (int) MAX_REPORT_HOURS + "): ", 0, MAX_REPORT_HOURS);
    }

    private static void monthlyEstimate() {
        double hoursPerDay = InputHelper.readOptionalDouble("Average hours per day (0-24) [default 8]: ", 0, 24, 8);
        double kwh = energyCalculator.monthlyKwh(home, hoursPerDay);
        double flat = energyCalculator.calculateCost(kwh);
        double slab = energyCalculator.calculateSlabBill(kwh);
        System.out.println();
        System.out.printf("Monthly estimate for %s: current load %.1f W x %.1f h/day x %d days%n",
                home.getName(), home.getTotalPowerWatts(), hoursPerDay, EnergyCalculator.DAYS_PER_MONTH);
        System.out.printf("  %-14s %-26s %s%n", "Units (kWh)", "Flat @ " + money(energyCalculator.getRatePerKwh())
                + "/kWh", "Slab tariff*");
        System.out.printf("  %-14s %-26s %s%n", String.format("%,.2f", kwh), money(flat), money(slab));
        System.out.println("  * Illustrative slab tariff: 0-100 units Rs. 3, 101-300 Rs. 5, 301-500 Rs. 7,");
        System.out.println("    above 500 Rs. 9 per unit, plus a fixed charge of "
                + money(EnergyCalculator.SLAB_FIXED_CHARGE) + ".");
    }

    private static void changeRate() {
        double oldRate = energyCalculator.getRatePerKwh();
        double newRate = InputHelper.readDouble("New rate in Rs. per kWh (0.01-100): ", 0.01,
                EnergyCalculator.MAX_RATE_PER_KWH);
        try {
            energyCalculator.setRatePerKwh(newRate);
            System.out.println("Rate changed to " + money(newRate) + " per kWh for this session.");
            fileManager.appendLog("ENERGY", "Rate changed from " + money(oldRate) + " to " + money(newRate)
                    + " per kWh");
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
        }
    }

    // ============================================================ log & save

    private static void showLog() {
        int n = InputHelper.readOptionalInt("How many recent entries (1-100) [default 20]: ", 1, 100, 20);
        try {
            String[] lines = fileManager.readLastLogLines(n);
            if (lines.length == 0) {
                System.out.println("The activity log is empty.");
            }
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Could not read the activity log: " + e.getMessage());
        }
        InputHelper.pause();
    }

    private static void saveNow() {
        if (saveAll()) {
            System.out.println("Saved: " + counts() + ".");
        }
    }

    /** Writes the home and scenes; returns false (after explaining why) if saving failed. */
    private static boolean saveAll() {
        try {
            fileManager.saveHome(home);
            fileManager.saveScenes(home);
            fileManager.appendLog("SYSTEM", "State saved (" + counts() + ")");
            return true;
        } catch (IOException e) {
            System.out.println("Could not save: " + e.getMessage());
            return false;
        }
    }

    private static void resetToDemo() {
        if (!InputHelper.readYesNo("Replace the current home with the demo home? (y/n): ")) {
            System.out.println("Nothing changed.");
            return;
        }
        home = DemoData.buildDemoHome();
        System.out.println("Demo home restored: " + counts() + ". (Use 8 to save it now, or it is saved on exit.)");
        fileManager.appendLog("SYSTEM", "Reset to demo home");
    }

    private static void saveAndExit() {
        boolean saved = saveAll();
        fileManager.appendLog("SYSTEM", "Program exited");
        System.out.println(saved ? "State saved. Goodbye!" : "Goodbye! (the state could NOT be saved)");
    }

    // =============================================================== helpers

    /** "3 rooms, 10 devices, 3 scenes" for the current home. */
    private static String counts() {
        return plural(home.getRooms().length, "room") + ", " + plural(home.getTotalDeviceCount(), "device")
                + ", " + plural(home.getScenes().length, "scene");
    }

    private static String plural(int n, String word) {
        return n + " " + word + (n == 1 ? "" : "s");
    }

    private static String money(double rupees) {
        return "Rs. " + String.format("%,.2f", rupees);
    }
}
