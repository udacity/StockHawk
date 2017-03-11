package com.udacity.stockhawk.data.repository;


import android.database.Cursor;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface StockRepository {

   public Single<Cursor> getStockCursor();

   public Completable deleteSymbolFromStock(String symbol);
}
