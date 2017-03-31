package com.udacity.stockhawk.util;

import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

/**
 * Created by Dell on 3/31/2017.
 */

public class Util {
    private static String priceBackground =  "setBackgroundResource";
    public static final String[] COLUMN_PROJECTION = {
            Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
            Contract.Quote.COLUMN_HISTORY,
    };


    public static void setBackColourPrice(RemoteViews row, String percent) {
        if ( Float.parseFloat(percent) > 0 )
            row.setInt(R.id.change, priceBackground, R.color.material_green_700);
        else
            row.setInt(R.id.change, priceBackground, R.color.material_red_700);
    }

    public static String addSignOnPrice(String percent) {
        StringBuilder newPercent = new StringBuilder(percent);
        if (Float.parseFloat(percent) > 0 )
            newPercent.insert( 0, "+" );
        return newPercent.toString();
    }


}
