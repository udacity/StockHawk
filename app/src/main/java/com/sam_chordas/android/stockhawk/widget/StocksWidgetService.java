package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.Quote;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juan-castillo on 10/7/2016.
 */

/**
 * RemoveViewsService for current stock quotes
 */
public class StocksWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory();
    }

    class ListRemoteViewsFactory implements RemoteViewsFactory {

        private List<Quote> quotes;

        @Override
        public void onCreate() {
            quotes = new ArrayList<>();
        }

        @Override
        public void onDataSetChanged() {

            // Clear current list
            quotes.clear();

            long token = Binder.clearCallingIdentity();
            try {
                // Get stock data
                Cursor cursor = getContentResolver().query(
                        QuoteProvider.Quotes.CONTENT_URI,
                        new String[] {QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[] {"1"},
                        null
                );

                // Add data to quotes list
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        for (int i = 0; i < cursor.getCount(); i++) {
                            quotes.add(new Quote(
                                            cursor.getLong(cursor.getColumnIndex(QuoteColumns._ID)),
                                            cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)),
                                            cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)),
                                            cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE)),
                                            cursor.getInt(cursor.getColumnIndex(QuoteColumns.ISUP)) == 1
                                    )
                            );
                            cursor.moveToNext();
                        }
                    }

                    cursor.close();
                }
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        @Override
        public void onDestroy() {
            quotes = null;
        }

        @Override
        public int getCount() {
            return quotes.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (quotes.size() > 0) {
                // Get quote at position
                Quote quote = quotes.get(position);

                // Setup RemoteViews
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_item_quote);
                remoteViews.setTextViewText(R.id.stock_symbol, quote.getSymbol());
                remoteViews.setTextViewText(R.id.bid_price, quote.getPrice());
                if (quote.isUp()) {
                    remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                    remoteViews.setTextViewText(R.id.change, quote.getPercent());
                } else {
                    remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                    remoteViews.setTextViewText(R.id.change, quote.getPercent());
                }

                return remoteViews;
            }

            return null;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(getPackageName(), R.layout.list_item_quote);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if (quotes.size() > 0) {
                return quotes.get(position).getId();
            }

            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
