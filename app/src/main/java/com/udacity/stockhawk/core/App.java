package com.udacity.stockhawk.core;

import android.app.Application;

import com.udacity.stockhawk.BuildConfig;
import com.udacity.stockhawk.core.scopes.ApplicationScope;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * Application instance responsible for initialice the application and basic Dagger components.
 */
public class App extends Application {
   private AppComponent appComponent;

   @Override public void onCreate() {
      super.onCreate();
      appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
      if (BuildConfig.DEBUG) {
         Timber.uprootAll();
         Timber.plant(new Timber.DebugTree());
      }
   }

   public AppComponent getAppComponent() {
      return appComponent;
   }

   @Module
   static class AppModule {
      private final App app;

      AppModule(App app) {
         this.app = app;
      }

      @ApplicationScope
      @Provides
      App provideApp() {
         return app;
      }
   }

}
