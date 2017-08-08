package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.MainActivity;

import timber.log.Timber;

/**
 * Created by uelordi on 17/05/2017.
 */

public class WidgetUpdateIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public WidgetUpdateIntentService() {
        super(WidgetUpdateIntentService.class.getSimpleName());
        Timber.d(WidgetUpdateIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int appWidgetIds[] = intent.getIntArrayExtra(
                context.getString(Integer.parseInt(appWidgetManager.ACTION_APPWIDGET_ENABLED)));

        for (int appWidgetID : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            Intent returnIntent = new Intent(context, MainActivity.class);
            //TODO HERE YOU HAVE TO PUT YOUR DATA USING QUERY CURSOR.

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, returnIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetID, views);
        }
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget, description);
    }
}
