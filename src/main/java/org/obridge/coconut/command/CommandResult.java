package org.obridge.coconut.command;

import java.util.ArrayList;
import java.util.Collection;

public interface CommandResult<T extends Command> {

    static <T extends Command> CommandResult<T> error(T command, Collection<ResultMessage> resultMessages) {
        return new CommandResult<T>() {
            @Override
            public T command() {
                return command;
            }

            @Override
            public Collection<ResultMessage> results() {
                return resultMessages;
            }

            @Override
            public boolean result() {
                return false;
            }
        };
    }

    T command();

    Collection<ResultMessage> results();

    boolean result();

    class OK<T extends Command> implements CommandResult<T> {

        private final T t;
        private final Collection<ResultMessage> results = new ArrayList<>();

        public OK(T t) {
            this.t = t;
        }

        @Override
        public T command() {
            return this.t;
        }

        @Override
        public Collection<ResultMessage> results() {
            return this.results;
        }

        @Override
        public boolean result() {
            return true;
        }

        public CommandResult<T> addMessage(ResultMessage.MessageLevel level, String message) {
            results.add(new ResultMessage() {
                @Override
                public MessageLevel getLevel() {
                    return level;
                }

                @Override
                public String getMessage() {
                    return message;
                }

                @Override
                public Object getAdditionalInfo() {
                    return null;
                }
            });
            return this;
        }
    }

}
