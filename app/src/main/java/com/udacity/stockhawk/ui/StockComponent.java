package com.udacity.stockhawk.ui;

import com.udacity.stockhawk.core.ActivityModule;
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
      StockComponent.StockModule.class,
      StockComponent.StockDetailModule.class,
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

   @Module
   class StockDetailModule {
      @ViewScope
      @Provides
      StockDetailPresenter providePresenter(StockDetailPresenterImpl impl) {
         return impl;
      }

      @ViewScope
      @Provides
      StockDetailInteractor provideInteractor(StockDetailInteractorImpl impl) {
         return impl;
      }

   }
}
