package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ID_DETAIL_LOADER = 353;
    private DecimalFormat percentageFormat;

    private String symbol;
    private Uri mUri;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.textViewStockPrice)
    TextView textViewStockPrice;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.textViewStockMargin)
    TextView textViewStockMargin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        symbol = getIntent().getStringExtra("SYMBOL");
        setTitle(symbol);

        Timber.d("Symbol details: %s", symbol);

        mUri = Contract.Quote.makeUriForStock(symbol);

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }

        Timber.d("Cursor count: %d", data.getCount());

        textViewStockPrice.setText(
                data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_PRICE)));

        float percentageChange = Float.valueOf(data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE)
        ));

        float absoluteChange = Float.valueOf(data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE)
        ));

        if (percentageChange > 0) {
            textViewStockMargin.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            textViewStockMargin.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String percentage = percentageFormat.format(percentageChange / 100);

        textViewStockMargin.setText(
                String.valueOf(absoluteChange) + " ("+
                percentage + ")"
        );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
