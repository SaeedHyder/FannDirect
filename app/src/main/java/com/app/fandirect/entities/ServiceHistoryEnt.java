package com.app.fandirect.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by saeedhyder on 3/13/2018.
 */

public class ServiceHistoryEnt {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sender_id")
    @Expose
    private String senderId;
    @SerializedName("receiver_id")
    @Expose
    private String receiverId;
    @SerializedName("service_id")
    @Expose
    private String serviceId;
    @SerializedName("datetime")
    @Expose
    private String datetime;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("is_rate")
    @Expose
    private String isRate;
    @SerializedName("service_detail")
    @Expose
    private ServiceDetail serviceDetail;
    @SerializedName("sender_detail")
    @Expose
    private SenderDetail senderDetail;
    @SerializedName("receiver_detail")
    @Expose
    private ReceiverDetail receiverDetail;

    public String getIsRate() {
        return isRate;
    }

    public void setIsRate(String isRate) {
        this.isRate = isRate;
    }

    public ReceiverDetail getReceiverDetail() {
        return receiverDetail;
    }

    public void setReceiverDetail(ReceiverDetail receiverDetail) {
        this.receiverDetail = receiverDetail;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ServiceDetail getServiceDetail() {
        return serviceDetail;
    }

    public void setServiceDetail(ServiceDetail serviceDetail) {
        this.serviceDetail = serviceDetail;
    }

    public SenderDetail getSenderDetail() {
        return senderDetail;
    }

    public void setSenderDetail(SenderDetail senderDetail) {
        this.senderDetail = senderDetail;
    }
}
