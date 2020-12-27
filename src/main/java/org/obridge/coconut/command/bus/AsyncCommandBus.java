package org.obridge.coconut.command.bus;

import org.obridge.coconut.command.Command;
import org.obridge.coconut.command.CommandResultCallback;
import org.obridge.coconut.command.Identifiable;

public interface AsyncCommandBus<T extends Command & Identifiable> extends CommandBus<T> {

    void handle(T command, CommandResultCallback<T> cr);

}
