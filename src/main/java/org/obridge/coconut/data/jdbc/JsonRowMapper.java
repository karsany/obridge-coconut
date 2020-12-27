package org.obridge.coconut.data.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonRowMapper implements RowMapper<String> {
    @Override
    public String mapRow(ResultSet resultSet, int i) throws SQLException {
        final Map<String, String> rowMap = new StringMapRowMaper().mapRow(resultSet, i);

        return "{" + rowMap.entrySet().stream()
                .map(e -> "\"" + e.getKey() + "\"" + ":\"" + String.valueOf(e.getValue()) + "\"")
                .collect(Collectors.joining(", ")) + "}";

    }
}
