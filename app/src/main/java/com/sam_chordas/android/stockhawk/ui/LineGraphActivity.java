package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.HistoricalColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.service.StockIntentService;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.sam_chordas.android.stockhawk.rest.Utils.hasConnectivity;
import static com.sam_chordas.android.stockhawk.rest.Utils.networkToast;
import static com.sam_chordas.android.stockhawk.service.StockTaskService.TAG_END_DATE;
import static com.sam_chordas.android.stockhawk.service.StockTaskService.TAG_HISTORIC;
import static com.sam_chordas.android.stockhawk.service.StockTaskService.TAG_START_DATE;
import static com.sam_chordas.android.stockhawk.service.StockTaskService.TAG_SYMBOL;

/**
 * Created by juan-castillo on 10/03/2016.
 */

/**
 * Activity which fetches historical data for a specific stock and handles creating a graph.
 */
public class LineGraphActivity extends AppCompatActivity {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final static String START_DATE_TYPE = "start_date";
    private final static String END_DATE_TYPE = "end_date";

    private TextView symbolView;
    private LineChart chart;

    private String symbol;
    private String startDate;
    private String endDate;

    private String[] months = new DateFormatSymbols().getMonths();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        // Get extras
        Bundle extras = getIntent().getExtras();
        symbol = extras.getString(TAG_SYMBOL);

        // Setup symbol view
        symbolView = (TextView) findViewById(R.id.chart_title);
        symbolView.setText(symbol);

        // Setup Chart view
        chart = (LineChart) findViewById(R.id.line_chart);

        // Setup dates to eventually fetch historical stock data for the past year
        Date endDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -366);
        Date startDate = calendar.getTime();
        this.startDate = sdf.format(startDate);
        this.endDate = sdf.format(endDate);

        if (savedInstanceState == null) {
            if (hasConnectivity(this)){
                updateHistoricalData();
            } else{
                networkToast(getApplicationContext());
            }
        }

        // Create line chart
        createGraph();
    }

    /**
     * Fetches historical stock data using YAHOO API's forwarded to a GcmTaskService
     */
    private void updateHistoricalData() {
        Intent stockServiceIntent = new Intent(this, StockIntentService.class);
        stockServiceIntent.putExtra("tag", TAG_HISTORIC);
        stockServiceIntent.putExtra(TAG_SYMBOL, symbol);
        stockServiceIntent.putExtra(TAG_START_DATE, startDate);
        stockServiceIntent.putExtra(TAG_END_DATE, endDate);
        startService(stockServiceIntent);
    }

    /**
     * Graph's the historical stock data
     */
    private void createGraph() {
        Cursor cursor = null;
        List<Entry> entries = new ArrayList<Entry>();
        final List<String> labels = new ArrayList<String>();

        try {
            // Fetch historical data for specific stock(symbol)
            cursor = getContentResolver().query(
                    QuoteProvider.Historical.CONTENT_URI,
                    new String[]{HistoricalColumns.DATE, HistoricalColumns.CLOSE_PRICE},
                    HistoricalColumns.SYMBOL + " = ? AND " + HistoricalColumns.DATE
                            + " >= ? AND " +HistoricalColumns.DATE + " <= ?",
                    new String[]{symbol, startDate, endDate},
                    HistoricalColumns.DATE.concat(" ASC"));

            System.out.println("Graph Data received: " + cursor.getCount() + " records");

            if (cursor != null && cursor.getCount() > 0) {

                if (cursor.moveToFirst()) {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        String date = cursor.getString(cursor.getColumnIndex(HistoricalColumns.DATE));
                        int month = Integer.valueOf(date.substring(5, 7));
                        float v = cursor.getFloat(cursor.getColumnIndex(HistoricalColumns.CLOSE_PRICE));
                        String l = months[month-1].substring(0,3);

                        entries.add(new Entry(i, v));
                        labels.add(l);
                        System.out.println(l + ": " + v);

                        cursor.moveToNext();
                    }
                }
            }

            // Configure data
            LineDataSet lineDataSet = new LineDataSet(entries, "Close Price");
            lineDataSet.setFillDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_fill));
            lineDataSet.setColor(Color.WHITE);
            lineDataSet.setCircleColor(Color.WHITE);
            lineDataSet.setLineWidth(1f);
            lineDataSet.setCircleRadius(4f);
            lineDataSet.setDrawCircleHole(false);
            lineDataSet.setValueTextSize(9f);
            lineDataSet.setDrawFilled(true);
            lineDataSet.setValueTextSize(10f);
            lineDataSet.setValueTextColor(Color.YELLOW);

            LineData lineData = new LineData(lineDataSet);

            // Configure left Y-Axis
            YAxis yAxisLeft = chart.getAxisLeft();
            yAxisLeft.setDrawGridLines(false);
            yAxisLeft.setDrawAxisLine(false);
            yAxisLeft.setDrawLabels(false);

            // Configure right Y-Axis
            YAxis yAxisRight = chart.getAxisRight();
            yAxisRight.setDrawGridLines(false);
            yAxisRight.setDrawAxisLine(false);
            yAxisRight.setDrawLabels(false);

            // Configure X-Axis
            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawGridLines(false);
            xAxis.setTextColor(Color.YELLOW);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setLabelCount(labels.size()-1);
            xAxis.setValueFormatter(new AxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    int index = (int) value;
                    System.out.println("index: " + index + ", value: " + value);
                    return labels.get(index);
                }

                @Override
                public int getDecimalDigits() {
                    return 0;
                }
            });

            chart.setData(lineData);
            chart.setDrawGridBackground(false);
            chart.setDescription("");
            chart.setContentDescription(getString(R.string.historical_data));
            chart.invalidate();

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                cursor.close();
            } catch(Exception e){}
        }

    }

}
