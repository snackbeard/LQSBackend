package de.capouschek.airqualitybackend.classes;

import java.time.LocalDateTime;

public class QualityObject {

    private double value;
    private String timeOfRecording;

    public QualityObject(double lqs, String timeOfRecording) {
        this.value = lqs;
        this.timeOfRecording = timeOfRecording;
    }


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTimeOfRecording() {
        return timeOfRecording;
    }

    public void setTimeOfRecording(String timeOfRecording) {
        this.timeOfRecording = timeOfRecording;
    }

}
