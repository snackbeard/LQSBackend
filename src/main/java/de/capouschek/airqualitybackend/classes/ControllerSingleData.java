package de.capouschek.airqualitybackend.classes;

import java.util.ArrayList;
import java.util.List;

public class ControllerSingleData {

    private String name;
    private String color;
    private List<QualityObject> data;

    public ControllerSingleData(String name, String color) {
        this.data = new ArrayList<>();
        this.name = name;
        this.color = color;
    }

    public void setData(List<QualityObject> list) {
        this.data = list;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public List<QualityObject> getData() {
        return this.data;
    }
}
