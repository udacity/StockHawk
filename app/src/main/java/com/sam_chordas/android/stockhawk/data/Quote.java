package com.sam_chordas.android.stockhawk.data;

/**
 * Created by juan-castillo on 10/07/2016.
 */

public class Quote {

    private long id;
    private String symbol;
    private String price;
    private String percent;
    private boolean isUp;

    public Quote(long id, String symbol, String price, String percent, boolean isUp) {
        this.id = id;
        this.symbol = symbol;
        this.price = price;
        this.percent = percent;
        this.isUp = isUp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean isUp) {
        this.isUp = isUp;
    }
}
