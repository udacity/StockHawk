package com.udacity.stockhawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.udacity.stockhawk.utils.NetworkUtils.isNetworkUp;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        StockAdapter.StockAdapterOnClickHandler {

    private static final int STOCK_LOADER = 0;
    @BindView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView stockRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.error)
    TextView error;

    private StockAdapter adapter;

    private Snackbar noConnectionSnackbar;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!NetworkUtils.isNetworkUp(context)) {
                showNoNetworkSnackbar();
            } else {
                swipeRefreshLayout.setRefreshing(true);
                if (noConnectionSnackbar != null) noConnectionSnackbar.dismiss();
                updateEmptyView();
            }
        }
    };

    @Override
    public void onClick(String symbol) {
        Timber.d("Symbol clicked: %s", symbol);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        adapter = new StockAdapter(this, this);
        stockRecyclerView.setAdapter(adapter);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        stockRecyclerView.addItemDecoration(dividerItemDecoration);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (savedInstanceState == null) {
            QuoteSyncJob.initialize(this);
            swipeRefreshLayout.setRefreshing(true);
        }

        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

        configureDeleteOnSwipe();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        if (NetworkUtils.isNetworkUp(this)) {
            QuoteSyncJob.syncImmediately(this);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            showNoNetworkSnackbar();
        }
    }

    public void operAddStockDialog(@SuppressWarnings("UnusedParameters") View view) {
        new AddStockDialog().show(getFragmentManager(), getString(R.string.stock_dialog_fragment_name));
    }

    void addStock(String symbol) {
        if (symbol != null && !symbol.isEmpty()) {
            if(PrefUtils.addStock(this, symbol)) {
                if (isNetworkUp(this)) {
                    swipeRefreshLayout.setRefreshing(true);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                } else {
                    showStockWillBeAddedSnackBar(symbol);
                }
                QuoteSyncJob.syncImmediately(this);
            } else {
                Snackbar.make(stockRecyclerView, getString(R.string.stock_already_included, symbol), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.setCursor(data);
        swipeRefreshLayout.setRefreshing(false);
        updateEmptyView();
        if (data.getCount() == 0) {
        } else {
        }
    }

    private void updateEmptyView() {
        swipeRefreshLayout.setVisibility(View.GONE);
        int message;

            @QuoteSyncJob.StockStatus int status = PrefUtils.getStockStatus(this);
            switch (status) {
                case QuoteSyncJob.STOCK_STATUS_EMPTY:
                    message = R.string.error_no_stocks;
                    break;
                case QuoteSyncJob.STOCK_STATUS_SERVER_DOWN:
                    message = R.string.error_server_down;
                    break;
                case QuoteSyncJob.STOCK_STATUS_UNKNOWN:
                    message = R.string.empty_stock_list;
                    break;
                default:
                    message = R.string.loading_data;
                    break;
            }
            if (!NetworkUtils.isNetworkUp(this)) message = R.string.error_no_network;
            if (PrefUtils.getStocks(this).size() == 0) message = R.string.error_no_stocks;

        if (adapter.getItemCount() == 0) {
            error.setText(message);
            error.setVisibility(View.VISIBLE);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            error.setVisibility(View.INVISIBLE);
            Snackbar.make(stockRecyclerView, message, Snackbar.LENGTH_LONG);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setCursor(null);
        updateEmptyView();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_units) {
            PrefUtils.toggleDisplayMode(this);
            setDisplayModeMenuItemIcon(item);
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_stock_status_key))) {
            updateEmptyView();
        } else if (key.equals(getString(R.string.pref_invalid_stock_key))) {
            String symbol = PrefUtils.getInvalidStock(this);
            PrefUtils.removeInvalidStock(this);
            if (TextUtils.isEmpty(symbol)) {
                return;
            }
            int stockStatus = PrefUtils.getStockStatus(this);
            if(QuoteSyncJob.STOCK_STATUS_INVALID == stockStatus) {
                Snackbar.make(stockRecyclerView, getString(R.string.error_server_down_stock, symbol), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.try_again), tryToAddAgainListener(symbol))
                        .show();
            } else {
                showInvalidStockSnackBar(symbol);
            }
            updateEmptyView();
        }
    }

    private void configureDeleteOnSwipe() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String symbol = adapter.getSymbolAtPosition(viewHolder.getAdapterPosition());
                int stockSize = PrefUtils.removeStock(MainActivity.this, symbol);
                getContentResolver().delete(Contract.Quote.makeUriForStock(symbol), null, null);
                if (stockSize == 0) {
                    adapter.setCursor(null);
                    updateEmptyView();
                }
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return !swipeRefreshLayout.isRefreshing();
            }
        }).attachToRecyclerView(stockRecyclerView);
    }

    private void showNoNetworkSnackbar() {
        noConnectionSnackbar = Snackbar.make(stockRecyclerView, getString(R.string.error_no_network), Snackbar.LENGTH_INDEFINITE);
        noConnectionSnackbar.setAction(getString(R.string.try_again), getRefreshListener()).show();
    }

    private void showInvalidStockSnackBar(String symbol) {
        Snackbar.make(stockRecyclerView, getString(R.string.error_stock_invalid, symbol), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.try_again), getAddStockListener())
                .show();
    }

    private void showStockWillBeAddedSnackBar(String symbol) {
        Snackbar.make(stockRecyclerView, getString(R.string.toast_stock_added_no_connectivity, symbol), Snackbar.LENGTH_LONG).show();
    }

    @NonNull
    private View.OnClickListener getRefreshListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
                if (!isNetworkUp(MainActivity.this)) {
                    showNoNetworkSnackbar();
                }
            }
        };
    }

    @NonNull
    private View.OnClickListener getAddStockListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operAddStockDialog(null);
            }
        };
    }

    @NonNull
    private View.OnClickListener tryToAddAgainListener(final String symbol) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStock(symbol);
            }
        };
    }
}
