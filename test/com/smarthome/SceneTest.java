package com.smarthome;

import static com.smarthome.TestRunner.assertEquals;
import static com.smarthome.TestRunner.assertFalse;
import static com.smarthome.TestRunner.assertThrows;
import static com.smarthome.TestRunner.assertTrue;
import static com.smarthome.TestRunner.test;

import com.smarthome.exception.CapacityExceededException;
import com.smarthome.exception.InvalidInputException;
import com.smarthome.model.ActionType;
import com.smarthome.model.AirConditioner;
import com.smarthome.model.DoorLock;
import com.smarthome.model.Fan;
import com.smarthome.model.Home;
import com.smarthome.model.Light;
import com.smarthome.model.Room;
import com.smarthome.model.Scene;
import com.smarthome.model.SceneAction;
import com.smarthome.service.DemoData;

/**
 * Tests for {@link Scene} and {@link SceneAction}: running steps, failure
 * isolation, capacity and file format.
 */
public final class SceneTest {

    private SceneTest() {
        // static tests only
    }

    /** Runs every scene test. */
    public static void runAll() {
        test("A step on a deleted device fails but the other steps still run", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Home home = new Home("Test Home");
                Room room = new Room("Hall");
                home.addRoom(room);
                Light lamp = new Light("Lamp");
                Fan fan = new Fan("Fan");
                AirConditioner ac = new AirConditioner("AC");
                room.addDevice(lamp);
                room.addDevice(fan);
                room.addDevice(ac);
                Scene scene = new Scene("Evening");
                scene.addAction(new SceneAction(lamp.getId(), ActionType.TURN_ON, 0));
                scene.addAction(new SceneAction(fan.getId(), ActionType.TURN_ON, 0));
                scene.addAction(new SceneAction(ac.getId(), ActionType.SET_LEVEL, 22));
                room.removeDevice(fan.getId()); // the middle step now points to a missing device
                String report = scene.run(home);
                assertTrue("lamp on", lamp.isOn());
                assertTrue("AC on (setLevel turns it on)", ac.isOn());
                assertEquals("AC temperature", 22, ac.getTemperature());
                assertFalse("removed fan untouched", fan.isOn());
                assertTrue("report has FAILED", report.contains("[FAILED] F001"));
                assertTrue("report has OK", report.contains("[OK] L001 Lamp -> ON"));
                assertTrue("summary", report.contains("Evening finished: 2 of 3 steps succeeded."));
                assertEquals("success count", 2, scene.getLastSuccessCount());
            }
        });

        test("SET_LEVEL on a door lock and LOCK on a light fail with a reason", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Home home = new Home("Test Home");
                Room room = new Room("Hall");
                home.addRoom(room);
                DoorLock lock = new DoorLock("Door");
                Light lamp = new Light("Lamp");
                room.addDevice(lock);
                room.addDevice(lamp);
                Scene scene = new Scene("Odd");
                scene.addAction(new SceneAction(lock.getId(), ActionType.SET_LEVEL, 5));
                scene.addAction(new SceneAction(lamp.getId(), ActionType.LOCK, 0));
                scene.addAction(new SceneAction(lamp.getId(), ActionType.SET_LEVEL, 150));
                String report = scene.run(home);
                assertTrue("not adjustable", report.contains("is not adjustable"));
                assertTrue("not a lock", report.contains("is not a door lock"));
                assertTrue("out of range", report.contains("Brightness must be between 0 and 100"));
                assertTrue("summary", report.contains("0 of 3 steps succeeded"));
            }
        });

        test("Demo Night Mode runs all 8 steps successfully", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Home home = DemoData.buildDemoHome();
                String report = home.findScene("night mode").run(home);
                assertTrue("summary", report.contains("Night Mode finished: 8 of 8 steps succeeded."));
                assertFalse("ceiling light off", home.findDeviceAnywhere("L001").isOn());
                assertEquals("bedroom AC at 26", 26, ((AirConditioner) home.findDeviceAnywhere("AC002")).getTemperature());
                assertTrue("door locked", ((DoorLock) home.findDeviceAnywhere("DL001")).isLocked());
            }
        });

        test("A scene holds at most 15 steps; removeAction shifts later steps", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                final Scene scene = new Scene("Big");
                for (int i = 1; i <= Scene.MAX_ACTIONS; i++) {
                    scene.addAction(new SceneAction("L" + i, ActionType.TOGGLE, 0));
                }
                assertThrows("16th step", CapacityExceededException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        scene.addAction(new SceneAction("F001", ActionType.TURN_ON, 0));
                    }
                });
                scene.removeAction(0);
                assertEquals("count", 14, scene.getActionCount());
                assertEquals("first is former second", "L2", scene.getActions()[0].getDeviceId());
                assertThrows("bad index", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        scene.removeAction(14);
                    }
                });
            }
        });

        test("SceneAction file format and validation", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                assertEquals("TURN_OFF line", "ACTION|L001|TURN_OFF|0",
                        new SceneAction("l001", ActionType.TURN_OFF, 7).toFileString());
                assertEquals("SET_LEVEL line", "ACTION|AC002|SET_LEVEL|26",
                        new SceneAction("AC002", ActionType.SET_LEVEL, 26).toFileString());
                assertThrows("empty ID", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        new SceneAction(" ", ActionType.TURN_ON, 0);
                    }
                });
                assertThrows("missing type", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        new SceneAction("L001", null, 0);
                    }
                });
            }
        });
    }
}
