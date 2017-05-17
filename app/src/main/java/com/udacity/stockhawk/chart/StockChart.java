package com.udacity.stockhawk.chart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;

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
        chartDataSet.setDrawFilled(true);
        chartDataSet.setFillColor(ColorTemplate.colorWithAlpha(Color.CYAN, 150));

        // Description
        Description description = new Description();
        description.setText(model.getStockSymbol());
        description.setTextSize(22.0f);
        description.setTextColor(Color.WHITE);
        int windowWidth = getWindowWidth();
        description.setPosition(windowWidth - 130, 60f);
        chart.setDescription(description);

        // Legend
        chart.getLegend().setEnabled(false);

        // X Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(ColorTemplate.colorWithAlpha(Color.LTGRAY, 200));
        xAxis.setDrawAxisLine(true);
        xAxis.setYOffset(20f);
        xAxis.setAxisLineColor(Color.LTGRAY);
        xAxis.setAxisLineWidth(2.0f);
        xAxis.setValueFormatter(new MonthAxisFormatter(model.getRecords()));
        xAxis.setLabelRotationAngle(-45f);

        // Y Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setXOffset(20.0f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setAxisLineColor(Color.LTGRAY);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(ColorTemplate.colorWithAlpha(Color.LTGRAY, 200));
        leftAxis.setGranularityEnabled(false);
        leftAxis.setAxisLineWidth(2.0f);
        chart.getAxisRight().setEnabled(false);

        leftAxis.setAxisMaximum(model.getMaxPrice() * 1.05f);
        leftAxis.setAxisMinimum(0f);
    }

    private void addDataToChart() {
        Collections.sort(records);

        List<Entry> entriesToChart = new ArrayList<>();

        int position = 0;
        for (StockRecord record : records) {
            entriesToChart.add(new Entry(position++, record.getValue()));
        }

        chartDataSet = new LineDataSet(entriesToChart, null);

        LineData lineData = new LineData(chartDataSet);
        lineData.setValueTextSize(35.0f);
        chart.setData(lineData);
        chart.invalidate();
        chart.notifyDataSetChanged();
    }

    public int getWindowWidth() {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}
