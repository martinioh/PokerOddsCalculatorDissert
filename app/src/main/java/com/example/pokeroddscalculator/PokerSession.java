package com.example.pokeroddscalculator;

import java.math.BigDecimal;
import java.util.Date;
//basic object for a poker session, used when logging sessions into database
public class PokerSession {
    public String uid, location, description, date, profit;

    public PokerSession(){

    }

    public PokerSession(String uid, String location, String description, String profit, String date) {
        this.uid = uid;
        this.location = location;
        this.description = description;
        this.profit = profit;
        this.date = date;
    }
    public String getUid() {
        return uid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
