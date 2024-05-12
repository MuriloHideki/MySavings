package com.example.mysavings.model;

import java.util.Date;

public class Transaction {
    private String id;
    private String name;
    private double value;
    private Date date;
    private String type;

    public Transaction() {
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public double getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getDate() {return date; }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}