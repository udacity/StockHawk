package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.core.ui.activity.BaseActivity;
import com.udacity.stockhawk.utils.PrefUtils;

public class StockActivity extends BaseActivity implements StockView {
   public static final String STOCK_DETAILS_FRAGMENT_TAG = "fragment_stock_details";
   private static final String STOCK_FRAGMENT_TAG = "fragment_stock";
   public static final String STOCK_EXTRA = "stock_detail_extra";
   public static final String STOCK_HISTORY_EXTRA = "stock_history_detail_extra";
   private boolean tabletMode;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      tabletMode = findViewById(R.id.stock_details_container) != null;

      setStockFragment(StockFragment.newInstance());
   }

   @Override
   public void startStockDetailActivity(String symbol, String history) {

      if (tabletMode) {
         setStockDetailsFragment(StockDetailFragment.newInstance(symbol, history));
      } else {
         Intent intent = new Intent(this, StockDetailActivity.class);

         intent.putExtra(STOCK_EXTRA, symbol);
         intent.putExtra(STOCK_HISTORY_EXTRA, history);
         startActivity(intent);
      }
   }

   private void setStockDetailsFragment(StockDetailFragment fragment) {
      getSupportFragmentManager().beginTransaction()
         .replace(R.id.stock_details_container, fragment, STOCK_DETAILS_FRAGMENT_TAG)
         .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
         .commit();
   }

   private void setStockFragment(StockFragment fragment) {
      getSupportFragmentManager().beginTransaction()
         .replace(R.id.stock_container, fragment, STOCK_FRAGMENT_TAG)
         .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
         .commit();
   }

   private void setDisplayModeMenuItemIcon(MenuItem item) {
      if (PrefUtils.getDisplayMode(this)
         .equals(getString(R.string.pref_display_mode_absolute_key))) {
         item.setIcon(R.drawable.ic_percentage);
      } else {
         item.setIcon(R.drawable.ic_dollar);
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main_activity_settings, menu);
      MenuItem item = menu.findItem(R.id.action_change_units);
      setDisplayModeMenuItemIcon(item);
      return true;
   }

}

interface StockView {

   void startStockDetailActivity(String stockHystory, String history);
}
