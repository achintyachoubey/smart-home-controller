package com.smarthome;

import com.smarthome.util.IdGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A tiny home-made test framework (no JUnit) and the entry point that runs
 * every test class.
 *
 * <p>OOP concepts: <b>interfaces</b> ({@link ThrowingBlock}, implemented by
 * anonymous inner classes because lambdas are not used in this project),
 * <b>static members</b> (pass/fail counters shared by all tests),
 * <b>method overloading</b> (two {@code assertEquals} methods) and
 * <b>exception handling</b> (a failed assertion throws {@link TestFailure},
 * which is caught so the remaining tests still run).</p>
 */
public final class TestRunner {

    /** Temporary folder used by file tests; deleted before and after the run. */
    public static final String TEST_DIR = "test-data";

    private static int passed;
    private static int failed;

    private TestRunner() {
        // static helpers only
    }

    /** A piece of test code that may throw any exception. */
    public interface ThrowingBlock {
        /**
         * Runs the code.
         *
         * @throws Exception anything the code under test throws
         */
        void run() throws Exception;
    }

    /** Thrown by the assert methods when a check fails. */
    public static class TestFailure extends RuntimeException {

        private static final long serialVersionUID = 1L;

        /**
         * Creates a failure.
         *
         * @param message what was expected and what happened
         */
        public TestFailure(String message) {
            super(message);
        }
    }

    /**
     * Runs every test class and exits with status 1 if any test failed.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        File dir = new File(TEST_DIR);
        deleteRecursively(dir);
        System.out.println("Running Smart Home Controller tests...");
        System.out.println();
        DeviceTest.runAll();
        RoomHomeTest.runAll();
        SceneTest.runAll();
        EnergyCalculatorTest.runAll();
        FileManagerTest.runAll();
        IdGeneratorTest.runAll();
        deleteRecursively(dir);
        int total = passed + failed;
        System.out.println();
        System.out.println("Result: " + passed + " of " + total + " tests passed.");
        if (failed > 0) {
            System.exit(1);
        }
    }

    /**
     * Runs one named test after resetting the ID counters, and prints PASS or FAIL.
     *
     * @param name description of the test
     * @param body the test code
     */
    public static void test(String name, ThrowingBlock body) {
        IdGenerator.reset();
        try {
            body.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (TestFailure e) {
            failed++;
            System.out.println("FAIL  " + name + ": " + e.getMessage());
        } catch (Exception e) {
            failed++;
            System.out.println("FAIL  " + name + ": unexpected " + e);
        }
    }

    /**
     * Checks that two objects are equal (null-safe).
     *
     * @param name what is being checked
     * @param expected the expected value
     * @param actual the actual value
     */
    public static void assertEquals(String name, Object expected, Object actual) {
        boolean same = (expected == null) ? actual == null : expected.equals(actual);
        if (!same) {
            throw new TestFailure(name + ": expected <" + expected + "> but was <" + actual + ">");
        }
    }

    /**
     * Checks that two decimals are equal within a tolerance.
     *
     * @param name what is being checked
     * @param expected the expected value
     * @param actual the actual value
     * @param tolerance largest allowed difference
     */
    public static void assertEquals(String name, double expected, double actual, double tolerance) {
        if (Math.abs(expected - actual) > tolerance) {
            throw new TestFailure(name + ": expected <" + expected + "> but was <" + actual + ">");
        }
    }

    /**
     * Checks that a condition is true.
     *
     * @param name what is being checked
     * @param condition the condition
     */
    public static void assertTrue(String name, boolean condition) {
        if (!condition) {
            throw new TestFailure(name + ": expected true");
        }
    }

    /**
     * Checks that a condition is false.
     *
     * @param name what is being checked
     * @param condition the condition
     */
    public static void assertFalse(String name, boolean condition) {
        if (condition) {
            throw new TestFailure(name + ": expected false");
        }
    }

    /**
     * Checks that running the block throws an exception of the expected class.
     *
     * @param name what is being checked
     * @param expected the exception class that must be thrown
     * @param block code that should throw
     */
    public static void assertThrows(String name, Class<? extends Exception> expected, ThrowingBlock block) {
        try {
            block.run();
        } catch (Exception e) {
            if (expected.isInstance(e)) {
                return;
            }
            throw new TestFailure(name + ": expected " + expected.getSimpleName() + " but got " + e);
        }
        throw new TestFailure(name + ": expected " + expected.getSimpleName() + " but nothing was thrown");
    }

    /**
     * Writes lines to a file, replacing it (used to create hand-made save files).
     *
     * @param file the file to write
     * @param lines the lines
     * @throws IOException if writing fails
     */
    public static void writeLines(File file, String[] lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Deletes a file or a folder with everything inside it.
     *
     * @param file the file or folder
     */
    public static void deleteRecursively(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        if (file.exists() && !file.delete()) {
            System.out.println("Warning: could not delete " + file.getPath());
        }
    }
}
