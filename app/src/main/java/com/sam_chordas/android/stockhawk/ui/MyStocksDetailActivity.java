package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;

public class MyStocksDetailActivity extends AppCompatActivity {
    LineChartView mLineChartView;
    LineSet mDataSet;

    private final String[] mLabels = {"Jan", "Fev", "Mar", "Apr", "Jun", "May", "Jul", "Aug", "Sep"};
    private final float[] mValues = {3.5f, 4.7f, 4.3f, 8f, 6.5f, 9.9f, 7f, 8.3f, 7.0f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        mLineChartView = (LineChartView) findViewById(R.id.linechart);
        mDataSet = new LineSet(mLabels, mValues);
        mLineChartView.addData(mDataSet);
        mLineChartView.setXAxis(true);
        mLineChartView.setYAxis(true);
        mDataSet.setSmooth(true);
        mDataSet.setColor(getResources().getColor(R.color.material_white));
        mDataSet.setFill(getResources().getColor(R.color.material_blue_700));
        mLineChartView.show();
    }
}
