package com.example.enrollmentsystem.model;

public class AssessmentFee {
    private int id;
    private String title;
    private String amount;
    private String description;

    public AssessmentFee(int id, String title, String amount, String description) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.description = description;
    }

    public int getAssessmentId() {
        return id;
    }

    public String getAssessmentTitle() {
        return title;
    }

    public String getAssessmentAmount() {
        return amount;
    }

    public String getAssessmentDescription() {
        return description;
    }
}
