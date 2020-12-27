package org.obridge.coconut.command;

public class LoggingCommandHandler<T extends Command> implements CommandHandler<T> {

    private final CommandHandler<T> commandHandler;
    private final Logger logger;

    public LoggingCommandHandler(CommandHandler<T> commandHandler, Logger logger) {
        this.commandHandler = commandHandler;
        this.logger = logger;
    }

    @Override
    public CommandResult<T> handle(T t) {
        logger.info("Command started: " + t.getClass().getCanonicalName());
        try {
            final CommandResult<T> handle = commandHandler.handle(t);
            if (handle.result()) {
                logger.info("Command finished: " + t.getClass().getCanonicalName());
            } else {
                logger.warning("Command failed with business error: " + t.getClass().getCanonicalName());
            }
            return handle;
        } catch (Exception e) {
            logger.error("Critical error during command: " + t.getClass().getCanonicalName(), e);
            throw e;
        }

    }
}
