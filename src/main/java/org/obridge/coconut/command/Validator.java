package org.obridge.coconut.command;

import java.util.List;

public interface Validator<T extends Command> {
    List<ResultMessage> validate(T t);
}
