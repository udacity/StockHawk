package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.core.ui.activity.BaseActivity;

import static com.udacity.stockhawk.ui.StockActivity.STOCK_DETAILS_FRAGMENT_TAG;
import static com.udacity.stockhawk.ui.StockActivity.STOCK_EXTRA;
import static com.udacity.stockhawk.ui.StockActivity.STOCK_HISTORY_EXTRA;

/**
 * Detailed activity for the selected Stock. It launch {@link StockDetailFragment}.
 */
public class StockDetailActivity extends BaseActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_stock_detail);

      Intent intent = getIntent();

      if (intent == null || !intent.hasExtra(STOCK_EXTRA)) {
         throw new NullPointerException("Movie can't be null");
      }
      final String symbol = intent.getStringExtra(STOCK_EXTRA);
      final String hystory = intent.getStringExtra(STOCK_HISTORY_EXTRA);

      StockDetailFragment fragment = StockDetailFragment.newInstance(symbol, hystory);
      getSupportFragmentManager().beginTransaction()
         .replace(R.id.stock_detail_container, fragment, STOCK_DETAILS_FRAGMENT_TAG)
         .commit();
   }
}
