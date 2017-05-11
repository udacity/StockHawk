package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.dto.Stock;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.DetailActivity;
import com.udacity.stockhawk.utils.StockContentProviderUtils;

import java.util.List;

/**
 * Created by fmoyader on 9/5/17.
 */

public class StockWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class StockRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private final int appWidgetId;
        private final Context context;
        private List<Stock> stocks;

        public StockRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            Log.i("", "");
        }

        @Override
        public void onDataSetChanged() {
            Cursor cursor = context.getContentResolver().query(
                    Contract.Quote.URI, null, null, null,
                    Contract.Quote.COLUMN_SYMBOL
            );

            stocks = StockContentProviderUtils.cursor2StocksList(cursor);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return stocks.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews row = new RemoteViews(context.getPackageName(),
                    R.layout.list_item_quote);

            Stock stock = stocks.get(position);

            row.setTextViewText(R.id.symbol, stock.getSymbol());
            row.setTextViewText(R.id.price, stock.getPrice() + "");

            if (PrefUtils.getDisplayMode(context)
                    .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
                row.setTextViewText(R.id.change, stock.getAbsoluteChange() + "");
            } else {
                row.setTextViewText(R.id.change, stock.getPercentageChange() + "");
            }

            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(DetailActivity.EXTRA_STOCK_HISTORY, stock.getHistory());
            row.setOnClickFillInIntent(R.id.ll_widget_row, fillInIntent);

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
    }

}
