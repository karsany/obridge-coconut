package org.obridge.coconut.incubator.method2refined.business;

import org.obridge.coconut.data.session.DatabaseSession;

import java.util.function.Consumer;

public class AddUserDbCommand implements AddUserCommand, Consumer<DatabaseSession> {
    private final AddUserCommand auc;

    public AddUserDbCommand(AddUserCommand auc) {
        this.auc = auc;
    }

    @Override
    public String name() {
        return auc.name();
    }

    @Override
    public String password() {
        return auc.password();
    }

    @Override
    public void accept(DatabaseSession databaseSession) {
        databaseSession.execute("insert into users values (?,?, sysdate)", this.name(), this.password());
    }
}
