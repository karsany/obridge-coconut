package org.obridge.coconut.command;

import org.junit.Test;
import org.obridge.coconut.command.bus.AsyncCommandBus;
import org.obridge.coconut.command.bus.CommandBus;
import org.obridge.coconut.command.bus.SimpleCommandBus;
import org.obridge.coconut.command.busimpl.SimpleAsyncCommandBus;
import org.obridge.coconut.command.busimpl.TestPersistedCommandBus;
import org.obridge.coconut.command.example.FormRegisterNewUserCommand;
import org.obridge.coconut.command.example.RegisterNewUserCommand;
import org.obridge.coconut.command.example.RegisterNewUserCommandHandler;

public class CommandBusTest {

    @Test
    public void registerHandler() throws InterruptedException {
        final FormRegisterNewUserCommand command = new FormRegisterNewUserCommand("hupu@example.com", "PassCode12345");

        final CommandBus cb = new TestPersistedCommandBus(new SimpleCommandBus());
        cb.registerHandler(new RegisterNewUserCommandHandler());

        final CommandResult<RegisterNewUserCommand> commandCommandResult = cb.handle(command);

        for (ResultMessage result : commandCommandResult.results()) {
            System.out.println(result.getLevel() + "  " + result.getMessage());
        }

        System.out.println("------------------------------------");

        final AsyncCommandBus sacb = new SimpleAsyncCommandBus(cb);
        sacb.handle(command, commandResult -> {
            commandResult.results()
                    .stream()
                    .forEach(o -> {
                        System.out.println("   Msg: " + ((ResultMessage) o).getMessage());
                    });
        });

        Thread.sleep(3000);

    }
}