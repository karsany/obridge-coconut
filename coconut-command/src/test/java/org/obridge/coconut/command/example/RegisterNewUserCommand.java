package org.obridge.coconut.command.example;

import org.obridge.coconut.command.Command;
import org.obridge.coconut.command.Identifiable;

public interface RegisterNewUserCommand extends Command, Identifiable {

    String getUserName();

    String getPasswordHash();

    String getEmailAddress();

}
