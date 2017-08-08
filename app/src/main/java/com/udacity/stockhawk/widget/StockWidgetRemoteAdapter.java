package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by uelordi on 18/05/2017.
 */

public class StockWidgetRemoteAdapter implements
        RemoteViewsService.RemoteViewsFactory {
    Cursor data;
    int mWidgetId;
    private final Context context;
    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;
    public StockWidgetRemoteAdapter(Context context, Intent intent) {
        this.context = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

    }

    public void onCreate() {
            // Nothing to do
    }

        @Override
        public void onDataSetChanged() {
            if (data != null) {
                data.close();
            }
            final long identityToken = Binder.clearCallingIdentity();
            data = context.getContentResolver().query(Contract.Quote.URI,
                                             Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                                             null,
                                             null,
                                             Contract.Quote.COLUMN_SYMBOL);
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if (data != null) {
                data.close();
                data = null;
            }
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position == AdapterView.INVALID_POSITION ||
                    data == null || !data.moveToPosition(position)) {
                return null;
            }
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_detail_item);
            if (data.moveToPosition(position)) {
                int symbolColumn = data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
                int priceColumn = data.getColumnIndex(Contract.Quote.COLUMN_PRICE);
                int percentageColumn = data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE);
                int absChangeColumn = data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE);

                float priceRaw = data.getFloat(priceColumn);
                float percentageRaw = data.getFloat(percentageColumn);
                float absChangeRaw = data.getFloat(absChangeColumn);
                String symbol = data.getString(symbolColumn);
                String priceValue = dollarFormat.format(priceRaw);
                String percentageValue =percentageFormat.format(percentageRaw);
                int color;
                if(absChangeRaw > 0 ) {
                    views.setInt(R.id.tv_widget_stock_change_percentage,
                            "setBackgroundResource",
                            R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.tv_widget_stock_change_percentage,
                            "setBackgroundResource",
                            R.drawable.percent_change_pill_red);
                }
                views.setTextViewText(R.id.tv_widget_stock_name, symbol);
                views.setTextViewText(R.id.tv_widget_stock_price, priceValue);
                views.setTextViewText(R.id.tv_widget_stock_change_percentage, percentageValue);

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(context.getString(R.string.pref_stocks_key),symbol);
                fillInIntent.setData(Contract.Quote.URI);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
            }
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(context.getPackageName(),
                        R.layout.widget_detail_item);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if (data.moveToPosition(position))
                //todo there will be a problem if the value is integer.
                return data.getLong(0);
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
}
