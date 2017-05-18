package com.udacity.stockhawk.widget;

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

/**
 * Created by uelordi on 18/05/2017.
 */

public class StockWidgetRemoteAdapter implements
        RemoteViewsService.RemoteViewsFactory {
    Context context;
    Cursor data;

    public StockWidgetRemoteAdapter(Context context, Intent intent) {
        this.context = context;
        this.data = data;
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
            int symbolColumn = data.getColumnIndex(Contract.HistoricQuote.COLUMN_HISTORIC);
            int priceColumn = data.getColumnIndex(Contract.Quote.COLUMN_PRICE);
            views.setTextViewText(R.id.tv_widget_stock_name,data.getString(symbolColumn));
            views.setTextViewText(R.id.tv_widget_stock_price,data.getString(priceColumn));
            final Intent fillInIntent = new Intent();
            fillInIntent.setData(Contract.Quote.URI);
            views.setOnClickFillInIntent(R.id.widget_stock_list, fillInIntent);
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
