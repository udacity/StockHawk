package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.TaskParams;

import static com.sam_chordas.android.stockhawk.service.StockTaskService.TAG_END_DATE;
import static com.sam_chordas.android.stockhawk.service.StockTaskService.TAG_START_DATE;
import static com.sam_chordas.android.stockhawk.service.StockTaskService.TAG_SYMBOL;

/**
 * Created by sam_chordas on 10/1/15.
 * Last updated by juan-castillo on 10/03/16.
 */
public class StockIntentService extends IntentService {


    public StockIntentService(){
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");

        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        args.putString(TAG_SYMBOL, intent.getStringExtra(TAG_SYMBOL));
        args.putString(TAG_START_DATE, intent.getStringExtra(TAG_START_DATE));
        args.putString(TAG_END_DATE, intent.getStringExtra(TAG_END_DATE));

        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
    }
}
