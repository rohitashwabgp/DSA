package parking.lot;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

enum VehicleType {
    BIKE, CAR, TRUCK
}

enum SlotType {
    BIKE, CAR, TRUCK
}

abstract class Vehicle {
    abstract VehicleType getType();
}

class Car extends Vehicle {
    public VehicleType getType() {
        return VehicleType.CAR;
    }
}

class Bike extends Vehicle {
    public VehicleType getType() {
        return VehicleType.BIKE;
    }
}

class Truck extends Vehicle {
    public VehicleType getType() {
        return VehicleType.TRUCK;
    }
}

class ParkingSlot {
    private final String slotId;
    private final SlotType slotType;
    private Vehicle parkedVehicle;

    public ParkingSlot(String slotId, SlotType slotType) {
        this.slotId = slotId;
        this.slotType = slotType;
    }

    public boolean isAvailable() {
        return parkedVehicle == null;
    }

    public boolean canFit(Vehicle vehicle) {
        return vehicle.getType().name().equals(slotType.name());
    }

    public void park(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
    }

    public void unpark() {
        this.parkedVehicle = null;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public String getSlotId() {
        return slotId;
    }
}

class Floor {
    private final int floorNumber;
    private final List<ParkingSlot> slots;

    public Floor(int floorNumber, List<ParkingSlot> slots) {
        this.floorNumber = floorNumber;
        this.slots = slots;
    }

    public List<ParkingSlot> getSlots() {
        return slots;
    }

    public int getFloorNumber() {
        return floorNumber;
    }
}

class Ticket {
    private final String ticketId;
    private final ParkingSlot slot;

    public Ticket(String ticketId, ParkingSlot slot) {
        this.ticketId = ticketId;
        this.slot = slot;
    }

    public String getTicketId() {
        return ticketId;
    }

    public ParkingSlot getSlot() {
        return slot;
    }
}

interface SlotAllocationStrategy {
    ParkingSlot allocateSlot(Vehicle vehicle, Map<SlotType, Deque<ParkingSlot>> availableSlots);
}

class NearestSlotStrategy implements SlotAllocationStrategy {

    @Override
    public ParkingSlot allocateSlot(Vehicle vehicle, Map<SlotType, Deque<ParkingSlot>> availableSlots) {

        SlotType type = SlotType.valueOf(vehicle.getType().name());
        Deque<ParkingSlot> slots = availableSlots.get(type);

        if (slots == null || slots.isEmpty()) return null;

        return slots.poll(); // nearest
    }
}


class ParkingLotService {

    private final Map<SlotType, Deque<ParkingSlot>> availableSlots = new EnumMap<>(SlotType.class);
    private final Map<String, Ticket> activeTickets = new ConcurrentHashMap<>();

    // One lock per slot type (fine-grained)
    private final Map<SlotType, ReentrantLock> slotLocks = new EnumMap<>(SlotType.class);

    private volatile SlotAllocationStrategy allocationStrategy;

    public ParkingLotService(List<Floor> floors, SlotAllocationStrategy strategy) {

        this.allocationStrategy = strategy;

        for (SlotType type : SlotType.values()) {
            availableSlots.put(type, new ArrayDeque<>());
            slotLocks.put(type, new ReentrantLock());
        }

        for (Floor floor : floors) {
            for (ParkingSlot slot : floor.getSlots()) {
                availableSlots.get(slot.getSlotType()).add(slot);
            }
        }
    }


    public void setAllocationStrategy(SlotAllocationStrategy strategy) {
        this.allocationStrategy = strategy;
    }

    public String parkVehicle(Vehicle vehicle) {
        SlotType type = SlotType.valueOf(vehicle.getType().name());
        ReentrantLock lock = slotLocks.get(type);

        lock.lock();
        try {
            ParkingSlot slot = allocationStrategy.allocateSlot(vehicle, availableSlots);

            if (slot == null) return null;

            slot.park(vehicle);

            String ticketId = UUID.randomUUID().toString();
            activeTickets.put(ticketId, new Ticket(ticketId, slot));

            return ticketId;
        } finally {
            lock.unlock();
        }
    }

    public boolean unparkVehicle(String ticketId) {
        Ticket ticket = activeTickets.remove(ticketId);
        if (ticket == null) return false;

        ParkingSlot slot = ticket.getSlot();
        SlotType type = slot.getSlotType();
        ReentrantLock lock = slotLocks.get(type);

        lock.lock();
        try {
            slot.unpark();
            availableSlots.get(type).offer(slot);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public int getAvailableSlots(SlotType type) {
        ReentrantLock lock = slotLocks.get(type);
        lock.lock();
        try {
            return availableSlots.get(type).size();
        } finally {
            lock.unlock();
        }
    }
}
