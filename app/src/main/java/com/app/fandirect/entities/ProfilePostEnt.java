package com.app.fandirect.entities;

/**
 * Created by saeedhyder on 3/2/2018.
 */

public class ProfilePostEnt {

    String image;
    String name;
    String date;
    String address;
    String description;

    public ProfilePostEnt(String image, String name, String date, String address, String description) {
        this.image = image;
        this.name = name;
        this.date = date;
        this.address = address;
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
