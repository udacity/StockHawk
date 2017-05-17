package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.chart.StockChartController;
import com.udacity.stockhawk.dto.StockHistory;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_STOCK_HISTORY = "stock_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_STOCK_HISTORY)) {
            StockHistory stockHistory = intent.getParcelableExtra(EXTRA_STOCK_HISTORY);
            if (stockHistory != null && !stockHistory.isEmpty()) {
                renderStockChart(stockHistory);
            }
        }
    }

    private void renderStockChart(StockHistory stockHistory) {
        new StockChartController(this).renderChart(stockHistory);
    }
}
