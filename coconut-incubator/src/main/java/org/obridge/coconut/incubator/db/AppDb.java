package org.obridge.coconut.incubator.db;

import oracle.jdbc.pool.OracleDataSource;

import java.sql.SQLException;

public class AppDb {

    public static void main(String... args) throws SQLException {

        OracleDataSource ds = new OracleDataSource();
        ds.setURL("jdbc:oracle:thin:projects/projects@localhost:1521:xe");

        final Database database = new JdbcDatabase(ds);
        final Session session = database.connect();

        session.executeCommand(
                new AddUserCommand("joskapista", "MasterPass1234")
        );

        session.commit();
        session.rollback();
        session.commitAndClose();
        boolean dirty = session.isDirty();


    }

}
