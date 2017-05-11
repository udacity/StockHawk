package com.udacity.stockhawk.utils;

import android.database.Cursor;

import com.udacity.stockhawk.dto.Stock;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fmoyader on 9/5/17.
 */

public class StockContentProviderUtils {

    public static List<Stock> cursor2StocksList(Cursor cursor) {
        List<Stock> stocks = new ArrayList<Stock>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Stock stock = findStock(cursor);
                stocks.add(stock);
            }
        }

        return stocks;
    }

    private static Stock findStock(Cursor cursor) {
        String symbol = cursor.getString(
            cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)
        );
        float price = cursor.getFloat(
                cursor.getColumnIndex(Contract.Quote.COLUMN_PRICE)
        );
        float percentageChange = cursor.getFloat(
                cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE)
        );
        float change = cursor.getFloat(
                cursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE)
        );
        String history = cursor.getString(
                cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY)
        );

        Stock stock = new Stock(
                symbol, price,
                change, percentageChange,
                StockHistoryUtils.parse(symbol, history)
        );

        return stock;
    }

}
