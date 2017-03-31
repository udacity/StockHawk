package com.udacity.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.widget.StockWidgetProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements IAxisValueFormatter {

    String history = "";
    String symbol = "";
    String[] History;
    List<Entry> entries;
    String[] data;
    ArrayList<String> stockDates;
    LineDataSet dataSet;
    LineData lineData;
    Legend legend;
    @BindView(R.id.lineChart)
    LineChart lineChart;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        Bundle arguments = getArguments();
        if (arguments != null) {
            history = arguments.getString(StockWidgetProvider.EXTRA_HISTORY);
            symbol = arguments.getString(StockWidgetProvider.EXTRA_SYMBOL);
        }

            entries = new ArrayList<>();
            stockDates = new ArrayList<>();
            History = history.split("\n");    // break up entries by the newline
            int timeCount = 0;

            for (String historyDataItem : History) {
                data = historyDataItem.split(", ");
                entries.add(new Entry(timeCount++, Float.parseFloat(data[1])));
                stockDates.add(data[0]);
            }

            lineChartStock();
        return view;
    }

    private void lineChartStock() {
        lineChart.setBackgroundColor(Color.WHITE);
        dataSet = new LineDataSet(entries, getString(R.string.legend_chart_stock_price));
        lineData = new LineData(dataSet);
        legend = lineChart.getLegend();
        legend.setTextColor(Color.BLACK);
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.CYAN);
        dataSet.setLineWidth(1f);
        dataSet.setFillAlpha(10);
        dataSet.setFillColor(Color.BLUE);
        dataSet.setDrawCircles(true);
        lineChart.setData(lineData);
        Description description = new Description();
        description.setText(symbol + " - " + getString(R.string.label_chart_stock_price));
        description.setTextColor(Color.BLACK);
        description.setTextSize(10f);
        lineChart.setDescription(description);
        XYAxis();
        lineChart.animateX(10000);
        // refresh
        lineChart.invalidate();

    }

    private void XYAxis() {
        XAxis XAxis = lineChart.getXAxis();
        XAxis.setValueFormatter(this);
        XAxis.setTextColor(Color.BLACK);
        YAxis YAxisL = lineChart.getAxis(YAxis.AxisDependency.LEFT);
        YAxis YAxisR = lineChart.getAxis(YAxis.AxisDependency.RIGHT);
        YAxisL.setTextColor(Color.BLACK);
        YAxisR.setTextColor(Color.BLACK);

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long) Float.parseFloat(stockDates.get((int) Math.floor(value))));
        int mYear = calendar.get(Calendar.YEAR) - 2000;
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        return mDay + "/" + mMonth + "/" + mYear;
    }
}