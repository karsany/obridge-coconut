package org.obridge.coconut.query.annotation;

import org.obridge.coconut.query.interfaces.Query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QuerySource {
    Class<? extends Query> value();
}
