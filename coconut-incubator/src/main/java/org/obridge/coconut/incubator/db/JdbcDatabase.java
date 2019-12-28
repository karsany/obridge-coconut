package org.obridge.coconut.incubator.db;

import javax.sql.DataSource;
import java.sql.SQLException;

public class JdbcDatabase implements Database {
    private final DataSource ds;

    public JdbcDatabase(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public Session connect() {
        try {
            return new JdbcSession(ds.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
