package org.obridge.coconut.command.busimpl;

import org.obridge.coconut.command.*;
import org.obridge.coconut.command.bus.AsyncCommandBus;
import org.obridge.coconut.command.bus.CommandBus;

public class SimpleAsyncCommandBus<T extends Command & Identifiable> implements AsyncCommandBus<T> {

    private final CommandBus cb;

    public SimpleAsyncCommandBus(CommandBus cb) {
        this.cb = cb;
    }

    @Override
    public void handle(T command, CommandResultCallback<T> cr) {

        new Thread(
                () -> {
                    System.out.println("NEW THREAD: DO WORK");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    final CommandResult handle = cb.handle(command);

                    new Thread(() -> {
                        System.out.println("NEW THREAD FOR CALLBACK");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        cr.callback(handle);
                        System.out.println("CALLBACK FINISHED");
                    }).start();
                    System.out.println("DO WORK FINISHED");
                }).start();

    }

    @Override
    public void registerHandler(Class<T> commandClass, CommandHandler<T> ch) {
        cb.registerHandler(commandClass, ch);
    }

    @Override
    public CommandResult<T> handle(T command) {
        return cb.handle(command);
    }
}
