package com.udacity.stockhawk.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Inmutable class that stores the evolution of the stock value within time
 *
 * Created by fmoyader on 6/5/17.
 */

public class StockHistory implements Parcelable{

    /**
     * Stores a list of value-date pairs of the stock
     */
    private List<StockRecord> records;

    /**
     * Symbol of the stock
     */
    private String stockSymbol;

    /**
     *
     * @param stockSymbol Name of the stock
     */
    public StockHistory(String stockSymbol) {
        this.stockSymbol = stockSymbol;
        records = new ArrayList<>();
    }

    protected StockHistory(Parcel in) {
        records = in.createTypedArrayList(StockRecord.CREATOR);
        stockSymbol = in.readString();
    }

    public static final Creator<StockHistory> CREATOR = new Creator<StockHistory>() {
        @Override
        public StockHistory createFromParcel(Parcel in) {
            return new StockHistory(in);
        }

        @Override
        public StockHistory[] newArray(int size) {
            return new StockHistory[size];
        }
    };

    /**
     * Adds a new entry to the stock history
     * @param value stock value
     * @param milliseconds date in milliseconds when the stock had this value
     */
    public void addEntry(Float value, Long milliseconds) {
        if (value == null || milliseconds == null) return;

        Date date = new Date(milliseconds);
        StockRecord valueDatePair = new StockRecord(value, date);
        records.add(valueDatePair);
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public List<StockRecord> getRecords() {
        return new ArrayList<>(records);
    }

    public float getMaxPrice() {
        float maxPrice = 0.0f;
        for (StockRecord record : records) {
            if (record.getValue() > maxPrice) {
                maxPrice = record.getValue();
            }
        }
        return maxPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(records);
        dest.writeString(stockSymbol);
    }
}
