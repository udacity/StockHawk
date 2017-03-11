package com.udacity.stockhawk.ui;

import com.udacity.stockhawk.core.presentation.BasePresenter;
import com.udacity.stockhawk.core.presentation.BasePresenterImpl;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class StockListPresenterImpl extends BasePresenterImpl<StockListView> implements StockListPresenter {
   private StockListInteractor interactor;

   @Inject
   public StockListPresenterImpl(StockListInteractor interactor) {
      this.interactor = interactor;
   }

   @Override public void loadStock() {
      interactor.loadStock()
         .subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
         .subscribe(cursor -> {
         getView().setStockData(cursor);
      });
   }

   @Override public void deleteSymbolFromStock(String symbol) {
      interactor.deleteSymbolFromStock(symbol)
         .subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
         .subscribe(new CompletableObserver() {
         @Override public void onSubscribe(Disposable d) {

         }

         @Override public void onComplete() {

         }

         @Override public void onError(Throwable e) {

         }
      });
   }

   @Override public void onCreateView() {
      super.onCreateView();
      loadStock();
   }
}

interface StockListPresenter extends BasePresenter<StockListView> {

   void loadStock();

   void deleteSymbolFromStock(String symbol);
}
