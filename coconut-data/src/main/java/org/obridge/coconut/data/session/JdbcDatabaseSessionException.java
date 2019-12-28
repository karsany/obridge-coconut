package org.obridge.coconut.data.session;

import java.sql.SQLException;

public class JdbcDatabaseSessionException extends RuntimeException {
    public JdbcDatabaseSessionException(SQLException e) {
        super(e);
    }
}
