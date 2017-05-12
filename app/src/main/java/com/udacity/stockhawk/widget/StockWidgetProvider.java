package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.DetailActivity;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.utils.NetworkUtils;

/**
 * Created by fmoyader on 8/5/17.
 */

public class StockWidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_LIST = "update_widget_list";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intentToStockWidgetService = new Intent(context, StockWidgetService.class);

            intentToStockWidgetService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intentToStockWidgetService.setData(Uri.parse(intentToStockWidgetService.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(),
                    R.layout.stock_widget);

            widget.setRemoteAdapter(R.id.lv_stock_list, intentToStockWidgetService);
            widget.setEmptyView(R.id.lv_stock_list, R.id.tv_empty_list_message);

            // General
            Intent intentToMainActivity = new Intent(context, MainActivity.class);
            PendingIntent intentClickGeneral = TaskStackBuilder.create(context)
                    .addNextIntent(intentToMainActivity)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setOnClickPendingIntent(R.id.ll_widget, intentClickGeneral);

            // Each row
            Intent intentToDetailActivity = new Intent(context, DetailActivity.class);
            PendingIntent intentClickTemplate = TaskStackBuilder.create(context)
                    .addNextIntent(intentToDetailActivity)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setPendingIntentTemplate(R.id.lv_stock_list, intentClickTemplate);

            appWidgetManager.updateAppWidget(appWidgetId, widget);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().equalsIgnoreCase(UPDATE_LIST)){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, StockWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_stock_list);
        }
    }

}
