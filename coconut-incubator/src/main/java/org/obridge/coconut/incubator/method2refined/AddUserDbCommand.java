package org.obridge.coconut.incubator.method2refined;

public class AddUserDbCommand implements DBCommand, AddUserCommand {
    private final AddUserCommand auc;

    public AddUserDbCommand(AddUserCommand auc) {
        this.auc = auc;
    }

    @Override
    public void run(DBConnection db) {
        db.execute("insert into users values (?,?, sysdate)", this.name(), this.password());
    }

    @Override
    public String name() {
        return auc.name();
    }

    @Override
    public String password() {
        return auc.password();
    }
}
