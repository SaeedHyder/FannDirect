package com.app.fandirect.entities;

/**
 * Created by saeedhyder on 3/14/2018.
 */

public class FeedsEnt {

    String image;
    String post_image;
    String name;
    String date;
    String address;
    String description;

    public FeedsEnt(String image, String post_image, String name, String date, String address, String description) {
        this.image = image;
        this.post_image = post_image;
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

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
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
