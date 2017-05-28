package com.udacity.stockhawk.ui.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by albertoruiz on 28/5/17.
 */

public class StockWidgetIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public StockWidgetIntentService() {
        super("StockWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StockWidget.class));

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_stock);

            views.setTextViewText(R.id.wName, getString(R.string.app_name));

            views.setRemoteAdapter(R.id.wListView, new Intent(this, StockWidgetRemoteViewService.class));

            Intent mainIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingMainIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);

            views.setOnClickPendingIntent(R.id.wMain, pendingMainIntent);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.wListView);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
