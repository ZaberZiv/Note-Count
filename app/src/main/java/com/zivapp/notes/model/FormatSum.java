package com.zivapp.notes.model;

public class FormatSum {

    private String converted_sum;

    private int countNotes;

    public FormatSum(String converted_sum) {
        this.converted_sum = converted_sum;
    }

    public FormatSum(int countNotes) {
        this.countNotes = countNotes;
    }

    public String getConverted_sum() {
        return converted_sum;
    }

    public void setConverted_sum(String converted_sum) {
        this.converted_sum = converted_sum;
    }

    public int getCountNotes() {
        return countNotes;
    }

    public void setCountNotes(int countNotes) {
        this.countNotes = countNotes;
    }
}
