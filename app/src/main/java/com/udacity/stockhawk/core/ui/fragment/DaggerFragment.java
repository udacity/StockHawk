package com.udacity.stockhawk.core.ui.fragment;

import java.lang.reflect.Method;

/**
 * Custom abstract class which extends from {@link BaseFragment} and works together with Dagger
 * to auto inject the activity with the associated component.
 *
 * @param <C> associated component to the fragment.
 */
public abstract class DaggerFragment<C> extends BaseFragment {
   private C component;

   protected abstract C buildComponent();

   /**
    * Return the associated dagger component.
    */
   public C getComponent() {
      if (component == null) {
         component = buildComponent();
      }
      return component;
   }

   protected void doInject(C component) {
      try {
         final Method injectMethod = component.getClass().getMethod("inject", this.getClass());
         injectMethod.invoke(component, this);
      } catch (NoSuchMethodException e) {
         throw new UnsupportedOperationException(
            "No injection point for: " + this.getClass()
               + " in: " + component.getClass()
               + ". Expected a method in the component with signature: " +
               "void inject(" + this.getClass() + ");");
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}