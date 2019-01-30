package com.app.fandirect.entities;

public class TagUser {

    String Name;
    String Id;
    String startPosition;
    String endPosition;

    public TagUser(String name, String id) {
        Name = name;
        Id = id;
    }

    public TagUser(String name, String id, String startPosition, String endPosition) {
        Name = name;
        Id = id;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public String getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(String startPosition) {
        this.startPosition = startPosition;
    }

    public String getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(String endPosition) {
        this.endPosition = endPosition;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
