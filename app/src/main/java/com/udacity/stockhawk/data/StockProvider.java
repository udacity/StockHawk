package com.udacity.stockhawk.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class StockProvider extends ContentProvider {

    private static final int QUOTE = 100;
    private static final int QUOTE_FOR_SYMBOL = 101;
    private static final int HISTORIC = 200;
    private static final int HISTORIC_FOR_SYMBOL = 201;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private DbHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_QUOTE, QUOTE);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_QUOTE_WITH_SYMBOL, QUOTE_FOR_SYMBOL);
        matcher.addURI(Contract.AUTHORITY,Contract.PATH_HISTORIC,HISTORIC);
        matcher.addURI(Contract.AUTHORITY,Contract.PATH_HISTORIC_WITH_QUOTE,HISTORIC_FOR_SYMBOL);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case QUOTE:
                returnCursor = db.query(
                        Contract.Quote.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case HISTORIC_FOR_SYMBOL:
                returnCursor = getGraphInformatinoFromReferenceTable(
                        Contract.HistoricQuote.TABLE_NAME,
                        projection,
                        Contract.Quote.COLUMN_SYMBOL + " = ?",
                        new String[]{Contract.HistoricQuote.getIDFromStock(uri)},
                        sortOrder
                );

                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null){
            returnCursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriMatcher.match(uri)) {
            case QUOTE: {
                long result = db.insert(
                        Contract.Quote.TABLE_NAME,
                        null,
                        values
                );
                returnUri = Contract.Quote.URI;
                break;
            }
            case HISTORIC:
            {
                long result =  db.insert(
                        Contract.HistoricQuote.TABLE_NAME,
                        null,
                        values
                );
                returnUri = Contract.HistoricQuote.URI;
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null){
            context.getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;

        if (null == selection) {
            selection = "1";
        }
        switch (uriMatcher.match(uri)) {
            case QUOTE:
                rowsDeleted = db.delete(
                        Contract.Quote.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;

            case QUOTE_FOR_SYMBOL:
                String symbol = Contract.Quote.getStockFromUri(uri);
                rowsDeleted = db.delete(
                        Contract.Quote.TABLE_NAME,
                        '"' + symbol + '"' + " =" + Contract.Quote.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;
            case HISTORIC:

                //rowsDeleted = db.delete(Contract.HistoricQuote.TABLE_NAME,selection,selectionArgs);
                db.execSQL("TRUNCATE TABLE "+Contract.HistoricQuote.TABLE_NAME);
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        if (rowsDeleted != 0) {
            Context context = getContext();
            if (context != null){
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int returnCount = 0;
        Context context = getContext();
        switch (uriMatcher.match(uri)) {
            case QUOTE:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        db.insert(
                                Contract.Quote.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return returnCount;
            case HISTORIC:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                Contract.HistoricQuote.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }


                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }
            default:
                return super.bulkInsert(uri, values);
        }


    }
    private Cursor getGraphInformatinoFromReferenceTable(String tableName, String[] projection, String selection,
                                               String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(
                tableName + " INNER JOIN " + Contract.Quote.TABLE_NAME +
                        " ON " + tableName + "." + Contract.HistoricQuote.COLUMN_QUOTE_SYMBOL +
                        " = " +Contract.Quote.TABLE_NAME + "." + Contract.Quote.COLUMN_SYMBOL
        );

        return sqLiteQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
}
