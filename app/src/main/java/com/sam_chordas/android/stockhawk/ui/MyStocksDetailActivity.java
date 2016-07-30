package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.retrofit.Deserializable;
import com.sam_chordas.android.stockhawk.retrofit.StockItem;
import com.sam_chordas.android.stockhawk.retrofit.StockService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyStocksDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = getClass().getSimpleName();
    private final String BASE_URL = "https://query.yahooapis.com/";
    private List<StockItem> items;

    private LineChart mLineChart;

    ArrayList<Entry> entries;
    LineDataSet mLineDataSet;
    LineData data;

    ArrayList<String> mDate = new ArrayList<>();
    ArrayList<Float> mCloseValue = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        mLineChart = (LineChart) findViewById(R.id.linechart);
        final String stockSymbol = getIntent().getStringExtra("symbol");
        String endDate = Utils.getEndDate();
        final String startDate = Utils.getStartDate();
        Log.v(LOG_TAG, startDate + " " + endDate);
        String query = "select * from yahoo.finance.historicaldata where symbol='" +
                stockSymbol +
                "' and startDate ='" + startDate + "'and endDate ='" + endDate + "'";


        //Think of a TypeToken as a way of creating, manipulating, and querying Type
        // (and, implicitly Class) objects in a way that respects generics.
        //you can't pass around generic Class objects at runtime --
        // you might be able to cast them and pretend they're generic, but they really aren't.
        Type listtype = new TypeToken<List<StockItem>>() {
        }.getType();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder().registerTypeAdapter(listtype, new Deserializable()).create()))
                .build();

        StockService.StockAPI api = retrofit.create(StockService.StockAPI.class);
        Call<List<StockItem>> call = api.getStock(query);

        call.enqueue(new Callback<List<StockItem>>() {
            @Override
            public void onResponse(Call<List<StockItem>> call, Response<List<StockItem>> response) {
                items = response.body();
                for (StockItem item : items) {
                    mDate.add(item.getDate());
                    mCloseValue.add(item.getClose());
                }
                setData(mDate, mCloseValue, stockSymbol);
            }

            @Override
            public void onFailure(Call<List<StockItem>> call, Throwable t) {

            }
        });

    }

    public void setData(ArrayList<String> date, ArrayList<Float> closeValue, String stockSymbol) {
        // no description text
        mLineChart.setDescription("Stock's Value Over Time");
        mLineChart.setNoDataTextDescription(getString(R.string.no_data_text_description));

        mLineChart.setScaleEnabled(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(true);

        mLineChart.setBackgroundColor(Color.LTGRAY);

        //Add Data
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < closeValue.size(); i++) {
            entries.add(new Entry(closeValue.get(i), i));
            Log.v(LOG_TAG, "Close Value: " + closeValue.get(i));
        }

        LineDataSet mLineDataSet = new LineDataSet(entries, stockSymbol);
        mLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);


        data = new LineData(date, mLineDataSet);
        Log.v(LOG_TAG, String.valueOf(date));
        mLineChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mLineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        //xAxis.setDrawAxisLine(true);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawZeroLine(false);
        //leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setTextColor(ColorTemplate.getHoloBlue());
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        //rightAxis.setGranularityEnabled(true);

    }
}
