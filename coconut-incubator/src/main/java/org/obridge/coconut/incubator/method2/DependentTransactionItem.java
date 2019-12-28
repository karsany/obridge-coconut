package org.obridge.coconut.incubator.method2;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface DependentTransactionItem<P, Q> {

    Q perform(Connection connection, P p) throws SQLException;

}
