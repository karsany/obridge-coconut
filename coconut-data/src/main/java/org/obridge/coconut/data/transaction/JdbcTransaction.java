package org.obridge.coconut.data.transaction;

import org.obridge.coconut.data.session.DatabaseSession;
import org.obridge.coconut.data.session.JdbcDatabaseSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class JdbcTransaction implements Transaction {
    private final Connection connection;

    public JdbcTransaction(DataSource ds) {
        try {
            this.connection = ds.getConnection();
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            this.rollback();
            throw new JdbcTransactionException(e);
        }
    }

    @Override
    public void apply(Consumer<DatabaseSession> c) {
        c.accept(new JdbcDatabaseSession(this.connection));
    }

    @Override
    public <T> T apply(Function<DatabaseSession, T> c) {
        return c.apply(new JdbcDatabaseSession(this.connection));
    }

    @Override
    public <T> void apply(BiConsumer<DatabaseSession, T> t, T u) {
        t.accept(new JdbcDatabaseSession(this.connection), u);
    }

    @Override
    public <T, R> R apply(BiFunction<DatabaseSession, T, R> t, T u) {
        return t.apply(new JdbcDatabaseSession(this.connection), u);
    }

    @Override
    public void commit() {
        try {
            this.connection.commit();
        } catch (SQLException e) {
            this.rollback();
            throw new JdbcTransactionException(e);
        }
    }

    @Override
    public void rollback() {
        try {
            this.connection.rollback();
        } catch (SQLException e) {
            throw new JdbcTransactionException(e);
        }
    }

    @Override
    public TransactionChain chain() {
        return new GenericTransactionChain(this);
    }

}
