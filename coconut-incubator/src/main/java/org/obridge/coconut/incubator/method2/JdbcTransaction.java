package org.obridge.coconut.incubator.method2;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcTransaction<Q> implements Transaction<Q> {
    private final DataSource dataSource;
    private final Q lastReturn;
    private final Connection connection;

    public JdbcTransaction(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        this.connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        lastReturn = null;
    }

    private JdbcTransaction(Connection connection, Q lastReturn) {
        this.dataSource = null;
        this.connection = connection;
        this.lastReturn = lastReturn;
    }


    @Override
    public <R> Transaction<R> apply(DependentTransactionItem<Q, R> ti) throws SQLException {
        final R result = ti.perform(this.connection, lastReturn);
        return new JdbcTransaction<R>(this.connection, result);
    }

    @Override
    public Transaction<Q> apply(Callback<Q> callback) {
        callback.call(this.lastReturn);
        return this;
    }

    @Override
    public <Q1> Transaction<Q1> apply(UndependentTransactionItem<Q1> ti) throws SQLException {
        final Q1 perform = ti.perform(this.connection);
        return new JdbcTransaction<>(this.connection, perform);
    }

    @Override
    public void commit() throws SQLException {
        this.connection.commit();
        this.connection.close();
    }
}
