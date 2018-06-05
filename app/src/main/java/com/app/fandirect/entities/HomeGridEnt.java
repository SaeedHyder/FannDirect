package com.app.fandirect.entities;

/**
 * Created by saeedhyder on 3/1/2018.
 */

public class HomeGridEnt {

    int image;
    String text;

    public HomeGridEnt(int image, String text) {
        this.image = image;
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
