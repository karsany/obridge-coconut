package org.obridge.coconut.incubator.method2refined;

import java.sql.SQLException;

public class DBConnectionException extends RuntimeException {
    public DBConnectionException(SQLException e) {
        super(e);
    }
}
