package com.udacity.stockhawk.core;


import android.support.v4.app.FragmentActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Utility Module used by activities to provide FragmentActivity.
 */
@Module
public class ActivityModule {

   private FragmentActivity activity;

   public ActivityModule(FragmentActivity activity) {
      this.activity = activity;
   }

   @Provides
   FragmentActivity provideActivity() {
      return activity;
   }

}