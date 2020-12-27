package org.obridge.coconut.command.bus;

import org.obridge.coconut.command.Command;
import org.obridge.coconut.command.CommandHandler;
import org.obridge.coconut.command.CommandResult;

public interface CommandBus<Q extends Command> {

    void registerHandler(Class<Q> commandClass, CommandHandler<Q> ch);

    CommandResult<Q> handle(Q command);

}
