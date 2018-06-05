package com.app.fandirect.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by saeedhyder on 5/11/2018.
 */

public class UserInfo {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("profession")
    @Expose
    private Object profession;
    @SerializedName("work_at")
    @Expose
    private Object workAt;
    @SerializedName("hobbies")
    @Expose
    private Object hobbies;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("delted_at")
    @Expose
    private Object deltedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Object getProfession() {
        return profession;
    }

    public void setProfession(Object profession) {
        this.profession = profession;
    }

    public Object getWorkAt() {
        return workAt;
    }

    public void setWorkAt(Object workAt) {
        this.workAt = workAt;
    }

    public Object getHobbies() {
        return hobbies;
    }

    public void setHobbies(Object hobbies) {
        this.hobbies = hobbies;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Object getDeltedAt() {
        return deltedAt;
    }

    public void setDeltedAt(Object deltedAt) {
        this.deltedAt = deltedAt;
    }

}
