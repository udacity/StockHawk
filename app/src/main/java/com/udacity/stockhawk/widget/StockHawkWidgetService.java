package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class StockHawkWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockHawkWidgetFactory(getApplicationContext(), intent);
    }
}
