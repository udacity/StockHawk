package com.udacity.stockhawk.content;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Created by uelordi on 14/05/2017.
 */

public class QuoteData implements Parcelable {
    String mSymbol;
    float mPrice;
    float mPositionAbsChange;
    float mPositionPercentageChange;
    HashMap<String,String> mHistoricValues;

    public QuoteData(Parcel in) {
        mSymbol = in.readString();
        mPrice = in.readFloat();
        mPositionAbsChange = in.readFloat();
        mPositionPercentageChange = in.readFloat();
    }

    public static final Creator<QuoteData> CREATOR = new Creator<QuoteData>() {
        @Override
        public QuoteData createFromParcel(Parcel in) {
            return new QuoteData(in);
        }

        @Override
        public QuoteData[] newArray(int size) {
            return new QuoteData[size];
        }
    };

    public QuoteData() {

    }

    public String getmSymbol() {
        return mSymbol;
    }

    public void setmSymbol(String mSymbol) {
        this.mSymbol = mSymbol;
    }

    public float getmPrice() {
        return mPrice;
    }

    public void setmPrice(float mPrice) {
        this.mPrice = mPrice;
    }

    public float getmPositionAbsChange() {
        return mPositionAbsChange;
    }

    public void setmPositionAbsChange(float mPositionAbsChange) {
        this.mPositionAbsChange = mPositionAbsChange;
    }

    public float getmPositionPercentageChange() {
        return mPositionPercentageChange;
    }

    public void setmPositionPercentageChange(float mPositionPercentajeChange) {
        this.mPositionPercentageChange = mPositionPercentajeChange;
    }
    public String getHistoricByOption(@NotNull String option) {
        if(mHistoricValues!= null ) {
            if(mHistoricValues.size()>0) {
                return mHistoricValues.get(option);
            }
            else {
                return null;
            }
        }
        return null;
    }
    public int updateCursorData(Cursor data,Context context) {
        //update the basic data:
        mHistoricValues = new HashMap<>();


        int selectionIndex = -1;
        if (data.getCount() != 0) {
            for(int i=0;i<data.getCount();i++) {
                data.moveToPosition(i);
                int symbolColumn = data.getColumnIndex(Contract.HistoricQuote.COLUMN_QUOTE_SYMBOL);
                int intervalType = data.getColumnIndex(Contract.HistoricQuote.COLUMN_QUOTE_VIS_OPTION);
                int priceColumn = data.getColumnIndex(Contract.Quote.COLUMN_PRICE);
                int perc_change =data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE);
                int abs_change =   data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE);

                mSymbol = data.getString(symbolColumn);
                mPrice = data.getFloat(priceColumn);
                mPositionPercentageChange = data.getFloat(perc_change);
                mPositionAbsChange = data.getFloat(abs_change);
                mHistoricValues.put(data.getString(intervalType),data.getString(symbolColumn));
            }
        }
        return data.getCount();
    }
    // public ContentValues toContentValues[]



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mSymbol);
        dest.writeFloat(mPrice);
        dest.writeFloat(mPositionAbsChange);
        dest.writeFloat(mPositionPercentageChange);
    }

}
