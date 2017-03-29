package com.udacity.stockhawk.core.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.udacity.stockhawk.core.ActivityModule;
import com.udacity.stockhawk.core.AppComponent;
import com.udacity.stockhawk.core.ui.activity.BaseActivity;


/**
 * Base application fragment.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Retrieves the application component from the host Activity. Can be safely called after
     * {@link #onAttach(Activity)} is called.
     */
    protected AppComponent getApplicationComponent() {
        try {
            return ((BaseActivity) getActivity()).getApplicationComponent();
        } catch (ClassCastException exception) {
            throw new RuntimeException("You need to inject the Fragment to a " +
                    "BaseActivity to get ApplicationComponent");
        }
    }

    /**
     * Retrieves the Activity module from the host Activity. Can be safely called after
     * {@link #onAttach(Activity)} is called.
     */
    protected ActivityModule getActivityModule() {
        try {
            return ((BaseActivity) getActivity()).getActivityModule();
        } catch (ClassCastException exception) {
            throw new RuntimeException("You need to inject the Fragment to a " +
                    "BaseActivity to get ActivityModule");
        }
    }

}
