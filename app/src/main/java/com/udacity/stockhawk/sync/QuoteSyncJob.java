package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntDef;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;


public final class QuoteSyncJob {

    private static final int ONE_OFF_ID = 2;
    public static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 1;
    // this is a debug flag remove to distribution
    private static boolean enable_historic = false;

    //annotations of the resulting queries
    @Retention(RetentionPolicy.SOURCE)
    @IntDef( {
        INTERVAL_WEEKLY,INTERVAL_MONTH,INTERVAL_SIZE
    })
    public  @interface TimeInterval{};


    public static final int INTERVAL_WEEKLY = 0;
    public static final int INTERVAL_MONTH = 1;
    public static final int INTERVAL_SIZE = 2;

    //TODO PUT HERE THE ANNOTATIONS OF THE INTDEF LIKE A ENUM (WITH THE INTERVAL)
    //TODO so you have to decide your ranges in the period:


    private QuoteSyncJob() {
    }

    static void getQuotes(Context context) {

        Timber.d("Running sync job");
        Interval [] theIntervals={Interval.DAILY, Interval.DAILY, Interval.MONTHLY,};//,Interval.DAILY};
        int [] calendar_from = {Calendar.WEEK_OF_MONTH,Calendar.MONTH,Calendar.YEAR, };//,Calendar.DAY_OF_WEEK};
        String [] graphOptionsValues = context.getResources().getStringArray(R.array.pref_graph_option_values);

        //from.add(Calendar.MONTH,-1);
        //from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        try {

            Set<String> stockPref = PrefUtils.getStocks(context);
            Set<String> stockCopy = new HashSet<>();
            stockCopy.addAll(stockPref);
            String[] stockArray = stockPref.toArray(new String[stockPref.size()]);

            Timber.d(stockCopy.toString());

            if (stockArray.length == 0) {
                return;
            }

            Map<String, Stock> quotes = YahooFinance.get(stockArray);
            Iterator<String> iterator = stockCopy.iterator();

            Timber.d(quotes.toString());

            ArrayList<ContentValues> quoteCVs = new ArrayList<>();
            ArrayList<ContentValues> historicCVs = new ArrayList<>();

            while (iterator.hasNext()) {
                String symbol = iterator.next();
                Stock stock = quotes.get(symbol);
                StockQuote quote = stock.getQuote();
                if(isTheQuerySafe(quote)) {
                    float price = quote.getPrice().floatValue();
                    float change = quote.getChange().floatValue();
                    float percentChange = quote.getChangeInPercent().floatValue();

                    // WARNING! Don't request historical data for a stock that doesn't exist!
                    // The request will hang forever X_x
                    //context.getContentResolver().delete(Contract.HistoricQuote.URI,null,null);

                    ContentValues quoteCV = new ContentValues();
                    quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                    quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                    quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                    quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
                    quoteCVs.add(quoteCV);
                    if (enable_historic) {
                        for (int j = 0; j < theIntervals.length; j++) {
                            ContentValues historicValue = new ContentValues();
                            Calendar from = Calendar.getInstance();
                            Calendar to = Calendar.getInstance();
                            from.add(calendar_from[j], -1);

                            List<HistoricalQuote> history = stock.getHistory(from, to, theIntervals[j]);
                            StringBuilder historyBuilder = new StringBuilder();

                            for (HistoricalQuote it : history) {
                                historyBuilder.append(it.getDate().getTimeInMillis());
                                historyBuilder.append(", ");
                                historyBuilder.append(it.getClose());
                                historyBuilder.append("\n");
                            }
                            historicValue.put(Contract.HistoricQuote.COLUMN_QUOTE_SYMBOL,
                                    symbol);
                            historicValue.put(Contract.HistoricQuote.COLUMN_QUOTE_VIS_OPTION,
                                    graphOptionsValues[j]);
                            historicValue.put(Contract.HistoricQuote.COLUMN_HISTORIC,
                                    historyBuilder.toString());
                            historicCVs.add(historicValue);
                        }
                    }
                } else {
                    //TODO MAKE HERE THE ERROR HANDLING.
                    // Toast.makeText(context,"the requested qquote edoes not exist",Toast.LENGTH_LONG);
                }
            }

            context.getContentResolver()
                    .bulkInsert(
                            Contract.Quote.URI,
                            quoteCVs.toArray(new ContentValues[quoteCVs.size()]));
            if(enable_historic) {
                context.getContentResolver()
                        .bulkInsert(
                                Contract.HistoricQuote.URI,
                                historicCVs.toArray(new ContentValues[historicCVs.size()]));
            }
            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            context.sendBroadcast(dataUpdatedIntent);

        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }
    }

    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");


        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {

        schedulePeriodic(context);
        syncImmediately(context);

    }

    public static synchronized void syncImmediately(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());


        }
    }
    private static boolean isTheQuerySafe(StockQuote quote) {
        if(quote.getPrice() != null &&
                quote.getChange() != null &&
                quote.getChangeInPercent()!= null ) {
            return true;
        }
        return false;
    }



}
