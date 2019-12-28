package org.obridge.coconut.incubator.method2;

import oracle.jdbc.pool.OracleDataSource;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class Test {

    public static void main(String... args) throws SQLException {

        AddUserCommand auc = new JsonAddUserCommand("{\"name\": \"Lo\", \"password\": \"Bela\"}");

        System.out.println(auc.name());
        System.out.println(auc.password());
        System.out.println(auc.toString());

        final AddUserTransactionItem nst = new AddUserTransactionItem(auc);

        OracleDataSource ds = new OracleDataSource();
        ds.setURL("jdbc:oracle:thin:projects/projects@localhost:1521:xe");

        /*
        final Connection connection = ds.getConnection();
        connection.setAutoCommit(false);
        nst.run(connection);

        connection.commit();
        connection.close();
         */

        final Transaction<LocalDateTime> trx = new JdbcTransaction<Void>(ds)
                .apply(nst)
                .apply((Callback<LocalDateTime>) System.out::println);

        final Transaction<String> jajj = trx.apply(connection -> {
            System.out.println("jajj");
            return "Hello World";
        });

        final Transaction<Boolean> apply = jajj.apply((connection, s) -> connection.prepareStatement("insert into users values ('aaa','" + s + "',sysdate)").execute());

        apply.commit();


    }
}
