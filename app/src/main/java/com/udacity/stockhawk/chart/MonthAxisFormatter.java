package com.udacity.stockhawk.chart;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.dto.StockRecord;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by fmoyader on 10/5/17.
 */

public class MonthAxisFormatter implements IAxisValueFormatter {

    private List<String> axisLabels;

    public MonthAxisFormatter(List<StockRecord> records) {
        axisLabels = new ArrayList<>();
        Collections.sort(records);

        DateFormat monthFormat = new SimpleDateFormat("MMM");
        DateFormat yearFormat = new SimpleDateFormat("yy");
        DateFormat dayFormat = new SimpleDateFormat("dd");

        for (StockRecord record : records) {
            Date date = record.getDate();

            String day = dayFormat.format(date);
            String month = monthFormat.format(date);
            String year = yearFormat.format(date);

            String label = day + " " + month + " " + year;
            if (!axisLabels.contains(label)) {
                axisLabels.add(label);
            } else {
                axisLabels.add("");
            }
        }
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return axisLabels.get((int) value);
    }
}
