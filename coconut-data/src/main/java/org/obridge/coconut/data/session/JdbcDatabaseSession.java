package org.obridge.coconut.data.session;

import org.obridge.coconut.data.jdbc.NamedParameterStatement;
import org.obridge.coconut.data.jdbc.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JdbcDatabaseSession implements DatabaseSession {
    private final Connection connection;

    public JdbcDatabaseSession(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void execute(String sql, Object... parameters) {
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            bindParameters(parameters, ps);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            throw new JdbcDatabaseSessionException(e);
        }
    }

    @Override
    public void execute(String sql, Map<String, Object> namedParameters) {
        try {
            NamedParameterStatement nps = new NamedParameterStatement(connection, sql);
            for (Map.Entry<String, Object> entry : namedParameters.entrySet()) {
                String s = entry.getKey();
                Object o = entry.getValue();
                nps.setObject(s, o);
            }
            nps.execute();
            nps.close();
        } catch (SQLException e) {
            throw new JdbcDatabaseSessionException(e);
        }
    }

    @Override
    public <T> Collection<T> query(String sql, RowMapper<T> rowMapper, Object[] parameters) {
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            bindParameters(parameters, ps);
            ResultSet resultSet = ps.executeQuery();
            return fetchData(rowMapper, resultSet);
        } catch (SQLException e) {
            throw new JdbcDatabaseSessionException(e);
        }
    }

    @Override
    public <T> Collection<T> query(String sql, RowMapper<T> rowMapper, Map<String, Object> namedParameters) {
        try {
            NamedParameterStatement nps = new NamedParameterStatement(connection, sql);
            for (Map.Entry<String, Object> entry : namedParameters.entrySet()) {
                String s = entry.getKey();
                Object o = entry.getValue();
                nps.setObject(s, o);
            }
            final ResultSet resultSet = nps.executeQuery();
            return fetchData(rowMapper, resultSet);
        } catch (SQLException e) {
            throw new JdbcDatabaseSessionException(e);
        }
    }

    private <T> Collection<T> fetchData(RowMapper<T> rowMapper, ResultSet resultSet) throws SQLException {
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
