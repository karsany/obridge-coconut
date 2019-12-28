/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Ferenc Karsany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.obridge.coconut.data.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fkarsany on 2015.03.04..
 */
public class JdbcTemplate {

    private final DataSource dataSource;

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> queryForList(String sql) {
        return query(sql, (resultSet, i) -> (T) resultSet.getObject(1));
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        return query(sql, null, rowMapper);
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
        return query(sql, args, rowMapper);
    }

    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) {

        List<T> ret = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(sql);

            bindParameters(args, ps);

            resultSet = ps.executeQuery();

            ret = fetchData(rowMapper, resultSet);

            tryCloseConnection(connection, ps, resultSet);

            return ret;

        } catch (SQLException e) {
            tryCloseConnection(connection, ps, resultSet);
            throw new JdbcTemplateException(e);
        }
    }

    public void execute(String sql, Object... args) {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(sql);

            bindParameters(args, ps);

            ps.execute();

            ps.close();
            ps = null;
            connection.close();

        } catch (SQLException e) {
            tryCloseConnection(connection, ps, null);
            throw new JdbcTemplateException(e);
        }
    }

    private void tryCloseConnection(Connection connection, PreparedStatement ps, ResultSet resultSet) {
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }
            if (ps != null && !ps.isClosed()) {
                ps.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }

        } catch (SQLException ex) {
            throw new JdbcTemplateException(ex);
        }
    }

    private <T> List<T> fetchData(RowMapper<T> rowMapper, ResultSet resultSet) throws SQLException {
        List<T> ret = new ArrayList<>();
        int i = 0;
        while (resultSet.next()) {
            i++;
            ret.add(rowMapper.mapRow(resultSet, i));
        }
        return ret;
    }

    private void bindParameters(Object[] args, PreparedStatement ps) throws SQLException {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
        }
    }

}
