package com.udacity.stockhawk.core.presentation;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * {@link BasePresenter} implementation.
 *
 * @param <V> View associated the current presenter.
 */
public abstract class BasePresenterImpl<V> implements BasePresenter<V> {
   private V view;
   private CompositeDisposable compositeSubscription;

   @Override
   public void onCreateView() {
      cancelSubscriptions();
   }

   @Override
   public void onDestroyView() {
      cancelSubscriptions();
   }

   @Override
   public void bindView(V view) {
      this.view = view;
   }

   @Override
   public void unBindView() {
      this.view = null;
   }

   @Override
   public V getView() {
      if (view == null)
         throw new NullPointerException("View is not ready yet");
      return view;
   }

   @Override
   public void track(Disposable subscription) {
      if (compositeSubscription == null)
         throw new NullPointerException("Can't track a subscription outside view lifecycle");

      compositeSubscription.add(subscription);
   }

   @Override
   public void cancelSubscriptions() {
      if (compositeSubscription != null) {
         compositeSubscription.dispose();
      }
      compositeSubscription = new CompositeDisposable();
   }

}
