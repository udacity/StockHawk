package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.FormatUtil;
import com.udacity.stockhawk.ui.DetailActivity;

public class StockWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {
            Cursor data;

            @Override
            public void onCreate() {}

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                data = getContentResolver().query(Contract.Quote.URI,
                        Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                        null, null, Contract.Quote.COLUMN_SYMBOL);
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
                if (data == null) return 0;
                return data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_list_item);

                data.moveToPosition(position);
                float rawPercentChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE) / 100;

                String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);

                remoteViews.setTextViewText(R.id.symbol,
                        symbol);
                remoteViews.setTextViewText(R.id.price,
                        FormatUtil.dollarFormat.format(data.getFloat(Contract.Quote.POSITION_PRICE)));
                remoteViews.setTextViewText(R.id.change,
                        FormatUtil.percentageFormat.format(rawPercentChange));

                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(DetailActivity.SYMBOL_PARAM, symbol);
                remoteViews.setOnClickFillInIntent(R.id.stock_list_item, fillInIntent);

//                Intent detailActivityIntent = new Intent(getApplicationContext(), DetailActivity.class);
//                intent.putExtra(DetailActivity.SYMBOL_PARAM, symbol);
//                PendingIntent launchDetailActivity = PendingIntent.getActivity(
//                        getApplicationContext(), 0, detailActivityIntent, 0);
//                remoteViews.setOnClickPendingIntent(R.id.symbol, launchDetailActivity);

                if (rawPercentChange > 0) {
                    remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                return remoteViews;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean hasStableIds() { return true; }
        };
    }

}
