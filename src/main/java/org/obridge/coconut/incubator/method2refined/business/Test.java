package org.obridge.coconut.incubator.method2refined.business;


import oracle.jdbc.pool.OracleDataSource;
import org.obridge.coconut.data.session.DatabaseSession;
import org.obridge.coconut.data.transaction.JdbcTransaction;
import org.obridge.coconut.data.transaction.Transaction;
import org.obridge.coconut.data.transaction.TransactionChain;

import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class Test {

    public static void main(String... args) throws SQLException {
        OracleDataSource ds = new OracleDataSource();
        ds.setURL("jdbc:oracle:thin:projects/projects@localhost:1521:xe");

        AddUserCommand auc = new JsonAddUserCommand("{\"name\": \"PLo\", \"password\": \"Bela\"}");
        System.out.println(auc.toString());


        AddUserDbCommand audbc = new AddUserDbCommand(auc);
        Transaction trx = new JdbcTransaction(ds);

        trx.apply(audbc);

        Collection<String> x = trx.apply(new Function<DatabaseSession, Collection<String>>() {
            @Override
            public Collection<String> apply(DatabaseSession databaseSession) {
                return databaseSession.query("select distinct name from users", (resultSet, i) -> resultSet.getString(1));
            }
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
                .transact(new AddUserDbCommand(auc))
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
                .process(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        System.out.println(s);
                    }
                })
                .get((dbConnection, s) -> dbConnection.query("select count(1) from users where name = ?", (resultSet, i) -> resultSet.getInt(1), s).stream().findFirst().get())
                .process(integer -> System.out.println(integer))
                .rollback();

    }

}
