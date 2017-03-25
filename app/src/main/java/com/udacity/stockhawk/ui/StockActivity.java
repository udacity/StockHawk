package com.udacity.stockhawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.core.ui.activity.BaseActivity;
import com.udacity.stockhawk.utils.PrefUtils;

/**
 * Application main activity.
 */
public class StockActivity extends BaseActivity implements StockView {
   public static final String STOCK_EXTRA = "stock_detail_extra";
   public static final String STOCK_DETAILS_FRAGMENT_TAG = "fragment_stock_details";
   public static final String STOCK_HISTORY_EXTRA = "stock_history_detail_extra";
   private static final String STOCK_FRAGMENT_TAG = "fragment_stock";
   private boolean tabletMode;
   private StockFragment stockFragment;
   private StockDataReceiver dataBroadcastReceiver;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      tabletMode = findViewById(R.id.stock_details_container) != null;
      stockFragment = StockFragment.newInstance();
      initBroadcastReceiver();
      setStockFragment(stockFragment);
   }

   private void initBroadcastReceiver() {
      dataBroadcastReceiver = new StockDataReceiver();
      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction("com.udacity.stockhawk.ACTION_DATA_FAILED");
      registerReceiver(dataBroadcastReceiver, intentFilter);
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

   /**
    * Display the right menu icon.
    */
   public void setDisplayModeMenuItemIcon(MenuItem item) {
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

   @Override public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == R.id.action_change_units) {
         PrefUtils.toggleDisplayMode(this);
         (this).setDisplayModeMenuItemIcon(item);
         stockFragment.getAdapter().notifyDataSetChanged();
         return true;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override protected void onDestroy() {
      super.onDestroy();
      unregisterReceiver(dataBroadcastReceiver);
   }

   private void onStockDataError() {
      if (stockFragment == null) return;
      stockFragment.onDataLoadFailed(this);
   }

   /**
    * Custom {@link BroadcastReceiver} to manage the Stock updates.
    */
   public class StockDataReceiver extends BroadcastReceiver {

      // Empty constructor
      public StockDataReceiver() {
         super();
      }

      @Override public void onReceive(Context context, Intent intent) {
         onStockDataError();
      }
   }
}

interface StockView {

   void startStockDetailActivity(String stockHystory, String history);
}
