package com.smarthome.service;

import com.smarthome.exception.InvalidInputException;
import com.smarthome.model.Device;
import com.smarthome.model.Home;
import com.smarthome.model.Room;

/**
 * Converts live power into energy (kWh) and electricity cost in rupees.
 *
 * <p>OOP concepts: <b>method overloading</b> (three {@code calculateKwh}
 * methods for a device, a room and a home, and two {@code calculateCost}
 * methods), <b>constructor overloading</b> (default rate or a given rate),
 * <b>static constants</b>, <b>encapsulation</b> (validated rate setter) and
 * an <b>if-else chain</b> with compound assignment for the slab bill.</p>
 */
public class EnergyCalculator {

    /** Flat electricity rate used when none is given, in rupees per kWh. */
    public static final double DEFAULT_RATE_PER_KWH = 8.0;
    /** Highest rate accepted, in rupees per kWh. */
    public static final double MAX_RATE_PER_KWH = 100.0;
    /** Days in a billing month. */
    public static final int DAYS_PER_MONTH = 30;
    /** Fixed monthly charge of the slab tariff, in rupees (illustrative). */
    public static final double SLAB_FIXED_CHARGE = 50.0;

    private double ratePerKwh;

    /** Creates a calculator using {@link #DEFAULT_RATE_PER_KWH}. */
    public EnergyCalculator() {
        this.ratePerKwh = DEFAULT_RATE_PER_KWH;
    }

    /**
     * Creates a calculator with a custom flat rate.
     *
     * @param ratePerKwh rupees per kWh, more than 0 and at most 100
     * @throws InvalidInputException if the rate is out of range
     */
    public EnergyCalculator(double ratePerKwh) throws InvalidInputException {
        this.ratePerKwh = checkRate(ratePerKwh);
    }

    /** @return the flat rate in rupees per kWh */
    public double getRatePerKwh() {
        return ratePerKwh;
    }

    /**
     * Changes the flat rate.
     *
     * @param ratePerKwh rupees per kWh, more than 0 and at most 100
     * @throws InvalidInputException if the rate is out of range (the old rate is kept)
     */
    public void setRatePerKwh(double ratePerKwh) throws InvalidInputException {
        this.ratePerKwh = checkRate(ratePerKwh);
    }

    /**
     * Energy used by one device at its current power: watts x hours / 1000.
     *
     * @param d the device
     * @param hours how long it runs (0 or more)
     * @return energy in kWh
     */
    public double calculateKwh(Device d, double hours) {
        checkHours(hours);
        return d.getCurrentPowerWatts() * hours / 1000;
    }

    /**
     * Energy used by every device in a room at their current power.
     *
     * @param r the room
     * @param hours how long they run (0 or more)
     * @return energy in kWh
     */
    public double calculateKwh(Room r, double hours) {
        checkHours(hours);
        return r.getTotalPowerWatts() * hours / 1000;
    }

    /**
     * Energy used by the whole home at its current power.
     *
     * @param h the home
     * @param hours how long everything runs (0 or more)
     * @return energy in kWh
     */
    public double calculateKwh(Home h, double hours) {
        checkHours(hours);
        return h.getTotalPowerWatts() * hours / 1000;
    }

    /**
     * Flat cost at this calculator's rate.
     *
     * @param kwh energy in kWh (0 or more)
     * @return cost in rupees
     */
    public double calculateCost(double kwh) {
        return calculateCost(kwh, ratePerKwh);
    }

    /**
     * Flat cost at a given rate.
     *
     * @param kwh energy in kWh (0 or more)
     * @param ratePerKwh rupees per kWh (more than 0)
     * @return cost in rupees
     */
    public double calculateCost(double kwh, double ratePerKwh) {
        if (!(kwh >= 0)) {
            throw new IllegalArgumentException("Energy cannot be negative.");
        }
        if (!(ratePerKwh > 0)) {
            throw new IllegalArgumentException("Rate must be more than 0.");
        }
        return kwh * ratePerKwh;
    }

    /**
     * Monthly energy of the home if its current load runs for the given hours
     * every day: watts x hoursPerDay x 30 / 1000.
     *
     * @param h the home
     * @param hoursPerDay average hours per day, 0 to 24
     * @return energy for a 30-day month in kWh
     */
    public double monthlyKwh(Home h, double hoursPerDay) {
        if (!(hoursPerDay >= 0 && hoursPerDay <= 24)) {
            throw new IllegalArgumentException("Hours per day must be between 0 and 24.");
        }
        return h.getTotalPowerWatts() * hoursPerDay * DAYS_PER_MONTH / 1000;
    }

    /**
     * Illustrative slab tariff: first 100 units at Rs. 3, units 101-300 at Rs. 5,
     * units 301-500 at Rs. 7, above 500 at Rs. 9, plus a fixed Rs. 50.
     *
     * @param units energy in kWh for the month (0 or more)
     * @return total bill in rupees
     */
    public double calculateSlabBill(double units) {
        if (!(units >= 0)) {
            throw new IllegalArgumentException("Units cannot be negative.");
        }
        double bill = SLAB_FIXED_CHARGE;
        // each branch charges the full lower slabs, then the part inside the current slab
        if (units <= 100) {
            bill += units * 3.0;
        } else if (units <= 300) {
            bill += 100 * 3.0;
            bill += (units - 100) * 5.0;
        } else if (units <= 500) {
            bill += 100 * 3.0 + 200 * 5.0;
            bill += (units - 300) * 7.0;
        } else {
            bill += 100 * 3.0 + 200 * 5.0 + 200 * 7.0;
            bill += (units - 500) * 9.0;
        }
        return bill;
    }

    private static double checkRate(double rate) throws InvalidInputException {
        if (!(rate > 0 && rate <= MAX_RATE_PER_KWH)) {
            throw new InvalidInputException("Rate must be more than 0 and at most Rs. "
                    + String.format("%,.2f", MAX_RATE_PER_KWH) + " per kWh.");
        }
        return rate;
    }

    /** Written as !(hours >= 0) so that NaN is rejected too. */
    private static void checkHours(double hours) {
        if (!(hours >= 0) || Double.isInfinite(hours)) {
            throw new IllegalArgumentException("Hours cannot be negative.");
        }
    }
}
