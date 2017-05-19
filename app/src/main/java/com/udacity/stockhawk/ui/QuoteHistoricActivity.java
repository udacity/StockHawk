package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.content.QuoteData;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.graphic.ChartHandler;

import butterknife.BindView;
import butterknife.ButterKnife;


public class QuoteHistoricActivity extends AppCompatActivity
                                implements LoaderManager.LoaderCallbacks<Cursor>{
    private String currentStockName = "";
    private static final int HISTORIC_LOADER = 2;
    ChartHandler stockChart;
    FrameLayout mGraphRootLayout;
    String [] graphOptions;
    String [] graphOptionsLabels;
    private static final int NUMBER_OF_OPTIONS = 3;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_symbol)
    TextView mTvSymbol;

//    @SuppressWarnings("WeakerAccess")
//    @BindView(R.id.tv_change_percentage)
//    TextView mTvChangePercentage;
//    @SuppressWarnings("WeakerAccess")
//    @BindView(R.id.tv_change_abs)
//    TextView mTvChangeAbs;
//
//    @SuppressWarnings("WeakerAccess")
//    @BindView(R.id.tv_price)
//    TextView mTvPrice;
    //private static int mSelectionCounter = 0;

//    LineChart mChart;
    String mgraphSelectedOption;
    private QuoteData mQuoteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_historic);
        ButterKnife.bind(this);
        Intent parent_activity = getIntent();
        String stockNameKey = getString(R.string.pref_stocks_key);
        currentStockName = parent_activity.getStringExtra(stockNameKey);
        mGraphRootLayout = (FrameLayout)findViewById(R.id.historic_root_layout);

        startLoader();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quotes_historic_settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            int index = getOptionIndex(PrefUtils.getCurrentQuotesChartPref(this));
            if(index < graphOptions.length-1) {
                 index++;
            } else {
                index = 0;
            }
                mgraphSelectedOption = graphOptions[index];
                item.setTitle(graphOptionsLabels[index]);
                PrefUtils.setCurrentQuotesChartPref(this,mgraphSelectedOption);
                startLoader();
            }
        return super.onOptionsItemSelected(item);

    }
    public int getOptionIndex(String option) {
       for(int i=0;i<graphOptions.length;i++) {
           if(option.equals(graphOptions[i])){
               return i;
           }
       }
       return 0;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // String    symbol = args.getString(getString(R.string.pref_stocks_key));
        //return null;
        return new CursorLoader(this,
                Contract.HistoricQuote.makeUriForQuotes(currentStockName),
                Contract.HistoricQuote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
         mQuoteData = new QuoteData();
        int rowsCounted = mQuoteData.updateCursorData(data,this);
        mgraphSelectedOption = PrefUtils.getCurrentQuotesChartPref(this);
        int selectionIndex = getOptionIndex(mgraphSelectedOption);
        if(selectionIndex < rowsCounted) {
            stockChart = new ChartHandler(getApplicationContext(),currentStockName);
            stockChart.setData(mQuoteData.getHistoricByOption(mgraphSelectedOption), mgraphSelectedOption);
            stockChart.showGraph(mGraphRootLayout);
        }
        updateView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    public void startLoader() {
        graphOptions  = getResources().getStringArray(R.array.pref_graph_option_values);
        graphOptionsLabels = getResources().getStringArray(R.array.pref_graph_option_labels);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> historic_loader = loaderManager.getLoader(HISTORIC_LOADER);
        if ( historic_loader == null ) {
            getSupportLoaderManager().initLoader(HISTORIC_LOADER, null, this);
        } else {
            loaderManager.restartLoader(HISTORIC_LOADER, null, this);
        }
    }
    private void updateView() {
        if(mQuoteData != null) {
//            mTvPrice.setText(""+mQuoteData.getmPrice());
//            mTvChangePercentage.setText("%"+mQuoteData.getmPositionPercentageChange());
//            mTvChangeAbs.setText("$"+mQuoteData.getmPositionAbsChange());
            mTvSymbol.setText(""+mQuoteData.getmSymbol());
        } else {
            //TODO PUT HERE YOUR ERROR VIEW
        }

    }
}
