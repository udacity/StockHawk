package com.udacity.stockhawk.core.presentation;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Base presenter interface.
 *
 * @param <V> View associated the current presenter.
 */
public interface BasePresenter<V> {

   /**
    * Called when the bound view is created.
    * getView() will return a valid, active view from this point, so
    * it is safe to start subscriptions or any other code depending on getView()
    */
   void onCreateView();

   /**
    * Called when the bound view is about to be destroyed and unbound.
    * getView() will return null from this point, so every subscription
    * or any other code depending on getView() should be unsubscribed/managed.
    */
   void onDestroyView();

   /**
    * Needed to create the relation between View-Presenter.
    *
    * @param view an active view of the needed type.
    */
   void bindView(V view);

   /**
    * Called after onDestroyView. Should perform clean-up of variables pointing to the view.
    */
   void unBindView();

   /**
    * @return the bound view. May be null if the view was unbound.
    */
   V getView();

   /**
    * Add {@link Disposable} to a {@link CompositeDisposable} to unsuscribe all of them at the end of the
    * activity lifecycle.
    */
   void track(Disposable subscription);

   /**
    * cancel all the pendent Rxjava subscriptions.
    */
   void cancelSubscriptions();
}
