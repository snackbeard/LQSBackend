package de.capouschek.airqualitybackend.classes;

public class ControllerColorId {
    private long objectId;
    private String color;

    public ControllerColorId(long objectId, String color) {
        this.objectId = objectId;
        this.color = color;
    }

    public long getObjectId() {
        return this.objectId;
    }

    public String getColor() {
        return this.color;
    }
}
