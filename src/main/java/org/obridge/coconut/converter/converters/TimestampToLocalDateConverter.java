package org.obridge.coconut.converter.converters;

import org.obridge.coconut.converter.Converter;

import java.sql.Timestamp;
import java.time.LocalDate;

public class TimestampToLocalDateConverter implements Converter<Timestamp, LocalDate> {
    @Override
    public LocalDate convert(Timestamp from) {
        return from.toLocalDateTime().toLocalDate();
    }
}
