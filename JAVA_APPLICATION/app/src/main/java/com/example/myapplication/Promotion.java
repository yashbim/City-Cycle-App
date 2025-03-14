package com.example.myapplication;

public class Promotion {
    private String title;
    private String description;
    private String discount;

    public Promotion(String title, String description, String discount) {
        this.title = title;
        this.description = description;
        this.discount = discount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}