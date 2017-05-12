package com.udacity.stockhawk.chart;

import android.content.Context;

import com.udacity.stockhawk.dto.StockHistory;

/**
 * Class in charge of render Stock Evolution Chart and its interactions
 *
 * Created by fmoyader on 7/5/17.
 */
public class StockChartController {

    /**
     * Context of the Application
     */
    private Context context;

    /**
     * Contains the data to render the chart
     */
    private StockHistory model;

    /**
     *
     * @param context context of the application
     */
    public StockChartController(Context context) {
        this.context = context;
    }

    /**
     * Renders the chart
     * @param stockHistory
     */
    public void renderChart(StockHistory stockHistory) {
        this.model = stockHistory;
        new StockChart(context).renderChart(stockHistory);
        //configureAxis();
    }
}
