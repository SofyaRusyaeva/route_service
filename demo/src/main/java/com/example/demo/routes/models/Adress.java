package com.example.demo.routes.models;

import java.util.List;

public class Adress {
    private List<Double> coordinates;
    private String adress; // опционально

    public Adress() {
    }

    public Adress(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public Adress(List<Double> coordinates, String adress) {
        this.coordinates = coordinates;
        this.adress = adress;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public String getAdress() {
        return adress;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
