package com.example.mysavings.model;

import java.util.Currency;
import java.util.Date;

public class Transaction {
    private String description;
    private double value;
    private Date date;
    private String category;
    private String type;
    private String status;

    public Transaction() {
    }

    public double getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getDate() {return date; }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getStatus() {return status; }
    public void setStatus(String status) {
        this.status = status;
    }
}