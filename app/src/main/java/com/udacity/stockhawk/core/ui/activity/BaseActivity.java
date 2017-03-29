package com.udacity.stockhawk.core.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.core.ActivityModule;
import com.udacity.stockhawk.core.App;
import com.udacity.stockhawk.core.AppComponent;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Base application activity.
 */
public class BaseActivity extends AppCompatActivity {
   private final ActivityModule activityModule;
   private CompositeDisposable compositeSubscription;

   public BaseActivity() {
         activityModule = new ActivityModule(this);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      cancelSubscriptions();
   }

   public AppComponent getApplicationComponent() {
      return getAppApplication().getAppComponent();
   }

   protected App getAppApplication() {
      return (App) getApplication();
   }

   protected void track(Disposable subscription) {
      if (compositeSubscription == null) {
         compositeSubscription = new CompositeDisposable();
      }
      compositeSubscription.add(subscription);
   }

   public ActivityModule getActivityModule() {
      return activityModule;
   }

   protected void cancelSubscriptions() {
      if (compositeSubscription != null) {
         compositeSubscription.dispose();
         compositeSubscription = null;
      }
   }
}
