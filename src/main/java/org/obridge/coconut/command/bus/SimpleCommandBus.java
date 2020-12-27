package org.obridge.coconut.command.bus;

import org.obridge.coconut.command.Command;
import org.obridge.coconut.command.CommandHandler;
import org.obridge.coconut.command.CommandResult;

import java.util.HashMap;
import java.util.Map;

public class SimpleCommandBus<T extends Command> implements CommandBus<T> {

    private Map<String, CommandHandler> handlers = new HashMap<>();

    @Override
    public void registerHandler(Class<T> commandClass, CommandHandler<T> ch) {
        handlers.put(commandClass.getName(), ch);
        System.out.println("Registered handler: " + ch.getClass() + " to " + commandClass.getName());
    }

    @Override
    public CommandResult<T> handle(T command) {

        CommandHandler commandHandler = null;

        if (handlers.containsKey(command.getClass()
                .getName())) {
            commandHandler = handlers.get(command.getClass()
                    .getName());
        } else {
            for (Class<?> anInterface : command.getClass()
                    .getInterfaces()) {

                if (handlers.containsKey(anInterface.getName())) {
                    commandHandler = handlers.get(anInterface.getName());
                    break;
                }

            }

        }

        if (commandHandler != null) {
            return commandHandler.handle(command);
        } else {
            throw new UnsupportedOperationException("Command handler not found for: " + command.getClass().getName());
        }
    }
}
