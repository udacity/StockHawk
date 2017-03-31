package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.DetailActivity;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by Dell on 3/31/2017.
 */

public class StockWidgetProvider extends AppWidgetProvider {

    public static String EXTRA_SYMBOL = "_SYMBOL";
    public static String EXTRA_HISTORY = "_HISTORY";
    public static String ACTION_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        int[] appWidgetIDs;
        if (intent.getAction().equals(ACTION_UPDATE)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetIDs = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIDs, R.id.stocklist);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {

            RemoteViews widgetViews = new RemoteViews(context.getPackageName(), R.layout.stock_widget);
            Intent svcIntent = new Intent(context, StockWidgetService.class);
            svcIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
            widgetViews.setRemoteAdapter(R.id.stocklist, svcIntent);
            widgetViews.setEmptyView(R.id.stocklist, R.id.emptyMessage);

            Intent clickAppIntent = new Intent(context, MainActivity.class);
            Intent clickIntent = new Intent(context, DetailActivity.class);
            PendingIntent appPI = PendingIntent.getActivity(context, 0, clickAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent clickPI = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widgetViews.setOnClickPendingIntent(R.id.stockTitle, appPI);
            widgetViews.setPendingIntentTemplate(R.id.stocklist, clickPI);

            appWidgetManager.updateAppWidget(appWidgetId, widgetViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}