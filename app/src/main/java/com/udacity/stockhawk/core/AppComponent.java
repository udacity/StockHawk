package com.udacity.stockhawk.core;

import com.udacity.stockhawk.core.scopes.ApplicationScope;
import com.udacity.stockhawk.data.repository.RepositoryModule;
import com.udacity.stockhawk.data.repository.StockRepository;

import dagger.Component;

/**
 * Application dagger component.
 */
@ApplicationScope
@Component(
   modules = {
      App.AppModule.class,
      RepositoryModule.class,
   }
)

public interface AppComponent {
   /**
    * Return application repository.
    */
   StockRepository provideRepository();
}
