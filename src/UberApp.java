//import java.util.Comparator;
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//enum RideStatus {
//    REQUESTED, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
//}
//
//enum DriverStatus {
//    ONLINE, OFFLINE
//}
//
//class Location {
//    double lat;
//    double lon;
//
//    Location(double lat, double lon) {
//        this.lat = lat;
//        this.lon = lon;
//    }
//}
//
//class User {
//    final long id;
//    final String name;
//    Location location;
//    volatile Trip activeTrip;
//
//    private User(Builder b) {
//        this.id = b.id;
//        this.name = b.name;
//        this.location = b.location;
//    }
//
//    static class Builder {
//        long id;
//        String name;
//        Location location;
//
//        Builder id(long id) {
//            this.id = id;
//            return this;
//        }
//
//        Builder name(String name) {
//            this.name = name;
//            return this;
//        }
//
//        Builder location(Location l) {
//            this.location = l;
//            return this;
//        }
//
//        User build() {
//            return new User(this);
//        }
//    }
//}
//
//class Driver {
//    final long id;
//    final String name;
//    volatile DriverStatus status;
//    Location location;
//    volatile Trip activeTrip;
//
//    private Driver(Builder b) {
//        this.id = b.id;
//        this.name = b.name;
//        this.status = b.status;
//        this.location = b.location;
//    }
//
//    boolean isAvailable() {
//        return status == DriverStatus.ONLINE && activeTrip == null;
//    }
//
//    static class Builder {
//        long id;
//        String name;
//        DriverStatus status;
//        Location location;
//
//        Builder id(long id) {
//            this.id = id;
//            return this;
//        }
//
//        Builder name(String name) {
//            this.name = name;
//            return this;
//        }
//
//        Builder status(DriverStatus s) {
//            this.status = s;
//            return this;
//        }
//
//        Builder location(Location l) {
//            this.location = l;
//            return this;
//        }
//
//        Driver build() {
//            return new Driver(this);
//        }
//    }
//}
//
//class Trip {
//    final long id;
//    final User user;
//    Driver driver;
//    RideStatus status;
//    long startTime;
//    long endTime;
//
//    Trip(long id, User user) {
//        this.id = id;
//        this.user = user;
//        this.status = RideStatus.REQUESTED;
//    }
//}
//
//interface DriverMatchingStrategy {
//    Driver findDriver(User user, List<Driver> drivers);
//}
//
//class NearestDriverStrategy implements DriverMatchingStrategy {
//
//    @Override
//    public Driver findDriver(User user, List<Driver> drivers) {
//        return drivers.stream()
//                .filter(Driver::isAvailable)
//                .min(Comparator.comparingDouble(
//                        d -> distance(user.location, d.location)))
//                .orElse(null);
//    }
//
//    private double distance(Location a, Location b) {
//        return Math.abs(a.lat - b.lat) + Math.abs(a.lon - b.lon);
//    }
//}
//
//
//class RideService {
//
//    private final List<Driver> drivers;
//    private final DriverMatchingStrategy strategy;
//    private final ExecutorService executor = Executors.newFixedThreadPool(10);
//
//    RideService(List<Driver> drivers, DriverMatchingStrategy strategy) {
//        this.drivers = drivers;
//        this.strategy = strategy;
//    }
//
//    public Trip bookRide(User user) throws Exception {
//        if (user.activeTrip != null)
//            throw new IllegalStateException("User already on trip");
//
//        Trip trip = new Trip(System.nanoTime(), user);
//
//        Driver driver = strategy.findDriver(user, drivers);
//
//        if (driver == null) {
//            trip.status = RideStatus.CANCELLED;
//            return trip;
//        }
//
//        driver.activeTrip = trip;
//        trip.driver = driver;
//        trip.status = RideStatus.ASSIGNED;
//        user.activeTrip = trip;
//
//        // async driver confirmation
//        Future<Boolean> confirmation = executor.submit(() -> driverAccepts());
//
//        if (!confirmation.get()) {
//            cleanup(trip);
//            trip.status = RideStatus.CANCELLED;
//            return trip;
//        }
//
//        trip.status = RideStatus.IN_PROGRESS;
//        trip.startTime = System.currentTimeMillis();
//        return trip;
//    }
//
//    public void completeRide(Trip trip) {
//        trip.status = RideStatus.COMPLETED;
//        trip.endTime = System.currentTimeMillis();
//        cleanup(trip);
//    }
//
//    private void cleanup(Trip trip) {
//        if (trip.driver != null)
//            trip.driver.activeTrip = null;
//        trip.user.activeTrip = null;
//    }
//
//    private boolean driverAccepts() {
//        return new Random().nextBoolean();
//    }
//}
//
//
//public class UberApp {
//
//    public static void main(String[] args) throws Exception {
//
//        User user = new User.Builder()
//                .id(1)
//                .name("Rohit")
//                .location(new Location(10, 10))
//                .build();
//
//        Driver d1 = new Driver.Builder()
//                .id(101)
//                .name("Driver1")
//                .status(DriverStatus.ONLINE)
//                .location(new Location(11, 11))
//                .build();
//
//        Driver d2 = new Driver.Builder()
//                .id(102)
//                .name("Driver2")
//                .status(DriverStatus.ONLINE)
//                .location(new Location(20, 20))
//                .build();
//
//        RideService service = new RideService(
//                List.of(d1, d2),
//                new NearestDriverStrategy()
//        );
//
//        Trip trip = service.bookRide(user);
//        System.out.println("Trip status: " + trip.status);
//
//        if (trip.status == RideStatus.IN_PROGRESS) {
//            service.completeRide(trip);
//            System.out.println("Trip completed");
//        }
//    }
//}
