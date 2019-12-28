package org.obridge.coconut.data.session;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JdbcDatabaseSessionTest {

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
    public void test_execute_positional_parameters() throws SQLException {

        DatabaseSession sess = new JdbcDatabaseSession(this.ds.getConnection());

        final Integer cntStart = sess.query("select count(1) from test", (resultSet, i) -> resultSet.getInt(1)).stream().findFirst().get();

        sess.execute("insert into test(id, name) values (?, ?)", UUID.randomUUID(), "test");
        sess.execute("insert into test(id, name) values (?, ?)", UUID.randomUUID(), "test2");

        final Integer cntEnd = sess.query("select count(1) from test", (resultSet, i) -> resultSet.getInt(1)).stream().findFirst().get();

        Assert.assertEquals(cntEnd - cntStart, 2);
    }

    @Test
    public void test_execute_named_parameters() throws SQLException {

        DatabaseSession sess = new JdbcDatabaseSession(this.ds.getConnection());

        final Integer cntStart = sess.query("select count(1) from test", (resultSet, i) -> resultSet.getInt(1)).stream().findFirst().get();

        Map<String, Object> mso = new HashMap<>();
        mso.put("uuid", UUID.randomUUID());
        mso.put("name", "named_params");

        sess.execute("insert into test(id, name) values (:uuid, :name)", mso);

        final Integer cntEnd = sess.query("select count(1) from test", (resultSet, i) -> resultSet.getInt(1)).stream().findFirst().get();

        Assert.assertEquals(cntEnd - cntStart, 1);

    }

    @Test
    public void test_query_by_where() throws SQLException {

        DatabaseSession sess = new JdbcDatabaseSession(this.ds.getConnection());

        sess.execute("delete from test where name = ?", "b2");
        sess.execute("insert into test(id, name) values (?, ?)", UUID.randomUUID(), "b2");
        sess.execute("insert into test(id, name) values (?, ?)", UUID.randomUUID(), "b2");

        final Collection<TestTable> query = sess.query("select * from test where name = ?", (resultSet, i) -> TestTable.of(resultSet.getString(1), resultSet.getString(2)), "b2");

        System.out.println(query);

        Assert.assertEquals(2, query.size());

    }

    @Test
    public void text_query_by_where_named_parameter() throws SQLException {

        DatabaseSession sess = new JdbcDatabaseSession(this.ds.getConnection());

        Map<String, Object> mso = new HashMap<>();
        final String nameValue = "b3";
        mso.put("name", nameValue);


        sess.execute("delete from test where name = :name", mso);
        sess.execute("insert into test(id, name) values (?, ?)", UUID.randomUUID(), nameValue);
        sess.execute("insert into test(id, name) values (?, ?)", UUID.randomUUID(), nameValue);

        final Collection<TestTable> query = sess.query("select * from test where name = :name", (resultSet, i) -> TestTable.of(resultSet.getString(1), resultSet.getString(2)), mso);

        System.out.println(query);

        Assert.assertEquals(2, query.size());


    }

    public interface TestTable {

        static TestTable of(String id, String name) {
            return new TestTable() {
                @Override
                public String id() {
                    return id;
                }

                @Override
                public String name() {
                    return name;
                }

                @Override
                public String toString() {
                    return "\n  --> " + id() + "  " + name();
                }
            };
        }

        String id();

        String name();
    }
}