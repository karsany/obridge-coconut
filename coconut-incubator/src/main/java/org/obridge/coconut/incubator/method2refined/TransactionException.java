package org.obridge.coconut.incubator.method2refined;

public class TransactionException extends RuntimeException {
    public TransactionException(Exception e) {
        super(e);
    }
}
