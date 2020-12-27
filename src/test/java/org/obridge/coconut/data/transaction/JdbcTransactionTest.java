package org.obridge.coconut.data.transaction;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.obridge.coconut.data.session.DatabaseSession;
import org.obridge.coconut.data.session.JdbcDatabaseSession;
import org.obridge.coconut.data.session.JdbcDatabaseSessionTest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

public class JdbcTransactionTest {

    private DataSource ds;

    @Before
    public void initDataSource() throws SQLException {

        JdbcDataSource h2ds = new JdbcDataSource();
        h2ds.setURL("jdbc:h2:mem:test");
        h2ds.setUser("sa");
        h2ds.setPassword("sa");

        this.ds = h2ds;

        DatabaseSession sess = new JdbcDatabaseSession(this.ds.getConnection());
        sess.execute("CREATE TABLE IF NOT EXISTS TEST(ID varchar2(255) PRIMARY KEY, NAME VARCHAR(255))");
    }


    @Test
    public void test1() {

        new JdbcTransaction(ds)
                .chain()
                .transact(databaseSession -> databaseSession.execute("insert into test values(?,?)", UUID.randomUUID(), "trxtst"))
                .get(databaseSession -> databaseSession.query("select count(1) from test where name = 'trxtst'", (resultSet, i) -> resultSet.getInt(1)).stream().findFirst().get())
                .process(integer ->
                        Assert.assertEquals(Integer.valueOf(1), integer)
                )
                .rollback()
                .get(databaseSession -> databaseSession.query("select count(1) from test where name = 'trxtst'", (resultSet, i) -> resultSet.getInt(1)).stream().findFirst().get())
                .process(integer ->
                        Assert.assertEquals(Integer.valueOf(0), integer)
                )
                .process(() -> System.out.println("helloWorld"))
                .transact(databaseSession -> databaseSession.execute("insert into test values(?,?)", UUID.randomUUID(), "trxtst"))
                .commit()
                .get(databaseSession -> databaseSession.query("select min(id) from test", (resultSet, i) -> resultSet.getString(1)).stream().findFirst().get())
                .get((databaseSession, s) -> databaseSession.query("select * from test where id = ?", (resultSet, i) -> JdbcDatabaseSessionTest.TestTable.of(resultSet.getString(1), resultSet.getString(2)), s))
                .get((databaseSession, testTables) -> {
                    final int[] i = {0};
                    testTables.forEach(testTable -> {
                                databaseSession.execute("update test t set t.name = ? where t.id = ?", testTable.name().toUpperCase(), testTable.id());
                                i[0]++;
                            }
                    );

                    return i[0];
                })
                .process(integer -> System.out.println(integer))
                .transact((databaseSession, integer) -> databaseSession.execute("insert into test values (?,?)", UUID.randomUUID(), "teszt" + integer))
                .commit();

    }

}