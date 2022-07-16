package de.capouschek.airqualitybackend.classes;

public class SensorData {
    private long controllerId;
    private double value;

    SensorData(long controllerId, double value) {
        this.controllerId = controllerId;
        this.value = value;
    }

    public long getControllerId() {
        return this.controllerId;
    }

    public double getValue() {
        return this.value;
    }
}
