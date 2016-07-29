package com.sam_chordas.android.stockhawk.retrofit;

import java.util.List;

/**
 * Created by Abhishek on 29-07-2016.
 */
public class StockResult {

    public List<StockItem> getStockItems() {
        return quote;
    }

    public void setStockItems(List<StockItem> quote) {
        this.quote = quote;
    }

    private List<StockItem> quote;



}
