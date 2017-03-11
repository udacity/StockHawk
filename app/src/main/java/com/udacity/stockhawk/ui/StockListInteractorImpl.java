package com.udacity.stockhawk.ui;


import android.database.Cursor;

import com.udacity.stockhawk.data.repository.StockRepository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

public class StockListInteractorImpl implements StockListInteractor {
   private StockRepository repository;

   @Inject
   public StockListInteractorImpl(StockRepository repository) {
      this.repository = repository;
   }

   @Override public Single<Cursor> loadStock() {
      return repository.getStockCursor();
   }

   @Override public Completable deleteSymbolFromStock(String symbol) {
      return repository.deleteSymbolFromStock(symbol);
   }
}

interface StockListInteractor {

   Single<Cursor> loadStock();

   Completable deleteSymbolFromStock(String symbol);
}
