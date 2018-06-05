package com.app.fandirect.entities;

/**
 * Created by saeedhyder on 3/5/2018.
 */

public class HomeSearchEnt {

    String category;
    String image;

    public HomeSearchEnt(String category, String image) {
        this.category = category;
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
