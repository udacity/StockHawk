package com.sam_chordas.android.stockhawk.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by sam_chordas on 10/5/15.
 * Last updated by juan-castillo on 10/03/16.
 */
@Database(version = QuoteDatabase.VERSION)
public class QuoteDatabase {
    private QuoteDatabase(){}

    public static final int VERSION = 14;

    @Table(QuoteColumns.class)
    public static final String QUOTES = "quotes";

    @Table(HistoricalColumns.class)
    public static final String HISTORICAL = "historical";

}
