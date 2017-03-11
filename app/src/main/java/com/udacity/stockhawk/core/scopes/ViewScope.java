package com.udacity.stockhawk.core.scopes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Scope
@Documented
@SuppressWarnings("javadoctype")
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ViewScope {
}
