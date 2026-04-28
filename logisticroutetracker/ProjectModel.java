package com.logisticroutetracker;
import java.util.LinkedList;

// Abstract Base Class for all Checkpoints
abstract class Checkpoint {
    String checkpointId;
    String locationName;
    double distanceFromLast;
    int expectedDuration;
    int actualDuration;

    public Checkpoint(String id, String name, double dist, int expected, int actual) {
        this.checkpointId = id;
        this.locationName = name;
        this.distanceFromLast = dist;
        this.expectedDuration = expected;
        this.actualDuration = actual;
    }

    public boolean isDelayed() { return actualDuration > expectedDuration; }
    public abstract boolean isCritical();
    public abstract String getType();
    public abstract double calculatePenalty();
}

// Specialized Checkpoint Types
class DeliveryCheckpoint extends Checkpoint {
    public DeliveryCheckpoint(String id, String name, double dist, int exp, int act) { super(id, name, dist, exp, act); }
    @Override public boolean isCritical() { return true; }
    @Override public String getType() { return "DeliveryCheckpoint"; }
    @Override public double calculatePenalty() {
        return isDelayed() ? (actualDuration - expectedDuration) * 2.0 : 0.0;
    }
}

class FuelCheckpoint extends Checkpoint {
    public FuelCheckpoint(String id, String name, double dist, int exp, int act) { super(id, name, dist, exp, act); }
    @Override public boolean isCritical() { return true; }
    @Override public String getType() { return "FuelCheckpoint"; }
    @Override public double calculatePenalty() {
        return isDelayed() ? 10.0 : 0.0;
    }
}

class RestCheckpoint extends Checkpoint {
    public RestCheckpoint(String id, String name, double dist, int exp, int act) { super(id, name, dist, exp, act); }
    @Override public boolean isCritical() { return false; }
    @Override public String getType() { return "RestCheckpoint"; }
    @Override public double calculatePenalty() {
        int delay = actualDuration - expectedDuration;
        return (delay > 30) ? (delay * 0.5) : 0.0;
    }
}

// Wrapper class using java.util.LinkedList and Generics
class RouteTracker<T extends Checkpoint> {
    private LinkedList<T> route = new LinkedList<>();

    public void addCheckpoint(T checkpoint) {
        route.add(checkpoint);
    }

    public boolean removeCheckpoint(String id) {
        return route.removeIf(cp -> cp.checkpointId.equals(id));
    }

    public T findCheckpoint(String id) {
        for (T cp : route) {
            if (cp.checkpointId.equals(id)) return cp;
        }
        return null;
    }

    public double computeTotalDistance() {
        double total = 0;
        for (T cp : route) total += cp.distanceFromLast;
        return total;
    }

    public double computeTotalPenalty() {
        double total = 0;
        for (T cp : route) total += cp.calculatePenalty();
        return total;
    }

    public boolean checkConsistency() {
        boolean hasDelivery = false;
        boolean hasFuel = false;
        for (T cp : route) {
            if (cp instanceof DeliveryCheckpoint) hasDelivery = true;
            if (cp instanceof FuelCheckpoint) hasFuel = true;
        }
        return hasDelivery && hasFuel;
    }

    public void printRoute() {
        int i = 1;
        for (T cp : route) {
            System.out.printf("%d. %s – %s – %s – Penalty: %.1f%n",
                    i++, cp.getType(), cp.locationName,
                    cp.isDelayed() ? "Delayed" : "On Time", cp.calculatePenalty());
        }
    }
}

// Main class set to ProjectModel
public class ProjectModel {
    public static void main(String[] args) {
        RouteTracker<Checkpoint> tracker = new RouteTracker<>();

        // Adding Sample Data
        tracker.addCheckpoint(new DeliveryCheckpoint("C1", "Warehouse A", 40.0, 60, 70));
        tracker.addCheckpoint(new FuelCheckpoint("C2", "Pump 12", 20.0, 15, 12));
        tracker.addCheckpoint(new RestCheckpoint("C3", "Motel X", 10.0, 30, 35));
        tracker.addCheckpoint(new DeliveryCheckpoint("C4", "Client Hub", 50.0, 45, 60));

        // Output Generation
        System.out.println("Driver: D1204 – Kavita Nair");
        System.out.println("Route Summary:");
        tracker.printRoute();

        double totalDist = tracker.computeTotalDistance();
        double totalPen = tracker.computeTotalPenalty();

        System.out.println("Total Distance: " + totalDist + " km");
        System.out.println("Total Penalty: " + totalPen);
        System.out.println("Route Score: " + (totalDist - totalPen));
        System.out.println("Critical Route Check: " + (tracker.checkConsistency() ? "All required checkpoints present" : "Inconsistent Route"));
    }
}
