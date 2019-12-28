package org.obridge.coconut.incubator;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.pool.OracleDataSource;
import org.json.JSONObject;
import org.obridge.coconut.command.Command;
import org.obridge.coconut.command.CommandHandler;
import org.obridge.coconut.command.CommandResult;
import org.obridge.coconut.command.ResultMessage;
import org.obridge.coconut.command.bus.SimpleCommandBus;
import org.obridge.coconut.data.jdbc.JdbcTemplate;
import org.obridge.coconut.incubator.commandserver.CommandServer;
import org.obridge.coconut.incubator.commandserver.NamingStrategy;
import org.obridge.coconut.json.JsonString;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class App {

    public static void main(String[] args) throws SQLException {
        OracleDataSource ds = new OracleDataSource();
        ds.setURL("jdbc:oracle:thin:projects/projects@localhost:1521:xe");

        final CommandServer commandServer = new CommandServer(8888, new NamingStrategy() {
            @Override
            public String route(Class<? extends CommandHandler> commandHandler) {
                return commandHandler.getSimpleName().replace("Handler", "").toLowerCase();
            }
        }, new SimpleCommandBus());

        commandServer.registerHandler(new AddUserHandler(ds));
        commandServer.printUrls();

        //   commandServer.run();

        final AddUser addUser = new AddUser() {
            @Override
            public String name() {
                return "ferko";
            }

            @Override
            public String password() {
                return "lolo";
            }

            @Override
            public String toJson() {
                return new JSONObject().append("name", name()).append("password", password()).toString(2);
            }
        };

        System.out.println(addUser.toJson());

        commandServer.handle(addUser);
    }

    public interface AddUser extends Command, JsonString {
        String name();

        String password();
    }

    public static class AddUserHandler implements CommandHandler<AddUser> {

        private final DataSource dataSource;

        public AddUserHandler(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public CommandResult<AddUser> handle(AddUser addUser) {
            System.out.println(addUser.name());
            System.out.println(addUser.password());

            new Transaction(new JdbcSession(dataSource)).run(session -> {

                session.insert("users")
                        .value("name", addUser.name())
                        .value("password", addUser.password())
                        .exec(session);

                session.update("users")
                        .set("createdDt", LocalDateTime.now())
                        .where("name = ?", addUser.name())
                        .exec(session);
            });

            return new CommandResult.OK<>(addUser).addMessage(ResultMessage.MessageLevel.INFO, "User registered");
        }

        private interface TransactionCommands {

            void run(JdbcSession session);

        }

        private class JdbcSession {

            private final DataSource dataSource;
            private final Connection connection;
            private final JdbcTemplate jdbcTemplate;

            public JdbcSession(DataSource dataSource) {
                try {
                    this.dataSource = dataSource;
                    this.connection = dataSource.getConnection();
                    this.connection.setAutoCommit(false);
                    this.jdbcTemplate = new JdbcTemplate(connection);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            public void execute(String s) {
                System.out.println(s);
            }

            public void commit() {
                try {
                    connection.commit();
                } catch (SQLException e) {
                    try {
                        this.connection.rollback();
                    } catch (SQLException ex) {
                        throw new RuntimeException(e);
                    }
                    throw new RuntimeException(e);
                }
            }

            public Insert insert(String table) {
                return new Insert(table);
            }

            public void execute(String sql, Object... params) {
                System.out.println(sql);
                System.out.println(Arrays.asList(params));
                jdbcTemplate.execute(sql, params);
            }

            public void close() {
                try {
                    this.connection.rollback();
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            public Update update(String table) {
                return new Update(table);
            }

            public void rollback() {
                try {
                    this.connection.rollback();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private class Transaction {


            private final JdbcSession jdbcSession;

            public Transaction(JdbcSession jdbcSession) {
                this.jdbcSession = jdbcSession;
            }

            public void run(TransactionCommands trx) {

                try {
                    trx.run(jdbcSession);
                    jdbcSession.commit();

                } catch (Exception e) {
                    jdbcSession.rollback();
                }

                jdbcSession.close();

            }

        }

        private class Insert {

            private final String tableName;
            private final Map<String, Object> values = new HashMap<>();

            public Insert(String tableName) {
                this.tableName = tableName;
            }

            public Insert value(String column, Object value) {
                values.put(column, value);
                return this;
            }

            public void exec(JdbcSession session) {

                final String cols = values.keySet().stream().collect(Collectors.joining(","));
                final String questionmarks = values.keySet().stream().map(s -> "?").collect(Collectors.joining(","));
                final Object[] vals = values.values().toArray();

                session.execute("insert into " + tableName + "(" + cols + ") values (" + questionmarks + ")", vals);
            }
        }

        private class Update {
            private final String tableName;
            private final Map<String, Object> values = new HashMap<>();
            private final List<String> whereAnds = new ArrayList<>();
            private final List<Object> whereBinds = new ArrayList<>();

            public Update(String tableName) {
                this.tableName = tableName;
            }

            public Update set(String field, Object value) {
                values.put(field, value);
                return this;
            }

            public Update where(String expression, Object... bind) {
                whereAnds.add(expression);
                whereBinds.addAll(Arrays.asList(bind));
                return this;
            }

            public void exec(JdbcSession session) {

                final String sets = values.keySet().stream().map(s -> s + " = ?").collect(Collectors.joining(","));
                final String where = whereAnds.stream().map(s -> "(" + s + ")").collect(Collectors.joining(" and "));

                session.execute("UPDATE " + tableName + " SET " + sets + " WHERE " + where, Stream.concat(values.values().stream(), whereBinds.stream()).toArray());

            }
        }
    }
}
