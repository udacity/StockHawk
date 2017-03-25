package com.udacity.stockhawk.data.repository;


import android.database.Cursor;
import android.support.v4.app.LoaderManager;

import io.reactivex.Completable;
import io.reactivex.processors.PublishProcessor;

/**
 * Stock repository interface.
 */
public interface StockRepository {
   /**
    * Completable which delete a symbol from the stock.
    */
   Completable deleteSymbolFromStock(String symbol);

   /**
    * Custom {@link PublishProcessor} to check {@link android.content.ContentResolver} updates.
    */
   PublishProcessor<Cursor> getContentResolverUpdateProcessor();

   /**
    * Custom {@link PublishProcessor} to check {@link android.content.ContentResolver} reset calls.
    */
   PublishProcessor<Boolean> getContentResolverResetProcessor();

   /**
    * Method to initialize the loader.
    */
   void startLoader(LoaderManager supportLoaderManager);
}
