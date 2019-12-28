package org.obridge.coconut.incubator.method2refined;

import org.obridge.coconut.data.jdbc.RowMapper;

import java.util.Collection;
import java.util.Map;

public interface DBConnection {

    void execute(String sql, Object... parameters);

    void execute(String sql, Map<String, Object> namedParameters);

    <T> Collection<T> query(String sql, RowMapper<T> rowMapper, Object... parameters);

    <T> Collection<T> query(String sql, RowMapper<T> rowMapper, Map<String, Object> namedParameters);

}
