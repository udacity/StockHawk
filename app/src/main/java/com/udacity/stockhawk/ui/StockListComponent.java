package com.udacity.stockhawk.ui;

import com.udacity.stockhawk.core.AppComponent;
import com.udacity.stockhawk.core.scopes.ViewScope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Durdin on 11/03/2017.
 */

@ViewScope
@Component(
   modules = {
      StockListComponent.StockListModule.class,
   },
   dependencies = AppComponent.class
)
interface StockListComponent {

   void inject(StockListActivity stockListActivity);

   @Module
   class StockListModule {
      @ViewScope
      @Provides
      StockListPresenter providePresenter(StockListPresenterImpl impl) {
         return impl;
      }

      @ViewScope
      @Provides
      StockListInteractor provideInteractor(StockListInteractorImpl impl) {
         return impl;
      }

   }
}
