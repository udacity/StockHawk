package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");

        ResultReceiver resultReceiver = intent.getParcelableExtra(
                QuoteSyncResultReceiver.RECEIVER_TAG);

        QuoteSyncJob.getQuotes(resultReceiver, getApplicationContext());
    }
}
