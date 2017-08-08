package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import timber.log.Timber;

/**
 * Created by uelordi on 17/05/2017.
 */

public class StockWidgetService extends RemoteViewsService {
    private final String LOG_TAG = StockWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockWidgetRemoteAdapter(this, intent);
    }

}
