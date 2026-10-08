package com.smarthome;

import static com.smarthome.TestRunner.assertEquals;
import static com.smarthome.TestRunner.assertThrows;
import static com.smarthome.TestRunner.test;

import com.smarthome.exception.InvalidInputException;
import com.smarthome.model.AirConditioner;
import com.smarthome.model.Fan;
import com.smarthome.model.Home;
import com.smarthome.model.Light;
import com.smarthome.model.Room;
import com.smarthome.service.EnergyCalculator;

/**
 * Tests for {@link EnergyCalculator}: the overloaded kWh and cost methods,
 * the monthly estimate and the slab bill edges from the specification.
 */
public final class EnergyCalculatorTest {

    private static final double MONEY = 0.005;

    private EnergyCalculatorTest() {
        // static tests only
    }

    /** Runs every energy test. */
    public static void runAll() {
        test("calculateKwh overloads: device, room and home over 8 hours", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                EnergyCalculator calc = new EnergyCalculator();
                Light lamp = new Light("Lamp", 10.0, 75, "Warm White"); // 7.5 W
                Fan fan = new Fan("Fan");                               // 45 W
                AirConditioner ac = new AirConditioner("AC");           // 1500 W at 24 C COOL
                lamp.turnOn();
                fan.turnOn();
                ac.turnOn();
                Room living = new Room("Living");
                living.addDevice(lamp);
                living.addDevice(fan);
                Room bedroom = new Room("Bedroom");
                bedroom.addDevice(ac);
                Home home = new Home("Test Home");
                home.addRoom(living);
                home.addRoom(bedroom);
                assertEquals("device", 0.06, calc.calculateKwh(lamp, 8), 1e-9);
                assertEquals("room", 0.42, calc.calculateKwh(living, 8), 1e-9);
                assertEquals("home", 12.42, calc.calculateKwh(home, 8), 1e-9);
            }
        });

        test("360 kWh at Rs. 8 costs Rs. 2880.00 via both calculateCost overloads", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                EnergyCalculator calc = new EnergyCalculator();
                assertEquals("instance rate", 2880.00, calc.calculateCost(360), MONEY);
                assertEquals("explicit rate", 2880.00, calc.calculateCost(360, 8.0), MONEY);
                EnergyCalculator custom = new EnergyCalculator(10.0);
                assertEquals("custom rate", 3600.00, custom.calculateCost(360), MONEY);
            }
        });

        test("1500 W AC, 24 C COOL, 8 h/day for 30 days = 360 kWh", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                EnergyCalculator calc = new EnergyCalculator();
                Home home = new Home("Test Home");
                Room room = new Room("Bedroom");
                AirConditioner ac = new AirConditioner("AC");
                ac.turnOn();
                room.addDevice(ac);
                home.addRoom(room);
                double kwh = calc.monthlyKwh(home, 8);
                assertEquals("monthly kWh", 360.0, kwh, 1e-9);
                assertEquals("flat cost", 2880.00, calc.calculateCost(kwh), MONEY);
                assertEquals("slab bill", 1770.00, calc.calculateSlabBill(kwh), MONEY);
            }
        });

        test("Slab bill for 360 units is Rs. 1770.00", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                assertEquals("360 units", 1770.00, new EnergyCalculator().calculateSlabBill(360), MONEY);
            }
        });

        test("Slab edges give 50, 350, 355, 1350, 2750, 2759 (0, 100, 101, 300, 500, 501 units)", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                EnergyCalculator calc = new EnergyCalculator();
                double[] units = {0, 100, 101, 300, 500, 501};
                double[] bills = {50, 350, 355, 1350, 2750, 2759};
                for (int i = 0; i < units.length; i++) {
                    assertEquals(units[i] + " units", bills[i], calc.calculateSlabBill(units[i]), MONEY);
                }
            }
        });

        test("Invalid energy inputs are rejected", new TestRunner.ThrowingBlock() {
            public void run() throws Exception {
                final EnergyCalculator calc = new EnergyCalculator();
                assertThrows("negative units", IllegalArgumentException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        calc.calculateSlabBill(-1);
                    }
                });
                assertThrows("25 hours per day", IllegalArgumentException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        calc.monthlyKwh(new Home("H"), 25);
                    }
                });
                assertThrows("negative hours", IllegalArgumentException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        calc.calculateKwh(new Light("Lamp"), -2);
                    }
                });
                assertThrows("rate 0", InvalidInputException.class, new TestRunner.ThrowingBlock() {
                    public void run() throws Exception {
                        calc.setRatePerKwh(0);
                    }
                });
                assertEquals("rate unchanged", EnergyCalculator.DEFAULT_RATE_PER_KWH, calc.getRatePerKwh(), 0.0);
            }
        });
    }
}
