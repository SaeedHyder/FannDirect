package com.app.fandirect.entities;

import android.support.annotation.NonNull;

public class FannListEnt {

    String name;
    String image;
    String userId;
    String roleId;

    public FannListEnt(String name, String image, String userId, String roleId) {
        this.name = name;
        this.image = image;
        this.userId = userId;
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }


}
