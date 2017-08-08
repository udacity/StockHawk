package com.udacity.stockhawk.data;


import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableList;

public final class Contract {

    static final String AUTHORITY = "com.udacity.stockhawk";
    static final String PATH_QUOTE = "quote";
    static final String PATH_HISTORIC = "historic";
    static final String PATH_QUOTE_WITH_SYMBOL = "quote/*";
    static final String PATH_HISTORIC_WITH_QUOTE = "historic/*";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }
    //todo try to define the foreign key of the second table:
    @SuppressWarnings("unused")
    public static final class Quote implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_QUOTE).build();
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_ABSOLUTE_CHANGE = "absolute_change";
        public static final String COLUMN_PERCENTAGE_CHANGE = "percentage_change";
        public static final int POSITION_ID = 0;
        public static final int POSITION_SYMBOL = 1;
        public static final int POSITION_PRICE = 2;
        public static final int POSITION_ABSOLUTE_CHANGE = 3;
        public static final int POSITION_PERCENTAGE_CHANGE = 4;
        public static final int POSITION_HISTORY = 5;
        public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_SYMBOL,
                COLUMN_PRICE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE
        );
        static final String TABLE_NAME = "quotes";

        public static Uri makeUriForStock(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        static String getStockFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }
    public static final class HistoricQuote implements BaseColumns {
        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_HISTORIC).build();

        public static final String COLUMN_HISTORIC = "historic_dataset";
        public static final String COLUMN_QUOTE_VIS_OPTION = "quote_vis_option";
        public static final String COLUMN_QUOTE_SYMBOL ="quote_symbol";
        public static final int POSITION_SYMBOL_ID = 0;
        public static final int POSITION_QUOTE_INTERVAL = 1;
        public static final int POSITION_HISTORIC_DATASET = 2;
        static final String TABLE_NAME = "historic_data";
        public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                COLUMN_QUOTE_SYMBOL,
                COLUMN_QUOTE_VIS_OPTION,
                COLUMN_HISTORIC,
                Quote.COLUMN_PRICE,
                Quote.COLUMN_PERCENTAGE_CHANGE,
                Quote.COLUMN_ABSOLUTE_CHANGE
        );

        public static Uri makeUriForQuotes(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }
        static String getIDFromStock(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }
    }
    public static final ImmutableList<String> COMPLETE_QUOTES_HISTORIC_COLUMNS =
            ImmutableList.of(
            );
}
