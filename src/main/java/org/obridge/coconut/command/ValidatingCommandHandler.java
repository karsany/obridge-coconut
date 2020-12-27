package org.obridge.coconut.command;

import java.util.List;
import java.util.stream.Collectors;

import static org.obridge.coconut.command.ResultMessage.MessageLevel.CRITICAL;
import static org.obridge.coconut.command.ResultMessage.MessageLevel.ERROR;

public class ValidatingCommandHandler<T extends Command> implements CommandHandler<T> {

    private final CommandHandler<T> commandHandler;
    private final List<Validator<T>> validators;

    public ValidatingCommandHandler(CommandHandler<T> commandHandler, List<Validator<T>> validators) {
        this.commandHandler = commandHandler;
        this.validators = validators;
    }


    @Override
    public CommandResult<T> handle(T t) {

        final List<ResultMessage> validationResult = validators.stream().map(validator -> validator.validate(t)).flatMap(resultMessages -> resultMessages.stream()).collect(Collectors.toList());

        if (validationResult.stream().anyMatch(v -> ERROR.equals(v.getLevel()) || CRITICAL.equals(v.getLevel()))) {
            return CommandResult.error(t, validationResult);
        }

        return commandHandler.handle(t);
    }


}
