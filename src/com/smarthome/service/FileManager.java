package com.smarthome.service;

import com.smarthome.exception.CapacityExceededException;
import com.smarthome.exception.DeviceNotFoundException;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Saves and loads the home, its scenes and the activity log as plain text files.
 *
 * <p>OOP concepts: <b>file handling</b> (write with {@link BufferedWriter},
 * read with {@link BufferedReader}, append with {@code FileWriter(file, true)}),
 * <b>exception handling</b> (try-with-resources closes every file
 * automatically; multi-catch skips a corrupt line without stopping the load),
 * <b>polymorphism</b> (each device writes its own line through its overridden
 * {@code toFileString()}), and a <b>classic switch on strings</b> to pick the
 * right loading constructor.</p>
 */
public class FileManager {

    /** Folder used by the real program. */
    public static final String DEFAULT_DIRECTORY = "data";
    /** File holding rooms and devices. */
    public static final String HOME_FILE = "home_state.txt";
    /** File holding scenes and their steps. */
    public static final String SCENES_FILE = "scenes.txt";
    /** Append-only activity log. */
    public static final String LOG_FILE = "activity_log.txt";

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final File directory;

    /** Creates a file manager that uses the {@code data} folder. */
    public FileManager() {
        this(DEFAULT_DIRECTORY);
    }

    /**
     * Creates a file manager for the given folder, creating the folder if it is missing.
     *
     * @param baseDirectory folder to keep the files in (tests pass a temporary folder)
     */
    public FileManager(String baseDirectory) {
        this.directory = new File(baseDirectory);
        if (!directory.exists() && !directory.mkdirs()) {
            System.out.println("Warning: could not create folder " + directory.getPath());
        }
    }

    // ------------------------------------------------------------------ saving

    /**
     * Writes the home, its rooms and devices to home_state.txt, replacing the old file.
     *
     * @param h the home to save
     * @throws IOException if the file cannot be written
     */
    public void saveHome(Home h) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(directory, HOME_FILE)))) {
            writeLine(writer, "HOME|" + h.getName());
            for (Room room : h.getRooms()) {
                writeLine(writer, "ROOM|" + room.getName());
                for (Device d : room.getDevices()) {
                    writeLine(writer, d.toFileString()); // each subclass writes its own fields
                }
            }
        }
    }

    /**
     * Writes every scene and its steps to scenes.txt, replacing the old file.
     *
     * @param h the home whose scenes are saved
     * @throws IOException if the file cannot be written
     */
    public void saveScenes(Home h) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(directory, SCENES_FILE)))) {
            for (Scene scene : h.getScenes()) {
                writeLine(writer, "SCENE|" + scene.getName());
                for (SceneAction action : scene.getActions()) {
                    writeLine(writer, action.toFileString());
                }
            }
        }
    }

    // ----------------------------------------------------------------- loading

    /**
     * Reads home_state.txt. Corrupt lines are skipped with a warning and the rest is loaded.
     *
     * @return the loaded home, or null if there is no save file (or it has no valid HOME line)
     * @throws IOException if the file exists but cannot be read
     */
    public Home loadHome() throws IOException {
        File file = new File(directory, HOME_FILE);
        if (!file.exists()) {
            return null;
        }
        Home home = null;
        Room currentRoom = null;
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                // limit -1 keeps empty trailing fields, so "a|b|" has 3 fields, not 2
                String[] fields = line.split("\\|", -1);
                try {
                    switch (fields[0]) {
                        case "HOME":
                            if (home != null) {
                                throw new InvalidInputException("a second HOME line");
                            }
                            expectFields(fields, 2);
                            home = new Home(fields[1]);
                            break;
                        case "ROOM":
                            currentRoom = null; // devices after a bad ROOM line must not join the previous room
                            if (home == null) {
                                throw new InvalidInputException("ROOM line before the HOME line");
                            }
                            expectFields(fields, 2);
                            Room room = new Room(fields[1]);
                            home.addRoom(room);
                            currentRoom = room;
                            break;
                        case "DEVICE":
                            if (currentRoom == null) {
                                throw new InvalidInputException("DEVICE line without a valid ROOM line before it");
                            }
                            currentRoom.addDevice(parseDevice(fields, home));
                            break;
                        default:
                            throw new InvalidInputException("unknown record type '" + fields[0] + "'");
                    }
                } catch (InvalidInputException | CapacityExceededException e) {
                    warnSkipped(lineNumber, HOME_FILE, e.getMessage());
                } catch (NumberFormatException e) {
                    warnSkipped(lineNumber, HOME_FILE, "bad number: " + e.getMessage());
                }
            }
        }
        if (home == null) {
            System.out.println("Warning: " + HOME_FILE + " has no valid HOME line, so it was ignored.");
        }
        return home;
    }

    /**
     * Reads scenes.txt into the given home. Corrupt lines (including unknown
     * action types) are skipped with a warning; a scene left with no valid
     * steps is skipped as well.
     *
     * @param h the home to add the scenes to
     * @throws IOException if the file exists but cannot be read
     */
    public void loadScenes(Home h) throws IOException {
        File file = new File(directory, SCENES_FILE);
        if (!file.exists()) {
            return;
        }
        Scene pending = null;   // scene being read; added to the home once all its steps are read
        int pendingLine = 0;
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = line.split("\\|", -1);
                try {
                    switch (fields[0]) {
                        case "SCENE":
                            commitScene(h, pending, pendingLine);
                            pending = null; // ACTION lines after a bad SCENE line are skipped
                            expectFields(fields, 2);
                            pending = new Scene(fields[1]);
                            pendingLine = lineNumber;
                            break;
                        case "ACTION":
                            if (pending == null) {
                                throw new InvalidInputException("ACTION line without a valid SCENE line before it");
                            }
                            expectFields(fields, 4);
                            ActionType type = parseActionType(fields[2]);
                            pending.addAction(new SceneAction(fields[1], type, Integer.parseInt(fields[3])));
                            break;
                        default:
                            throw new InvalidInputException("unknown record type '" + fields[0] + "'");
                    }
                } catch (InvalidInputException | CapacityExceededException e) {
                    warnSkipped(lineNumber, SCENES_FILE, e.getMessage());
                } catch (NumberFormatException e) {
                    warnSkipped(lineNumber, SCENES_FILE, "bad number: " + e.getMessage());
                }
            }
        }
        commitScene(h, pending, pendingLine);
    }

    // --------------------------------------------------------------------- log

    /**
     * Appends one line to the activity log, e.g.
     * {@code 2026-10-08 21:14:03 | CONTROL | L001 Ceiling Light turned OFF}.
     * A logging failure only prints a warning; it never stops the program.
     *
     * @param category SYSTEM, ROOM, DEVICE, CONTROL, SCENE or ENERGY
     * @param message what happened
     */
    public void appendLog(String category, String message) {
        String line = LocalDateTime.now().format(TIMESTAMP) + " | " + String.format("%-7s", category)
                + " | " + message.replace('\n', ' ').replace('\r', ' ');
        // the second FileWriter argument (true) means append instead of overwrite
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(directory, LOG_FILE), true))) {
            writeLine(writer, line);
        } catch (IOException e) {
            System.out.println("Warning: could not write to the activity log (" + e.getMessage() + ").");
        }
    }

    /**
     * Returns the last n lines of the activity log, oldest first.
     *
     * @param n how many lines (1 or more)
     * @return up to n lines; an empty array if the log does not exist yet
     * @throws IOException if the log exists but cannot be read
     */
    public String[] readLastLogLines(int n) throws IOException {
        File file = new File(directory, LOG_FILE);
        if (n <= 0 || !file.exists()) {
            return new String[0];
        }
        // circular buffer: line number k goes into slot k % n, overwriting the oldest
        String[] buffer = new String[n];
        int total = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer[total % n] = line;
                total++;
            }
        }
        int size = Math.min(total, n);
        int start = (total > n) ? total % n : 0; // slot holding the oldest kept line
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = buffer[(start + i) % n];
        }
        return result;
    }

    // ----------------------------------------------------------------- helpers

    /** Builds a device from the fields of a DEVICE line using its loading constructor. */
    private Device parseDevice(String[] f, Home home) throws InvalidInputException {
        // DEVICE|type|id|name|isOn|rating|...type-specific fields
        if (f.length < 6) {
            throw new InvalidInputException("expected at least 6 fields but found " + f.length);
        }
        String type = f[1];
        String id = f[2];
        if (isIdTaken(home, id)) {
            throw new InvalidInputException("duplicate device ID " + id);
        }
        switch (type) {
            case "LIGHT":
                expectFields(f, 8);
                checkPrefix(id, Light.ID_PREFIX, type);
                return new Light(id, f[3], parseBoolean(f[4]), Double.parseDouble(f[5]),
                        Integer.parseInt(f[6]), f[7]);
            case "FAN":
                expectFields(f, 7);
                checkPrefix(id, Fan.ID_PREFIX, type);
                return new Fan(id, f[3], parseBoolean(f[4]), Double.parseDouble(f[5]), Integer.parseInt(f[6]));
            case "AC":
                expectFields(f, 8);
                checkPrefix(id, AirConditioner.ID_PREFIX, type);
                return new AirConditioner(id, f[3], parseBoolean(f[4]), Double.parseDouble(f[5]),
                        Integer.parseInt(f[6]), f[7]);
            case "TV":
                expectFields(f, 8);
                checkPrefix(id, SmartTV.ID_PREFIX, type);
                return new SmartTV(id, f[3], parseBoolean(f[4]), Double.parseDouble(f[5]),
                        Integer.parseInt(f[6]), Integer.parseInt(f[7]));
            case "LOCK":
                expectFields(f, 8);
                checkPrefix(id, DoorLock.ID_PREFIX, type);
                return new DoorLock(id, f[3], parseBoolean(f[4]), Double.parseDouble(f[5]),
                        parseBoolean(f[6]), Integer.parseInt(f[7]));
            default:
                throw new InvalidInputException("unknown device type '" + type + "'");
        }
    }

    /** Adds a fully read scene to the home, or warns and skips it. */
    private void commitScene(Home h, Scene scene, int sceneLine) {
        if (scene == null) {
            return;
        }
        if (scene.getActionCount() == 0) {
            warnSkipped(sceneLine, SCENES_FILE, "scene '" + scene.getName() + "' has no valid steps");
            return;
        }
        try {
            h.addScene(scene);
        } catch (InvalidInputException | CapacityExceededException e) {
            warnSkipped(sceneLine, SCENES_FILE, e.getMessage());
        }
    }

    private static boolean isIdTaken(Home home, String id) {
        try {
            home.findDeviceAnywhere(id);
            return true;
        } catch (DeviceNotFoundException e) {
            return false;
        }
    }

    private static void checkPrefix(String id, String expectedPrefix, String type) throws InvalidInputException {
        String prefix;
        try {
            prefix = IdGenerator.prefixOf(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("invalid device ID '" + id + "'");
        }
        if (!prefix.equals(expectedPrefix)) {
            throw new InvalidInputException("ID " + id + " does not belong to a " + type);
        }
    }

    private static void expectFields(String[] fields, int expected) throws InvalidInputException {
        if (fields.length != expected) {
            throw new InvalidInputException("expected " + expected + " fields but found " + fields.length);
        }
    }

    /** Stricter than Boolean.parseBoolean alone, which treats every typo as false. */
    private static boolean parseBoolean(String text) throws InvalidInputException {
        if (!text.equals("true") && !text.equals("false")) {
            throw new InvalidInputException("'" + text + "' is not true or false");
        }
        return Boolean.parseBoolean(text);
    }

    private static ActionType parseActionType(String text) throws InvalidInputException {
        try {
            return ActionType.valueOf(text);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("unknown action type '" + text + "'");
        }
    }

    private static void warnSkipped(int lineNumber, String fileName, String reason) {
        System.out.println("Warning: skipped line " + lineNumber + " of " + fileName + " (" + reason + ")");
    }

    private static void writeLine(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }
}
