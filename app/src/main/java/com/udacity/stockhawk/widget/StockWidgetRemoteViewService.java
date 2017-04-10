package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract.Quote;

import java.util.List;

/**
 * Created by katsiarynamashokha on 4/4/17.
 */

public class StockWidgetRemoteViewService extends RemoteViewsService {
    static final int INDEX_WEATHER_ID = 0;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                List<String> stockList = Quote.QUOTE_COLUMNS;
                String[] stockArray = new String[stockList.size()];
                stockArray = stockList.toArray(stockArray);
                data = getContentResolver().query(
                        Quote.URI,
                        stockArray,
                        null,
                        null,
                        Quote.COLUMN_SYMBOL);
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
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.stock_widget_detail_list_item);

                String stockSymbol = data.getString(Quote.POSITION_SYMBOL);
                Float stockPrice = data.getFloat(Quote.POSITION_PRICE);
                Float absoluteStockChange = data.getFloat(Quote.POSITION_ABSOLUTE_CHANGE);

                views.setTextViewText(R.id.stock_name, stockSymbol);
                views.setTextViewText(R.id.stock_price, stockPrice.toString());
                views.setTextViewText(R.id.stock_change, absoluteStockChange.toString());

                final Intent fillIntent = new Intent();
                fillIntent.setData(Quote.makeUriForStock(stockSymbol));
                views.setOnClickFillInIntent(R.id.widget_list_item, fillIntent);
                return views;
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
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_WEATHER_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
