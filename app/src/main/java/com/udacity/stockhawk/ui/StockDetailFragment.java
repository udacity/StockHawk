package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.core.ui.fragment.DaggerCleanFragment;

import javax.inject.Inject;

import static com.udacity.stockhawk.ui.StockActivity.STOCK_EXTRA;
import static com.udacity.stockhawk.ui.StockActivity.STOCK_HISTORY_EXTRA;

public class StockDetailFragment extends DaggerCleanFragment<StockDetailPresenter, StockDetailView, StockComponent> implements StockDetailView {
   String stockHistory;

   @Inject
   public StockDetailFragment() {
   }

   public static StockDetailFragment newInstance(String symbol, String history) {
      StockDetailFragment stockDetailFragment = new StockDetailFragment();

      Bundle bundle = new Bundle();
      bundle.putString(STOCK_EXTRA, symbol);
      bundle.putString(STOCK_HISTORY_EXTRA, history);

      stockDetailFragment.setArguments(bundle);
      return stockDetailFragment;
   }

   @Nullable @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      return inflater.inflate(R.layout.stock_details, container, false);
   }

   @Override public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      if (stockHistory != null)
         outState.putString(STOCK_EXTRA, stockHistory);
   }

   @Override protected StockComponent buildComponent() {
      return DaggerStockComponent.builder().
         appComponent(getApplicationComponent()).
         stockDetailModule(new StockComponent.StockDetailModule()).
         build();
   }
}

interface StockDetailView {

}
