import java.util.*;

public class SmartParkingSystem {
    enum Status { EMPTY, OCCUPIED, DELETED }

    class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status = Status.EMPTY;

        public ParkingSpot() {}
    }

    private final int capacity = 500;
    private final ParkingSpot[] spots;
    private int occupiedCount = 0;
    private final double HOURLY_RATE = 5.0;

    public SmartParkingSystem() {
        spots = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) spots[i] = new ParkingSpot();
    }

    // Custom Hash Function
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode() % capacity);
    }

    public String parkVehicle(String licensePlate) {
        if (occupiedCount >= capacity) return "Error: Lot Full";

        int preferredSpot = hash(licensePlate);
        int current = preferredSpot;
        int probes = 0;

        // Linear Probing logic
        while (spots[current].status == Status.OCCUPIED) {
            current = (current + 1) % capacity;
            probes++;
        }

        // Assign Spot
        spots[current].licensePlate = licensePlate;
        spots[current].entryTime = System.currentTimeMillis();
        spots[current].status = Status.OCCUPIED;
        occupiedCount++;

        return String.format("Assigned spot #%d (%d probes)", current, probes);
    }

    public String exitVehicle(String licensePlate) {
        int preferredSpot = hash(licensePlate);
        int current = preferredSpot;

        // Search for the vehicle using the same probing logic
        while (spots[current].status != Status.EMPTY) {
            if (spots[current].status == Status.OCCUPIED &&
                    spots[current].licensePlate.equals(licensePlate)) {

                long durationMillis = System.currentTimeMillis() - spots[current].entryTime;
                double hours = Math.max(1, durationMillis / 3600000.0);
                double fee = hours * HOURLY_RATE;

                // Mark as DELETED (Lazy Deletion) to preserve probing chains
                spots[current].status = Status.DELETED;
                spots[current].licensePlate = null;
                occupiedCount--;

                return String.format("Spot #%d freed, Fee: $%.2f", current, fee);
            }
            current = (current + 1) % capacity;
        }
        return "Vehicle not found.";
    }
}
