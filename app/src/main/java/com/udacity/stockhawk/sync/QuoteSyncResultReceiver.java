package com.udacity.stockhawk.sync;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class QuoteSyncResultReceiver extends ResultReceiver {

    public static final String RECEIVER_TAG ="RECEIVER_TAG";
    public static final int NOT_EXISTING_SYMBOL_CODE = 0;

    private Receiver mReceiver;

    public QuoteSyncResultReceiver(Handler handler) {
        super(handler);
    }
    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
