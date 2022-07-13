package de.capouschek.airqualitybackend.classes;

import java.util.ArrayList;
import java.util.List;

public class ControllerSingleData {

    private String name;
    private List<QualityObject> data;

    public ControllerSingleData(String name) {
        this.data = new ArrayList<>();
        this.name = name;
    }

    public void setData(List<QualityObject> list) {
        this.data = list;
    }

    public String getName() {
        return this.name;
    }

    public List<QualityObject> getData() {
        return this.data;
    }
}
