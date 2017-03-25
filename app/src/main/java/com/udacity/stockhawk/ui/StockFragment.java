package com.udacity.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.core.ui.fragment.DaggerCleanFragment;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.utils.PrefUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Main activity Fragment.
 */
public class StockFragment extends DaggerCleanFragment<StockPresenter, StockListView, StockComponent>
   implements StockListView, SwipeRefreshLayout.OnRefreshListener,
   StockAdapter.StockAdapterOnClickHandler, AddStockDialog.OnStockDialogClick {

   @SuppressWarnings("WeakerAccess")
   @BindView(R.id.recycler_view)
   RecyclerView stockRecyclerView;
   @SuppressWarnings("WeakerAccess")
   @BindView(R.id.swipe_refresh)
   SwipeRefreshLayout swipeRefreshLayout;
   @SuppressWarnings("WeakerAccess")
   @BindView(R.id.error)
   TextView error;
   private StockAdapter adapter;

   @Inject
   public StockFragment() {
   }

   /**
    * Return a new instance for {@link StockFragment}.
    */
   public static StockFragment newInstance() {
      return new StockFragment();
   }

   @Nullable @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      return inflater.inflate(R.layout.stock_layout, container, false);
   }

   @Override public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      ButterKnife.bind(this, getActivity());
      initializeInterface();
      QuoteSyncJob.initialize(getActivity());
      getPresenter().startLoader(getActivity().getSupportLoaderManager());
   }

   @Override protected StockComponent buildComponent() {
      return DaggerStockComponent.builder()
         .appComponent(getApplicationComponent())
         .stockModule(new StockComponent.StockModule())
         .build();
   }

   @Override public void setStockData(Cursor data) {
      swipeRefreshLayout.setRefreshing(false);

      if (data.getCount() != 0) {
         error.setVisibility(View.GONE);
      }
      adapter.setCursor(data);
   }

   @Override public void resetStock() {
      swipeRefreshLayout.setRefreshing(false);
      adapter.setCursor(null);
   }

   @Override public void onDataLoadFailed(Context context) {
      if (swipeRefreshLayout != null) {
         swipeRefreshLayout.setRefreshing(false);
         Toast.makeText(context, "There`s no data for the introduced symbol", Toast.LENGTH_SHORT).show();
      }
   }

   @Override public void onRefresh() {

      QuoteSyncJob.syncImmediately(getActivity());

      if (!networkUp() && adapter.getItemCount() == 0) {
         swipeRefreshLayout.setRefreshing(false);
         error.setText(getString(R.string.error_no_network));
         error.setVisibility(View.VISIBLE);
      } else if (!networkUp()) {
         swipeRefreshLayout.setRefreshing(false);
         Toast.makeText(getActivity(), R.string.toast_no_connectivity, Toast.LENGTH_LONG).show();
      } else if (PrefUtils.getStocks(getActivity()).size() == 0) {
         swipeRefreshLayout.setRefreshing(false);
         error.setText(getString(R.string.error_no_stocks));
         error.setVisibility(View.VISIBLE);
      } else {
         error.setVisibility(View.GONE);
      }
   }

   @Override public void onClick(String symbol, String history) {
      ((StockActivity) getActivity()).startStockDetailActivity(symbol, history);
   }

   @OnClick(R.id.fab)
   @SuppressWarnings("javadocmethod")
   public void button(@SuppressWarnings("UnusedParameters") View view) {
      new AddStockDialog(this).show(getActivity().getFragmentManager(), "StockDialogFragment");
   }

   void addStock(String symbol) {
      if (symbol != null && !symbol.isEmpty()) {

         if (networkUp()) {
            swipeRefreshLayout.setRefreshing(true);
         } else {
            String message = getString(R.string.toast_stock_added_no_connectivity, symbol);
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
         }

         PrefUtils.addStock(getActivity(), symbol);
         QuoteSyncJob.syncImmediately(getActivity());
      }
   }

   private boolean networkUp() {
      ConnectivityManager cm =
         (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = cm.getActiveNetworkInfo();
      return networkInfo != null && networkInfo.isConnectedOrConnecting();
   }

   private void initializeInterface() {
      adapter = new StockAdapter(getActivity(), this);
      stockRecyclerView.setAdapter(adapter);
      stockRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

      swipeRefreshLayout.setOnRefreshListener(this);
      swipeRefreshLayout.setRefreshing(true);
      onRefresh();

      new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
         @Override
         public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
         }

         @Override
         public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            String symbol = adapter.getSymbolAtPosition(viewHolder.getAdapterPosition());
            PrefUtils.removeStock(getActivity(), symbol);
            getPresenter().deleteSymbolFromStock(symbol);
         }
      }).attachToRecyclerView(stockRecyclerView);
   }

   @Override public void onStockDialogClick(String symbol) {
      addStock(symbol);
   }

   public StockAdapter getAdapter() {
      return adapter;
   }

}


interface StockListView {

   void setStockData(Cursor data);

   void resetStock();

   void onDataLoadFailed(Context context);
}