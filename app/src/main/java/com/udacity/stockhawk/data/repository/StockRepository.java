package com.udacity.stockhawk.data.repository;


import android.database.Cursor;
import android.support.v4.app.LoaderManager;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.processors.PublishProcessor;

public interface StockRepository {

   public Single<Cursor> getStockCursor();

   public Completable deleteSymbolFromStock(String symbol);

   public PublishProcessor<Cursor> getContentResolverUpdateProcessor();

   public PublishProcessor<Boolean> getContentResolverResetProcessor();

   void startLoader(LoaderManager supportLoaderManager);
}
