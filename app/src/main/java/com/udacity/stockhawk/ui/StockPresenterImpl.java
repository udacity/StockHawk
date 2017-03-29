package com.udacity.stockhawk.ui;

import android.support.v4.app.LoaderManager;

import com.udacity.stockhawk.core.presentation.BasePresenter;
import com.udacity.stockhawk.core.presentation.BasePresenterImpl;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
            .subscribe(new CompletableObserver() {
               @Override public void onSubscribe(Disposable d) {

               }

               @Override public void onComplete() {
                  getView().updateWidget();
               }

               @Override public void onError(Throwable e) {

               }
            });
   }

   @Override public void startLoader(LoaderManager supportLoaderManager) {
      interactor.startLoader(supportLoaderManager);
   }

   @Override public void onCreateView() {
      super.onCreateView();
      startListeningContentResolverUpdates();
      startListeningContentResolverReset();
   }

   private void startListeningContentResolverUpdates() {
      track(interactor.getContentResolverUpdateProcessor()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(data -> {
               getView().setStockData(data);
            }));
   }

   private void startListeningContentResolverReset() {
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
