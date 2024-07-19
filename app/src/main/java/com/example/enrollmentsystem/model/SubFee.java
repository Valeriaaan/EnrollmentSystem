package com.example.enrollmentsystem.model;

public class SubFee {
    private int id;
    private String title;

    public SubFee(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getSubFeeId() {
        return id;
    }

    public String getSubFeeTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title; // This will be displayed in the Spinner
    }
}
