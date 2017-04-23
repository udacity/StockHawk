package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

public class StockHawkWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor cursor;
    private Context context;
    int mWidgetId;

    StockHawkWidgetFactory(Context context, Intent intent) {
        this.context = context;
        mWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {}

    @Override
    public int getCount() { return cursor.getCount(); }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(),
                R.layout.stocks_widget_list_item);

        if (cursor.moveToPosition(position)) {
            remoteViews.setTextViewText(
                    R.id.widget_stock_symbol,
                    cursor.getString(
                            cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)
                    )
            );
            remoteViews.setTextViewText(
                    R.id.widget_stock_price,
                    cursor.getString(
                            cursor.getColumnIndex(Contract.Quote.COLUMN_PRICE)
                    )
            );
            remoteViews.setTextViewText(
                    R.id.widget_stock_absolute_change,
                    cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE)
                    )
            );
        }

        Intent fillInIntent = new Intent();
        remoteViews.setOnClickFillInIntent(R.id.widget_list_item_layout, fillInIntent);

        return remoteViews;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }
        cursor = context.getContentResolver().query(
                Contract.Quote.makeUriForStocks(),
                new String[]{Contract.Quote._ID, Contract.Quote.COLUMN_SYMBOL,
                        Contract.Quote.COLUMN_PRICE,
                        Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
                        Contract.Quote.COLUMN_ABSOLUTE_CHANGE},
                null,
                null,
                null);
    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
        }
    }
}
