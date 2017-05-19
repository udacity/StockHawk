package com.udacity.stockhawk.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.udacity.stockhawk.data.Contract.Quote;

import timber.log.Timber;


class DbHelper extends SQLiteOpenHelper {


    private static final String NAME = "StockHawk.db";
    private static final int VERSION = 3;


    DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String quoteTable = "CREATE TABLE " + Quote.TABLE_NAME + " ("
                + Quote._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Quote.COLUMN_SYMBOL + " TEXT NOT NULL, "
                + Quote.COLUMN_PRICE + " REAL NOT NULL, "
                + Quote.COLUMN_ABSOLUTE_CHANGE + " REAL NOT NULL, "
                + Quote.COLUMN_PERCENTAGE_CHANGE + " REAL NOT NULL, "
                + "UNIQUE (" + Quote.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";
        Timber.d("the table "+Quote.TABLE_NAME+"\n"+quoteTable);
        String historicTable = "CREATE TABLE " + Contract.HistoricQuote.TABLE_NAME + " ("
                + Contract.HistoricQuote._ID +"INTEGER PRIMARY KEY,"
                + Contract.HistoricQuote.COLUMN_QUOTE_SYMBOL + " TEXT NOT NULL , "
                + Contract.HistoricQuote.COLUMN_QUOTE_VIS_OPTION + " TEXT NOT NULL, "
                + Contract.HistoricQuote.COLUMN_HISTORIC + " text NOT NULL,"
                + " UNIQUE ("+Contract.HistoricQuote.COLUMN_QUOTE_SYMBOL+", "
                + Contract.HistoricQuote.COLUMN_QUOTE_VIS_OPTION+") ON CONFLICT REPLACE"+
                " ); ";
        Timber.d("the table "+Contract.HistoricQuote.TABLE_NAME+"\n"+historicTable);
        try {
        db.execSQL(quoteTable);
        db.execSQL(historicTable);
        } catch(SQLException e) {
            Timber.d("SQLITE ERROR -> "+ e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + Quote.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + Contract.HistoricQuote.TABLE_NAME);


        onCreate(db);
    }
}
