package org.obridge.coconut.incubator.method2refined;

public interface Transaction {
    void apply(DBCommand dbCommand);

    <T> T ret(ReturningDBCommand<T> dbCommand);

    <T> void apply(ConsumingDBCommand<T> dbCommand, T t);

    <C, R> R apply(ConsumingReturningDBCommand<C, R> dbCommand, C c);

    void commit();

    void rollback();

    TransactionChain chain();

}
