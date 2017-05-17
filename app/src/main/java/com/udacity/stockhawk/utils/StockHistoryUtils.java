package com.udacity.stockhawk.utils;

import com.udacity.stockhawk.dto.StockHistory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by fmoyader on 6/5/17.
 */

public class StockHistoryUtils {

    /**
     * Parses a string with all stock records formatted in the following way
     * "{dateInMillis}, {value}\r\n". The stock symbol is specified as argument.
     * @param stockSymbol stock symbol
     * @param stockHistoryString stock records
     * @return stock history
     */
    public static StockHistory parse(String stockSymbol, String stockHistoryString) {

        StockHistory stockHistory = new StockHistory(stockSymbol);

        try (BufferedReader bufferedReader = new BufferedReader(new StringReader(stockHistoryString))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] stockValuePairString = line.split(",");
                if (stockValuePairString != null && stockValuePairString.length == 2) {
                    Long dateInMillis = stockValuePairString[0] == null ?
                            0L : Long.valueOf(stockValuePairString[0].trim());
                    Float value = stockValuePairString[1] == null ?
                            0.0F : Float.valueOf(stockValuePairString[1].trim());

                    stockHistory.addEntry(value, dateInMillis);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stockHistory;
    }

}
