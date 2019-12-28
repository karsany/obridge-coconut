package org.obridge.coconut.incubator.method2refined;

public interface ReturningDBCommand<T> {

    T run(DBConnection dbConnection);

}
