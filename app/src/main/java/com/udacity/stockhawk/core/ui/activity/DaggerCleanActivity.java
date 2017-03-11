package com.udacity.stockhawk.core.ui.activity;

import android.os.Bundle;

import com.udacity.stockhawk.core.presentation.BasePresenter;

import javax.inject.Inject;



/**
 * Custom abstract class which extends from {@link DaggerActivity} and works together with Dagger
 * to join the lifecycle calls to the presenter and instanciate it.
 *
 * @param <C> associated component to the activity.
 * @param <P> associated presenter to the activity.
 * @param <V> associated view to the activity.
 */
public abstract class DaggerCleanActivity<P extends BasePresenter<V>, V, C> extends DaggerActivity<C> {

   @Inject
   P presenter; //Dagger injections can't be private

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //noinspection unchecked
      presenter.bindView((V) this);
   }

   @Override
   protected void onPostCreate(Bundle savedInstanceState) {
      super.onPostCreate(savedInstanceState);
      this.presenter.onCreateView();
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      getPresenter().onDestroyView();
      getPresenter().unBindView();
   }

   protected P getPresenter() {
      if (presenter.getView() != this) throw new IllegalArgumentException("Not bound!");
      return presenter;
   }

}
