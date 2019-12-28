package org.obridge.coconut.data.transaction;

public class JdbcTransactionException extends RuntimeException {
    public JdbcTransactionException(Exception e) {
        super(e);
    }
}
