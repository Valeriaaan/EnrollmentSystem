package com.example.enrollmentsystem.model;

public class DiscountFee {
    private int id;
    private String code;
    private String title;
    private String amount;
    private String description;

    public DiscountFee(int id, String code,String title, String amount, String description) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.amount = amount;
        this.description = description;
    }

    public int getDiscountId() {
        return id;
    }

    public String getDiscountCode() {
        return code;
    }

    public String getDiscountTitle() {
        return title;
    }

    public String getDiscountAmount() {
        return amount;
    }

    public String getDiscountDescription() {
        return description;
    }
}
