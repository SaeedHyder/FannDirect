package com.app.fandirect.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TilesCountEnt {


    @SerializedName("feeds")
    @Expose
    private Integer feeds;
    @SerializedName("promotions")
    @Expose
    private Integer promotions;
    @SerializedName("messages")
    @Expose
    private Integer messages;
    @SerializedName("friend_request")
    @Expose
    private Integer friendRequest;

    private Integer notification_count;

    public Integer getNotification_count() {
        return notification_count;
    }

    public void setNotification_count(Integer notification_count) {
        this.notification_count = notification_count;
    }

    public Integer getFeeds() {
        return feeds;
    }

    public void setFeeds(Integer feeds) {
        this.feeds = feeds;
    }

    public Integer getPromotions() {
        return promotions;
    }

    public void setPromotions(Integer promotions) {
        this.promotions = promotions;
    }

    public Integer getMessages() {
        return messages;
    }

    public void setMessages(Integer messages) {
        this.messages = messages;
    }

    public Integer getFriendRequest() {
        return friendRequest;
    }

    public void setFriendRequest(Integer friendRequest) {
        this.friendRequest = friendRequest;
    }
}
