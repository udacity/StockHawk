package com.sam_chordas.android.stockhawk.retrofit;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Abhishek on 29-07-2016.
 */
public class StockItem implements Parcelable {
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


    protected StockItem(Parcel in) {
        Symbol = in.readString();
        Close = in.readFloat();
        Date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Symbol);
        dest.writeFloat(Close);
        dest.writeString(Date);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StockItem> CREATOR = new Parcelable.Creator<StockItem>() {
        @Override
        public StockItem createFromParcel(Parcel in) {
            return new StockItem(in);
        }

        @Override
        public StockItem[] newArray(int size) {
            return new StockItem[size];
        }
    };
}