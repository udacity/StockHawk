package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import timber.log.Timber;

public class DetailsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        TabHost.OnTabChangeListener{

    private static final int ID_DETAIL_LOADER = 353;
    private static final String Y_AXIS_DATE_FORMAT = "yyyy-MM-dd";

    private DecimalFormat percentageFormat;

    private String symbol;
    private Uri mUri;
    private int limit = 5;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.textViewStockPrice)
    TextView textViewStockPrice;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.textViewStockMargin)
    TextView textViewStockMargin;

    @BindView(android.R.id.tabhost)
    TabHost mTabHost;

    @BindView(android.R.id.tabcontent)
    View mTabContent;

    @BindView(R.id.stock_chart)
    LineChartView mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setupTabs();

        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        symbol = getIntent().getStringExtra("SYMBOL");
        setTitle(symbol);

        Timber.d("Symbol details: %s", symbol);

        mUri = Contract.Quote.makeUriForStock(symbol);

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }

        Timber.d("Cursor count: %d", data.getCount());

        textViewStockPrice.setText(
                data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_PRICE)));

        float percentageChange = Float.valueOf(data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE)
        ));

        float absoluteChange = Float.valueOf(data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE)
        ));

        if (percentageChange > 0) {
            textViewStockMargin.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            textViewStockMargin.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String percentage = percentageFormat.format(percentageChange / 100);

        textViewStockMargin.setText(
                String.valueOf(absoluteChange) + " ("+
                percentage + ")"
        );

        String historicalData = data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_HISTORY)
        );
        updateChart(historicalData);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void setupTabs() {

        mTabHost.setup();

        TabHost.TabSpec tabSpec;
        tabSpec = mTabHost.newTabSpec(getString(R.string.stock_detail_tab_5_days));
        tabSpec.setIndicator(getString(R.string.stock_detail_tab_5_days));
        tabSpec.setContent(android.R.id.tabcontent);
        mTabHost.addTab(tabSpec);

        tabSpec = mTabHost.newTabSpec(getString(R.string.stock_detail_tab_2_weeks));
        tabSpec.setIndicator(getString(R.string.stock_detail_tab_2_weeks));
        tabSpec.setContent(android.R.id.tabcontent);
        mTabHost.addTab(tabSpec);

        tabSpec = mTabHost.newTabSpec(getString(R.string.stock_detail_tab_1_month));
        tabSpec.setIndicator(getString(R.string.stock_detail_tab_1_month));
        tabSpec.setContent(android.R.id.tabcontent);
        mTabHost.addTab(tabSpec);

        mTabHost.setOnTabChangedListener(this);

        mTabHost.setCurrentTab(0);

    }

    private void updateChart(String historicalData) {
        try{
            String[] stocksPricePerDayArray = historicalData.split("\n");

            if(stocksPricePerDayArray.length > 0){


                Map<String,String> stockPerDayMap = new HashMap<>();

                for(String stockPricePerDay : Arrays.copyOfRange(stocksPricePerDayArray, 0, limit)){
                    String[] stockPricePerDayArray = stockPricePerDay.split(", ");

                    if( stockPricePerDayArray.length != 2 ) throw new Exception("Illegal format!");
                    stockPerDayMap.put(
                            stockPricePerDayArray[0],
                            stockPricePerDayArray[1]);
                }

                List<AxisValue> axisValuesX = new ArrayList<>();
                List<PointValue> pointValues = new ArrayList<>();

                int counter = -1;
                int stockPerDayMapSize = stockPerDayMap.size();

                Iterator iterator = stockPerDayMap.keySet().iterator();

                do {
                    counter++;

                    String dateInMillis = (String) iterator.next();
                    String bidPrice = stockPerDayMap.get(dateInMillis);

                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat(Y_AXIS_DATE_FORMAT, Locale.getDefault());
                    String formattedDate =
                            dateFormat.format(new Date(Long.valueOf(
                                    dateInMillis
                            )));

                    int x = stockPerDayMapSize - 1 - counter;

                    PointValue pointValue = new PointValue(x, Float.valueOf(bidPrice));
                    pointValue.setLabel(formattedDate);
                    pointValues.add(pointValue);

                    if (counter != 0 && counter % (stockPerDayMapSize / 3) == 0) {
                        AxisValue axisValueX = new AxisValue(x);
                        axisValueX.setLabel(formattedDate);
                        axisValuesX.add(axisValueX);
                    }

                } while (iterator.hasNext());

                Line line = new Line(pointValues).setColor(Color.WHITE).setCubic(false);

                List<Line> lines = new ArrayList<>();
                lines.add(line);
                LineChartData lineChartData = new LineChartData();
                lineChartData.setLines(lines);

                Axis axisX = new Axis(axisValuesX);
                axisX.setHasLines(true);
                axisX.setMaxLabelChars(4);
                lineChartData.setAxisXBottom(axisX);

                Axis axisY = new Axis();
                axisY.setAutoGenerated(true);
                axisY.setHasLines(true);
                axisY.setMaxLabelChars(4);
                lineChartData.setAxisYLeft(axisY);

                mChart.setInteractive(false);
                mChart.setLineChartData(lineChartData);
                mChart.setVisibility(View.VISIBLE);
            }

        }catch (Exception e ){
            Timber.e("Illegal format data!");
        }

        mTabContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTabChanged(String tabId) {
        if(tabId.equals(getString(R.string.stock_detail_tab_2_weeks))) limit = 14;
        else if(tabId.equals(getString(R.string.stock_detail_tab_5_days))) limit = 5;
        else if(tabId.equals(getString(R.string.stock_detail_tab_1_month))) limit = 30;
        getSupportLoaderManager().restartLoader(ID_DETAIL_LOADER, null, this);
    }
}
