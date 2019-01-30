package com.app.fandirect.entities;

import com.app.fandirect.helpers.DateHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by saeedhyder on 9/15/2017.
 */

public class NotificationEnt {

    @SerializedName("sender_id")
    @Expose
    private String senderId;
    @SerializedName("receiver_id")
    @Expose
    private String receiverId;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("action_id")
    @Expose
    private String action_id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("sender_detail")
    @Expose
    private SenderDetail senderDetail;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public String getAction_id() {
        return action_id;
    }

    public void setAction_id(String action_id) {
        this.action_id = action_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SenderDetail getSenderDetail() {
        return senderDetail;
    }

    public void setSenderDetail(SenderDetail senderDetail) {
        this.senderDetail = senderDetail;
    }
}