package com.udacity.stockhawk.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fmoyader on 9/5/17.
 */

public class Stock implements Parcelable{
    private String symbol;
    private float price;
    private float absoluteChange;
    private float percentageChange;
    private StockHistory history;

    public Stock(String symbol, float price, float absoluteChange, float percentageChange, StockHistory history) {
        this.symbol = symbol;
        this.price = price;
        this.absoluteChange = absoluteChange;
        this.percentageChange = percentageChange;
        this.history = history;
    }

    protected Stock(Parcel in) {
        symbol = in.readString();
        price = in.readFloat();
        absoluteChange = in.readFloat();
        percentageChange = in.readFloat();
        history = in.readParcelable(StockHistory.class.getClassLoader());
    }

    public static final Creator<Stock> CREATOR = new Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };

    public String getSymbol() {
        return symbol;
    }

    public float getPrice() {
        return price;
    }

    public float getAbsoluteChange() {
        return absoluteChange;
    }

    public float getPercentageChange() {
        return percentageChange;
    }

    public StockHistory getHistory() {
        return history;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(symbol);
        dest.writeFloat(price);
        dest.writeFloat(absoluteChange);
        dest.writeFloat(percentageChange);
        dest.writeParcelable(history, flags);
    }
}
