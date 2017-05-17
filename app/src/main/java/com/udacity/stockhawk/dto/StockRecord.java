package com.udacity.stockhawk.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Wrapper for the stock value and the date when the stock was valued
 *
 * Created by fmoyader on 6/5/17.
 */

public class StockRecord implements Parcelable, Comparable<StockRecord>{
    /**
     * Stock value
     */
    private float value;

    /**
     * Date when de the stock was valued
     */
    private Date date;

    /**
     *
     * @param value stock value
     * @param date date when de the stock was valued
     */
    public StockRecord(float value, Date date) {
        this.value = value;
        this.date = date;
    }

    public float getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }

    protected StockRecord(Parcel in) {
        value = in.readFloat();
        date = new Date(in.readLong());
    }

    public static final Creator<StockRecord> CREATOR = new Creator<StockRecord>() {
        @Override
        public StockRecord createFromParcel(Parcel in) {
            return new StockRecord(in);
        }

        @Override
        public StockRecord[] newArray(int size) {
            return new StockRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(value);
        dest.writeLong(date.getTime());
    }

    @Override
    public int compareTo(@NonNull StockRecord stockRecord) {
        if (stockRecord == null) return 1;
        return this.date.compareTo(stockRecord.date);
    }
}
