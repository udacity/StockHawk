package com.udacity.stockhawk.data.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.udacity.stockhawk.core.App;
import com.udacity.stockhawk.data.provider.Contract;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;


public class StockRepositoryImpl implements StockRepository {
   private final Context context;
   private ContentResolver contentResolver;

   @Inject public StockRepositoryImpl(ContentResolver contentResolver, App app) {
      this.contentResolver = contentResolver;
      this.context = app.getApplicationContext();
   }

   @Override public Single<Cursor> getStockCursor() {
      return Single.create(new SingleOnSubscribe<Cursor>() {
         @Override public void subscribe(SingleEmitter<Cursor> e) throws Exception {
            final Cursor stockCursor = contentResolver.query(Contract.Quote.URI,Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
               null, null, Contract.Quote.COLUMN_SYMBOL);
            if (stockCursor != null){
               e.onSuccess(stockCursor);
            } else {
               e.onError(new Throwable("No result for the given uri"));
            }
         }
      });
   }

   @Override public Completable deleteSymbolFromStock(String symbol) {
      return Completable.create(e -> {
         contentResolver.delete(Contract.Quote.makeUriForStock(symbol), null, null);
         e.onComplete();
      });
   }
}
