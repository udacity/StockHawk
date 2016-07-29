package com.sam_chordas.android.stockhawk.retrofit;

/**
 * Created by Abhishek on 29-07-2016.
 */
public class StockItem {
    String Symbol;
    float Close;
    String Date;

    public StockItem(String Symbol,
                     float Close,
                     String Date) {
        this.Symbol = Symbol;
        this.Close = Close;
        this.Date = Date;

    }

    public void setSymbol(String symbol) {
        Symbol = symbol;
    }

    public void setClose(float close) {
        Close = close;
    }

    public void setDate(String date) {
        Date = date;
    }

    public float getClose() {
        return Close;
    }

    public String getDate() {
        return Date;
    }

    public String getSymbol() {
        return Symbol;
    }

}
