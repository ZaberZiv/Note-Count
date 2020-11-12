package com.zivapp.notes.model;

public class GroupNote extends Note {

    private String member;
    private String date;
    private String group_id;

    public GroupNote() {
    }

    public GroupNote(String message, int sum, String member, String date) {
        super(message, sum);
        this.member = member;
        this.date = date;
    }

    public GroupNote(String message, int sum, String member, String date, String id) {
        super(message, sum, id);
        this.member = member;
        this.date = date;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }
}
