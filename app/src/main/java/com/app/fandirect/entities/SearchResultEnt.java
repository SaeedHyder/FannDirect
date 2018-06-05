package com.app.fandirect.entities;

/**
 * Created by saeedhyder on 3/7/2018.
 */

public class SearchResultEnt {

    String image;
    String name;
    String address;
    float rating;

    public SearchResultEnt(String image, String name, String address, float rating) {
        this.image = image;
        this.name = name;
        this.address = address;
        this.rating = rating;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
