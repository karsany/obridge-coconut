package org.obridge.coconut.incubator.commandserver;

import org.obridge.coconut.command.Command;
import org.obridge.coconut.command.CommandResult;
import org.obridge.coconut.command.ResultMessage;
import org.obridge.coconut.json.JsonCollection;
import org.obridge.coconut.json.JsonString;

import java.util.Collection;
import java.util.stream.Collectors;

public class JsonCommandResult<T extends Command & JsonString> implements CommandResult<T>, JsonString {
    private final CommandResult<T> cr;

    public JsonCommandResult(CommandResult<T> cr) {
        this.cr = cr;
    }


    @Override
    public T command() {
        return cr.command();
    }

    @Override
    public Collection<ResultMessage> results() {
        return this.cr.results();
    }

    public JsonCollection<JsonResultMessage> resultsInJsonCollection() {
        return new JsonCollection<JsonResultMessage>
                (
                        this
                                .results()
                                .stream()
                                .map
                                        (
                                                resultMessage -> new JsonResultMessage() {
                                                    @Override
                                                    public MessageLevel getLevel() {
                                                        return resultMessage.getLevel();
                                                    }

                                                    @Override
                                                    public String getMessage() {
                                                        return resultMessage.getMessage();
                                                    }

                                                    @Override
                                                    public Object getAdditionalInfo() {
                                                        return resultMessage.getAdditionalInfo();
                                                    }

                                                    @Override
                                                    public String toJson() {
                                                        return String.format(
                                                                "{\"level\": \"%s\", \"message\": \"%s\"}",
                                                                this.getLevel(),
                                                                this.getMessage()
                                                        );
                                                    }
                                                }
                                        )
                                .collect(Collectors.toList())
                );
    }

    @Override
    public boolean result() {
        return this.cr.result();
    }

    @Override
    public String toJson() {
        return String.format(
                "{ \"result\": \"%s\", \"command\": %s, \"results\": %s}",
                this.result(),
                this.command().toJson(),
                this.resultsInJsonCollection().toJson()
        );
    }

    private interface JsonResultMessage extends ResultMessage, JsonString {
    }
}
