package com.udacity.stockhawk.ui;

import android.support.v4.app.LoaderManager;

import com.udacity.stockhawk.core.presentation.BasePresenter;
import com.udacity.stockhawk.core.presentation.BasePresenterImpl;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Stock presenter implementation.
 */
public class StockPresenterImpl extends BasePresenterImpl<StockListView> implements StockPresenter {
   private StockInteractor interactor;

   @Inject StockPresenterImpl(StockInteractor interactor) {
      this.interactor = interactor;
   }

   @Override public void deleteSymbolFromStock(String symbol) {
      interactor.deleteSymbolFromStock(symbol)
         .subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
         .subscribe();
   }

   @Override public void startLoader(LoaderManager supportLoaderManager) {
      interactor.startLoader(supportLoaderManager);
   }

   @Override public void onCreateView() {
      super.onCreateView();
      startListeningContentResolverResets();
      startListeningContentResolverUpdates();
   }

   private void startListeningContentResolverResets() {
      track(interactor.getContentResolverUpdateProcessor()
         .subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
         .subscribe(data -> {
            getView().setStockData(data);
         }));
   }

   private void startListeningContentResolverUpdates() {
      track(interactor.getContentResolverResetProcessor()
         .subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
         .subscribe(dataChanged -> {
            if (!dataChanged)
               getView().resetStock();
         }));
   }
}

interface StockPresenter extends BasePresenter<StockListView> {

   void deleteSymbolFromStock(String symbol);

   void startLoader(LoaderManager supportLoaderManager);
}
