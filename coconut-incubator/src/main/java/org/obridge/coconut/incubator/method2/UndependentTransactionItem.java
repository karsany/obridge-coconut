package org.obridge.coconut.incubator.method2;

import java.sql.Connection;
import java.sql.SQLException;

public interface UndependentTransactionItem<Q> {

    Q perform(Connection connection) throws SQLException;

}
