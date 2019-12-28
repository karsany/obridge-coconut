package org.obridge.coconut.incubator.db;

public interface Session {
    void commit();

    void rollback();

    void commitAndClose();

    boolean isDirty();

    void executeCommand(String command);

    void executeCommand(DatabaseCommand command);
}
