package org.obridge.coconut.data.jdbc;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

public class TestDataSourceProvider {

    public static DataSource dataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:");
        ds.setUser("sa");
        ds.setPassword("sa");
        return ds;
    }

}
