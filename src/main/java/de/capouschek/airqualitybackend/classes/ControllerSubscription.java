package de.capouschek.airqualitybackend.classes;

public class ControllerSubscription {

    private boolean isSubscribed;
    private ControllerEsp controller;
    // private int color;

    public ControllerSubscription() {};

    public ControllerSubscription(boolean isSubscribed, ControllerEsp controller) {
        this.isSubscribed = isSubscribed;
        this.controller = controller;
    }

    public boolean getIsSubscribed() {
        return this.isSubscribed;
    }

    public ControllerEsp getController() {
        return this.controller;
    }

}
