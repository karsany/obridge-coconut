package org.obridge.coconut.incubator.method2;

public interface StateChangingCommand<T> {

    void perform();

    T original();

}
