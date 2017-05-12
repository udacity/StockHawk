package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.dto.Stock;
import com.udacity.stockhawk.ui.DetailActivity;
import com.udacity.stockhawk.utils.StockContentProviderUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class StockRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat dollarFormat;
    private DecimalFormat percentageFormat;

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
        int layoutDirection = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault());
        int layoutId;
        if (layoutDirection == View.LAYOUT_DIRECTION_LTR) {
            layoutId = R.layout.list_item_quote;
        } else {
            layoutId = R.layout.list_item_quote_rtl;
        }

        RemoteViews row = new RemoteViews(context.getPackageName(),
                layoutId);

        Stock stock = stocks.get(position);

        row.setTextViewText(R.id.symbol, stock.getSymbol());
        row.setTextColor(R.id.symbol, Color.LTGRAY);
        row.setTextViewText(R.id.price, stock.getPrice() + "");
        row.setTextColor(R.id.price, Color.LTGRAY);

        String absoluteChange = dollarFormatWithPlus.format(stock.getAbsoluteChange());
        String percentageChange = percentageFormat.format(stock.getPercentageChange() / 100);

        if (PrefUtils.getDisplayMode(context)
                .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
            row.setTextViewText(R.id.change, absoluteChange + "");
        } else {
            row.setTextViewText(R.id.change, percentageChange + "");
        }

        if (stock.getAbsoluteChange() > 0) {
            row.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            row.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
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