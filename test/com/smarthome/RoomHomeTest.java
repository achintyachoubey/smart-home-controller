package com.smarthome;

import static com.smarthome.TestRunner.assertEquals;
import static com.smarthome.TestRunner.assertFalse;
import static com.smarthome.TestRunner.assertThrows;
import static com.smarthome.TestRunner.assertTrue;
import static com.smarthome.TestRunner.test;

import com.smarthome.exception.CapacityExceededException;
import com.smarthome.exception.DeviceNotFoundException;
import com.smarthome.exception.InvalidInputException;
import com.smarthome.model.Device;
import com.smarthome.model.DoorLock;
import com.smarthome.model.Fan;
import com.smarthome.model.Home;
import com.smarthome.model.Light;
import com.smarthome.model.Room;

/**
 * Tests for the array handling in {@link Room} and {@link Home}:
 * capacity, remove-with-shift, search and defensive copies.
 */
public final class RoomHomeTest {

    private RoomHomeTest() {
        // static tests only
    }

    /** Runs every room and home test. */
    public static void runAll() {
        test("Adding an 11th device to a room throws CapacityExceededException", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                final Room room = new Room("Hall");
                for (int i = 1; i <= Room.MAX_DEVICES; i++) {
                    room.addDevice(new Light("Light " + i));
                }
                assertEquals("count", 10, room.getDeviceCount());
                assertThrows("11th device", CapacityExceededException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        room.addDevice(new Light("One too many"));
                    }
                });
                assertEquals("count unchanged", 10, room.getDeviceCount());
            }
        });

        test("Removing the middle of three devices shifts the third left", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                final Room room = new Room("Hall");
                Device first = new Light("First");
                Device middle = new Fan("Middle");
                Device third = new Light("Third");
                room.addDevice(first);
                room.addDevice(middle);
                room.addDevice(third);
                Device removed = room.removeDevice(middle.getId());
                assertTrue("returns the removed device", removed == middle);
                assertEquals("count", 2, room.getDeviceCount());
                assertTrue("index 0 unchanged", room.findDevice(0) == first);
                assertTrue("index 1 is the former third", room.findDevice(1) == third);
                assertThrows("index 2 now empty", DeviceNotFoundException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        room.findDevice(2);
                    }
                });
            }
        });

        test("findDevice(\"X999\") throws; findDevice(\"l001\") finds L001", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                final Room room = new Room("Hall");
                Light lamp = new Light("Lamp");
                room.addDevice(lamp);
                assertThrows("unknown ID", DeviceNotFoundException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        room.findDevice("X999");
                    }
                });
                assertTrue("case-insensitive", room.findDevice("l001") == lamp);
                assertThrows("remove unknown ID", DeviceNotFoundException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        room.removeDevice("F404");
                    }
                });
            }
        });

        test("Room.getDevices() returns a copy", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Room room = new Room("Hall");
                Light lamp = new Light("Lamp");
                room.addDevice(lamp);
                Device[] copy = room.getDevices();
                assertEquals("trimmed length", 1, copy.length);
                copy[0] = new Fan("Intruder");
                assertTrue("room still holds the lamp", room.getDevices()[0] == lamp);
                assertEquals("count unchanged", 1, room.getDeviceCount());
            }
        });

        test("turnAllOff() switches off everything except door locks", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                Room room = new Room("Entrance");
                Light porch = new Light("Porch");
                DoorLock lock = new DoorLock("Door");
                porch.turnOn();
                room.addDevice(porch);
                room.addDevice(lock);
                room.turnAllOff();
                assertFalse("light off", porch.isOn());
                assertTrue("lock still powered", lock.isOn());
            }
        });

        test("Home rejects duplicate room names (any case) and a 9th room", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                final Home home = new Home("Test Home");
                home.addRoom(new Room("Kitchen"));
                assertThrows("duplicate name", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        home.addRoom(new Room("KITCHEN"));
                    }
                });
                for (int i = 2; i <= Home.MAX_ROOMS; i++) {
                    home.addRoom(new Room("Room " + i));
                }
                assertThrows("9th room", CapacityExceededException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        home.addRoom(new Room("Attic"));
                    }
                });
            }
        });

        test("Home finds devices anywhere, renames and removes rooms with shifting", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                final Home home = new Home("Test Home");
                Room a = new Room("A");
                Room b = new Room("B");
                Room c = new Room("C");
                home.addRoom(a);
                home.addRoom(b);
                home.addRoom(c);
                Fan fan = new Fan("Fan");
                c.addDevice(fan);
                assertTrue("found anywhere", home.findDeviceAnywhere("f001") == fan);
                assertTrue("room of device", home.findRoomOfDevice("F001") == c);
                assertThrows("missing device", DeviceNotFoundException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        home.findDeviceAnywhere("TV009");
                    }
                });
                home.renameRoom("b", "Bedroom");
                assertTrue("findRoom after rename", home.findRoom("BEDROOM") == b);
                assertThrows("rename to existing name", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        home.renameRoom("A", "c");
                    }
                });
                home.removeRoom("A");
                Room[] rooms = home.getRooms();
                assertEquals("room count", 2, rooms.length);
                assertTrue("shifted left", rooms[0] == b && rooms[1] == c);
                assertEquals("device total", 1, home.getTotalDeviceCount());
                assertThrows("findRoom unknown", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        home.findRoom("A");
                    }
                });
            }
        });
    }
}
