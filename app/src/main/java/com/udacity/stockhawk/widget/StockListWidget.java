package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.ui.QuoteHistoricActivity;

/**
 * Created by uelordi on 17/05/2017.
 */
public class StockListWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetID : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views,appWidgetID);
            } else {
                setRemoteAdapterV11(context, views,appWidgetID);
            }
            Intent clickIntentTemplate = new Intent(context,
                    QuoteHistoricActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_stock_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_stock_list, R.id.widget_empty);
            appWidgetManager.updateAppWidget(appWidgetID, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetID,
                                                            R.id.widget_stock_list);
        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context,intent);
        String action =  intent.getAction();

        if(action.equals(QuoteSyncJob.ACTION_DATA_UPDATED)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.
                    getAppWidgetIds(new ComponentName(context,getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,
                                                    R.id.widget_stock_list);
        }
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context,
                                  @NonNull final RemoteViews views,
                                  int appWidgetId) {
        Intent adapterIntent = new Intent(context,StockWidgetService.class);
        adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.widget_stock_list,adapterIntent );
    }
//
//    /**
//     * Sets the remote adapter used to fill in the list items
//     *
//     * @param views RemoteViews to set the RemoteAdapter
//     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context,
                                     @NonNull final RemoteViews views,
                                     int appWidgetId) {
        Intent adapterIntent = new Intent(context,StockWidgetService.class);
        adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(0, R.id.widget_stock_list, adapterIntent);
    }
}
