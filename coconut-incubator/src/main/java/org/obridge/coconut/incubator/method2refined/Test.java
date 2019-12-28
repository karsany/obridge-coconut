package org.obridge.coconut.incubator.method2refined;


import oracle.jdbc.pool.OracleDataSource;

import java.sql.SQLException;
import java.util.Collection;

public class Test {

    public static void main(String... args) throws SQLException {
        OracleDataSource ds = new OracleDataSource();
        ds.setURL("jdbc:oracle:thin:projects/projects@localhost:1521:xe");

        AddUserCommand auc = new JsonAddUserCommand("{\"name\": \"PLo\", \"password\": \"Bela\"}");
        System.out.println(auc.toString());


        AddUserDbCommand audbc = new AddUserDbCommand(auc);
        Transaction trx = new JdbcTransaction(ds);

        trx.apply(audbc);

        Collection<String> x = trx.ret(dbConnection -> {
            return dbConnection.query("select distinct name from users", (resultSet, i) -> resultSet.getString(1));
        });

        System.out.println(x);

        final String tesztUser = "teszt";

        trx.apply((dbConnection, s) -> {
            dbConnection.execute("insert into users values (?,?,sysdate)", s, s);
        }, tesztUser);

        int y = trx.apply((dbConnection, s) -> {
            return dbConnection.query("select count(1) from users where name = ?", (resultSet, i) -> resultSet.getInt(1), s).stream().findFirst().get();
        }, tesztUser);


        System.out.println(y);

        trx.commit();

        final TransactionChain tc = new JdbcTransaction(ds).chain();


        tc
                .apply(dbConnection -> audbc.run(dbConnection))
                .get(
                        dbConnection ->
                                dbConnection
                                        .query(
                                                "select min(name) from users",
                                                (resultSet, i) ->
                                                        resultSet.getString(1)
                                        )
                                        .stream()
                                        .findFirst()
                                        .get()
                )
                .callback(s -> System.out.println(s))
                .putAndGet((dbConnection, s) -> dbConnection.query("select count(1) from users where name = ?", (resultSet, i) -> resultSet.getInt(1), s).stream().findFirst().get())
                .callback(integer -> System.out.println(integer))
                .rollback();

    }

}
