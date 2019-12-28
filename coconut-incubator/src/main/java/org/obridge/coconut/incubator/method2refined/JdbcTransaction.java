package org.obridge.coconut.incubator.method2refined;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcTransaction implements Transaction {
    private final DataSource ds;
    private final Connection connection;

    public JdbcTransaction(DataSource ds) {
        try {
            this.ds = ds;
            this.connection = ds.getConnection();
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            this.rollback();
            throw new TransactionException(e);
        }
    }

    @Override
    public void apply(DBCommand dbCommand) {
        dbCommand.run(new JdbcConnection(this.connection));
    }

    @Override
    public void commit() {
        try {
            this.connection.commit();
            this.connection.close();
        } catch (SQLException e) {
            this.rollback();
            throw new TransactionException(e);
        }
    }

    @Override
    public void rollback() {
        try {
            this.connection.rollback();
        } catch (SQLException e) {
            throw new TransactionException(e);
        }
    }

    @Override
    public TransactionChain chain() {
        return new GenericTransactionChain(this);
    }

    @Override
    public <T> T ret(ReturningDBCommand<T> dbCommand) {
        return dbCommand.run(new JdbcConnection(this.connection));
    }

    @Override
    public <T> void apply(ConsumingDBCommand<T> dbCommand, T t) {
        dbCommand.run(new JdbcConnection(this.connection), t);
    }

    @Override
    public <C, R> R apply(ConsumingReturningDBCommand<C, R> dbCommand, C c) {
        return dbCommand.run(new JdbcConnection(this.connection), c);
    }
}
