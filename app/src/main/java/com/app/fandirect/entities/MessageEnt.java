package com.app.fandirect.entities;

/**
 * Created by saeedhyder on 3/14/2018.
 */

public class MessageEnt {

    String image;
    String name;
    String time;
    String message;

    public MessageEnt(String image, String name, String time, String message) {
        this.image = image;
        this.name = name;
        this.time = time;
        this.message = message;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
