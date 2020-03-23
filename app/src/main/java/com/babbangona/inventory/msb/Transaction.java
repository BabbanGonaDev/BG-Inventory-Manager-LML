package com.babbangona.inventory.msb;

import java.sql.Date;

public class Transaction {
    private String id;
    private String itemid;
    private String itemName;
    private String unit;
    private String in;
    private String out;
    private String enteredDate;
    private String date;
    private String type;
    private String unitPrice;
    private String suggUnitPrice;
    private String hub;
    private String sync;

    //constructor for new transactions
    public Transaction(String id, String itemid, String itemName, String unit, String in, String out, String type, String unitPrice, String suggUnitPrice, String hub, String enteredDate) {
        this.id = id;
        this.itemid = itemid;
        this.itemName = itemName;
        this.enteredDate = enteredDate;
        this.unit = unit;
        this.in = in;
        this.out = out;
        this.date = new Date(System.currentTimeMillis()).toString();
        this.type = type;
        this.unitPrice = unitPrice;
        this.suggUnitPrice = suggUnitPrice;
        this.hub = hub;
        this.sync = "no";
    }

    //adapter constructor-sync and date are added
    public Transaction(String id, String itemid, String itemName, String unit, String in, String out, String type, String unitPrice, String suggUnitPrice, String hub, String enteredDate, String date, String sync) {
        this.id = id;
        this.itemid = itemid;
        this.itemName = itemName;
        this.unit = unit;
        this.in = in;
        this.out = out;
        this.enteredDate = enteredDate;
        this.date = date;
        this.type = type;
        this.unitPrice = unitPrice;
        this.suggUnitPrice = suggUnitPrice;
        this.hub = hub;
        this.sync = sync;
    }

    public String getId() {
        return id;
    }

    public String getItemid() {
        return itemid;
    }

    public String getItemName() {
        return itemName;
    }

    public String getUnit() {
        return unit;
    }

    public String getIn() {
        return in;
    }

    public String getOut() {
        return out;
    }

    public String getEnteredDate() {
        return enteredDate;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public String getSuggUnitPrice() {
        return suggUnitPrice;
    }

    public String getHub() {
        return hub;
    }

    public String getSync() {
        return sync;
    }
}
