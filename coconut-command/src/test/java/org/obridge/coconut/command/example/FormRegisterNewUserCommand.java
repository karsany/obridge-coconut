package org.obridge.coconut.command.example;

import org.obridge.coconut.command.IdentifiableAbstractCommand;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FormRegisterNewUserCommand extends IdentifiableAbstractCommand implements RegisterNewUserCommand {

    private final String email;
    private final String password;

    public FormRegisterNewUserCommand(String email, String password) {

        this.email = email;
        this.password = password;
    }

    @Override
    public String getUserName() {
        return this.email;
    }

    @Override
    public String getPasswordHash() {

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter.printHexBinary(digest)
                                             .toUpperCase();
            return myHash;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getEmailAddress() {
        return this.email;
    }
}
