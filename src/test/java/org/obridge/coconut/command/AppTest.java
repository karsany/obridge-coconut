package org.obridge.coconut.command;

import org.junit.Test;
import org.obridge.coconut.command.example.FormRegisterNewUserCommand;

public class AppTest {

    @Test
    public void reguser() {

        final FormRegisterNewUserCommand register = new FormRegisterNewUserCommand("example@example.com", "123456789");

    }
}
