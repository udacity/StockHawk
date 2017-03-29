package com.udacity.stockhawk.core.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.udacity.stockhawk.core.presentation.BasePresenter;

import javax.inject.Inject;


/**
 * Custom abstract class which extends from {@link DaggerFragment} and works together with Dagger
 * to join the lifecycle calls to the presenter and instanciate it.
 *
 * @param <C> associated component to the activity.
 * @param <P> associated presenter to the activity.
 * @param <V> associated view to the activity.
 */
public abstract class DaggerCleanFragment<P extends BasePresenter<V>, V, C> extends DaggerFragment<C> {

    @Inject
    P presenter; //Can't be private

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doInject(getComponent());
        //noinspection unchecked
        presenter.bindView((V) this);
        presenter.onCreateView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPresenter().onDestroyView();
        getPresenter().unBindView();
    }

    protected P getPresenter() {
        if (presenter.getView() != this) throw new IllegalArgumentException("Not bound!");
        return presenter;
    }
}
