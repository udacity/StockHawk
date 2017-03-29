package com.udacity.stockhawk.core.ui.activity;

import android.os.Bundle;

import java.lang.reflect.Method;

/**
 * Custom abstract class which extends from {@link BaseActivity} and works together with DAgger
 * to auto inject the activity with the associated component.
 *
 * @param <C> associated component to the activity.
 */
public abstract class DaggerActivity<C> extends BaseActivity {

   private C component;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      doInject(getComponent());
   }

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

   /**
    * Use reflection to call the inject method from the associated component to the current activity.
    */
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
