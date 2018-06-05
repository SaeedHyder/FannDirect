package com.app.fandirect.entities;

/**
 * Created by saeedhyder on 3/15/2018.
 */

public class ChatEnt {

    String msgLeft;
    String ImageLeft;
    String msgRight;
    String ImageRight;

    public ChatEnt(String msgLeft, String imageLeft, String msgRight, String imageRight) {
        this.msgLeft = msgLeft;
        ImageLeft = imageLeft;
        this.msgRight = msgRight;
        ImageRight = imageRight;
    }

    public String getMsgLeft() {
        return msgLeft;
    }

    public void setMsgLeft(String msgLeft) {
        this.msgLeft = msgLeft;
    }

    public String getImageLeft() {
        return ImageLeft;
    }

    public void setImageLeft(String imageLeft) {
        ImageLeft = imageLeft;
    }

    public String getMsgRight() {
        return msgRight;
    }

    public void setMsgRight(String msgRight) {
        this.msgRight = msgRight;
    }

    public String getImageRight() {
        return ImageRight;
    }

    public void setImageRight(String imageRight) {
        ImageRight = imageRight;
    }
}
