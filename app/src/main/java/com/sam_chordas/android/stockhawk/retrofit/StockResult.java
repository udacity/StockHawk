package com.sam_chordas.android.stockhawk.retrofit;

import java.util.List;

/**
 * Created by Abhishek on 29-07-2016.
 */
public class StockResult {

    public List<StockItem> getStockItems() {
        return stockItems;
    }

    public void setStockItems(List<StockItem> stockItems) {
        this.stockItems = stockItems;
    }

    private List<StockItem> stockItems;

    public static class StockItem {
        String Symbol;
        float Close;
        String Date;

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

}
