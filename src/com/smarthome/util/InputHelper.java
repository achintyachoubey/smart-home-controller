package com.smarthome.util;

import com.smarthome.exception.InvalidInputException;

import java.util.Scanner;

/**
 * Reads and validates everything the user types, so that bad input never
 * crashes the program. Every method loops until it gets a valid value.
 *
 * <p>OOP concepts: <b>static members</b> (one shared {@link Scanner} for the
 * whole program), <b>encapsulation</b> (all console reading is hidden behind
 * these methods), <b>exception handling</b> ({@code NumberFormatException}
 * is caught and turned into a friendly message) and a <b>nested class</b>
 * ({@link InputClosedException}) used to signal end of input.</p>
 */
public final class InputHelper {

    /** Longest name allowed for homes, rooms, devices and scenes. */
    public static final int MAX_NAME_LENGTH = 30;

    private static final Scanner SCANNER = new Scanner(System.in);

    private InputHelper() {
        // utility class: no objects
    }

    /**
     * Unchecked signal thrown when the input stream has ended (Ctrl+D / Ctrl+Z
     * or a piped file running out). {@code Main} catches it to save and exit.
     */
    public static class InputClosedException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        /** Creates the signal with a fixed message. */
        public InputClosedException() {
            super("End of input reached");
        }
    }

    /**
     * Checks a name against the shared naming rules and returns it trimmed.
     * Used both by the console prompts and by the model setters, so the rule
     * lives in exactly one place.
     *
     * @param name the name to check
     * @return the trimmed name
     * @throws InvalidInputException if the name is empty, too long or contains '|'
     */
    public static String validateName(String name) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Name cannot be empty.");
        }
        String trimmed = name.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new InvalidInputException("Name must be at most " + MAX_NAME_LENGTH + " characters.");
        }
        if (trimmed.indexOf('|') >= 0) {
            throw new InvalidInputException("Name cannot contain the '|' character.");
        }
        return trimmed;
    }

    /**
     * Reads a whole number between min and max (inclusive).
     *
     * @param prompt text shown before the cursor
     * @param min smallest allowed value
     * @param max largest allowed value
     * @return the number entered
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            String text = readLine(prompt);
            Integer value = parseIntInRange(text, min, max);
            if (value != null) {
                return value;
            }
        }
    }

    /**
     * Reads a whole number between min and max, returning a default on empty input.
     *
     * @param prompt text shown before the cursor (should mention the default)
     * @param min smallest allowed value
     * @param max largest allowed value
     * @param defaultValue value returned when the user just presses Enter
     * @return the number entered, or the default
     */
    public static int readOptionalInt(String prompt, int min, int max, int defaultValue) {
        while (true) {
            String text = readLine(prompt);
            if (text.isEmpty()) {
                return defaultValue;
            }
            Integer value = parseIntInRange(text, min, max);
            if (value != null) {
                return value;
            }
        }
    }

    /**
     * Reads a decimal number between min and max (inclusive).
     *
     * @param prompt text shown before the cursor
     * @param min smallest allowed value
     * @param max largest allowed value
     * @return the number entered
     */
    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            String text = readLine(prompt);
            Double value = parseDoubleInRange(text, min, max);
            if (value != null) {
                return value;
            }
        }
    }

    /**
     * Reads a decimal number between min and max, returning a default on empty input.
     *
     * @param prompt text shown before the cursor (should mention the default)
     * @param min smallest allowed value
     * @param max largest allowed value
     * @param defaultValue value returned when the user just presses Enter
     * @return the number entered, or the default
     */
    public static double readOptionalDouble(String prompt, double min, double max, double defaultValue) {
        while (true) {
            String text = readLine(prompt);
            if (text.isEmpty()) {
                return defaultValue;
            }
            Double value = parseDoubleInRange(text, min, max);
            if (value != null) {
                return value;
            }
        }
    }

    /**
     * Reads a name: not empty, at most 30 characters, no '|'.
     *
     * @param prompt text shown before the cursor
     * @return the trimmed, valid name
     */
    public static String readName(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                return validateName(text);
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Reads a device ID, trimmed and upper-cased so that {@code l001} finds {@code L001}.
     *
     * @param prompt text shown before the cursor
     * @return the non-empty, upper-case ID
     */
    public static String readId(String prompt) {
        while (true) {
            String text = readLine(prompt);
            if (!text.isEmpty()) {
                return text.toUpperCase();
            }
            System.out.println("Please enter an ID.");
        }
    }

    /**
     * Reads a yes/no answer (y, yes, n or no in any case).
     *
     * @param prompt text shown before the cursor
     * @return true for yes, false for no
     */
    public static boolean readYesNo(String prompt) {
        while (true) {
            String text = readLine(prompt).toLowerCase();
            if (text.equals("y") || text.equals("yes")) {
                return true;
            }
            if (text.equals("n") || text.equals("no")) {
                return false;
            }
            System.out.println("Please answer y or n.");
        }
    }

    /** Waits until the user presses Enter. */
    public static void pause() {
        readLine("Press Enter to continue...");
    }

    /**
     * Prints the prompt and reads one trimmed line.
     * Throws {@link InputClosedException} when there is no more input.
     */
    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!SCANNER.hasNextLine()) {
            throw new InputClosedException();
        }
        return SCANNER.nextLine().trim();
    }

    /** Returns the parsed value, or prints why it is invalid and returns null. */
    private static Integer parseIntInRange(String text, int min, int max) {
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a whole number.");
            return null;
        }
        if (value < min || value > max) {
            System.out.println("Please enter a number between " + min + " and " + max + ".");
            return null;
        }
        return value;
    }

    /** Returns the parsed value, or prints why it is invalid and returns null. */
    private static Double parseDoubleInRange(String text, double min, double max) {
        double value;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number.");
            return null;
        }
        // NaN and Infinity parse successfully but are never valid amounts
        if (Double.isNaN(value) || Double.isInfinite(value) || value < min || value > max) {
            System.out.println("Please enter a number between " + formatPlain(min)
                    + " and " + formatPlain(max) + ".");
            return null;
        }
        return value;
    }

    /** Shows 24.0 as "24" but keeps 0.5 as "0.5", for friendlier messages. */
    private static String formatPlain(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
