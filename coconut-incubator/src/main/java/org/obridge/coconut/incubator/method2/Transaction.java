package org.obridge.coconut.incubator.method2;

import java.sql.SQLException;

public interface Transaction<P> {

    <Q> Transaction<Q> apply(DependentTransactionItem<P, Q> ti) throws SQLException;

    Transaction<P> apply(Callback<P> callback);

    <Q> Transaction<Q> apply(UndependentTransactionItem<Q> ti) throws SQLException;

    void commit() throws SQLException;
}
