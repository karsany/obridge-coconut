package org.obridge.coconut.converter;

public interface Converter<F, T> {

    T convert(F from);

}
