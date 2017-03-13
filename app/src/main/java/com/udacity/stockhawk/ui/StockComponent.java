package com.udacity.stockhawk.ui;

import com.udacity.stockhawk.core.ActivityModule;
import com.udacity.stockhawk.core.AppComponent;
import com.udacity.stockhawk.core.scopes.ViewScope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;


@ViewScope
@Component(
   modules = {
      StockComponent.StockModule.class,
      ActivityModule.class,
   },
   dependencies = AppComponent.class
)
public interface StockComponent {

   void inject(StockFragment stockFragment);

   void inject(StockDetailFragment stockDetailFragment);

   @Module
   class StockModule {
      @ViewScope
      @Provides
      StockPresenter providePresenter(StockPresenterImpl impl) {
         return impl;
      }

      @ViewScope
      @Provides
      StockInteractor provideInteractor(StockInteractorImpl impl) {
         return impl;
      }

   }


}