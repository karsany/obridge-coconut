package org.obridge.coconut.incubator.method2;

import java.sql.Connection;
import java.sql.SQLException;

public interface NativeSqlTransactionItem<T> {

    T run(Connection conn) throws SQLException;

}
