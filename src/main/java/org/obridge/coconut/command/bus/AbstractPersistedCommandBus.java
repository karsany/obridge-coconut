package org.obridge.coconut.command.bus;

import org.obridge.coconut.command.Command;
import org.obridge.coconut.command.CommandHandler;
import org.obridge.coconut.command.CommandResult;
import org.obridge.coconut.command.Identifiable;

public abstract class AbstractPersistedCommandBus<T extends Command & Identifiable> implements CommandBus<T> {

    private final CommandBus commandBus;

    protected AbstractPersistedCommandBus(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    protected abstract void persistCommandStart(T command);

    protected abstract void persistCommandFinished(CommandResult<T> commandResult);

    protected abstract void persistCommandFailed(T command);

    @Override
    public void registerHandler(Class<T> commandClass, CommandHandler<T> ch) {
        this.commandBus.registerHandler(commandClass, ch);
    }

    @Override
    public CommandResult<T> handle(T command) {
        persistCommandStart(command);

        try {
            final CommandResult<T> result = commandBus.handle(command);
            persistCommandFinished(result);
            return result;
        } catch (Exception e) {
            persistCommandFailed(command);
            throw e;
        }

    }

}
