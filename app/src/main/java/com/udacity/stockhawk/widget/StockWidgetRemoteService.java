package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.provider.Contract;
import com.udacity.stockhawk.utils.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Custom {@link RemoteViewsService} to update the application widget.
 */
public class StockWidgetRemoteService extends RemoteViewsService {

   @Override
   public RemoteViewsFactory onGetViewFactory(Intent intent) {
      return new RemoteViewsFactory() {
         public DecimalFormat percentageFormat;
         public DecimalFormat dollarFormatWithPlus;
         private Cursor data = null;

         @Override
         public void onCreate() {
            dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus.setPositivePrefix("+$");
            percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
            percentageFormat.setMaximumFractionDigits(2);
            percentageFormat.setMinimumFractionDigits(2);
            percentageFormat.setPositivePrefix("+");
         }

         @Override
         public RemoteViews getViewAt(int position) {
            if (position == AdapterView.INVALID_POSITION ||
               data == null || !data.moveToPosition(position)) {
               return null;
            }

            // Get the layout
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_adapter_item);

            //Retrieve the data from the cursor
            float rawAbsoluteChange = data.getFloat(data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
            float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

            // Bind data
            views.setTextViewText(R.id.stock_symbol, data.getString(data.getColumnIndex(getResources().getString(R.string.string_symbol))));

            if (rawAbsoluteChange > 0) {
               views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            } else {
               views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }
            //Format the values
            String change = dollarFormatWithPlus.format(rawAbsoluteChange);
            String percentage = percentageFormat.format(percentageChange / 100);

            //Set them
            if (PrefUtils.getDisplayMode(getApplicationContext())
               .equals(getString(R.string.pref_display_mode_absolute_key))) {
               views.setTextViewText(R.id.change, change);
            } else {
               views.setTextViewText(R.id.change, percentage);
            }

            final Intent fillInIntent = new Intent();
            fillInIntent.putExtra(getResources().getString(R.string.string_symbol),
               data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
            views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

            return views;
         }


         @Override
         public void onDataSetChanged() {
            if (data != null) {
               data.close();
            }

            //Retrieve the token
            final long identityToken = Binder.clearCallingIdentity();

            // Query the data again as we do on StockRepositoryImpl
            data = getContentResolver().query(
               Contract.Quote.URI,
               Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
               null, null,
               Contract.Quote.COLUMN_SYMBOL);
            Binder.restoreCallingIdentity(identityToken);
         }

         @Override
         public void onDestroy() {
            //Nothing to do
         }

         @Override
         public int getCount() {
            return data == null ? 0 : data.getCount();
         }


         @Override
         public RemoteViews getLoadingView() {
            return null;
         }

         @Override
         public int getViewTypeCount() {
            return 1;
         }

         @Override
         public long getItemId(int position) {
            // Get the row ID for the view at the specified position
            if (data != null && data.moveToPosition(position)) {
               final int quotesIdCol = 0;
               return data.getLong(quotesIdCol);
            }
            return position;
         }

         @Override
         public boolean hasStableIds() {
            return true;
         }
      };
   }
}