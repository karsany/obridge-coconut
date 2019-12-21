package org.obridge.coconut.converter.converters;

import org.obridge.coconut.converter.Converter;

public class NumberToIntegerConverter implements Converter<Number, Integer> {
    @Override
    public Integer convert(Number from) {
        return from.intValue();
    }
}
