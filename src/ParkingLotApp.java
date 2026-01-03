import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

enum V_TYPE { CAR, BIKE, BUS }
enum SPOT_TYPE { CAR, BIKE, BUS }

class NumberPlate {
    String vehicleNumber;
    String registeredUser;
    String addressOfUser;

    public NumberPlate(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleNumber);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NumberPlate)) return false;
        return vehicleNumber.equals(((NumberPlate) obj).vehicleNumber);
    }
}

class Dimension {
    int height, width, length;
    public Dimension(int h, int w, int l) { height=h; width=w; length=l; }
}

interface Vehicle {
    V_TYPE getType();
    NumberPlate getNumberPlate();
}

class Car implements Vehicle {
    private NumberPlate numberPlate;
    Car(NumberPlate plate) { this.numberPlate = plate; }
    public V_TYPE getType() { return V_TYPE.CAR; }
    public NumberPlate getNumberPlate() { return numberPlate; }
}

class Bus implements Vehicle {
    private NumberPlate numberPlate;
    Bus(NumberPlate plate) { this.numberPlate = plate; }
    public V_TYPE getType() { return V_TYPE.BUS; }
    public NumberPlate getNumberPlate() { return numberPlate; }
}

class Bike implements Vehicle {
    private NumberPlate numberPlate;
    Bike(NumberPlate plate) { this.numberPlate = plate; }
    public V_TYPE getType() { return V_TYPE.BIKE; }
    public NumberPlate getNumberPlate() { return numberPlate; }
}

interface Spot {
    SPOT_TYPE getType();
    boolean isAvailable();
    Vehicle getParkedVehicle();
    boolean park(Vehicle v);
    boolean unpark();
}

class ParkingSpot implements Spot {
    private final SPOT_TYPE type;
    private Vehicle vehicle;
    private final ReentrantLock lock = new ReentrantLock();

    ParkingSpot(SPOT_TYPE type) { this.type = type; this.vehicle = null; }

    public SPOT_TYPE getType() { return type; }
    public boolean isAvailable() { return vehicle == null; }
    public Vehicle getParkedVehicle() { return vehicle; }

    // Thread-safe park/unpark for single spot
    public boolean park(Vehicle v) {
        lock.lock();
        try {
            if (vehicle != null) return false;
            vehicle = v;
            return true;
        } finally { lock.unlock(); }
    }

    public boolean unpark() {
        lock.lock();
        try {
            if (vehicle == null) return false;
            vehicle = null;
            return true;
        } finally { lock.unlock(); }
    }
}

class Floor {
    private final int floorNumber;
    private final Map<SPOT_TYPE, List<ParkingSpot>> spots;

    Floor(int floorNumber, Map<SPOT_TYPE, List<ParkingSpot>> spots) {
        this.floorNumber = floorNumber;
        this.spots = spots;
    }

    int getFloorNumber() { return floorNumber; }

    // Return available spots of given type
    List<ParkingSpot> getAvailableSpots(SPOT_TYPE type) {
        List<ParkingSpot> list = spots.getOrDefault(type, Collections.emptyList());
        List<ParkingSpot> available = new ArrayList<>();
        for (ParkingSpot p : list) {
            if (p.isAvailable()) available.add(p);
        }
        return available;
    }

    Map<SPOT_TYPE, List<ParkingSpot>> getAllSpots() { return spots; }
}

class ParkingLot {
    private final List<Floor> floors;
    private final Map<NumberPlate, List<ParkingSpot>> vehicleSpotMap = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    ParkingLot(List<Floor> floors) { this.floors = floors; }

    // Park a vehicle
    public boolean parkVehicle(Vehicle vehicle) {
        lock.lock();
        try {
            if (vehicleSpotMap.containsKey(vehicle.getNumberPlate())) return false; // already parked

            V_TYPE vType = vehicle.getType();

            for (Floor floor : floors) {
                if (vType == V_TYPE.BUS) {
                    List<ParkingSpot> carSpots = floor.getAvailableSpots(SPOT_TYPE.CAR);
                    // find 5 consecutive spots
                    for (int i = 0; i <= carSpots.size() - 5; i++) {
                        boolean canPark = true;
                        for (int j = 0; j < 5; j++) {
                            if (!carSpots.get(i+j).isAvailable()) { canPark = false; break; }
                        }
                        if (canPark) {
                            List<ParkingSpot> busSpots = new ArrayList<>();
                            for (int j = 0; j < 5; j++) carSpots.get(i+j).park(vehicle);
                            for (int j = 0; j < 5; j++) busSpots.add(carSpots.get(i+j));
                            vehicleSpotMap.put(vehicle.getNumberPlate(), busSpots);
                            return true;
                        }
                    }
                } else if (vType == V_TYPE.CAR) {
                    List<ParkingSpot> carSpots = floor.getAvailableSpots(SPOT_TYPE.CAR);
                    if (!carSpots.isEmpty()) {
                        carSpots.getFirst().park(vehicle);
                        vehicleSpotMap.put(vehicle.getNumberPlate(), List.of(carSpots.getFirst()));
                        return true;
                    }
                } else if (vType == V_TYPE.BIKE) {
                    List<ParkingSpot> bikeSpots = floor.getAvailableSpots(SPOT_TYPE.BIKE);
                    if (!bikeSpots.isEmpty()) {
                        bikeSpots.getFirst().park(vehicle);
                        vehicleSpotMap.put(vehicle.getNumberPlate(), List.of(bikeSpots.getFirst()));
                        return true;
                    }
                }
            }
            return false; // no spot available
        } finally { lock.unlock(); }
    }

    // Unpark a vehicle
    public boolean unparkVehicle(NumberPlate plate) {
        lock.lock();
        try {
            List<ParkingSpot> spots = vehicleSpotMap.get(plate);
            if (spots == null || spots.isEmpty()) return false;

            for (ParkingSpot p : spots) p.unpark();
            vehicleSpotMap.remove(plate);
            return true;
        } finally { lock.unlock(); }
    }

    // Get available spots by vehicle type
    public List<ParkingSpot> getAvailableSpots(V_TYPE vType) {
        lock.lock();
        try {
            List<ParkingSpot> result = new ArrayList<>();
            for (Floor floor : floors) {
                if (vType == V_TYPE.BUS) result.addAll(floor.getAvailableSpots(SPOT_TYPE.CAR));
                else if (vType == V_TYPE.CAR) result.addAll(floor.getAvailableSpots(SPOT_TYPE.CAR));
                else if (vType == V_TYPE.BIKE) result.addAll(floor.getAvailableSpots(SPOT_TYPE.BIKE));
            }
            return result;
        } finally { lock.unlock(); }
    }
}
