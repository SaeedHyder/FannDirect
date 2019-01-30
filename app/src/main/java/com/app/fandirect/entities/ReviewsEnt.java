package com.app.fandirect.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewsEnt {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sender_id")
    @Expose
    private Integer senderId;
    @SerializedName("receiver_id")
    @Expose
    private Integer receiverId;
    @SerializedName("request_id")
    @Expose
    private Integer requestId;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("feedback")
    @Expose
    private String feedback;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("sender_detail")
    @Expose
    private SenderDetailReview senderDetail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public SenderDetailReview getSenderDetail() {
        return senderDetail;
    }

    public void setSenderDetail(SenderDetailReview senderDetail) {
        this.senderDetail = senderDetail;
    }
}
