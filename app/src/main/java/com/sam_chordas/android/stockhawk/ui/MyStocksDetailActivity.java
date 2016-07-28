package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.retrofit.StockResult;
import com.sam_chordas.android.stockhawk.retrofit.StockService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyStocksDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = getClass().getSimpleName();
    LineChartView mLineChartView;
    LineSet mDataSet;
    private final String BASE_URL= "https://query.yahooapis.com/";

    private final String[] mLabels = {"Jan", "Fev", "Mar", "Apr", "Jun", "May", "Jul", "Aug", "Sep"};
    private final float[] mValues = {3.5f, 4.7f, 4.3f, 8f, 6.5f, 9.9f, 7f, 8.3f, 7.0f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        String stockSymbol = getIntent().getStringExtra("symbol");
        String endDate = Utils.getEndDate();
        final String startDate = Utils.getStartDate();
        Log.v(LOG_TAG, startDate + " " + endDate);
        String query = "select * from yahoo.finance.historicaldata where symbol='" +
                stockSymbol +
                "' and startDate ='" + startDate + "'and endDate ='" + endDate+"'";

        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StockService.StockAPI api= retrofit.create(StockService.StockAPI.class);

        Call<StockResult> call= api.getStock(query);

        call.enqueue(new Callback<StockResult>() {
            @Override
            public void onResponse(Call<StockResult> call, Response<StockResult> response) {
                List<StockResult.StockItem> items= response.body().getStockItems();
                Log.v(LOG_TAG,response.raw().toString());
            }

            @Override
            public void onFailure(Call<StockResult> call, Throwable t) {
                Log.e(LOG_TAG,t.toString());

            }
        });



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
