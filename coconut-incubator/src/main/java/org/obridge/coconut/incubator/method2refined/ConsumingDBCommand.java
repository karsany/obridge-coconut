package org.obridge.coconut.incubator.method2refined;

public interface ConsumingDBCommand<T> {

    void run(DBConnection dbConnection, T t);

}
