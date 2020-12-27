package org.obridge.coconut.command;

public interface ResultMessage {

    static ResultMessage of(MessageLevel level, String message) {
        return new ResultMessage() {
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
        };
    }

    MessageLevel getLevel();

    String getMessage();

    Object getAdditionalInfo();

    enum MessageLevel {
        DEBUG, NOTICE, INFO, WARNING, ERROR, CRITICAL
    }

}
