package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.util.Util;

import java.util.Locale;

import static com.udacity.stockhawk.util.Util.COLUMN_PROJECTION;

/**
 * Created by Dell on 3/31/2017.
 */
public class StockWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    String symbol, price, percent, history, newPercent;
    private Cursor cursor;
    private Context context = null;
    private int appWidgetId;

    public StockWidgetViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        query();
    }

    @Override
    public void onDataSetChanged() {
        query();
    }

    public void query() {
        final long token = Binder.clearCallingIdentity();
        cursor = context.getContentResolver().query(
                Contract.Quote.URI,
                COLUMN_PROJECTION,
                null,
                null,
                Contract.Quote.COLUMN_SYMBOL + Contract.Quote.SORTORDER_ASCENDING);
        Binder.restoreCallingIdentity(token);
    }

    @Override
    public void onDestroy() {
        // Releases the cursor when we are done with it
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    @Override
    public int getCount() {
        // returns the count of the cursor or 0 if cursor is null
        return cursor != null ? cursor.getCount() : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        String currencySymbol = context.getResources().getString(R.string.currency_symbol);

        // Check we have data at this view position - If we don't then exit with null
        if (!cursor.moveToPosition(position))
            return null;
        symbol = getCursorValue(Contract.Quote.POSITION_SYMBOL);
        price = getCursorValue(Contract.Quote.POSITION_PRICE);
        percent = getCursorValue(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
        history = getCursorValue(Contract.Quote.POSITION_HISTORY);


        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.list_item_quote);
        newPercent = String.format(Locale.US, "%.2f", Float.parseFloat(percent));

        row.setTextViewText(R.id.symbol, symbol);
        row.setTextViewText(R.id.price, currencySymbol + price);
        row.setTextViewText(R.id.change, Util.addSignOnPrice(newPercent));
        Util.setBackColourPrice(row, percent);

        Intent intent = new Intent();
        Bundle extras = new Bundle();
        extras.putString(StockWidgetProvider.EXTRA_SYMBOL, symbol);
        extras.putString(StockWidgetProvider.EXTRA_HISTORY, history);
        intent.putExtras(extras);

        row.setOnClickFillInIntent(android.R.id.text1, intent);

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private String getCursorValue(int stringIndex) {
        return cursor.getString(stringIndex);
    }
}
