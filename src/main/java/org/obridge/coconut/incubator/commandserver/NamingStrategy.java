package org.obridge.coconut.incubator.commandserver;

import org.obridge.coconut.command.CommandHandler;

public interface NamingStrategy {

    String route(Class<? extends CommandHandler> commandHandler);

}
