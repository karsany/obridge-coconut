package org.obridge.coconut.incubator.method2refined;

public interface ConsumingReturningDBCommand<C, R> {

    R run(DBConnection dbConnection, C c);

}
