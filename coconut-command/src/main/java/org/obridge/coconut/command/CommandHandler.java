package org.obridge.coconut.command;

public interface CommandHandler<T extends Command> {

    CommandResult<T> handle(T t);

}
