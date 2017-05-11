package com.udacity.stockhawk.chart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.dto.StockHistory;
import com.udacity.stockhawk.dto.StockRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class in charge of configuring the aspect of the chart and fill data
 *
 * Created by fmoyader on 7/5/17.
 */

public class StockChart {

    /**
     * Context of the application
     */
    private Context context;

    /**
     * Data to fill the chart
     */
    private StockHistory model;

    /**
     * View for the chart to be rendered
     */
    private LineChart chart;

    /**
     * Points of the chart
     */
    private List<StockRecord> records;

    /**
     * Line rendered in the chart
     */
    private LineDataSet chartDataSet;

    /**
     * Constructor
     * @param context context of the application
     */
    public StockChart(Context context) {
        this.context = context;
        chart = (LineChart) ((Activity) context).findViewById(R.id.lc_stock_evolution);
    }

    public synchronized void renderChart(StockHistory model) {
        this.model = model;
        records = model.getRecords();

        addDataToChart();
        configureInteraction();
        styleChart();
        //configureAxis();
    }

    private void configureInteraction() {
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setScaleXEnabled(false);
        chart.setScaleYEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDragDecelerationEnabled(false);
        chart.setDragDecelerationFrictionCoef(0);
    }

    private void styleChart() {
        // Line
        chartDataSet.setColor(Color.CYAN);
        chartDataSet.setCircleColor(Color.BLUE);
        chartDataSet.setCircleColorHole(Color.CYAN);
        chartDataSet.setValueTextSize(35.0f);

        // Description
        Description description = new Description();
        description.setText(model.getStockSymbol());
        description.setTextSize(22.0f);
        description.setTextColor(Color.WHITE);
        chart.setDescription(description);

        // Legend
        chart.getLegend().setEnabled(false);

        // X Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setValueFormatter(new MonthAxisFormatter(model.getRecords()));
        xAxis.setLabelRotationAngle(45f);

        // Y Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setXOffset(20.0f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(false);
        chart.getAxisRight().setEnabled(false);
    }

    private void addDataToChart() {
        Collections.sort(records);

        List<Entry> entriesToChart = new ArrayList<>();

        for (StockRecord record : records) {
            entriesToChart.add(new Entry(record.getDate().getTime(), record.getValue()));
        }

        chartDataSet = new LineDataSet(entriesToChart, "");

        LineData lineData = new LineData(chartDataSet);
        lineData.setValueTextSize(35.0f);
        chart.setData(lineData);
        chart.invalidate();
        chart.notifyDataSetChanged();
    }

}
