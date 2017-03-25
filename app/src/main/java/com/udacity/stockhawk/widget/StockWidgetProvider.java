package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.StockActivity;

/**
 * Provider used for the application widget.
 */
public class StockWidgetProvider extends AppWidgetProvider {

   @Override
   public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

      for (int appWidgetId : appWidgetIds) {
         RemoteViews views = new RemoteViews(context.getPackageName(),
            R.layout.widget_adapter);

         //Intent to StockActivity
         Intent intent = new Intent(context, StockActivity.class);
         PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
         views.setOnClickPendingIntent(R.id.widget, pendingIntent);

         //Set adapter
         views.setRemoteAdapter(R.id.widget_list,
            new Intent(context, StockWidgetRemoteService.class));

         // Set up collection items
         Intent clickIntentTemplate = new Intent(context, StockActivity.class);
         PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(clickIntentTemplate)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
         views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
         //Update widget
         appWidgetManager.updateAppWidget(appWidgetId, views);
      }
   }
}
