package com.smarthome;

import static com.smarthome.TestRunner.assertEquals;
import static com.smarthome.TestRunner.assertFalse;
import static com.smarthome.TestRunner.assertThrows;
import static com.smarthome.TestRunner.assertTrue;
import static com.smarthome.TestRunner.test;

import com.smarthome.exception.InvalidInputException;
import com.smarthome.model.Adjustable;
import com.smarthome.model.AirConditioner;
import com.smarthome.model.Device;
import com.smarthome.model.DoorLock;
import com.smarthome.model.Fan;
import com.smarthome.model.Light;
import com.smarthome.model.SmartTV;

/**
 * Tests for the device classes: constructors, validation, power formulas,
 * polymorphism and the door lock's PIN logic.
 */
public final class DeviceTest {

    private static final double EPS = 1e-9;

    private DeviceTest() {
        // static tests only
    }

    /** Runs every device test. */
    public static void runAll() {
        test("Light(name) has defaults: off, 10 W, brightness 100, ID starts with L", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Light lamp = new Light("Lamp");
                assertFalse("off", lamp.isOn());
                assertEquals("rating", 10.0, lamp.getPowerRatingWatts(), EPS);
                assertEquals("brightness", 100, lamp.getBrightness());
                assertEquals("colour", "Warm White", lamp.getColour());
                assertTrue("ID prefix", lamp.getId().startsWith("L"));
                assertEquals("first ID", "L001", lamp.getId());
            }
        });

        test("Parameterized AirConditioner constructor stores all values", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                AirConditioner ac = new AirConditioner("Study AC", 1200.0, 20, "dry");
                assertEquals("name", "Study AC", ac.getName());
                assertEquals("rating", 1200.0, ac.getPowerRatingWatts(), EPS);
                assertEquals("temperature", 20, ac.getTemperature());
                assertEquals("mode stored upper-case", "DRY", ac.getMode());
                assertEquals("ID", "AC001", ac.getId());
            }
        });

        test("setBrightness(150) throws and leaves brightness unchanged", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                final Light lamp = new Light("Lamp", 10.0, 60, "Blue");
                assertThrows("brightness 150", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        lamp.setBrightness(150);
                    }
                });
                assertEquals("brightness unchanged", 60, lamp.getBrightness());
            }
        });

        test("AC setLevel(10) throws InvalidInputException", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                final AirConditioner ac = new AirConditioner("AC");
                assertThrows("temperature 10", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        ac.setLevel(10);
                    }
                });
                assertEquals("temperature unchanged", 24, ac.getTemperature());
                assertFalse("still off", ac.isOn());
            }
        });

        test("Invalid names, ratings, modes and colours are rejected", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                assertThrows("empty name", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        new Fan("   ");
                    }
                });
                assertThrows("name with |", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        new Fan("Bad|Name");
                    }
                });
                assertThrows("rating 0", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        new SmartTV("TV", 0, 10, 5);
                    }
                });
                assertThrows("mode HEAT", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        new AirConditioner("AC").setMode("HEAT");
                    }
                });
                assertThrows("colour Pink", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        new Light("Lamp").setColour("Pink");
                    }
                });
                assertThrows("PIN 123", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        new DoorLock("Door", 5.0, 123);
                    }
                });
            }
        });

        test("getDeviceType() through Device references gives LIGHT, FAN, AC, TV, LOCK", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Device[] devices = {new Light("L"), new Fan("F"), new AirConditioner("A"),
                    new SmartTV("T"), new DoorLock("D")};
                String[] expected = {"LIGHT", "FAN", "AC", "TV", "LOCK"};
                for (int i = 0; i < devices.length; i++) {
                    assertEquals("type " + i, expected[i], devices[i].getDeviceType());
                }
            }
        });

        test("Total live power of a mixed Device[] equals the sum of each formula", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Device light = new Light("Lamp", 10.0, 50, "Red");      // 10 x 50 / 100 = 5
                Device fan = new Fan("Fan", 75.0, 4);                    // 75 x 4 / 5    = 60
                Device ac = new AirConditioner("AC", 1000.0, 22, "COOL"); // 1000 x 1.10 = 1100
                Device tv = new SmartTV("TV", 120.0, 10, 5);             // 120
                Device lock = new DoorLock("Door");                     // 5 (starts on)
                Device[] all = {light, fan, ac, tv, lock};
                for (Device d : all) {
                    d.turnOn();
                }
                double total = 0;
                for (Device d : all) {
                    total += d.getCurrentPowerWatts();
                }
                assertEquals("total", 5 + 60 + 1100 + 120 + 5, total, 1e-6);
            }
        });

        test("Light 10 W, on, brightness 75 gives 7.5 W", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Light lamp = new Light("Lamp", 10.0, 75, "Daylight");
                lamp.turnOn();
                assertEquals("power", 7.5, lamp.getCurrentPowerWatts(), EPS);
            }
        });

        test("Fan 75 W, speed 3 gives 45.0 W", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Fan fan = new Fan("Fan");
                fan.turnOn();
                assertEquals("power", 45.0, fan.getCurrentPowerWatts(), EPS);
            }
        });

        test("AC 1500 W at 16 C: COOL 2100, DRY 900, FAN 450", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                AirConditioner ac = new AirConditioner("AC", 1500.0, 16, AirConditioner.MODE_COOL);
                ac.turnOn();
                assertEquals("COOL", 2100.0, ac.getCurrentPowerWatts(), 1e-6);
                ac.setMode("DRY");
                assertEquals("DRY", 900.0, ac.getCurrentPowerWatts(), 1e-6);
                ac.setMode("fan");
                assertEquals("FAN", 450.0, ac.getCurrentPowerWatts(), 1e-6);
                ac.setMode("COOL");
                ac.setTemperature(24);
                assertEquals("COOL at 24 C", 1500.0, ac.getCurrentPowerWatts(), 1e-6);
                ac.setTemperature(30);
                assertEquals("COOL at 30 C", 1050.0, ac.getCurrentPowerWatts(), 1e-6);
            }
        });

        test("Every device type returns 0.0 W when off", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Device[] devices = {new Light("L"), new Fan("F"), new AirConditioner("A"),
                    new SmartTV("T"), new DoorLock("D")};
                for (Device d : devices) {
                    d.turnOff();
                    assertEquals(d.getDeviceType() + " off", 0.0, d.getCurrentPowerWatts(), 0.0);
                }
            }
        });

        test("toggle() twice returns to the original state", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Fan fan = new Fan("Fan");
                boolean original = fan.isOn();
                fan.toggle();
                assertTrue("changed after one toggle", fan.isOn() != original);
                fan.toggle();
                assertEquals("back to original", original, fan.isOn());
            }
        });

        test("setLevel() switches an adjustable device on", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                SmartTV tv = new SmartTV("TV");
                assertFalse("starts off", tv.isOn());
                tv.setLevel(55);
                assertTrue("now on", tv.isOn());
                assertEquals("volume", 55, tv.getVolume());
            }
        });

        test("DoorLock: 3 wrong PINs lock the keypad; correct PIN then refused", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                DoorLock lock = new DoorLock("Front Door");
                assertTrue("new lock is powered on", lock.isOn());
                assertTrue("new lock is locked", lock.isLocked());
                assertFalse("wrong 1", lock.unlock(1111));
                assertEquals("remaining after 1", 2, lock.getRemainingAttempts());
                assertFalse("wrong 2", lock.unlock(2222));
                assertEquals("remaining after 2", 1, lock.getRemainingAttempts());
                assertFalse("wrong 3", lock.unlock(3333));
                assertEquals("remaining after 3", 0, lock.getRemainingAttempts());
                assertTrue("keypad locked", lock.isKeypadLocked());
                assertFalse("correct PIN refused", lock.unlock(DoorLock.DEFAULT_PIN));
                assertTrue("still locked", lock.isLocked());
                assertTrue("status shows keypad lock", lock.getStatus().contains("[KEYPAD LOCKED]"));
            }
        });

        test("DoorLock: correct PIN unlocks and resets attempts; changePin works", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                final DoorLock lock = new DoorLock("Front Door");
                assertFalse("wrong", lock.unlock(9999));
                assertTrue("correct", lock.unlock(1234));
                assertFalse("unlocked", lock.isLocked());
                assertEquals("attempts reset", DoorLock.MAX_ATTEMPTS, lock.getRemainingAttempts());
                lock.changePin(1234, 4321);
                lock.lock();
                assertTrue("new PIN works", lock.unlock(4321));
                assertThrows("wrong old PIN", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        lock.changePin(1234, 5555);
                    }
                });
                assertThrows("new PIN out of range", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        lock.changePin(4321, 99999);
                    }
                });
            }
        });

        test("DoorLock powered off keeps its state and refuses lock/unlock", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                final DoorLock lock = new DoorLock("Front Door");
                lock.unlock(1234);
                lock.turnOff();
                assertFalse("still unlocked after power off", lock.isLocked());
                assertThrows("lock() refused", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        lock.lock();
                    }
                });
                assertThrows("unlock() refused", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        lock.unlock(1234);
                    }
                });
                lock.turnOn();
                lock.lock();
                assertTrue("locks again once powered", lock.isLocked());
            }
        });

        test("instanceof Adjustable: true for Light, Fan, AC, TV; false for DoorLock", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Device[] adjustable = {new Light("L"), new Fan("F"), new AirConditioner("A"), new SmartTV("T")};
                for (Device d : adjustable) {
                    assertTrue(d.getDeviceType() + " is Adjustable", d instanceof Adjustable);
                }
                Device lock = new DoorLock("D");
                assertFalse("DoorLock is not Adjustable", lock instanceof Adjustable);
            }
        });

        test("toString() and toFileString() use each subclass's own fields", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Device tv = new SmartTV("Smart TV", 100.0, 40, 101);
                assertTrue("dashboard row", tv.toString().contains("Vol 40, Ch 101"));
                assertTrue("dashboard OFF", tv.toString().contains("OFF"));
                assertEquals("file line", "DEVICE|TV|TV001|Smart TV|false|100.0|40|101", tv.toFileString());
                Device lock = new DoorLock("Main Door Lock");
                assertEquals("lock line", "DEVICE|LOCK|DL001|Main Door Lock|true|5.0|true|1234", lock.toFileString());
            }
        });
    }
}
