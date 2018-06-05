package com.app.fandirect.entities;

/**
 * Created by saeedhyder on 3/2/2018.
 */

public class ProfileFannEnt {

    String image;
    String name;

    public ProfileFannEnt(String image, String name) {
        this.image = image;
        this.name = name;
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
}
