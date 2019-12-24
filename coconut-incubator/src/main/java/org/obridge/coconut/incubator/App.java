package org.obridge.coconut.incubator;

import org.obridge.coconut.command.Command;
import org.obridge.coconut.command.CommandHandler;
import org.obridge.coconut.command.CommandResult;
import org.obridge.coconut.command.ResultMessage;
import org.obridge.coconut.incubator.commandserver.CommandServer;
import org.obridge.coconut.json.JsonString;

public class App {
    public static void main(String[] args) {
        final CommandServer commandServer = new CommandServer(8888);

        commandServer.registerHandler(new AddUserHandler());

        commandServer.run();
    }

    public interface AddUser extends Command, JsonString {
        String name();

        String password();
    }

    public static class AddUserHandler implements CommandHandler<AddUser> {

        @Override
        public CommandResult<AddUser> handle(AddUser addUser) {
            System.out.println(addUser.name());
            System.out.println(addUser.password());

            return new CommandResult.OK<>(addUser).addMessage(ResultMessage.MessageLevel.INFO, "User registered");
        }
    }
}
