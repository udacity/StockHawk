package com.udacity.stockhawk.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.udacity.stockhawk.widget.StockWidgetProvider;

/**
 * Created by fmoyader on 11/5/17.
 */

public class StockWidgetUtils {
    public static void updateWidget(Context context) {
        Intent intent = new Intent(context, StockWidgetProvider.class);
        intent.setAction(StockWidgetProvider.UPDATE_LIST);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                new ComponentName(context, StockWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        context.sendBroadcast(intent);
    }
}
