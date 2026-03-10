
class ParkingSpot {

    String licensePlate;
    long entryTime;
    boolean occupied;

    ParkingSpot() {
        this.occupied = false;
    }
}

public class ParkingLotSystem {

    private static final int SIZE = 500;

    ParkingSpot[] table = new ParkingSpot[SIZE];

    int totalProbes = 0;
    int totalVehicles = 0;

    public ParkingLotSystem() {
        for (int i = 0; i < SIZE; i++) {
            table[i] = new ParkingSpot();
        }
    }

    // hash function
    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % SIZE;
    }

    // park vehicle
    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].occupied) {
            index = (index + 1) % SIZE;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        totalProbes += probes;
        totalVehicles++;

        System.out.println("Vehicle " + plate +
                " parked at spot #" + index +
                " (" + probes + " probes)");
    }

    // exit vehicle
    public void exitVehicle(String plate) {

        int index = hash(plate);

        int probes = 0;

        while (table[index].occupied) {

            if (plate.equals(table[index].licensePlate)) {

                long durationMillis = System.currentTimeMillis() - table[index].entryTime;

                double hours = durationMillis / (1000.0 * 60 * 60);

                double fee = hours * 5; // $5 per hour

                table[index].occupied = false;
                table[index].licensePlate = null;

                System.out.println("Vehicle " + plate + " exited.");
                System.out.println("Spot #" + index + " freed");
                System.out.printf("Duration: %.2f hours\n", hours);
                System.out.printf("Fee: $%.2f\n", fee);

                return;
            }

            index = (index + 1) % SIZE;
            probes++;

            if (probes >= SIZE)
                break;
        }

        System.out.println("Vehicle not found.");
    }

    // find nearest free spot
    public int findNearestSpot() {

        for (int i = 0; i < SIZE; i++) {
            if (!table[i].occupied)
                return i;
        }

        return -1;
    }

    // statistics
    public void getStatistics() {

        int occupied = 0;

        for (ParkingSpot spot : table) {
            if (spot.occupied)
                occupied++;
        }

        double occupancyRate = (occupied * 100.0) / SIZE;

        double avgProbes = totalVehicles == 0 ? 0 : (double) totalProbes / totalVehicles;

        System.out.println("Parking Statistics:");
        System.out.printf("Occupancy: %.2f%%\n", occupancyRate);
        System.out.printf("Average Probes: %.2f\n", avgProbes);
    }

    public static void main(String[] args) {

        ParkingLotSystem parking = new ParkingLotSystem();

        parking.parkVehicle("ABC-1234");
        parking.parkVehicle("ABC-1235");
        parking.parkVehicle("XYZ-9999");

        parking.getStatistics();

        parking.exitVehicle("ABC-1234");

        parking.getStatistics();
    }
}