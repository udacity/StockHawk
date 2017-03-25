package com.udacity.stockhawk.data.repository;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.udacity.stockhawk.core.App;
import com.udacity.stockhawk.data.provider.Contract;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.processors.PublishProcessor;


/**
 * {@link StockRepository} implementation.
 */
public class StockRepositoryImpl implements StockRepository, LoaderManager.LoaderCallbacks<Cursor> {

   private static final int STOCK_LOADER = 0;
   private final Context context;
   private final PublishProcessor<Cursor> resolverUpdatesProcessor = PublishProcessor.create();
   private final PublishProcessor<Boolean> resolverResetProcessor = PublishProcessor.create();

   @Inject public StockRepositoryImpl(App app) {
      this.context = app.getApplicationContext();
   }

   @Override public Completable deleteSymbolFromStock(String symbol) {
      return Completable.create(e -> {
         context.getContentResolver().delete(Contract.Quote.makeUriForStock(symbol), null, null);
         e.onComplete();
      });
   }

   @Override public PublishProcessor<Cursor> getContentResolverUpdateProcessor() {
      return resolverUpdatesProcessor;
   }

   @Override public PublishProcessor<Boolean> getContentResolverResetProcessor() {
      return resolverResetProcessor;
   }

   @Override public void startLoader(android.support.v4.app.LoaderManager supportLoaderManager) {
      supportLoaderManager.initLoader(STOCK_LOADER, null, this);
   }


   @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      return new CursorLoader(context,
         Contract.Quote.URI,
         Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
         null, null, Contract.Quote.COLUMN_SYMBOL);
   }

   @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
      resolverUpdatesProcessor.onNext(data);
   }

   @Override public void onLoaderReset(Loader<Cursor> loader) {
      resolverResetProcessor.onNext(true);
   }
}
