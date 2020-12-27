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

import java.util.Arrays;
import java.util.List;

public class CommandBusTest {

    @Test
    public void t2() {
        final FormRegisterNewUserCommand command = new FormRegisterNewUserCommand("hupu@example.com", "PassCode12345");
        final FormRegisterNewUserCommand command2 = new FormRegisterNewUserCommand("", "PassCode12345");

        final CommandBus cb = new TestPersistedCommandBus(new SimpleCommandBus());

        System.out.println("-----------------------");

        final Logger logger = new Logger() {
            @Override
            public void info(String message) {
                System.out.println("[INFO] " + message);
            }

            @Override
            public void warning(String message) {
                System.out.println("[WARN] " + message);
            }

            @Override
            public void error(String message, Exception e) {
                System.out.println("[ERROR]" + message);
                e.printStackTrace();
            }
        };

        cb.registerHandler(RegisterNewUserCommand.class,
                new LoggingCommandHandler<RegisterNewUserCommand>(
                        new ValidatingCommandHandler<RegisterNewUserCommand>(
                                new RegisterNewUserCommandHandler(),
                                Arrays.asList(new Validator<RegisterNewUserCommand>() {
                                    @Override
                                    public List<ResultMessage> validate(RegisterNewUserCommand c) {
                                        if (c.getUserName().isEmpty()) {
                                            return Arrays.asList(ResultMessage.of(ResultMessage.MessageLevel.ERROR, "user name is empty"));
                                        } else {
                                            return Arrays.asList();
                                        }
                                    }
                                })
                        ), logger
                )
        );
        System.out.println("-----------------------");

        cb.handle(command);
        System.out.println("----------");
        cb.handle(command2);

    }

    @Test
    public void registerHandler() throws InterruptedException {
        final FormRegisterNewUserCommand command = new FormRegisterNewUserCommand("hupu@example.com", "PassCode12345");

        final CommandBus cb = new TestPersistedCommandBus(new SimpleCommandBus());
        cb.registerHandler(RegisterNewUserCommand.class, new RegisterNewUserCommandHandler());

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