package com.smarthome.util;

/**
 * Generates unique, readable device IDs such as {@code L001} or {@code AC012}.
 *
 * <p>OOP concepts: <b>static members</b> (the counters belong to the class,
 * not to any object, so every part of the program shares one sequence per
 * device type) and <b>arrays</b> (a {@code String[]} of prefixes with a
 * parallel {@code int[]} of counters instead of a collection). The class
 * cannot be instantiated: its constructor is private.</p>
 */
public final class IdGenerator {

    /** Valid ID prefixes: Light, Fan, AirConditioner, SmartTV, DoorLock. */
    private static final String[] PREFIXES = {"L", "F", "AC", "TV", "DL"};

    /** counters[i] is the last number handed out for PREFIXES[i]. */
    private static final int[] COUNTERS = new int[PREFIXES.length];

    private IdGenerator() {
        // utility class: no objects
    }

    /**
     * Increments the counter for the given prefix and returns the next ID.
     *
     * @param prefix one of L, F, AC, TV, DL
     * @return the new ID, always with at least 3 zero-padded digits (e.g. L001)
     * @throws IllegalArgumentException if the prefix is unknown
     */
    public static String nextId(String prefix) {
        int index = indexOfPrefix(prefix);
        COUNTERS[index]++;
        return String.format("%s%03d", PREFIXES[index], COUNTERS[index]);
    }

    /**
     * Records an ID that was loaded from a file so that new IDs never collide
     * with it: after registering {@code L007} the next light gets {@code L008}.
     *
     * @param id an existing ID such as L007
     * @throws IllegalArgumentException if the prefix is unknown or the number part is invalid
     */
    public static void registerExisting(String id) {
        String prefix = prefixOf(id);
        String digits = id.substring(prefix.length());
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("ID '" + id + "' has no number part");
        }
        // Integer.parseInt would also accept "+7" or "-7", so check every character first
        for (int i = 0; i < digits.length(); i++) {
            if (!Character.isDigit(digits.charAt(i))) {
                throw new IllegalArgumentException("ID '" + id + "' has an invalid number part");
            }
        }
        int number = Integer.parseInt(digits);
        int index = indexOfPrefix(prefix);
        if (number > COUNTERS[index]) {
            COUNTERS[index] = number;
        }
    }

    /**
     * Returns the letter prefix of an ID, for example {@code AC} for {@code AC012}.
     *
     * @param id the ID to inspect
     * @return the leading letters of the ID
     * @throws IllegalArgumentException if the ID is null or its prefix is not a known prefix
     */
    public static String prefixOf(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID is missing");
        }
        int end = 0;
        while (end < id.length() && Character.isLetter(id.charAt(end))) {
            end++;
        }
        String prefix = id.substring(0, end);
        indexOfPrefix(prefix); // validates the prefix
        return prefix;
    }

    /** Sets every counter back to 0 (used by tests and by "Reset to demo home"). */
    public static void reset() {
        for (int i = 0; i < COUNTERS.length; i++) {
            COUNTERS[i] = 0;
        }
    }

    /** Linear search of the prefix table; unknown prefix is a programming error. */
    private static int indexOfPrefix(String prefix) {
        for (int i = 0; i < PREFIXES.length; i++) {
            if (PREFIXES[i].equals(prefix)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unknown ID prefix '" + prefix + "'");
    }
}
