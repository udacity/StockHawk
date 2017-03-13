package com.udacity.stockhawk.ui;

import com.udacity.stockhawk.core.presentation.BasePresenter;
import com.udacity.stockhawk.core.presentation.BasePresenterImpl;

import javax.inject.Inject;


public class StockDetailPresenterImpl extends BasePresenterImpl<StockDetailView> implements StockDetailPresenter {
   private StockDetailInteractor interactor;

   @Inject public StockDetailPresenterImpl(StockDetailInteractor interactor) {
      this.interactor = interactor;
   }
}

interface StockDetailPresenter extends BasePresenter<StockDetailView> {

}
