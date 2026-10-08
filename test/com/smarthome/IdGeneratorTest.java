package com.smarthome;

import static com.smarthome.TestRunner.assertEquals;
import static com.smarthome.TestRunner.assertThrows;
import static com.smarthome.TestRunner.test;

import com.smarthome.model.Light;
import com.smarthome.service.FileManager;
import com.smarthome.util.IdGenerator;

import java.io.File;

/** Tests for {@link IdGenerator}: format, separate counters, reset and loaded IDs. */
public final class IdGeneratorTest {

    private IdGeneratorTest() {
        // static tests only
    }

    /** Runs every ID test. */
    public static void runAll() {
        test("IDs are zero-padded to 3 digits with a counter per prefix", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                assertEquals("first light", "L001", IdGenerator.nextId("L"));
                assertEquals("second light", "L002", IdGenerator.nextId("L"));
                assertEquals("first AC", "AC001", IdGenerator.nextId("AC"));
                IdGenerator.registerExisting("AC011");
                assertEquals("after AC011", "AC012", IdGenerator.nextId("AC"));
                IdGenerator.registerExisting("L001"); // lower than the counter: no change
                assertEquals("counter never goes down", "L003", IdGenerator.nextId("L"));
                IdGenerator.reset();
                assertEquals("after reset", "DL001", IdGenerator.nextId("DL"));
            }
        });

        test("Unknown prefixes and malformed IDs throw IllegalArgumentException", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                assertThrows("prefix X", IllegalArgumentException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        IdGenerator.nextId("X");
                    }
                });
                assertThrows("no number", IllegalArgumentException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        IdGenerator.registerExisting("TV");
                    }
                });
                assertThrows("letters in number", IllegalArgumentException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        IdGenerator.registerExisting("F0A1");
                    }
                });
            }
        });

        test("After loading a file containing L007, a new Light gets L008", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                File dir = new File(TestRunner.TEST_DIR, "ids");
                TestRunner.deleteRecursively(dir);
                FileManager files = new FileManager(dir.getPath());
                TestRunner.writeLines(new File(dir, FileManager.HOME_FILE), new String[] {
                    "HOME|Test Home",
                    "ROOM|Hall",
                    "DEVICE|LIGHT|L007|Old Lamp|false|10.0|100|Warm White"
                });
                files.loadHome();
                assertEquals("next light", "L008", new Light("New Lamp").getId());
            }
        });
    }
}
