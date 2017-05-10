package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.FormatUtil;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final java.lang.String SYMBOL_PARAM = "stock_symbol";

    @BindView(R.id.error)
    TextView error;

    @BindView(R.id.chart)
    LineChart lineChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        error.setText(getString(R.string.error_no_stock_history_for_this_symbol));

        String symbol = getIntent().getStringExtra(SYMBOL_PARAM);
        if (symbol != null && !symbol.isEmpty()) {
            setTitle(symbol + " history");
            showHistoryGraph(symbol);
        }
    }

    private void showHistoryGraph(String symbol) {
        Cursor query = getContentResolver().query(
                Contract.Quote.URI.buildUpon().appendPath(symbol).build(),
                null, null, null, null);
        if (query != null && query.getCount() > 0) {
            query.moveToFirst();
            String history = query.getString(Contract.Quote.POSITION_HISTORY);
            query.close();

            List<Entry> entries = new ArrayList<>();
            final List<Long> timeValues = new ArrayList<>();
            int xIndex = 0;

            List<String[]> strings;
            CSVReader csvReader = new CSVReader(new StringReader(history));
            try {
                strings = csvReader.readAll();
                Collections.reverse(strings); // reverse history to show in correct date order
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            for (String[] pair : strings) {
                entries.add(new Entry(xIndex, Float.parseFloat(pair[1])));
                timeValues.add(xIndex, Long.valueOf(pair[0]));
                xIndex++;
            }

            lineChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float index, AxisBase axis) {
                    Date date = new Date(timeValues.get((int)index));
                    return new SimpleDateFormat("MM/YY", Locale.ENGLISH).format(date);
                }
            });
            formatChartForDataSet(new LineDataSet(entries, String.format("Stock price for %s", symbol)));

            error.setVisibility(View.GONE);
            lineChart.setVisibility(View.VISIBLE);
        }
    }

    private void formatChartForDataSet(LineDataSet lineDataSet) {
        int whiteColour = getResources().getColor(R.color.white);

        lineDataSet.setColor(getResources().getColor(R.color.colorAccent));
        lineDataSet.setDrawCircles(false);
        lineDataSet.setLineWidth(4f);

        DollarAxisValueFormatter dollarAxisValueFormatter = new DollarAxisValueFormatter();
        lineChart.getAxisLeft().setValueFormatter(dollarAxisValueFormatter);
        lineChart.getAxisRight().setValueFormatter(dollarAxisValueFormatter);

        lineChart.getXAxis().setGridColor(whiteColour);
        lineChart.getXAxis().setTextColor(whiteColour);
        lineChart.getAxisLeft().setGridColor(whiteColour);
        lineChart.getAxisLeft().setTextColor(whiteColour);
        lineChart.getAxisRight().setGridColor(whiteColour);
        lineChart.getAxisRight().setTextColor(whiteColour);

        lineChart.getLegend().setEnabled(false);

        lineChart.setData(new LineData(lineDataSet));
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private static class DollarAxisValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return FormatUtil.dollarFormat.format(value);
        }
    }
}
