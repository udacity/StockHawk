package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import au.com.bytecode.opencsv.CSVParser;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by albertoruiz on 28/5/17.
 */

public class GraphActivity  extends AppCompatActivity {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.graph)
    LineChart mGraph;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.symbol)
    TextView mSymbol;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.change_percent)
    TextView mChangePercent;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.price)
    TextView mPrice;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.change_absolute)
    TextView mChangeAbsolute;

    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat dollarFormat;
    private DecimalFormat percentageFormat;

    public static void startActivity(Context context, String stock) {
        Intent intent = new Intent(context, GraphActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, stock);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String symbol = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (symbol == null || symbol.isEmpty()) {
            finish();
        }
        getSupportActionBar().setTitle(symbol);
        initializeFields(symbol);
    }

    @NonNull
    private Cursor fetchData(String symbol) {
        Cursor cursor = getContentResolver().query(
                Contract.Quote.makeUriForStock(symbol),
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[0]),
                null,
                null,
                null);
        if (cursor == null || cursor.getCount() == 0) finish();
        cursor.moveToFirst();
        return cursor;
    }

    private void initializeChart(String symbol, String historyData) {
        CSVParser historyParser = new CSVParser();
        List<Entry> entries = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(historyData, "\n");
        while (tokenizer.hasMoreTokens()) {
            try {
                String nextLine = tokenizer.nextToken();
                String[] strings = historyParser.parseLine(nextLine);
                long x = Long.parseLong(strings[0]);
                float y = Float.parseFloat(strings[1]);
                entries.add(new Entry(x, y));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(entries, new EntryXComparator());
        mGraph.getDescription().setEnabled(false);
        LineDataSet lineDataSet = new LineDataSet(entries, symbol);
        LineData data = new LineData(lineDataSet);
        lineDataSet.setColor(R.color.colorAccent);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setDrawCircles(false);
        mGraph.getXAxis().setValueFormatter(new IAxisValueFormatter() {

            private DateFormat mDateInstance = DateFormat.getDateInstance();

            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                long stamp = (long) v;
                return mDateInstance.format(new Date(stamp));
            }
        });
        mGraph.setData(data);
        mGraph.invalidate();
    }

    private void initializeFields(String symbol) {

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        Cursor cursor = fetchData(symbol);
        mSymbol.setText(symbol);

        float rawPrice = cursor.getFloat(Contract.Quote.POSITION_PRICE);
        mPrice.setText(dollarFormat.format(rawPrice));

        float rawPercentChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
        mChangePercent.setText(percentageFormat.format(rawPercentChange / 100));
        if (rawPercentChange > 0) {
            mChangePercent.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            mChangePercent.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        mChangeAbsolute.setText(dollarFormatWithPlus.format(rawAbsoluteChange));
        if (rawAbsoluteChange > 0) {
            mChangeAbsolute.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            mChangeAbsolute.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String historyData = cursor.getString(Contract.Quote.POSITION_HISTORY);
        cursor.close();
        initializeChart(symbol, historyData);
    }
}
