package com.example.pokeroddscalculator;

import java.math.BigDecimal;
import java.util.Date;
//basic object for a poker session, used when logging sessions into database
public class PokerLimit {
    public String limit;

    public PokerLimit() {

    }

    public PokerLimit(String limit) {
        this.limit = limit;

    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }
}