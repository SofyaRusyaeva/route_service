package com.example.demo.routes.models;

public class LocationData {
    private String type;
    private String name;
    private Adress adress;
    private String review; // опционально

    public LocationData() {
    }

    public LocationData(String type, String name, Adress adress) {
        this.type = type;
        this.name = name;
        this.adress = adress;
    }

    public LocationData(String type, String name, Adress adress, String review) {
        this.type = type;
        this.name = name;
        this.adress = adress;
        this.review = review;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Adress getAdress() {
        return adress;
    }

    public String getReview() {
        return review;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdress(Adress adress) {
        this.adress = adress;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
