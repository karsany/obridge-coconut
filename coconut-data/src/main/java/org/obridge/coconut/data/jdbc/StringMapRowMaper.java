package org.obridge.coconut.data.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class StringMapRowMaper implements RowMapper<Map<String, String>> {

    private List<String> columns = null;

    @Override
    public Map<String, String> mapRow(ResultSet resultSet, int i) throws SQLException {
        if (columns == null) {
            initColumns(resultSet);
        }

        Map<String, String> jo = new HashMap<>();

        for (int j = 0; j < columns.size(); j++) {
            jo.put(columns.get(j), resultSet.getString(j + 1));
        }

        return jo;
    }

    private void initColumns(ResultSet resultSet) throws SQLException {
        columns = new ArrayList<>();
        int columnCount = resultSet.getMetaData().getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columns.add(camelCaseSmallBegin(resultSet.getMetaData().getColumnName(i)));
        }
    }

    private String camelCase(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        return capitalizeFirstLetters(s.replaceAll("_", " ")).replace(" ", "");
    }

    private String camelCaseSmallBegin(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        String ss = camelCase(s);
        return ss.substring(0, 1).toLowerCase() + ss.substring(1);
    }

    private String capitalizeFirstChar(final String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    private String capitalizeFirstLetters(String s) {
        return Arrays.stream(s.split(" ")).map(s2 -> capitalizeFirstChar(s2)).collect(Collectors.joining(" "));
    }


}
