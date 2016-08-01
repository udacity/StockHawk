package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

    private static String LOG_TAG = Utils.class.getSimpleName();

    public static boolean showPercent = true;
    public static boolean sInvalidSymbol=false;

    public static ArrayList quoteJsonToContentVals(String JSON) throws JSONException {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject = null;
        JSONArray resultsArray = null;
        if (JSON != null)

            try {
                jsonObject = new JSONObject(JSON);
                if (jsonObject != null && jsonObject.length() != 0) {
                    jsonObject = jsonObject.getJSONObject("query");
                    int count = Integer.parseInt(jsonObject.getString("count"));
                    if (count == 1) {
                        jsonObject = jsonObject.getJSONObject("results")
                                .getJSONObject("quote");
                        if (jsonObject.getString("Bid").equals("null")) {
                            Log.v(LOG_TAG,"This part executed");
                            return new ArrayList();
                        }
                        batchOperations.add(buildBatchOperation(jsonObject));
                    } else {
                        resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

                        if (resultsArray != null && resultsArray.length() != 0) {
                            for (int i = 0; i < resultsArray.length(); i++) {
                                jsonObject = resultsArray.getJSONObject(i);
                                if (jsonObject.getString("Bid").equals("null")) {
                                    Log.v(LOG_TAG,"This part executed");
                                    return new ArrayList();
                                }
                                batchOperations.add(buildBatchOperation(jsonObject));
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "String to JSON failed: " + e);
            }
        return batchOperations;
    }

    public static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format(Locale.getDefault(), "%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format(Locale.getDefault(), "%.2f", round);
        StringBuilder changeBuffer = new StringBuilder(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String change = jsonObject.getString("Change");
            Log.v(LOG_TAG, "Change: " + change);
            builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                    jsonObject.getString("ChangeinPercent"), true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.ISCURRENT, 1);

            //Checking whether stock price has increased or decreased
            if (change.charAt(0) == '-') {
                builder.withValue(QuoteColumns.ISUP, 0);
            } else {
                builder.withValue(QuoteColumns.ISUP, 1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    public static String getEndDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return df.format(c.getTime());
    }

    public static String getStartDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -7);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(c.getTime());
    }

    //Get the network status
    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static boolean containWhiteSpace(String string)
    {
        return string.contains(" ");
    }

}
