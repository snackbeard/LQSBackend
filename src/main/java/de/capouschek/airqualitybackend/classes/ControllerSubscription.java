package de.capouschek.airqualitybackend.classes;

public class ControllerSubscription {

    private boolean isSubscribed;
    private ControllerEsp controller;
    private String color;

    public ControllerSubscription() {};

    public ControllerSubscription(boolean isSubscribed, ControllerEsp controller, String color) {
        this.isSubscribed = isSubscribed;
        this.controller = controller;
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }

    public boolean getIsSubscribed() {
        return this.isSubscribed;
    }

    public ControllerEsp getController() {
        return this.controller;
    }

}
