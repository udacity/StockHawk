package com.udacity.stockhawk.ui.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
 * Created by albertoruiz on 28/5/17.
 */

public class StockWidgetRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {

            private Cursor data = null;

            private DecimalFormat dollarFormatWithPlus;
            private DecimalFormat dollarFormat;
            private DecimalFormat percentageFormat;

            @Override
            public void onCreate() {

                dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");
                percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");

            }

            @Override
            public void onDataSetChanged() {

                if (data != null) {
                    data.close();
                }

                final long identitiyToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Contract.Quote.URI,
                        Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                        null,
                        null,
                        null
                );

                Binder.restoreCallingIdentity(identitiyToken);

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
                return data == null ? 0 : data.getCount();            }

            @Override
            public RemoteViews getViewAt(int position) {

                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_item_quote);

                String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
                String price = dollarFormat.format(data.getFloat(Contract.Quote.POSITION_PRICE));
                float absoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
                float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

                views.setTextViewText(R.id.symbol, symbol);
                views.setTextViewText(R.id.price, price);

                if (absoluteChange > 0) {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                String percentage = percentageFormat.format(percentageChange / 100);
                views.setTextViewText(R.id.change_percent, percentage);

                final Intent fillIntent = new Intent();
                Uri stockUri = Contract.Quote.makeUriForStock(symbol);
                fillIntent.setData(stockUri);
                views.setOnClickFillInIntent(R.id.lListItem, fillIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex(Contract.Quote._ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}