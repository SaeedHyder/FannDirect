package com.app.fandirect.entities;

public class ResponseWrapper<T> {

    private String code;
    private T result;
    private String message;
    private int userBlocked;
    private int pages;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserBlocked() {
        return userBlocked;
    }

    public void setUserBlocked(int userBlocked) {
        this.userBlocked = userBlocked;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
