package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Dell on 3/31/2017.
 */
public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new StockWidgetViewsFactory(this.getApplicationContext(), intent));
    }
}