package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.core.ui.activity.BaseActivity;

/**
 * Created by Durdin on 13/03/2017.
 */

public class StockDetailActivity extends BaseActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_stock_detail);

      Intent intent = getIntent();

//      if (intent == null || !intent.hasExtra(MOVIE_EXTRA)) {
//         throw new NullPointerException("Movie can't be null");
//      }

//      MovieDetailFragment fragment = MovieDetailFragment.newInstance(movie, videos, reviews);
//      getSupportFragmentManager().beginTransaction()
//         .replace(R.id.movie_detail_container, fragment, MOVIE_DETAILS_FRAGMENT_TAG)
//         .commit();
   }
}
