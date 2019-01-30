package com.app.fandirect.entities;

public class TabsEnt {

    private String name;
    private String count;
    private boolean isSelected=false;

    public TabsEnt(String name, String count, boolean isSelected) {
        this.name = name;
        this.count = count;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
