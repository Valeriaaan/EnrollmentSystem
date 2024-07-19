package com.example.enrollmentsystem.model;

public class PaymentFee {
    private int id;
    private String title;
    private String amount;
    private String description;

    public PaymentFee(int id, String title, String amount, String description) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.description = description;
    }

    public int getPaymentId() {
        return id;
    }

    public String getPaymentTitle() {
        return title;
    }

    public String getPaymentAmount() {
        return amount;
    }

    public String getPaymentDescription() {
        return description;
    }
}
