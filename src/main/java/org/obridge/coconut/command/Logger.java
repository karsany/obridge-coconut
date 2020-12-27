package org.obridge.coconut.command;

public interface Logger {
    void info(String message);

    void warning(String message);

    void error(String message, Exception e);
}
