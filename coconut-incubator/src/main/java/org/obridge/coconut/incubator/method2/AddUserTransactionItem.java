package org.obridge.coconut.incubator.method2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AddUserTransactionItem implements AddUserCommand, UndependentTransactionItem<LocalDateTime> {

    private final AddUserCommand addUserCommand;

    public AddUserTransactionItem(AddUserCommand addUserCommand) {
        this.addUserCommand = addUserCommand;
    }

    @Override
    public String name() {
        return this.addUserCommand.name();
    }

    @Override
    public String password() {
        return this.addUserCommand.password();
    }

    @Override
    public LocalDateTime perform(Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("insert into users (name, password, createdt) values (?,?,sysdate)");
        preparedStatement.setString(1, name());
        preparedStatement.setString(2, password());
        preparedStatement.execute();
        return LocalDateTime.now();
    }
}
