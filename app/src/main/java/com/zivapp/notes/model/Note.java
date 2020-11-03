package com.zivapp.notes.model;

public class Note {

    private String message;
    private int sum;
    private String Uid;
    private String id_note;

    public Note() {
    }

    public Note(String message) {
        this.message = message;
    }

    public Note(String message, int sum) {
        this.message = message;
        this.sum = sum;
    }

    public Note(String message, int sum, String id_note) {
        this.message = message;
        this.sum = sum;
        this.id_note = id_note;
    }

    public Note(int totalSum) {
        this.sum = totalSum;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getId_note() {
        return id_note;
    }

    public void setId_note(String id_note) {
        this.id_note = id_note;
    }
}
