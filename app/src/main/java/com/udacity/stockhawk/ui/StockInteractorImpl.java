package com.udacity.stockhawk.ui;


import android.database.Cursor;
import android.support.v4.app.LoaderManager;

import com.udacity.stockhawk.data.repository.StockRepository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.processors.PublishProcessor;

public class StockInteractorImpl implements StockInteractor {
   private StockRepository repository;

   @Inject
   public StockInteractorImpl(StockRepository repository) {
      this.repository = repository;
   }

   @Override public Single<Cursor> loadStock() {
      return repository.getStockCursor();
   }

   @Override public Completable deleteSymbolFromStock(String symbol) {
      return repository.deleteSymbolFromStock(symbol);
   }

   @Override public PublishProcessor<Cursor> getContentResolverUpdateProcessor() {
      return repository.getContentResolverUpdateProcessor();
   }

   @Override public PublishProcessor<Boolean> getContentResolverResetProcessor() {
      return repository.getContentResolverResetProcessor();
   }

   @Override public void startLoader(LoaderManager supportLoaderManager) {
      repository.startLoader(supportLoaderManager);
   }
}

interface StockInteractor {

   Single<Cursor> loadStock();

   Completable deleteSymbolFromStock(String symbol);

   public PublishProcessor<Cursor> getContentResolverUpdateProcessor();

   public PublishProcessor<Boolean> getContentResolverResetProcessor();

   void startLoader(LoaderManager supportLoaderManager);
}
