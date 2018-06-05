package com.app.fandirect.entities;

/**
 * Created by saeedhyder on 3/5/2018.
 */

public class FannsEnt {

    String image;
    String name;

    public FannsEnt(String name,String image) {
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
