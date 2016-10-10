package com.sam_chordas.android.stockhawk.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.HistoricalColumns;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sam_chordas on 9/30/15.
 * Last updated by juan-castillo on 10/03/16
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 */
public class StockTaskService extends GcmTaskService {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public final static String TAG_SYMBOL = "symbol";
    public final static String TAG_START_DATE = "start_date";
    public final static String TAG_END_DATE = "end_date";
    public final static String TAG_HISTORIC = "historic";
    public final static String TAG_PERIODIC = "periodic";
    public final static String TAG_INIT = "init";
    public final static String TAG_ADD = "add";

    private final static int FAILURE = -1;

    private String LOG_TAG = StockTaskService.class.getSimpleName();
    private String BASE_URL = "https://query.yahooapis.com/v1/public/yql?q=";
    private String END_URL = "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    private OkHttpClient client = new OkHttpClient();
    private Context mContext;
    private StringBuilder mStoredSymbols = new StringBuilder();
    private boolean isUpdate;

    public StockTaskService(){}

    public StockTaskService(Context context){
        mContext = context;
    }

    String fetchData(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    public int onRunTask(TaskParams params){
        if (params.getTag().equals(TAG_HISTORIC)) {
            return getHistoricalData(params);
        } else {
            return getCurrentData(params);
        }
    }

    private int getHistoricalData(TaskParams params) {

        // Get extras from bundle
        Bundle extras = params.getExtras();
        String symbol = extras.getString(TAG_SYMBOL);
        String startDate = extras.getString(TAG_START_DATE);
        String endDate = extras.getString(TAG_END_DATE);

        if (startDate == null || endDate == null) {
            // Fetch data for 1 year if dates are null
            Date dateEnd = new Date();
            Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.add(Calendar.DATE, -366);
            Date dateStart = calendar.getTime();
            startDate = sdf.format(dateStart);
            endDate = sdf.format(dateEnd);
        }
        System.out.println(symbol + ", " + startDate + ", " + endDate);

        // Build URL String
        String[] dateStart = startDate.split("-");
        String[] dateEnd = endDate.split("-");
        StringBuilder urlStringBuilder = new StringBuilder()
                .append("http://chart.finance.yahoo.com/table.csv?s=")
                .append(symbol)
                .append("&a=").append(dateStart[1])
                .append("&b=").append(dateStart[2])
                .append("&c=").append(dateStart[0])
                .append("&d=").append(dateEnd[1])
                .append("&e=").append(dateEnd[2])
                .append("&f=").append(dateEnd[0])
                .append("&g=m&ignore=.csv");

        Request request = new Request.Builder()
                .url(urlStringBuilder.toString())
                .build();

        int result = GcmNetworkManager.RESULT_FAILURE;
        try {
            result = GcmNetworkManager.RESULT_SUCCESS;

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            if (responseBody != null && response.code() == 200) {
                // Delete current records
                int rowsDeleted = mContext.getContentResolver().delete(
                        QuoteProvider.Historical.CONTENT_URI,
                        HistoricalColumns.SYMBOL + "= ?",
                        new String[]{symbol}
                );

                System.out.println(rowsDeleted + " rows deleted.");

                // Update the records with new data
                try {
                    mContext.getContentResolver().applyBatch(
                            QuoteProvider.AUTHORITY,
                            Utils.historicalCsvToContentVals(symbol, responseBody));
                } catch (RemoteException | OperationApplicationException e) {
                    Log.e(LOG_TAG, "Error applying batch insert", e);
                }

            } else {
                return FAILURE;
            }

        } catch(IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private int getCurrentData(TaskParams params) {

        Cursor cursor;
        if (mContext == null){
            mContext = this;
        }

        StringBuilder urlStringBuilder = new StringBuilder();
        try {
            // Base URL for the Yahoo query
            urlStringBuilder.append(BASE_URL);
            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.quotes where symbol "
                    + "in (", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (params.getTag().equals(TAG_INIT) || params.getTag().equals(TAG_PERIODIC)) {
            isUpdate = true;
            cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                    new String[] { "Distinct " + QuoteColumns.SYMBOL }, null,
                    null, null);
            if (cursor.getCount() == 0 || cursor == null){
                // Init task. Populates DB with quotes for the symbols seen below
                try {
                    urlStringBuilder.append(
                            URLEncoder.encode("\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\")", "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                // Fetch graph data ahead of time
                params.getExtras().putString(TAG_SYMBOL, "YHOO");
                getHistoricalData(params);
                params.getExtras().putString(TAG_SYMBOL, "AAPL");
                getHistoricalData(params);
                params.getExtras().putString(TAG_SYMBOL, "GOOG");
                getHistoricalData(params);
                params.getExtras().putString(TAG_SYMBOL, "MSFT");
                getHistoricalData(params);

            } else if (cursor != null){
                DatabaseUtils.dumpCursor(cursor);
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++){
                    mStoredSymbols.append("\"")
                            .append(cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)))
                            .append("\",");

                    cursor.moveToNext();
                }
                mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), ")");

                try {
                    urlStringBuilder.append(URLEncoder.encode(mStoredSymbols.toString(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        } else if (params.getTag().equals(TAG_ADD)){
            isUpdate = false;
            // get symbol from params.getExtra and build query
            String stockInput = params.getExtras().getString(TAG_SYMBOL);
            try {
                urlStringBuilder.append(URLEncoder.encode("\""+stockInput+"\")", "UTF-8"));
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }

            // Fetch graph data ahead of time
            int response = getHistoricalData(params);
            if(response == FAILURE) {
                postToast();
                return FAILURE;
            }
        }
        // finalize the URL for the API query.
        urlStringBuilder.append(END_URL);

        String urlString;
        String getResponse;
        int result = GcmNetworkManager.RESULT_FAILURE;

        urlString = urlStringBuilder.toString();
        System.out.println(urlStringBuilder.toString());
        try {
            getResponse = fetchData(urlString);
            if (getResponse.contains("\"Change\":null")) {
                postToast();
                return FAILURE;
            }

            result = GcmNetworkManager.RESULT_SUCCESS;
            try {
                ContentValues contentValues = new ContentValues();
                // update ISCURRENT to 0 (false) so new data is current
                if (isUpdate){
                    contentValues.put(QuoteColumns.ISCURRENT, 0);
                    mContext.getContentResolver().update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
                            null, null);
                }
                mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
                        Utils.quoteJsonToContentVals(getResponse));

            } catch (RemoteException | OperationApplicationException e){
                Log.e(LOG_TAG, "Error applying batch insert", e);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }

    private void postToast() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext,
                        mContext.getString(R.string.stock_not_found),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
