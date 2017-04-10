package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Calendar;

import static com.udacity.stockhawk.R.id.chart;

/**
 * Created by katsiarynamashokha on 4/5/17.
 */

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 0;
    private Cursor mCursor;
    private Uri stockUri;
    public LineChart lineChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_details);
        stockUri = Uri.parse(getIntent().getStringExtra(MainActivity.STOCK_KEY));
        lineChart = (LineChart) findViewById(chart);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                stockUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0)
            drawChart();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void drawChart() {
        String history = "";
        String stockTitle = "";
        mCursor = getContentResolver().query(stockUri,
                null,
                null,
                null,
                null,
                null);
        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();

            int historyIndex = mCursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY);
            int stockIndex = mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
            history = mCursor.getString(historyIndex);
            stockTitle = mCursor.getString(stockIndex);
        }
        String[] historyArray = history.split("\n");
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = historyArray.length - 1; i > 0; i--) {
            String[] stock = historyArray[i].split(", ");
            entries.add(new Entry(Float.valueOf(stock[0]), Float.valueOf(stock[1])));
        }

        LineDataSet dataSet = new LineDataSet(entries, stockTitle + " stock graph");
        dataSet.setColor(0xffff00ff);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setLabelRotationAngle(-45);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                long time = (long) value;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int date = calendar.get(Calendar.DAY_OF_MONTH);
                return month + "/" + date + "/" + year;
            }

        });

        dataSet.setHighlightEnabled(true);
        dataSet.setLineWidth(2);

        lineChart.setScaleEnabled(true);
        lineChart.setBackgroundColor(0xffcccccc);

        dataSet.setValueTextColor(0xffff00ff);
        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}
