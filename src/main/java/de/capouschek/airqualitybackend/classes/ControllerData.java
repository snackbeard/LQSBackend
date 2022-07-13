package de.capouschek.airqualitybackend.classes;

import java.util.ArrayList;
import java.util.List;

public class ControllerData {
    private String name;
    private String color;
    private List<QualityObject> dataTvoc;
    private List<QualityObject> dataEco2;

    public ControllerData(String name) {
        this.dataTvoc = new ArrayList<>();
        this.dataEco2 = new ArrayList<>();
        this.name = name;
    }

    public void setDataTvoc(List<QualityObject> list) {
        this.dataTvoc = list;
    }

    public void setDataEco2(List<QualityObject> list) {
        this.dataEco2 = list;
    }

    public String getName() {
        return this.name;
    }

    public List<QualityObject> getDataTvoc() {
        return this.dataTvoc;
    }

    public List<QualityObject> getDataEco2() {
        return this.dataEco2;
    }

}
