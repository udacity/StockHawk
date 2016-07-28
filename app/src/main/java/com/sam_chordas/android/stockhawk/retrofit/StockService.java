package com.sam_chordas.android.stockhawk.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Abhishek on 28-07-2016.
 */
public class StockService {

    public interface StockAPI {

        @GET("v1/public/yql?&format=json&diagnostics=true&env=store://datatables.org/alltableswithkeys&callback=")
        Call<StockResult> getStock(
                @Query("q") String symbol
        );

    }
}
