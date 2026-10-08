package com.smarthome;

import static com.smarthome.TestRunner.assertEquals;
import static com.smarthome.TestRunner.assertTrue;
import static com.smarthome.TestRunner.test;

import com.smarthome.model.AirConditioner;
import com.smarthome.model.Device;
import com.smarthome.model.DoorLock;
import com.smarthome.model.Home;
import com.smarthome.model.Light;
import com.smarthome.model.Room;
import com.smarthome.model.Scene;
import com.smarthome.model.SceneAction;
import com.smarthome.model.SmartTV;
import com.smarthome.service.DemoData;
import com.smarthome.service.FileManager;

import java.io.File;

/**
 * Tests for {@link FileManager}: save/load round trip, corrupt lines,
 * and reading the end of the activity log. Every test works in its own
 * folder under {@code test-data/}, never in the real {@code data/} folder.
 */
public final class FileManagerTest {

    private FileManagerTest() {
        // static tests only
    }

    /** Creates a fresh, empty folder for one test and returns its path. */
    private static String freshDir(String name) {
        File dir = new File(TestRunner.TEST_DIR, name);
        TestRunner.deleteRecursively(dir);
        return dir.getPath();
    }

    /** Runs every file test. */
    public static void runAll() {
        test("File round trip: save and load reproduce every room, device field and scene step",
                new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Home original = DemoData.buildDemoHome();
                // change some values away from their defaults so the round trip proves they are saved
                ((Light) original.findDeviceAnywhere("L004")).setColour("Blue");
                ((AirConditioner) original.findDeviceAnywhere("AC001")).setMode("DRY");
                ((SmartTV) original.findDeviceAnywhere("TV001")).setChannel(250);
                DoorLock lock = (DoorLock) original.findDeviceAnywhere("DL001");
                lock.changePin(1234, 4321);
                lock.unlock(4321);
                original.findScene("Night Mode").removeAction(0);

                FileManager files = new FileManager(freshDir("roundtrip"));
                files.saveHome(original);
                files.saveScenes(original);
                Home loaded = files.loadHome();
                files.loadScenes(loaded);

                assertEquals("home name", original.getName(), loaded.getName());
                Room[] rooms1 = original.getRooms();
                Room[] rooms2 = loaded.getRooms();
                assertEquals("room count", rooms1.length, rooms2.length);
                for (int r = 0; r < rooms1.length; r++) {
                    assertEquals("room name " + r, rooms1[r].getName(), rooms2[r].getName());
                    Device[] d1 = rooms1[r].getDevices();
                    Device[] d2 = rooms2[r].getDevices();
                    assertEquals("device count in " + rooms1[r].getName(), d1.length, d2.length);
                    for (int i = 0; i < d1.length; i++) {
                        assertEquals("class of " + d1[i].getId(), d1[i].getClass(), d2[i].getClass());
                        assertEquals("fields of " + d1[i].getId(), d1[i].toFileString(), d2[i].toFileString());
                        assertEquals("on/off of " + d1[i].getId(), d1[i].isOn(), d2[i].isOn());
                    }
                }
                assertTrue("unlocked state kept", !((DoorLock) loaded.findDeviceAnywhere("DL001")).isLocked());
                assertTrue("new PIN kept", ((DoorLock) loaded.findDeviceAnywhere("DL001")).unlock(4321));

                Scene[] s1 = original.getScenes();
                Scene[] s2 = loaded.getScenes();
                assertEquals("scene count", s1.length, s2.length);
                for (int s = 0; s < s1.length; s++) {
                    assertEquals("scene name " + s, s1[s].getName(), s2[s].getName());
                    SceneAction[] a1 = s1[s].getActions();
                    SceneAction[] a2 = s2[s].getActions();
                    assertEquals("step count of " + s1[s].getName(), a1.length, a2.length);
                    for (int i = 0; i < a1.length; i++) {
                        assertEquals(s1[s].getName() + " step " + (i + 1), a1[i].toFileString(), a2[i].toFileString());
                    }
                }
            }
        });

        test("Loading a file with one corrupt line skips it and loads the rest", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                String dir = freshDir("corrupt");
                FileManager files = new FileManager(dir);
                TestRunner.writeLines(new File(dir, FileManager.HOME_FILE), new String[] {
                    "HOME|Test Home",
                    "ROOM|Hall",
                    "DEVICE|LIGHT|L001|Lamp|true|10.0|75|Warm White",
                    "DEVICE|FAN|F001|Fan|true|75.0|three",
                    "DEVICE|AC|AC001|AC|false|1500.0|24|COOL"
                });
                Home home = files.loadHome();
                Room hall = home.findRoom("Hall");
                assertEquals("devices loaded", 2, hall.getDeviceCount());
                assertEquals("first", "L001", hall.findDevice(0).getId());
                assertEquals("second", "AC001", hall.findDevice(1).getId());
            }
        });

        test("Every kind of corrupt line is skipped with the rest still loaded", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                String dir = freshDir("corrupt-kinds");
                FileManager files = new FileManager(dir);
                TestRunner.writeLines(new File(dir, FileManager.HOME_FILE), new String[] {
                    "DEVICE|LIGHT|L009|Early|true|10.0|75|Warm White",  // device before any ROOM
                    "HOME|Test Home",
                    "ROOM|Hall",
                    "DEVICE|LIGHT|L001|Lamp|true|10.0|75",              // wrong field count
                    "DEVICE|LIGHT|L002|Lamp|true|10.0|150|Warm White",  // brightness out of range
                    "DEVICE|LIGHT|L003|Lamp|yes|10.0|50|Warm White",    // bad boolean
                    "DEVICE|HEATER|H001|Heater|true|900.0|3",           // unknown type
                    "DEVICE|FAN|L004|Fan|true|75.0|3",                  // ID prefix does not match type
                    "DEVICE|TV|TV001|TV|false|abc|40|101",              // bad number
                    "DEVICE|LIGHT|L005|Good Lamp|false|10.0|20|Red",
                    "DEVICE|LIGHT|L005|Copy|false|10.0|20|Red",         // duplicate ID
                    "ROOM|hall",                                        // duplicate room name
                    "DEVICE|FAN|F001|Lost Fan|true|75.0|3",             // follows the bad ROOM line
                    "GARBAGE",                                          // unknown record
                    "ROOM|Porch",
                    "DEVICE|LOCK|DL001|Door|true|5.0|false|4321"
                });
                Home home = files.loadHome();
                assertEquals("rooms", 2, home.getRooms().length);
                assertEquals("devices", 2, home.getTotalDeviceCount());
                assertEquals("good lamp", "Good Lamp", home.findDeviceAnywhere("L005").getName());
                assertTrue("lock unlocked", !((DoorLock) home.findDeviceAnywhere("DL001")).isLocked());
            }
        });

        test("Scenes file: unknown action types and empty scenes are skipped", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                String dir = freshDir("scenes");
                FileManager files = new FileManager(dir);
                TestRunner.writeLines(new File(dir, FileManager.SCENES_FILE), new String[] {
                    "ACTION|L001|TURN_ON|0",       // before any SCENE
                    "SCENE|Morning",
                    "ACTION|L001|TURN_ON|0",
                    "ACTION|L001|DANCE|0",         // unknown action type
                    "ACTION|AC001|SET_LEVEL|x",    // bad number
                    "ACTION|AC001|SET_LEVEL|22",
                    "SCENE|Empty",                 // no valid steps
                    "ACTION|F001|EXPLODE|0",
                    "SCENE|morning",               // duplicate name
                    "ACTION|F001|TURN_OFF|0"
                });
                Home home = new Home("Test Home");
                files.loadScenes(home);
                assertEquals("scene count", 1, home.getScenes().length);
                Scene morning = home.findScene("Morning");
                assertEquals("steps", 2, morning.getActionCount());
                assertEquals("step 2", "ACTION|AC001|SET_LEVEL|22", morning.getActions()[1].toFileString());
            }
        });

        test("loadHome() returns null when there is no save file", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                FileManager files = new FileManager(freshDir("empty"));
                assertEquals("no file", null, files.loadHome());
                assertEquals("no log", 0, files.readLastLogLines(5).length);
            }
        });

        test("readLastLogLines(3) on a 5-line log returns the last 3 in order", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                FileManager files = new FileManager(freshDir("log"));
                String[] messages = {"one", "two", "three", "four", "five"};
                for (String m : messages) {
                    files.appendLog("SYSTEM", m);
                }
                String[] last = files.readLastLogLines(3);
                assertEquals("length", 3, last.length);
                assertTrue("first is three", last[0].endsWith("| SYSTEM  | three"));
                assertTrue("second is four", last[1].endsWith("| SYSTEM  | four"));
                assertTrue("third is five", last[2].endsWith("| SYSTEM  | five"));
                assertTrue("timestamp format", last[0].matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} \\| .*"));
                assertEquals("asking for more than exist", 5, files.readLastLogLines(20).length);
            }
        });
    }
}
