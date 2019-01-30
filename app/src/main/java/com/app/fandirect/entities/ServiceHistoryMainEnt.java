package com.app.fandirect.entities;

import java.util.ArrayList;

public class ServiceHistoryMainEnt {

    ArrayList<ServiceHistoryEnt> pending = new ArrayList<>();
    ArrayList<ServiceHistoryEnt> completed = new ArrayList<>();

    public ArrayList<ServiceHistoryEnt> getPending() {
        return pending;
    }

    public void setPending(ArrayList<ServiceHistoryEnt> pending) {
        this.pending = pending;
    }

    public ArrayList<ServiceHistoryEnt> getCompleted() {
        return completed;
    }

    public void setCompleted(ArrayList<ServiceHistoryEnt> completed) {
        this.completed = completed;
    }

}
