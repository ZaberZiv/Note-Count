package com.zivapp.notes.model;

public class MainMenuNote {

    private String id;
    private String date;
    private String title;
    private int total_sum;
    private boolean group;
    private String message;

    public MainMenuNote() {
    }

    public MainMenuNote(String date) {
        this.date = date;
    }

    public MainMenuNote(String date, String title, int total_sum, boolean group) {
        this.date = date;
        this.title = title;
        this.total_sum = total_sum;
        this.group = group;
    }


    public MainMenuNote(String date, String title, int total_sum, String id, boolean group) {
        this.date = date;
        this.title = title;
        this.total_sum = total_sum;
        this.id = id;
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotal_sum() {
        return total_sum;
    }

    public void setTotal_sum(int total_sum) {
        this.total_sum = total_sum;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
