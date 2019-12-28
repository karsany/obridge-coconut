package org.obridge.coconut.incubator.db;

import org.obridge.coconut.query.util.NamedParameterStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class JdbcSession implements Session {
    private final Connection connection;
    private boolean dirty = false;

    public JdbcSession(Connection connection) {
        this.connection = connection;
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            connection.commit();
            dirty = false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        try {
            connection.rollback();
            dirty = false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commitAndClose() {
        try {
            connection.commit();
            connection.close();
            dirty = false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void executeCommand(String command) {
        dirty = true;
    }

    @Override
    public void executeCommand(DatabaseCommand command) {
        dirty = true;
        for (Statement statement : command.commands()) {
            try {
                final NamedParameterStatement namedParameterStatement = new NamedParameterStatement(this.connection, statement.sql());

                for (Map.Entry<String, Object> ee : statement.variables().entrySet()) {

                    namedParameterStatement.setObject(ee.getKey(), ee.getValue());

                }

                namedParameterStatement.execute();

                namedParameterStatement.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
