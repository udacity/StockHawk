package com.udacity.stockhawk.data.repository;

import android.content.ContentResolver;

import com.udacity.stockhawk.core.App;
import com.udacity.stockhawk.core.scopes.ApplicationScope;

import dagger.Module;
import dagger.Provides;

/**
 * DaggerModule with the provides methods for the repository classes.
 */
@Module
public class RepositoryModule {

   @ApplicationScope
   @Provides StockRepository providesMoviesRepository(StockRepositoryImpl impl) {
      return impl;
   }

   @ApplicationScope
   @Provides
   ContentResolver provideContentResolver(App application) {
      return application.getContentResolver();
   }
}
